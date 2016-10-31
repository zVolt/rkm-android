package io.github.zkhan93.lanmak.callbacks;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Zeeshan Khan on 10/28/2016.
 */

public interface SocketConnectionClbk {
    void startAttemptToConnect();

    void setSocket(Socket socket) throws IOException;

    void failedToConnect();
}
