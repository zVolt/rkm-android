package io.github.zkhan93.lanmak.utility;

public interface Constants {
    static int PORT = 5556;
    static String SERVER_IP = "192.168.0.100";
    static String COLON = ":";
    static String ONE = "1";
    static String ZERO = "0";
    static String TWO = "2";
    static String UNDERSCORE = "_";

    String Button11 = "Page Up";

    String Button12 = "Page Down";

    String Button13 = "Context Menu";

    String Button14 = "Escape";

    String Button15 = "Cut";

    String Button16 = "Copy";

    String Button21 = "Tab";

    String Button22 = "Home";

    String Button23 = "End";

    String Button24 = "Start";

    String Button25 = "Paste";

    String Button26 = "Select All";

    interface SERVICE_STATE {
        int CONNECTED = 0;
        int CONNECTING = 1;
        int DISCONNECTED = 2;
    }

    interface STATES {
        int SEARCHING = 0;
        int FINISHED = 1;
    }
}
