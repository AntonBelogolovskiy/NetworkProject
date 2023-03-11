package ru.networking;

import java.net.InetAddress;
import java.util.Map;

public abstract class Switch {
    private InetAddress ipAddress;
    private String macAddress;
    private String prompt;

    abstract String getSwitchMac();

    abstract InetAddress getSwitchIPAddress();

    abstract String getSwitchConfig();

    abstract Map<String, String> getMacAddressTable();

    public String getDescription() {
        return null;
    }

    public Switch(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

}
