import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

public class Main {
    public static void main(String argv[]){
        InetAddress addr;
        PcapNetworkInterface nif;
        try{
            addr = InetAddress.getByName("192.168.1.1");
            nif = new NifSelector().selectNetworkInterface();
            if (nif == null) System.exit(1);
            final PcapHandle handle = nif.openLive(65536, PromiscuousMode.PROMISCUOUS, 10);
            PacketListener listener = new PacketListener() {
                //@Override
                public void gotPacket(Packet packet) {
                    printPacket(packet, handle);
                }
            };
            handle.loop(10, listener);
        }catch (UnknownHostException e){
            System.out.println("here" + e.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private static void printPacket(Packet packet, PcapHandle ph){
        StringBuilder sb = new StringBuilder();
//      sb.append("A Packet Captured at ")
//              .append(ph.getTimestamp())
//              .append(":");
        IpV4Packet ip = packet.get(IpV4Packet.class);
        InetAddress src = ip.getHeader().getSrcAddr();
//      System.out.println(packet.get(IpV4Packet.class));
        System.out.println(src);
//      System.out.println(packet);
    }
}
