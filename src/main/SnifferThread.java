package main;

import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.Packet;

public class SnifferThread extends Thread{

    private PcapHandle handle;
    private PacketListener listener;

    public SnifferThread(PcapHandle handle){

        this.handle = handle;
        this.listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
                Main.printPacket(packet, handle);
            }
        };
    }

    @Override
    public void run() {
        super.run();
        try{
            while (true){
                this.handle.loop(1, this.listener);
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
