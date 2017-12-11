package main;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.*;

public class SniffingViewController implements ControlledScreen {

    ScreenController screenController;

    @FXML
    TableView<PacketDetails> tableView;
    @FXML
    TableColumn<PacketDetails, String> numCol;
    @FXML
    TableColumn<PacketDetails, Date> timeCol;
    @FXML
    TableColumn<PacketDetails, String> sourceCol;
    @FXML
    TableColumn<PacketDetails, String> destCol;
    @FXML
    TableColumn<PacketDetails, String> protocolCol;
    @FXML
    TableColumn<PacketDetails, String> lenCol;
    @FXML
    TableColumn<PacketDetails, String> infoCol;
    @FXML
    TextArea packetDetails;
    @FXML
    Button startButton;
    @FXML
    Button stopButton;

    SniffingThread thread;

    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void onPacketSelected(){
        PacketDetails selectedPacketDetails = tableView.getSelectionModel().getSelectedItem();
        String text = selectedPacketDetails.packet.toString();
//        String text = "Package Numer: " + selectedPacketDetails.numProperty().get() + "\nDate: " + selectedPacketDetails.dateProperty().get() + "\nSource IP: " + selectedPacketDetails.sourceIPProperty().get() + "\nDestination IP: " + selectedPacketDetails.destIPProperty().get() + "\nProtocol: " + selectedPacketDetails.protocolProperty().get() + "\nPackage Length: " + selectedPacketDetails.origLenProperty().get();
        packetDetails.setText(text);
    }

    public void onStartButtonPressed(){
        thread = new SniffingThread();
        thread.start();
        startButton.setDisable(true);
        stopButton.setDisable(false);
    }

    public void onStopButtonPressed(){
        stopButton.setDisable(true);
        startButton.setDisable(false);
        thread.stop();
    }

    public void initialize() {
        numCol.setCellValueFactory(cellData -> cellData.getValue().numProperty());
        timeCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        sourceCol.setCellValueFactory(cellData -> cellData.getValue().sourceIPProperty());
        destCol.setCellValueFactory(cellData -> cellData.getValue().destIPProperty());
        protocolCol.setCellValueFactory(cellData -> cellData.getValue().protocolProperty());
        lenCol.setCellValueFactory(cellData -> cellData.getValue().origLenProperty());
        infoCol.setCellValueFactory(cellData -> cellData.getValue().infoProperty());

        tableView.setItems(Main.packetsList);
    }
}
