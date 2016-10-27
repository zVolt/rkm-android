package io.github.zkhan93.lanmak;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Zeeshan Khan on 10/28/2016.
 */

public interface OutputStreamHandler {
    void setSocket(Socket socket) throws IOException;

    void send(String command);

    void close();
}
