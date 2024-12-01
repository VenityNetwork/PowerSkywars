package com.venitymc.PowerSkywars.manager;

import cn.nukkit.Player;
import com.venitymc.PowerSkywars.game.SkywarsGame;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.val;

public class GameManager {

    private static final Object2ObjectOpenHashMap<String, SkywarsGame> games = new Object2ObjectOpenHashMap<>();

    public static void findGame(Player player) {
        synchronized (games) {
            // find a game available first
            for (SkywarsGame game : games.values()) {
                if (game.join(player)) {
                    return;
                }
            }

            // if no game is available, create a new one and join that instead
            SkywarsGame newGame = newGame();
            newGame.join(player);
        }
    }

    public static SkywarsGame newGame() {
        SkywarsGame game = new SkywarsGame();
        games.put(game.getId(), game);
        return game;
    }

    public static void disposeGame(SkywarsGame game) {
        synchronized (games){
            games.remove(game.getId());
        }
    }

}
