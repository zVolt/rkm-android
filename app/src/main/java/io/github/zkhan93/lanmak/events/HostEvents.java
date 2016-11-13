package io.github.zkhan93.lanmak.events;

import io.github.zkhan93.lanmak.models.Host;

/**
 * Created by Zeeshan Khan on 11/8/2016.
 */

public class HostEvents {
    private Host host;

    public HostEvents(Host host) {
        this.host = host;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
}
