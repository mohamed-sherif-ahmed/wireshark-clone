package main;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SniffingViewController {
    @FXML
    TableView<PacketDetails> tableView;
    TableColumn<PacketDetails, Integer> numCol;
    TableColumn<PacketDetails, Date> timeCol;
    TableColumn<PacketDetails, String> sourceCol;
    TableColumn<PacketDetails, String> destCol;
    TableColumn<PacketDetails, String> protocolCol;
    TableColumn<PacketDetails, Integer> lenCol;
    TableColumn<PacketDetails, String> infoCol;

    List<PacketDetails> packets;

    TextArea packetDetais;

    private void getPackets(PcapIf device) {
        StringBuilder errbuf = new StringBuilder(); //for holding any error msgs

        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        Pcap pcap =
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

        if (pcap == null) { //inform the user of an error
            System.err.printf("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        }

        /***************************************************************************
         * Third we create a packet handler which will receive packets from the
         * libpcap loop.
         **************************************************************************/
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {
            public void nextPacket(PcapPacket packet, String user) {
                Date date = new Date(packet.getCaptureHeader().timestampInMillis());
                int capLen = packet.getCaptureHeader().caplen();
                int origLen = packet.getCaptureHeader().wirelen();
            }
        };

        /***************************************************************************
         * Fourth we enter the loop and tell it to capture 10 packets. The loop
         * method does a mapping of pcap.datalink() DLT value to JProtocol ID, which
         * is needed by JScanner. The scanner scans the packet buffer and decodes
         * the headers. The mapping is done automatically, although a variation on
         * the loop method exists that allows the programmer to sepecify exactly
         * which protocol ID to use as the data link type for this pcap interface.
         **************************************************************************/
        pcap.loop(10, jpacketHandler, "");

        /***************************************************************************
         * Last thing to do is close the pcap handle
         **************************************************************************/
        pcap.close();
    }

    public void fillTable() {
        for (int i = 0; i < packets.size(); i++) {
            tableView.getItems().add(packets.get(i));
        }
    }

    public List<PacketDetails> getPackets() {
        return packets;
    }

    public void setPackets(List<PacketDetails> packets) {
        this.packets = packets;
    }

    public void initialize() {
//        getPackets(Main.device);
//        System.out.println(packets.get(0));\
        fillTable();
    }
}
