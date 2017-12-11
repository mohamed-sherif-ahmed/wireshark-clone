package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class SniffingThread extends Thread {

    StringBuilder errbuf;
    int snaplen;
    int flags;
    int timeout;
    Pcap pcap;
    PcapPacketHandler<String> jpacketHandler;

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

        /***************************************************************************
         * Third we create a packet handler which will receive packets from the
         * libpcap loop.
         **************************************************************************/
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
        /***************************************************************************
         * Fourth we enter the loop and tell it to capture 10 packets. The loop
         * method does a mapping of pcap.datalink() DLT value to JProtocol ID, which
         * is needed by JScanner. The scanner scans the packet buffer and decodes
         * the headers. The mapping is done automatically, although a variation on
         * the loop method exists that allows the programmer to sepecify exactly
         * which protocol ID to use as the data link type for this pcap interface.
         **************************************************************************/
        while (true){
            pcap.loop(1, jpacketHandler, "jNetPcap rocks!");
        }
    }
}
