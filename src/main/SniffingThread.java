package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;


public class SniffingThread extends Service {

    private StringBuilder errbuf;
    private int snaplen;
    private int flags;
    private int timeout;
    private Pcap pcap;
    private PcapPacketHandler<String> jpacketHandler;
    private Ip4 ip;
    private Tcp tcp;
    private Udp udp;
    private Http http;

    public SniffingThread(){
        this.errbuf = new StringBuilder(); // For any error msgs
        this.snaplen = 64 * 1024;           // Capture all packets, no trucation
        this.flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        this.timeout = 10 * 1000;           // 10 seconds in millis
        this.pcap = Pcap.openLive(Main.device.getName(), this.snaplen, this.flags, this.timeout, this.errbuf);
        this.ip = new Ip4();

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: " + this.errbuf.toString());
            return;
        }

        this.jpacketHandler = new PcapPacketHandler<String>() {
            public void nextPacket(PcapPacket packet, String user) {
//                System.out.printf("Received packet at %s caplen=%-4d len=%-4d %s\n",
//                        new Date(packet.getCaptureHeader().timestampInMillis()),
//                        packet.getCaptureHeader().caplen(),  // Length actually captured
//                        packet.getCaptureHeader().wirelen(), // Original length
//                        user                                 // User supplied object
//                );
                if(packet.hasHeader(ip)){
                    String date = FormatUtils.ip(ip.source());
                    String sourceIP = FormatUtils.ip(ip.source());
                    String destIP = FormatUtils.ip(ip.destination());
                    String protocol = ip.typeEnum().toString();
                    String origLen = String.valueOf(packet.getCaptureHeader().wirelen());
                    String info = "info";

                    PacketDetails pd = new PacketDetails(date,sourceIP,destIP,protocol,origLen,info);
                    Main.packetsList.add(pd);
                    System.out.println(pd.dateProperty()+" "+pd.sourceIPProperty()+" "+pd.destIPProperty()+" "+pd.protocolProperty()+" "+pd.origLenProperty()+" "+pd.infoProperty());
                }
            }
        };
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                while (true){
                    pcap.loop(1, jpacketHandler, "");
                }
            }
        };
    }

}
