package com.venitymc.PowerSkywars.game;

import cn.nukkit.Player;
import cn.nukkit.block.BlockChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import com.venitymc.PowerSkywars.game.player.SkywarsPlayer;
import com.venitymc.PowerSkywars.manager.GameManager;
import com.venitymc.PowerSkywars.map.SkywarsMap;
import com.venitymc.PowerSkywars.session.Session;
import com.venitymc.PowerSkywars.session.SessionManager;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SkywarsGame {

    private Object2ObjectOpenHashMap<Player, SkywarsPlayer> players = new Object2ObjectOpenHashMap<>();
    @Getter
    private final String id;
    @Getter
    private SkywarsMap map;
    @Getter
    private Level world;
    @Getter
    private GameStatus status = GameStatus.WAITING;
    @Getter
    private final int maxPlayers = 12;
    private TaskHandler tickTaskHandler;
    @Getter
    private Logger logger;
    @Getter
    private int time = 30;
    private AtomicBoolean stopped = new AtomicBoolean(false);
    private Long2BooleanOpenHashMap ignoreChestFill = new Long2BooleanOpenHashMap();
    private int voidY = -20;

    public SkywarsGame() {
        id = UUID.randomUUID().toString();
        map = SkywarsMap.getRandom(this);
        logger = new SkywarsLogger(this);
        init();
    }

    private void init() {
        logger.debug("Loading world...");
        world = map.initWorld();
        world.setTime(Level.TIME_DAY);
        world.stopTime();

        logger.debug("Starting game tick scheduler...");
        tickTaskHandler = world.getScheduler().scheduleRepeatingTask(this::onTick, 20);
    }

    public boolean join(Player nukkitPlayer) {
        if (status != GameStatus.WAITING) {
            return false;
        }

        if (players.size() == maxPlayers) {
            return false;
        }

        if (players.get(nukkitPlayer) != null) {
            return false;
        }

        Session session = SessionManager.get(nukkitPlayer);
        SkywarsGame current = session.getGame();
        if (current != null) {
            current.quit(nukkitPlayer);
        }

        SkywarsPlayer skywarsPlayer = new SkywarsPlayer(nukkitPlayer, this);
        players.put(nukkitPlayer, skywarsPlayer);
        skywarsPlayer.onJoin();

        return true;
    }

    public void quit(Player nukkitPlayer) {
        SkywarsPlayer skywarsPlayer = players.get(nukkitPlayer);

        if (skywarsPlayer == null) {
            return;
        }

        skywarsPlayer.onQuit();
        players.remove(nukkitPlayer);
    }

    public Position getNextSpawn() {
        List<Integer> spawn = map.getSpawns().get(getPlayerCount() - 1);
        return Position.fromObject(new Vector3(spawn.get(0) + 0.5, spawn.get(1), spawn.get(2) + 0.5), getWorld());
    }

    public void broadcastMessage(String message) {
        for (Player player : players.keySet()) {
            player.sendMessage(message);
        }
    }

    public void broadcastTitle(String title) {
        for (Player player : players.keySet()) {
            player.sendTitle(title);
        }
    }

    public void broadcastTitle(String title, String subtitle) {
        for (Player player : players.keySet()) {
            player.sendTitle(title, subtitle);
        }
    }

    public ArrayList<SkywarsPlayer> getPlayersAlive() {
        ArrayList<SkywarsPlayer> playersAlive = new ArrayList<>();
        for (SkywarsPlayer player : players.values()) {
            if (player.getCombatInfo().isAlive()) {
                playersAlive.add(player);
            }
        }

        return playersAlive;
    }

    public ArrayList<SkywarsPlayer> getPlayersDead() {
        ArrayList<SkywarsPlayer> playersDead = new ArrayList<>();
        for (SkywarsPlayer player : players.values()) {
            if (!player.getCombatInfo().isAlive()) {
                playersDead.add(player);
            }
        }

        return playersDead;
    }

    public void onPlayerDamaged(EntityDamageEvent event) {
        if(!isPlaying()){
            event.setCancelled();
            return;
        }

        if(getPlayingTime() - time <= 10 && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            event.setCancelled();
            return;
        }

        Player player = (Player) event.getEntity();

        SkywarsPlayer skywarsPlayer = players.get(player);

        if (event instanceof EntityDamageByEntityEvent) {
            Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
            if (!(entity instanceof Player)) {
                event.setCancelled();
                return;
            }

            SkywarsPlayer skywarsDamager = players.get((Player) ((EntityDamageByEntityEvent) event).getDamager());
            if (skywarsDamager == null) {
                getLogger().info(players.toString());
                event.setCancelled();
                return;
            }

            skywarsPlayer.getCombatInfo().onHitBy(skywarsDamager);
        }

        if (event.getFinalDamage() >= player.getHealth()) {
            event.setCancelled();
            if(skywarsPlayer.getCombatInfo().isAlive()) {
                skywarsPlayer.kill();
            }
        }
    }

    public void onPlayerMoved(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        SkywarsPlayer skywarsPlayer = players.get(player);

        if (event.getTo().getY() <= voidY && skywarsPlayer.getCombatInfo().isAlive()) {
            skywarsPlayer.kill();
        }
    }

    public void onPlayerChat(Player player, String message) {
        String formattedMessage = String.format(
                "%s: %s%s",
                player.getDisplayName(), TextFormat.WHITE, message
        );
        broadcastMessage(formattedMessage);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isPlaying(){
        return status == GameStatus.PLAYING;
    }

    public boolean isWaiting(){
        return status == GameStatus.WAITING;
    }

    public boolean isEnding(){
        return status == GameStatus.ENDING;
    }

    public int getWaitingTime() {
        return 30;
    }

    public int getPlayingTime() {
        return 900;
    }

    public boolean start() {
        if (!isWaiting()) {
            return false;
        }

        status = GameStatus.PLAYING;
        time = getPlayingTime();

        for (SkywarsPlayer player : players.values()) {
            player.onStart();
        }

        return true;
    }

    private void onTick(){
        switch (status) {
            case WAITING:
                onWaitingTick();
                break;
            case PLAYING:
                onPlayingTick();
                break;
            case ENDING:
                onEndingTick();
                break;
        }
        updateScoreboards();
    }

    private void updateScoreboards() {
        for (SkywarsPlayer player : players.values()) {
            player.getScoreboard().update();
        }
    }

    private void onWaitingTick(){
        if (getPlayerCount() >= 2){
            time--;
        } else {
            time = getWaitingTime();
        }

        if (time <= 0) {
            start();
        }
    }

    private void onPlayingTick() {
        time--;

        if (time <= 0) {
            draw();
        }
    }

    private void onEndingTick() {
        time--;

        if (time <= 0) {
            stop();
            return;
        }

        switch (time) {
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                broadcastMessage(TextFormat.MINECOIN_GOLD + "Closing in " + TextFormat.RESET + time + "s");
                break;
        }
    }

    private void stopTicking() {
        if (tickTaskHandler == null){
            return;
        }
        tickTaskHandler.cancel();
    }

    public void checkAlive() {
        var playersAlive = getPlayersAlive();
        if (playersAlive.size() == 1) {
            win(playersAlive.getFirst());
        }
    }

    private void win(SkywarsPlayer player) {
        broadcastMessage(String.format(
                "%s %shas won the game!",
                player.getNukkitPlayer().getDisplayName(), TextFormat.GREEN
        ));
        player.getNukkitPlayer().sendTitle(TextFormat.BOLD.toString() + TextFormat.GOLD + "VICTORY!");
        end();
    }

    private void draw() {
        broadcastMessage(TextFormat.GREEN + "Game has ended, no one won.");
        broadcastTitle(TextFormat.BOLD.toString() + TextFormat.RED + "DEFEAT!");
        end();
    }

    public void end() {
        if (isEnding()) {
            return;
        }

        status = GameStatus.ENDING;
        time = 10;
    }

    public void stop() {
        if (stopped.getAndSet(true)) {
            return;
        }

        for (var player : players.keySet()) {
            quit(player);
            GameManager.findGame(player);
        }

        logger.debug("Stop ticking...");
        stopTicking();

        logger.debug("Closing world...");
        world.unload(true);
    }

    public void ignoreChestFill(BlockChest chest){
        var hash = Level.chunkBlockHash(chest.getFloorX(), chest.getFloorY(), chest.getFloorZ());
        ignoreChestFill.put(hash, true);
    }

    public void onChestOpen(Player player, BlockChest chest) {
        fillChestIfNotIgnored(chest);
    }

    public void onChestBreak(Player player, BlockChest chest) {
        fillChestIfNotIgnored(chest);
    }

    private void fillChestIfNotIgnored(BlockChest chest){
        var hash = Level.chunkBlockHash(chest.getFloorX(), chest.getFloorY(), chest.getFloorZ());
        if(ignoreChestFill.containsKey(hash)){
            return;
        }

        var chestBlockEntity = chest.getBlockEntity();
        if(chestBlockEntity != null){
            ChestFill.randomFill(chestBlockEntity.getRealInventory());
        }

        ignoreChestFill.put(hash, true);
    }
}
