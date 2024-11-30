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
import cn.nukkit.scoreboard.Scoreboard;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import com.venitymc.PowerSkywars.game.player.SkywarsPlayer;
import com.venitymc.PowerSkywars.map.SkywarsMap;
import com.venitymc.PowerSkywars.scoreboard.SkywarsScoreboard;
import com.venitymc.PowerSkywars.session.Session;
import com.venitymc.PowerSkywars.session.SessionManager;
import com.venitymc.PowerSkywars.util.Utils;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
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
        this.logger = new SkywarsLogger(this);

        id = UUID.randomUUID().toString();
        map = SkywarsMap.getRandom(this);
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

    public void onPlayerDamaged(EntityDamageEvent event) {
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
                event.setCancelled();
                return;
            }

            skywarsPlayer.getCombatInfo().onHitBy(skywarsDamager);
        }

        if (event.getFinalDamage() >= player.getHealth() && skywarsPlayer.getCombatInfo().isAlive()) {
            skywarsPlayer.kill();
        }
    }

    public void onPlayerMoved(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        SkywarsPlayer skywarsPlayer = players.get(player);

        if (event.getTo().getY() <= voidY && skywarsPlayer.getCombatInfo().isAlive()) {
            skywarsPlayer.kill();
        }
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
    }

    private void onWaitingTick(){
        String playStatus;
        if(getPlayerCount() >= 2){
            playStatus = "Starting in " + time + " seconds...";
            time--;
        }else{
            time = getWaitingTime();
            playStatus = "Waiting...";
        }

        for (SkywarsPlayer player : players.values()){
            SkywarsScoreboard.display(player.getNukkitPlayer(), "sw", TextFormat.AQUA + TextFormat.BOLD.toString() + "SKYWARS", List.of(
                "",
                "Map: " + TextFormat.AQUA + getMap().getName(),
                "Players: " + TextFormat.AQUA + getPlayerCount() + "/" + getMaxPlayers(),
                "",
                playStatus,
                "",
                "Mode: " + TextFormat.AQUA + "Solo",
                "",
                TextFormat.AQUA + "play.venitymc.com"
            ));
        }

        if(time <= 0){
            start();
        }
    }

    private void onPlayingTick(){
        time--;

        if(time <= 0){
            // end
        }
    }

    private void onEndingTick(){
        time--;

        if(time <= 0){
            // end
        }
    }

    private void stopTicking(){
        if(tickTaskHandler == null){
            return;
        }
        tickTaskHandler.cancel();
    }

    public void end(){
        if(isEnding()){
            return;
        }

        status = GameStatus.ENDING;
        time = 10;
    }

    public void stop(){
        if(stopped.getAndSet(true)){
            return;
        }

        for(var player : players.keySet()){
            quit(player);
        }

        logger.debug("Stop ticking...");
        stopTicking();

        logger.debug("Closing world...");
        world.close();
    }

    public void ignoreChestFill(BlockChest chest){
        var hash = Level.chunkBlockHash(chest.getFloorX(), chest.getFloorY(), chest.getFloorZ());
        ignoreChestFill.put(hash, true);
    }

    public void onChestOpen(Player player, BlockChest chest) {
        var hash = Level.chunkBlockHash(chest.getFloorX(), chest.getFloorY(), chest.getFloorZ());
        if(ignoreChestFill.containsKey(hash)){
            return;
        }

        var chestInv = chest.getBlockEntity();
        if(chestInv != null){
            // TODO: refill chest
        }

        ignoreChestFill.put(hash, true);
    }
}
