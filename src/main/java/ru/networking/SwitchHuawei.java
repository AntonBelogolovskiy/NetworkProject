package ru.networking;

import java.net.InetAddress;
import java.util.Map;

public class SwitchHuawei extends Switch {
    @Override
    String getSwitchMac() {
        return null;
    }

    @Override
    InetAddress getSwitchIPAddress() {
        return null;
    }

    @Override
    String getSwitchConfig() {
        return null;
    }

    @Override
    Map<String, String> getMacAddressTable() {
        return null;
    }

    public SwitchHuawei(InetAddress ipAddress) {
        super(ipAddress);
    }
}
