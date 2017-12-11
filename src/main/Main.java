package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class Main extends Application {
    public static PcapIf device ;
    /**
     * Main startup method
     *
     * @param args
     *          ignored
     */
    public static void main(String[] args) {
         launch(args);
         SniffingThread st = new SniffingThread();
         st.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent loadScreen = (Parent) loader.load();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(loadScreen, 300, 275));
        primaryStage.show();
    }
}