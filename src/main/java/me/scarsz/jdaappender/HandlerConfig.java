package me.scarsz.jdaappender;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Configuration for the associated {@link ChannelLoggingHandler}
 */
@SuppressWarnings("unused")
public class HandlerConfig {

    /**
     * Message transformers that will be used to test incoming {@link LogItem}s before they are put in the queue.
     * Predicates should return {@code false} if it is neutral in respect to the LogItem; return {@code true} when the item should be modified/denied.
     * Can be used to block certain LogItems from being forwarded if, for example, it contains an unwanted message.
     */
    @Getter private final Map<Predicate<LogItem>, Function<String, String>> messageTransformers = new LinkedHashMap<>();

    /**
     * Adds a message transformer that will deny messages when the specified {@link Predicate} is {@code true}.
     * @param filter the predicate to filter {@link LogItem}s by
     */
    public void addFilter(Predicate<LogItem> filter) {
        messageTransformers.put(filter, s -> null);
    }

    /**
     * Adds a message transformer that will modify messages when the specified {@link Predicate} is {@code true}.
     * @param filter the predicate to filter {@link LogItem}s by
     */
    public void addTransformer(Predicate<LogItem> filter, Function<String, String> transformer) {
        messageTransformers.put(filter, transformer);
    }

    /**
     * Pads logger names less than the set amount of characters with whitespace.
     * Negative values indicate left-padding, positive values indicate right-padding.
     * Default disabled.
     */
    @Getter @Setter private int loggerNamePadding = 0;

    /**
     * Mappings representing a logger name prefix and associated Functions to transform those logger names.
     * Used to provide a more user-friendly name for a logger, such as translating "net.dv8tion.jda" to "JDA".
     * A logger name mapping may return {@code null} if messages from the logger should be ignored.
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     */
    @Getter private final Map<Predicate<String>, Function<String, String>> loggerMappings = new LinkedHashMap<>();

    /**
     * Simple logger name mapper that, assuming the logger's name is a fully-qualified class name, returns the simple name
     */
    @Getter private static final Function<String, String> friendlyMapper = s -> s.substring(s.lastIndexOf(".") + 1);

