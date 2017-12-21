package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainViewController implements ControlledScreen {
    @FXML
    Button selectButton;
    @FXML
    ComboBox interfaceDropMenu;
    @FXML
    CheckBox dumperStatus;
    @FXML
    TextField fileName;

    ScreenController mainScreen;

    List<PcapIf> alldevs;

    public void initialize() {
        alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs

        int r = Pcap.findAllDevs(alldevs, errbuf);
        for (PcapIf p : alldevs) {
            System.out.println("here" + p);
            interfaceDropMenu.getItems().addAll(p.getDescription());
        }
    }

    public void selectInterface() {
        Main.device = alldevs.get(interfaceDropMenu.getSelectionModel().getSelectedIndex());
        if (fileName.getText().isEmpty()){
            Main.dumpFileName = "pcapdump.cap";
        } else {
            Main.dumpFileName = fileName.getText() + ".cap";
        }
        Main.dumpStatus = dumperStatus.isSelected();
        mainScreen.setScreen("SniffingView");
    }

    @Override
    public void setScreenParent(ScreenController sc) {
        mainScreen = sc;
    }

    public void load (){
        Stage newStage = new Stage();
        FileChooser fc = new FileChooser();
        File pcapFile = fc.showOpenDialog(newStage);
        PcapFileIO fileLoad = new PcapFileIO(pcapFile.getAbsoluteFile().getAbsolutePath());
        try {
            fileLoad.readOfflineFiles();
            mainScreen.setScreen("SniffingView");
        } catch (Exception exceptionReadingPcapFiles) {
            exceptionReadingPcapFiles.printStackTrace();
        }
    }
}
