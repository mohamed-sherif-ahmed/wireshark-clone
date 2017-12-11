package main;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.util.PcapPacketArrayList;

import java.util.List;

public class PcapFile {
   String FileAddress = "" ;
   public PcapFile(String FileAddress){
       this.FileAddress = FileAddress;
   }
    public PcapPacketArrayList readOfflineFiles() throws ExceptionReadingPcapFiles{
        //First, setup error buffer and name for our file
        final StringBuilder errbuf = new StringBuilder(); // For any error msgs
        //Second ,open up the selected file using openOffline call
        Pcap pcap = Pcap.openOffline(FileAddress, errbuf);
        //Throw exception if it cannot open the file
        if (pcap == null) {
        throw new ExceptionReadingPcapFiles();
         }
        //Next, we create a packet handler which will receive packets from the libpcap loop.
        PcapPacketHandler<PcapPacketArrayList> jpacketHandler = new PcapPacketHandler<PcapPacketArrayList>() {
    public void nextPacket(PcapPacket packet, PcapPacketArrayList PaketsList) {
        PaketsList.add(packet);
    }
        };
        try {
            PcapPacketArrayList packets = new PcapPacketArrayList();
            pcap.loop(-1,jpacketHandler,packets);
            return packets;
        } finally {
    //Last thing to do is close the pcap handle
            pcap.close();
        }
   }

    private class ExceptionReadingPcapFiles extends Exception {
    }
}
