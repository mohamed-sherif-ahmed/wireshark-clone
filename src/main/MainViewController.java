package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;

public class MainViewController {
    @FXML
    Button selectButton ;
    @FXML
    ComboBox interfaceDropMenu ;

    List<PcapIf> alldevs;

    public void initialize (){
        alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs

        int r = Pcap.findAllDevs(alldevs, errbuf);
        for (PcapIf p: alldevs ) {
            System.out.println("here"+ p );
            interfaceDropMenu.getItems().addAll(p);
        }
    }

    public void selectInterface(){
        Main.device = alldevs.get(interfaceDropMenu.getSelectionModel().getSelectedIndex());
    }
}
