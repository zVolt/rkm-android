package io.github.zkhan93.lanmak.utility;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Zeeshan Khan on 10/28/2016.
 */

public class Util {
    public static Boolean isIPv4Address(String address) {
        if (address.isEmpty()) {
            return false;
        }
        try {
            Object res = InetAddress.getByName(address);
            return res instanceof Inet4Address || res instanceof Inet6Address;
        } catch (final UnknownHostException ex) {
            return false;
        }
    }

    public static boolean isValidPort(String port) {
        if (port == null || port.isEmpty())
            return false;
        try {
            int intPort = Integer.parseInt(port);
            if (intPort < 65536)
                return true;
            else
                return false;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
