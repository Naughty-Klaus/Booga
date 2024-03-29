package io.booga.plugin.player;

import java.util.ArrayList;

public class Extension {

    /**
     * Collection of UUIDs in string format.
     * Defines player's friends.
     */
    public ArrayList<String> friends = new ArrayList();

    /**
     * Options: server, tribe
     */
    public String channel = "server";

    public String lastTribeInvite = "";

}
