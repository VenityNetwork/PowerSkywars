package com.venitymc.PowerSkywars.session;

import cn.nukkit.Player;

import java.util.WeakHashMap;

public class SessionManager {

    public static final WeakHashMap<Player, Session> sessions = new WeakHashMap<>();

    public static Session get(Player player) {
        return sessions.computeIfAbsent(player, Session::new);
    }

}