    /**
     * See {@link #loggerMappings}. Shortcut for loggerMappings.put(prefix, v -> friendlyName).
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     *
     * <pre>
     * // translate "net.dv8tion.jda*" logger names to simply "JDA"
     * handlerConfig.mapLoggerName("net.dv8tion.jda", "JDA");
     *
     * // translate loggers in a "modules" package of your app to their simple class name + " module"
     * handlerConfig.mapLoggerNameFriendly("your.application.package.modules", name -> name + " module");
     *
     * // translate loggers in your application to the simple class name of the logger
     * handlerConfig.mapLoggerNameFriendly("your.application.package");
     *
     * // translate "Module" loggers in your application to the module's simple class name
     * handlerConfig.mapLoggerTypeFriendly(Module.class);
     * </pre>
     *
     * @param predicate the logger predicate to match
     * @param function a function that returns the name to replace the logger name with
     */
    public void mapLogger(Predicate<String> predicate, Function<String, String> function) {
        loggerMappings.put(predicate, function);
    }
    /**
     * See {@link #loggerMappings}. Shortcut for checking if logger names are assignable from the given superclass, assuming FQCNs.
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     *
     * <pre>
     * // translate "net.dv8tion.jda*" logger names to simply "JDA"
     * handlerConfig.mapLoggerName("net.dv8tion.jda", "JDA");
     *
     * // translate loggers in a "modules" package of your app to their simple class name + " module"
     * handlerConfig.mapLoggerNameFriendly("your.application.package.modules", name -> name + " module");
     *
     * // translate loggers in your application to the simple class name of the logger
     * handlerConfig.mapLoggerNameFriendly("your.application.package");
     *
     * // translate "Module" loggers in your application to the module's simple class name
     * handlerConfig.mapLoggerTypeFriendly(Module.class);
     * </pre>
     *
     * @param superclass the superclass to match
     * @param function a function that returns the name to replace the logger name with
     */
    public void mapLoggerType(Class<?> superclass, Function<String, String> function) {
        loggerMappings.put(s -> {
            try {
                return superclass.isAssignableFrom(Class.forName(s));
            } catch (Throwable ignored) {
                return false;
            }
        }, function);
    }
    /**
     * See {@link #loggerMappings}. Shortcut for checking if logger names are assignable from the given superclass, assuming FQCNs.
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     *
     * <pre>
     * // translate "net.dv8tion.jda*" logger names to simply "JDA"
     * handlerConfig.mapLoggerName("net.dv8tion.jda", "JDA");
     *
     * // translate loggers in a "modules" package of your app to their simple class name + " module"
     * handlerConfig.mapLoggerNameFriendly("your.application.package.modules", name -> name + " module");
     *
     * // translate loggers in your application to the simple class name of the logger
     * handlerConfig.mapLoggerNameFriendly("your.application.package");
     *
     * // translate "Module" loggers in your application to the module's simple class name
     * handlerConfig.mapLoggerTypeFriendly(Module.class);
     * </pre>
     *
     * @param superclass the superclass to match
     */
    public void mapLoggerTypeFriendly(Class<?> superclass) {
        mapLoggerType(superclass, friendlyMapper);
    }
    /**
     * See {@link #loggerMappings}. Shortcut for loggerMappings.put(prefix, v -> friendlyName).
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     *
     * <pre>
     * // translate "net.dv8tion.jda*" logger names to simply "JDA"
     * handlerConfig.mapLoggerName("net.dv8tion.jda", "JDA");
     *
     * // translate loggers in a "modules" package of your app to their simple class name + " module"
     * handlerConfig.mapLoggerNameFriendly("your.application.package.modules", name -> name + " module");
     *
     * // translate loggers in your application to the simple class name of the logger
     * handlerConfig.mapLoggerNameFriendly("your.application.package");
     *
     * // translate "Module" loggers in your application to the module's simple class name
     * handlerConfig.mapLoggerTypeFriendly(Module.class);
     * </pre>
     * @param prefix the logger name to match
     * @param friendlyName the friendly name to replace the logger name with
     */
    public void mapLoggerName(String prefix, String friendlyName) {
        loggerMappings.put(s -> s.startsWith(prefix), s -> friendlyName);
    }
    /**
     * See {@link #loggerMappings}. Shortcut for loggerMappings.put(prefix, function).
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     *
     * <pre>
     * // translate "net.dv8tion.jda*" logger names to simply "JDA"
     * handlerConfig.mapLoggerName("net.dv8tion.jda", "JDA");
     *
     * // translate loggers in a "modules" package of your app to their simple class name + " module"
     * handlerConfig.mapLoggerNameFriendly("your.application.package.modules", name -> name + " module");
     *
     * // translate loggers in your application to the simple class name of the logger
     * handlerConfig.mapLoggerNameFriendly("your.application.package");
     *
     * // translate "Module" loggers in your application to the module's simple class name
     * handlerConfig.mapLoggerTypeFriendly(Module.class);
     * </pre>
     * @param prefix the logger name to match
     * @param function the mapping function
     */
    public void mapLoggerName(String prefix, Function<String, String> function) {
        loggerMappings.put(s -> s.startsWith(prefix), function);
    }
    /**
     * See {@link #loggerMappings}. Shortcut for loggerMappings.put(class prefix, class -> class simple name).
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     *
     * <pre>
     * // translate "net.dv8tion.jda*" logger names to simply "JDA"
     * handlerConfig.mapLoggerName("net.dv8tion.jda", "JDA");
     *
     * // translate loggers in a "modules" package of your app to their simple class name + " module"
     * handlerConfig.mapLoggerNameFriendly("your.application.package.modules", name -> name + " module");
     *
     * // translate loggers in your application to the simple class name of the logger
     * handlerConfig.mapLoggerNameFriendly("your.application.package");
     *
     * // translate "Module" loggers in your application to the module's simple class name
     * handlerConfig.mapLoggerTypeFriendly(Module.class);
     * </pre>
     * @param prefix the logger name to match
     */
    public void mapLoggerNameFriendly(String prefix) {
        loggerMappings.put(s -> s.startsWith(prefix), friendlyMapper);
    }
    /**
     * See {@link #loggerMappings}. Shortcut for loggerMappings.put(class prefix, class -> function(class simple name)).
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     *
     * <pre>
     * // translate "net.dv8tion.jda*" logger names to simply "JDA"
     * handlerConfig.mapLoggerName("net.dv8tion.jda", "JDA");
     *
     * // translate loggers in a "modules" package of your app to their simple class name + " module"
     * handlerConfig.mapLoggerNameFriendly("your.application.package.modules", name -> name + " module");
     *
     * // translate loggers in your application to the simple class name of the logger
     * handlerConfig.mapLoggerNameFriendly("your.application.package");
     *
     * // translate "Module" loggers in your application to the module's simple class name
     * handlerConfig.mapLoggerTypeFriendly(Module.class);
     * </pre>
     * @param prefix the logger name to match
     * @param function the function to modify the determined friendly name
     */
    public void mapLoggerNameFriendly(String prefix, Function<String, String> function) {
        loggerMappings.put(s -> s.startsWith(prefix), s -> function.apply(friendlyMapper.apply(s)));
    }
    /**
     * See {@link #loggerMappings}. Ignores messages from the specified logger prefix. Shortcut for loggerMappings.put(prefix, v -> null).
     * <strong>Logger mappings are implemented in the default logging prefix! Changing the prefixer will require reimplementation of logger mappings!</strong>
     * @param prefix the logger name prefix to ignore
     */
    public void ignoreLoggerName(String prefix) {
        loggerMappings.put(s -> s.startsWith(prefix), s -> null);
    }

