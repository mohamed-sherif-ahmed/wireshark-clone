package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class SniffingThread extends Thread {

    private StringBuilder errbuf;
    private int snaplen;
    private int flags;
    private int timeout;
    private Pcap pcap;
    private PcapPacketHandler<String> jpacketHandler;

    public SniffingThread(){
        this.errbuf = new StringBuilder(); // For any error msgs
        this.snaplen = 64 * 1024;           // Capture all packets, no trucation
        this.flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        this.timeout = 10 * 1000;           // 10 seconds in millis
        this.pcap = Pcap.openLive(Main.device.getName(), this.snaplen, this.flags, this.timeout, this.errbuf);

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: " + this.errbuf.toString());
            return;
        }

        this.jpacketHandler = new PcapPacketHandler<String>() {
            public void nextPacket(PcapPacket packet, String user) {
                System.out.printf("Received packet at %s caplen=%-4d len=%-4d %s\n",
                        new Date(packet.getCaptureHeader().timestampInMillis()),
                        packet.getCaptureHeader().caplen(),  // Length actually captured
                        packet.getCaptureHeader().wirelen(), // Original length
                        user                                 // User supplied object
                );
            }
        };
    }

    public void run(){
        while (true){
            pcap.loop(1, jpacketHandler, "jNetPcap rocks!");
        }
    }
}
