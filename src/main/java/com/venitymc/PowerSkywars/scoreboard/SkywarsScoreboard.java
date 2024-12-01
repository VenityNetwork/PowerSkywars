package com.venitymc.PowerSkywars.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.RemoveObjectivePacket;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.utils.TextFormat;
import com.venitymc.PowerSkywars.PowerSkywars;
import com.venitymc.PowerSkywars.game.GameStatus;
import com.venitymc.PowerSkywars.game.SkywarsGame;
import com.venitymc.PowerSkywars.game.player.SkywarsPlayer;
import com.venitymc.PowerSkywars.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SkywarsScoreboard {

    private final SkywarsPlayer player;
    private Scoreboard current;

    public SkywarsScoreboard(SkywarsPlayer player) {
        this.player = player;
    }

    public void display(String objectiveName, String displayName, List<String> unsortedLines) {
        remove();
        Scoreboard scoreboard = new Scoreboard(objectiveName, displayName, "dummy", SortOrder.DESCENDING);
        int emptyLines = 0;

        for (int i = unsortedLines.size() - 1; i >= 0; i--) {
            String line = unsortedLines.get(i);
            if (line.isEmpty()) {
                line = " ".repeat(emptyLines);
                emptyLines++;
            }
            scoreboard.addLine(line, unsortedLines.size() - i);
        }

        player.getNukkitPlayer().display(scoreboard, DisplaySlot.SIDEBAR);
        current = scoreboard;
    }

    public void remove() {
        if (current != null) {
            RemoveObjectivePacket pk = new RemoveObjectivePacket();
            pk.objectiveName = current.getObjectiveName();
            player.getNukkitPlayer().dataPacket(pk);
        }
    }

    public void update() {
        display("sw", TextFormat.AQUA + TextFormat.BOLD.toString() + "SKYWARS", getLines());
    }

    public List<String> getLines() {
        SkywarsGame game = player.getGame();
        switch (game.getStatus()) {
            case WAITING:
                String playStatus;
                if (game.getPlayerCount() >= 2){
                    playStatus = "Starting in " + TextFormat.AQUA + game.getTime() + "s";
                } else {
                    playStatus = "Waiting...";
                }

                return List.of(
                    "",
                    "Players: " + TextFormat.AQUA + game.getPlayerCount() + "/" + game.getMaxPlayers(),
                    "",
                    playStatus,
                    "",
                    "Map: " + TextFormat.AQUA + game.getMap().getName(),
                    "Mode: " + TextFormat.AQUA + "Solo",
                    "",
                    PowerSkywars.SCOREBOARD_FOOTER
                );
            case PLAYING:
                return List.of(
                        "",
                        "Time left: " + TextFormat.AQUA + Utils.formatSecondsToMinutesAndSeconds(game.getTime()),
                        "",
                        "Players left: " + TextFormat.AQUA + game.getPlayersAlive().size(),
                        "",
                        "Kills: " + TextFormat.AQUA + player.getKills(),
                        "",
                        "Map: " + TextFormat.AQUA + game.getMap().getName(),
                        "Mode: " + TextFormat.AQUA + "Solo",
                        "",
                        PowerSkywars.SCOREBOARD_FOOTER
                );
            case ENDING:
                return List.of(
                        "",
                        "Closing in: " + TextFormat.AQUA + Utils.formatSecondsToMinutesAndSeconds(game.getTime()),
                        "",
                        "Players left: " + TextFormat.AQUA + game.getPlayersAlive().size(),
                        "",
                        "Kills: " + TextFormat.AQUA + player.getKills(),
                        "",
                        "Map: " + TextFormat.AQUA + game.getMap().getName(),
                        "Mode: " + TextFormat.AQUA + "Solo",
                        "",
                        PowerSkywars.SCOREBOARD_FOOTER
                );
        }
        return List.of("");
    }

}
