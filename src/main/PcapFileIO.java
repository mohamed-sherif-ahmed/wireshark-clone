package main;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDLT;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.PeeringException;
import org.jnetpcap.util.PcapPacketArrayList;

import java.nio.ByteBuffer;
import java.util.List;

public class PcapFileIO {

   String FileAddress;

    public PcapFileIO(String FileAddress){
       this.FileAddress = FileAddress;
    }

    /**
     *
     * @throws ExceptionReadingPcapFiles
     */

    public void readOfflineFiles() throws ExceptionReadingPcapFiles{
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
//                PaketsList.add(packet);
                SniffingThread.parsePacket(packet);
            }
        };
        try {
            PcapPacketArrayList packets = new PcapPacketArrayList();
            pcap.loop(-1,jpacketHandler,packets);
        } finally {
            //Last thing to do is close the pcap handle
            pcap.close();
        }
    }

    public void saveOfflineFiles(Pcap pcap){
        PcapDumper pdumper =  pcap.dumpOpen("dump.cap");
        for (PacketDetails pd : Main.packetsList) {
            ByteBuffer bbuf = ByteBuffer.allocateDirect(pd.packet.getCaptureHeader().wirelen());
            byte[] bytes = new byte[pd.packet.size()];
            System.out.println("bytes : " + bytes.length);
            pd.packet.transferStateAndDataTo(bytes);
            System.out.println("bytes : " + bytes.length);
            pdumper.dump(pd.packet.getCaptureHeader().timestampInMillis(),pd.packet.getCaptureHeader().hdr_len(),pd.packet.getCaptureHeader().caplen(),pd.packet.getCaptureHeader().wirelen(),bbuf);

        }

    }

    private class ExceptionReadingPcapFiles extends Exception {

    }
}
