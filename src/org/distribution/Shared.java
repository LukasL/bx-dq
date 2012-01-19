package org.distribution;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.basex.client.api.BaseXClient;

public enum Shared {

    INSTANCE;

    /** Clients map. */
    private ConcurrentMap<String, BaseXClient> clients;

    Shared() {
        clients = new ConcurrentHashMap<String, BaseXClient>();
    }

    public void put(final String url, final BaseXClient c) {
        clients.put(url, c);
    }

    public BaseXClient get(final String url) {
        return clients.get(url);
    }

    public boolean contains(final String url) {
        return clients.containsKey(url);
    }
}
