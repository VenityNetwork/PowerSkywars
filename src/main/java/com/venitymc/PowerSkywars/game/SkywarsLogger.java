package com.venitymc.PowerSkywars.game;

import cn.nukkit.utils.LogLevel;
import cn.nukkit.utils.Logger;
import com.venitymc.PowerSkywars.PowerSkywars;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class SkywarsLogger implements Logger {

    private final String gameId;
    private final org.apache.logging.log4j.Logger log;

    public SkywarsLogger(SkywarsGame game) {
        log = LogManager.getLogger(PowerSkywars.getInstance().getDescription().getMain());
        gameId = game.getId();
    }

    @Override
    public void emergency(String message) {
        this.log(LogLevel.EMERGENCY, message);
    }

    @Override
    public void alert(String message) {
        this.log(LogLevel.ALERT, message);
    }

    @Override
    public void critical(String message) {
        this.log(LogLevel.CRITICAL, message);
    }

    @Override
    public void error(String message) {
        this.log(LogLevel.ERROR, message);
    }

    @Override
    public void warning(String message) {
        this.log(LogLevel.WARNING, message);
    }

    @Override
    public void notice(String message) {
        this.log(LogLevel.NOTICE, message);
    }

    @Override
    public void info(String message) {
        this.log(LogLevel.INFO, message);
    }

    @Override
    public void debug(String message) {
        this.log(LogLevel.DEBUG, message);
    }

    @Override
    public void emergency(String message, Throwable t) {
        this.log(LogLevel.EMERGENCY, message, t);
    }

    @Override
    public void alert(String message, Throwable t) {
        this.log(LogLevel.ALERT, message, t);
    }

    @Override
    public void critical(String message, Throwable t) {
        this.log(LogLevel.CRITICAL, message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        this.log(LogLevel.ERROR, message, t);
    }

    @Override
    public void warning(String message, Throwable t) {
        this.log(LogLevel.WARNING, message, t);
    }

    @Override
    public void notice(String message, Throwable t) {
        this.log(LogLevel.NOTICE, message, t);
    }

    @Override
    public void info(String message, Throwable t) {
        this.log(LogLevel.INFO, message, t);
    }

    @Override
    public void debug(String message, Throwable t) {
        this.log(LogLevel.DEBUG, message, t);
    }

    @Override
    public void log(LogLevel level, String message, Throwable t) {
        log.log(toApacheLevel(level), "[{}]: {}", this.gameId, message, t);
    }

    @Override
    public void log(LogLevel level, String message) {
        log.log(toApacheLevel(level), "[{}]: {}", this.gameId, message);
    }


    private Level toApacheLevel(LogLevel level) {
        return switch (level) {
            case NONE -> Level.OFF;
            case EMERGENCY, CRITICAL -> Level.FATAL;
            case ALERT, WARNING, NOTICE -> Level.WARN;
            case ERROR -> Level.ERROR;
            case DEBUG -> Level.DEBUG;
            default -> Level.INFO;
        };
    }


}
