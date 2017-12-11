package main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class Main extends Application {
    public static PcapIf device;
    public static ObservableList<PacketDetails> packetsList = FXCollections.observableArrayList();

    ScreenController screenController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        screenController = new ScreenController();
        screenController.loadScreen("MainView", "MainView.fxml");
        screenController.loadScreen("SniffingView", "SniffingView.fxml");
        screenController.setScreen("MainView");
        Group root = new Group();
        root.getChildren().addAll(screenController);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}