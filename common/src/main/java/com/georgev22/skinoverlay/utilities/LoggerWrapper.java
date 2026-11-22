package com.georgev22.skinoverlay.utilities;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * LoggerWrapper is a wrapper class used to integrate java.util.Logger and log4j/slf4j loggers.
 * It takes logger as an input and logs the records based on their levels.
 */
public class LoggerWrapper extends Logger {
    private final org.apache.logging.log4j.Logger log4j;
    private final org.slf4j.Logger slf4j;

    /**
     * Constructor to create a LoggerWrapper wrapping the given Log4j Logger object.
     *
     * @param logger A Log4j Logger object.
     */
    public LoggerWrapper(org.apache.logging.log4j.Logger logger) {
        super("logger", null);
        this.log4j = logger;
        this.slf4j = null;
    }

    /**
     * Constructor to create a LoggerWrapper wrapping the given SLF4J Logger object.
     *
     * @param logger An SLF4J Logger object.
     */
    public LoggerWrapper(org.slf4j.Logger logger) {
        super("logger", null);
        this.log4j = null;
        this.slf4j = logger;
    }

    /**
     * Logs a message at the given LogRecord level.
     *
     * @param record A LogRecord object containing the log level and message.
     */
    @Override
    public void log(@NotNull LogRecord record) {
        log(record.getLevel(), record.getMessage());
    }

    /**
     * Logs a message at the specified log level.
     *
     * @param level The log level.
     * @param msg   The message to be logged.
     */
    @Override
    public void log(Level level, String msg) {
        if (level == Level.FINE) {
            if (log4j != null) {
                log4j.debug(msg);
            } else if (slf4j != null) {
                slf4j.debug(msg);
            }
        } else if (level == Level.WARNING) {
            if (log4j != null) {
                log4j.warn(msg);
            } else if (slf4j != null) {
                slf4j.warn(msg);
            }
        } else if (level == Level.SEVERE) {
            if (log4j != null) {
                log4j.error(msg);
            } else if (slf4j != null) {
                slf4j.error(msg);
            }
        } else if (level == Level.INFO) {
            if (log4j != null) {
                log4j.info(msg);
            } else if (slf4j != null) {
                slf4j.info(msg);
            }
        } else {
            if (log4j != null) {
                log4j.trace(msg);
            } else if (slf4j != null) {
                slf4j.trace(msg);
            }
        }
    }

    /**
     * Logs a message with a single parameter at the specified log level.
     *
     * @param level  The log level.
     * @param msg    The message to be logged.
     * @param param1 The single parameter.
     */
    @Override
    public void log(Level level, String msg, Object param1) {
        if (level == Level.FINE) {
            if (log4j != null) {
                log4j.debug(msg, param1);
            } else if (slf4j != null) {
                slf4j.debug(msg, param1);
            }
        } else if (level == Level.WARNING) {
            if (log4j != null) {
                log4j.warn(msg, param1);
            } else if (slf4j != null) {
                slf4j.warn(msg, param1);
            }
        } else if (level == Level.SEVERE) {
            if (log4j != null) {
                log4j.error(msg, param1);
            } else if (slf4j != null) {
                slf4j.error(msg, param1);
            }
        } else if (level == Level.INFO) {
            if (log4j != null) {
                log4j.info(msg, param1);
            } else if (slf4j != null) {
                slf4j.info(msg, param1);
            }
        } else {
            if (log4j != null) {
                log4j.trace(msg, param1);
            } else if (slf4j != null) {
                slf4j.trace(msg, param1);
            }
        }
    }

    /**
     * Logs a message with multiple parameters at the specified log level.
     *
     * @param level  The log level.
     * @param msg    The message to be logged.
     * @param params An array of parameters.
     */
    @Override
    public void log(Level level, String msg, Object[] params) {
        log(level, MessageFormat.format(msg, params)); // workaround not formatting correctly
    }

    /**
     * Logs a message with a Throwable at the specified log level.
     *
     * @param level  The log level.
     * @param msg    The message to be logged.
     * @param params The Throwable associated with the message.
     */
    @Override
    public void log(Level level, String msg, Throwable params) {
        if (level == Level.FINE) {
            if (log4j != null) {
                log4j.debug(msg, params);
            } else if (slf4j != null) {
                slf4j.debug(msg, params);
            }
        } else if (level == Level.WARNING) {
            if (log4j != null) {
                log4j.warn(msg, params);
            } else if (slf4j != null) {
                slf4j.warn(msg, params);
            }
        } else if (level == Level.SEVERE) {
            if (log4j != null) {
                log4j.error(msg, params);
            } else if (slf4j != null) {
                slf4j.error(msg, params);
            }
        } else if (level == Level.INFO) {
            if (log4j != null) {
                log4j.info(msg, params);
            } else if (slf4j != null) {
                slf4j.info(msg, params);
            }
        } else {
            if (log4j != null) {
                log4j.trace(msg, params);
            } else if (slf4j != null) {
                slf4j.trace(msg, params);
            }
        }
    }

}