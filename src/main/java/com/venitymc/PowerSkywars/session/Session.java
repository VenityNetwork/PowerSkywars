package com.venitymc.PowerSkywars.session;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.venitymc.PowerSkywars.game.SkywarsGame;
import lombok.Getter;
import lombok.Setter;

public class Session {

    @Getter @Setter
    private SkywarsGame game = null;
    private final String player;

    public Session(Player player) {
        this.player = player.getName();
    }

    public Player getPlayer() {
        return Server.getInstance().getPlayerExact(player);
    }

}
