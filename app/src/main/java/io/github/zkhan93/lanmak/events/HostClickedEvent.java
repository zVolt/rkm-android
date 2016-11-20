package io.github.zkhan93.lanmak.events;

import io.github.zkhan93.lanmak.models.Host;

/**
 * Created by zeeshan on 11/20/2016.
 */

public class HostClickedEvent {
    private Host host;

    public HostClickedEvent(Host host) {
        this.host = host;
    }

    public Host getHost() {
        return host;
    }
}
