package main;

import com.sun.javafx.charts.Legend;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.*;

import static main.Main.packetsList;

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
    PcapFileIO pcapFileIO = new PcapFileIO("ss.cap");

    @FXML
    TextField filterField ;

    Boolean x =false ;

    SortedList<PacketDetails> sortedData ;


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
        thread = new SniffingThread(Main.dumpStatus);
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

        tableView.setItems(packetsList);

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<PacketDetails> filteredData = new FilteredList<>(packetsList, p -> true);
        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(PacketDetails -> {
                // If filter text is empty, display all Data
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                if (PacketDetails.getProtocol().toLowerCase().contains(lowerCaseFilter)) {
                    System.out.println("filterProtocol");
                    return true; // Filter matches protocol.
                } else if (PacketDetails.getNum().contains(newValue)) {
                    System.out.println("filterno.");
                    return true; // Filter matches SourceIP.
                }
//                else if (PacketDetails.destIPProperty().toString().contains(newValue)) {
//                    System.out.println("filterDest");
//                    return true; // Filter matches DestIP.
//                }
//                else if (PacketDetails.sourceIPProperty().toString().toLowerCase().contains(lowerCaseFilter)) {
//                    System.out.println("filterSource");
//                    return true; // Filter matches SourceIP.
//                }
                System.out.println("filter0");
                return false; // Does not match.
            });
        });
        sortedData = new SortedList<>(filteredData);

    }

    public void filter(){
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
        //((Property<Comparator<? super PacketDetails>>) tableView.unbindBidirectional(sortedData.comparatorProperty());
    }

    public void returnToChangeInterface() {
        Main.packetsList = FXCollections.observableArrayList();
        screenController.setScreen("MainView");
    }

    public void saveCap(){
        pcapFileIO.saveOfflineFiles(thread.getPcap());
    }

}
