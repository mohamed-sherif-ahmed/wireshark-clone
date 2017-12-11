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

    @Override
    public void setScreenParent(ScreenController screenController) {
        this.screenController = screenController;
    }

    public void onPacketSelected(){
        PacketDetails selectedPacketDetails = tableView.getSelectionModel().getSelectedItem();
        String text = "Package Numer: " + selectedPacketDetails.numProperty().toString() + "\nDate: " + selectedPacketDetails.dateProperty().toString() + "\nSource IP: " + selectedPacketDetails.sourceIPProperty().toString() + "\nDestination IP: " + selectedPacketDetails.destIPProperty().toString() + "\nProtocol: " + selectedPacketDetails.protocolProperty() + "\nPackage Length: " + selectedPacketDetails.origLenProperty();
        packetDetails.setText(text);
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
