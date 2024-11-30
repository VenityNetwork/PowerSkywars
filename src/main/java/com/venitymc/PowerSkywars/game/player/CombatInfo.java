package com.venitymc.PowerSkywars.game.player;

import cn.nukkit.Server;
import lombok.Getter;
import lombok.Setter;

public class CombatInfo {

    @Setter @Getter
    private boolean isAlive = true;
    private SkywarsPlayer lastHitBy;
    private int tickAtLastHitBy;

    /**
     * Time range in tick
     */
    private final int timeRange = 100;

    public SkywarsPlayer getCombatLog() {
        if (lastHitBy == null) {
            return null;
        }

        int currentTick = Server.getInstance().getTick();
        if (currentTick <= tickAtLastHitBy + timeRange) {
            return lastHitBy;
        }

        return null;
    }

    public void onHitBy(SkywarsPlayer player) {
        lastHitBy = player;
        tickAtLastHitBy = Server.getInstance().getTick();
    }

}
