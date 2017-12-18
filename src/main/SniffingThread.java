package main;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.PcapHandler;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.tcpip.Http;

import java.nio.ByteBuffer;


public class SniffingThread extends Service {

    private StringBuilder errbuf;
    private int snaplen;
    private int flags;
    private int timeout;
    private Pcap pcap;
    private PcapPacketHandler<String> jpacketHandler;
    public static Ip4 ip =  new Ip4();
    public static Http http = new Http();
    public static Arp arp = new Arp();
    private boolean cancelled;

    //dumper variables
    private boolean enableDumper;
    private String fileOutput;
    private PcapDumper dumper;
    PcapHandler<PcapDumper> dumpHandler;

    public SniffingThread(boolean enableDumper){
        this.enableDumper = enableDumper;
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

        if (enableDumper){
            this.dumper = pcap.dumpOpen(Main.dumpFileName); // output file

            this.dumpHandler = new PcapHandler<PcapDumper>() {

                public void nextPacket(PcapDumper dumper, long seconds, int useconds,
                                       int caplen, int len, ByteBuffer buffer) {

                    dumper.dump(seconds, useconds, caplen, len, buffer);
                }
            };
        }
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                while (!cancelled){
                    pcap.loop(1, jpacketHandler, "");
                    if (enableDumper)
                        pcap.loop(1, dumpHandler, dumper);
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
        }else if(packet.hasHeader(SniffingThread.arp)){
            String srcIp = "";
            String dstIp = "";
            byte[] srcByte = arp.spa();
            byte[] dstByte = arp.tpa();
            srcIp = "" + (srcByte[0] & 0xFF) + "." + (srcByte[1] & 0xFF) + "." + (srcByte[2] & 0xFF) + "." + (srcByte[3] & 0xFF);
            dstIp = "" + (dstByte[0] & 0xFF) + "." + (dstByte[1] & 0xFF) + "." + (dstByte[2] & 0xFF) + "." + (dstByte[3] & 0xFF);
            String date = String.valueOf(packet.getCaptureHeader().timestampInMillis());
            String protocol = "ARP";
            String origLen = String.valueOf(packet.getCaptureHeader().wirelen());
            String info = "info";

            PacketDetails pd = new PacketDetails(date,srcIp,dstIp,protocol,origLen,info,packet);
            Main.packetsList.add(pd);
        }
    }

    public Pcap getPcap(){
        return pcap;
    }
}
