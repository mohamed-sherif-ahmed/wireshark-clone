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
import org.jnetpcap.protocol.lan.Ethernet;
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
    public static Ip4 ip =  new Ip4();
    public static Http http = new Http();
    private boolean cancelled;

    //dumper variables
    private boolean enableDumper;
    private String fileOutput;

    public SniffingThread(){
        this.errbuf = new StringBuilder(); // For any error msgs
        this.snaplen = 64 * 1024;           // Capture all packets, no trucation
        this.flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        this.timeout = 10 * 1000;           // 10 seconds in millis
        this.pcap = Pcap.openLive(Main.device.getName(), this.snaplen, this.flags, this.timeout, this.errbuf);

        cancelled = false;

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: " + this.errbuf.toString());
            return;
        }

        this.jpacketHandler = new PcapPacketHandler<String>() {
            public void nextPacket(PcapPacket packet, String user) {
                SniffingThread.parsePacket(packet);
            }
        };
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                while (!cancelled){
                    pcap.loop(1, jpacketHandler, "");
                }
                return null;
            }
        };
    }

    public void stop(){
        this.cancelled = true;
    }


    public static void parsePacket(PcapPacket packet) {
        if (packet.hasHeader(SniffingThread.http)){
            packet.hasHeader(SniffingThread.ip);
            String date = String.valueOf(packet.getCaptureHeader().timestampInMillis());
            String sourceIP = FormatUtils.ip(SniffingThread.ip.source());
            String destIP = FormatUtils.ip(SniffingThread.ip.destination());
            String protocol = "HTTP";
            String origLen = String.valueOf(packet.getCaptureHeader().wirelen());
            String info = "info";

            PacketDetails pd = new PacketDetails(date,sourceIP,destIP,protocol,origLen,info,packet);
            Main.packetsList.add(pd);

        }else if(packet.hasHeader(SniffingThread.ip)){
            String date = String.valueOf(packet.getCaptureHeader().timestampInMillis());
            String sourceIP = FormatUtils.ip(SniffingThread.ip.source());
            String destIP = FormatUtils.ip(SniffingThread.ip.destination());
            String protocol = SniffingThread.ip.typeEnum().toString();
            String origLen = String.valueOf(packet.getCaptureHeader().wirelen());
            String info = "info";

            PacketDetails pd = new PacketDetails(date,sourceIP,destIP,protocol,origLen,info,packet);
            Main.packetsList.add(pd);
            //System.out.println(pd.dateProperty()+" "+pd.sourceIPProperty()+" "+pd.destIPProperty()+" "+pd.protocolProperty()+" "+pd.origLenProperty()+" "+pd.infoProperty());
        }
    }

    public Pcap getPcap(){
        return pcap;
    }
}
