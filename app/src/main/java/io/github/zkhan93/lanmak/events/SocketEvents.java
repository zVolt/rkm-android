package io.github.zkhan93.lanmak.events;

/**
 * Created by Zeeshan Khan on 10/31/2016.
 */

public class SocketEvents {
    public SocketEvents(int socketState) {
        this.socketState = socketState;
    }

    private int socketState;

    public int getSocketState() {
        return socketState;
    }

    public void setSocketState(int socketState) {
        this.socketState = socketState;
    }
}
