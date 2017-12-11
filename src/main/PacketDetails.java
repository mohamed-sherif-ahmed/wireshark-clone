package main;

import java.util.Date;

public class PacketDetails {
    Date date;
    String sourceIP;
    String destIP;
    int origLen;
    String info;

    public Date getDate() {
        return date;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public String getDestIP() {
        return destIP;
    }

    public int getOrigLen() {
        return origLen;
    }

    public String getInfo() {
        return info;
    }
    public PacketDetails(Date date, String sourceIP, String destIP, int origLen, String info) {
        this.date = date;
        this.sourceIP= sourceIP;
        this.destIP = destIP;
        this.origLen = origLen;
        this.info = info;
    }

}
