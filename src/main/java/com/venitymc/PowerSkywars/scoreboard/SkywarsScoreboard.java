package com.venitymc.PowerSkywars.scoreboard;

import cn.nukkit.Player;
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SkywarsScoreboard {

    public static void display(Player player, String objectiveName, String displayName, List<String> unsortedLines) {
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

        player.display(scoreboard, DisplaySlot.SIDEBAR);
    }

}
