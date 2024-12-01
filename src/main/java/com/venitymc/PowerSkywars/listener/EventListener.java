package com.venitymc.PowerSkywars.listener;

import cn.nukkit.Player;
import cn.nukkit.block.BlockChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.utils.TextFormat;
import com.venitymc.PowerSkywars.game.SkywarsGame;
import com.venitymc.PowerSkywars.manager.GameManager;
import com.venitymc.PowerSkywars.session.SessionManager;

import javax.annotation.Nullable;

public class EventListener implements Listener {

    @Nullable
    private SkywarsGame getGame(Player player){
        return SessionManager.get(player).getGame();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        player.setDisplayName(TextFormat.GRAY + player.getName());
        player.setNameTag(TextFormat.GRAY + player.getName());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setCancelled();
        SkywarsGame game = getGame(event.getEntity());
        if (game != null) {
            game.onPlayerDeath(event);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        GameManager.findGame(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        Player player = event.getPlayer();
        SkywarsGame game = getGame(player);

        if (game != null) {
            game.quit(player);
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        event.setCancelled();
        Player player = event.getPlayer();
        SkywarsGame game = getGame(player);

        if (game != null) {
            game.onPlayerChat(player, event.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        SkywarsGame game  = getGame(player);
        if (game == null) {
            return;
        }

        var block = event.getBlock();
        if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && block instanceof BlockChest chest) {
            game.onChestOpen(player, chest);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        SkywarsGame game = getGame(player);
        if (game == null) {
            return;
        }

        if (event.getBlock() instanceof BlockChest chest) {
            game.ignoreChestFill(chest);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            SkywarsGame game = getGame(player);
            if (game != null) {
                event.setAttackCooldown(9);
                game.onPlayerDamaged(event);
            } else {
                event.setCancelled();
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        SkywarsGame game = getGame(player);
        if (game != null) {
            game.onPlayerMoved(event);
        }
    }

}
