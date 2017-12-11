package main;

import javafx.beans.property.*;
import org.jnetpcap.packet.PcapPacket;

import java.time.LocalDate;
import java.util.Date;

public class PacketDetails {
    private final ObjectProperty<Date> date;

    public StringProperty numProperty() {
        return num;
    }

    static private int count = 0;
    private final StringProperty num;
    private final StringProperty sourceIP;
    private final StringProperty destIP;
    private final StringProperty protocol;
    private final StringProperty origLen;
    private final StringProperty info;
    PcapPacket packet;

    public String getProtocol() {
        return protocol.get();
    }

    public String getNum() {
        return num.get();
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public StringProperty sourceIPProperty() {
        return sourceIP;
    }

    public StringProperty destIPProperty() {
        return destIP;
    }

    public StringProperty protocolProperty() {
        return protocol;
    }


    public StringProperty origLenProperty() {
        return origLen;
    }


    public StringProperty infoProperty() {
        return info;
    }

    public PacketDetails(String date, String sourceIP, String destIP, String protocol, String origLen, String info, PcapPacket packet) {
        this.num = new SimpleStringProperty(String.valueOf(++count));
        this.packet = packet;
        this.date = new SimpleObjectProperty(date);
        this.sourceIP = new SimpleStringProperty(sourceIP);
        this.destIP = new SimpleStringProperty(destIP);
        this.protocol = new SimpleStringProperty(protocol);
        this.origLen = new SimpleStringProperty(origLen);
        this.info = new SimpleStringProperty(info);
    }
}
