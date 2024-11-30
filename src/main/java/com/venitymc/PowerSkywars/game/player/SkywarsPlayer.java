package com.venitymc.PowerSkywars.game.player;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.venitymc.PowerSkywars.game.SkywarsCage;
import com.venitymc.PowerSkywars.game.SkywarsGame;
import com.venitymc.PowerSkywars.session.Session;
import com.venitymc.PowerSkywars.session.SessionManager;
import com.venitymc.PowerSkywars.util.Utils;
import lombok.Getter;

public class SkywarsPlayer {

    private final Player player;
    @Getter
    private final SkywarsGame game;
    @Getter
    private final CombatInfo combatInfo = new CombatInfo();
    @Getter
    private SkywarsCage cage;
    private int killCount = 0;

    public SkywarsPlayer(Player player, SkywarsGame game) {
        this.player = player;
        this.game = game;
    }

    public Player getNukkitPlayer() {
        return player;
    }

    private void setSessionGame() {
        Session session = SessionManager.get(player);
        session.setGame(game);
    }

    private void unsetSessionGame() {
        Session session = SessionManager.get(player);
        session.setGame(null);
    }

    public void spawn() {
        Position spawnPos = game.getNextSpawn();
        cage = new SkywarsCage(spawnPos.subtract(0, 1, 0));
        cage.set();
        player.teleport(spawnPos.add(0, 0.2, 0));
        player.setGamemode(Player.ADVENTURE);
        reset();
    }

    public void onStart() {
        player.setGamemode(Player.SURVIVAL);
        cage.unset();
    }

    public void onJoin() {
        setSessionGame();
        spawn();

        String joinMessage = String.format(
                "%s%s%s has joined! (%s%d%s/%s%d%s)",
                TextFormat.GRAY, player.getName(), TextFormat.AQUA,
                TextFormat.WHITE, game.getPlayerCount(), TextFormat.AQUA,
                TextFormat.WHITE, game.getMaxPlayers(), TextFormat.AQUA
        );
        game.broadcastMessage(joinMessage);
    }

    public void onQuit() {
        switch (game.getStatus()) {
            case WAITING:
                String leaveMessage = String.format(
                        "%s%s%s has quit! (%s%d%s/%s%d%s)",
                        TextFormat.GRAY, player.getName(), TextFormat.AQUA,
                        TextFormat.WHITE, game.getPlayerCount() - 1, TextFormat.AQUA,
                        TextFormat.WHITE, game.getMaxPlayers(), TextFormat.AQUA
                );
                game.broadcastMessage(leaveMessage);
                cage.unset();
                break;
            case PLAYING:
            case ENDING:
                if (getCombatInfo().isAlive()) {
                    kill();
                }
                break;
        }

        unsetSessionGame();
    }

    public void kill() {
        if (!combatInfo.isAlive()) {
            return;
        }

        SkywarsPlayer killer = combatInfo.getCombatLog();
        combatInfo.setAlive(false);

        if (killer != null) {
            game.broadcastMessage(String.format("%s %swas killed by %s", player.getDisplayName(), TextFormat.AQUA, killer.getNukkitPlayer().getDisplayName()));
        } else {
            game.broadcastMessage(String.format("%s %sdied.", player.getDisplayName(), TextFormat.AQUA));
        }

        reset();
        player.setGamemode(Player.SPECTATOR);
    }

    public void reset() {
        Utils.resetPlayer(player);
    }

}