    /**
     * Function to include any relevant details as a prefix to a {@link LogItem}'s content when formatting.
     * Default equates to "[LEVEL Logger] ".
     */
    @Getter @Setter @Nullable private Function<LogItem, String> prefixer = item -> {
        String name = padLoggerName(resolveLoggerName(item.getLogger()));
        return "[" + padLevelName(item.getLevel().name()) + (name != null && !name.isEmpty() ? " " + name : "") + "] ";
    };

    /**
     * Resolve the given logger name with any configured logger name mappings
     * @param name the logger name to resolve mappings for
     * @return if the logger name has been mapped to blank/null: null.
     * Otherwise, the resolved logger name if mapped, else same as input
     */
    public @Nullable String resolveLoggerName(@NotNull String name) {
        for (Map.Entry<Predicate<String>, Function<String, String>> entry : loggerMappings.entrySet()) {
            if (entry.getKey().test(name)) {
                return entry.getValue().apply(name);
            }
        }
        return name;
    }

    /**
     * Function to include any relevant details as a suffix to a {@link LogItem}'s content when formatting.
     * Default null.
     */
    @Getter @Setter @Nullable private Function<LogItem, String> suffixer;

    /**
     * Log levels that will be processed
     */
    @Getter @Setter private EnumSet<LogLevel> logLevels = EnumSet.complementOf(EnumSet.of(LogLevel.DEBUG));

    /**
     * Whether the logging handler should format log items which contain a URL to be outside the output code blocks.
     * This is useful for if you want links to be clickable or not in the Discord client.
     * Has the tradeoff that the log item will have no coloring/monospace font.
     * Default false.
     */
    @Getter @Setter private boolean splitCodeBlockForLinks = false;

    /**
     * Whether the logging handler should allow Discord to show embeds for links when {@link #splitCodeBlockForLinks} is enabled.
     * Default true.
     */
    @Getter @Setter private boolean allowLinkEmbeds = true;

    /**
     * Whether the logging handler should format log items with code syntax to highlight log levels in distinct colors.
     * Default true.
     */
    @Getter @Setter private boolean colored = true;

    /**
     * Whether the logging handler should truncate {@link LogItem}s with a formatted length longer than {@link LogItem#CLIPPING_MAX_LENGTH}.
     * Default true.
     */
    @Getter @Setter private boolean truncateLongItems = true;





    /**
     * Check how many characters that prefix/suffix formatting takes up for the given LogItem
     * @param logItem the log item to apply prefixes and suffixes for
     * @return length of prefixes and suffixes
     */
    int getFormattingLength(LogItem logItem) {
        int length = 0;
        if (prefixer != null) length += prefixer.apply(logItem).length();
        if (suffixer != null) length += suffixer.apply(logItem).length();
        return length;
    }

    /**
     * Utility method to either left or right-pad the given string
     * @param string the string to pad
     * @param to how many characters the string should equal after padding
     * @return the padded string
     */
    public String pad(String string, int to) {
        if (to == 0) return string;
        if (string == null || string.isEmpty() || string.length() >= Math.abs(to)) return string;

        StringBuilder builder = new StringBuilder();
        if (to > 0) {
            builder.append(string);
            while (builder.length() < Math.abs(to)) builder.append(' ');
        } else {
            while (builder.length() < Math.abs(to) - string.length()) builder.append(' ');
            builder.append(string);
        }
        return builder.toString();
    }
    /**
     * See {@link #pad(String, int)}. Shortcut for padding logger names.
     */
    public String padLoggerName(String loggerName) {
        return pad(loggerName, loggerNamePadding);
    }
    /**
     * See {@link #pad(String, int)}. Shortcut for padding log level names.
     */
    public String padLevelName(String levelName) {
        return pad(levelName, LogLevel.MAX_NAME_LENGTH);
    }

}
