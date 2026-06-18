package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.datastructures.maps.HashObjectMap;
import com.georgev22.skinoverlay.utilities.color.Color;
import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.georgev22.skinoverlay.utilities.Utils.hasClass;

public class BukkitMinecraftUtils {

    private static boolean join = false;
    private static String disableJoinMessage = "";

    public static boolean isList(final @NotNull FileConfiguration file, final String path) {
        return Utils.isList(file.get(path));
    }

    public static void broadcastMsg(final String input) {
        //noinspection deprecation
        Bukkit.broadcastMessage(colorize(input));
    }

    public static void printMsg(final String input) {
        Bukkit.getConsoleSender().sendMessage(colorize(input));
    }

    public static void broadcastMsg(final @NotNull List<String> input) {
        input.forEach(BukkitMinecraftUtils::broadcastMsg);
    }

    public static void broadcastMsg(final @NotNull String... input) {
        Arrays.stream(input).forEach(BukkitMinecraftUtils::broadcastMsg);
    }

    public static void broadcastMsg(final Object input) {
        broadcastMsg(String.valueOf(input));
    }

    public static void printMsg(final @NotNull List<String> input) {
        input.forEach(BukkitMinecraftUtils::printMsg);
    }

    public static void printMsg(final @NotNull String... input) {
        Arrays.stream(input).forEach(BukkitMinecraftUtils::printMsg);
    }

    public static void printMsg(final Object input) {
        printMsg(String.valueOf(input));
    }

    public static void msg(final CommandSender target, final String message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeholderAPI(target, message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final List<String> message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeholderAPI(target, message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final String[] message, final Map<String, String> map,
                           final boolean ignoreCase) {
        msg(target, placeholderAPI(target, message, map, ignoreCase));
    }

    public static void msg(final CommandSender target, final FileConfiguration file, final String path) {
        msg(target, file, path, null, false);
    }

    public static void msg(final CommandSender target, final FileConfiguration file, final String path,
                           final Map<String, String> map, final boolean replace) {
        if (file == null) {
            throw new IllegalArgumentException("The file can't be null");
        }
        if (path == null) {
            throw new IllegalArgumentException("The path can't be null");
        }

        if (!file.isSet(path)) {
            throw new IllegalArgumentException("The path: " + path + " doesn't exist.");
        }

        if (isList(file, path)) {
            msg(target, file.getStringList(path), map, replace);
        } else {
            msg(target, file.getString(path), map, replace);
        }
    }

    public static void msg(final CommandSender target, final String message) {
        if (target == null) {
            throw new IllegalArgumentException("The target can't be null");
        }
        if (message == null) {
            return;
        }
        target.sendMessage(colorize(message));
    }

    public static void msg(final CommandSender target, final String... message) {
        if (target == null) {
            throw new IllegalArgumentException("The target can't be null");
        }
        if (message == null || message.length == 0) {
            return;
        }
        if (Arrays.stream(message).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The string array can't have null elements");
        }
        target.sendMessage(colorize(message));
    }

    public static void msg(final CommandSender target, final List<String> message) {
        if (target == null) {
            throw new IllegalArgumentException("The target can't be null");
        }
        if (message == null || message.isEmpty()) {
            return;
        }
        if (message.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The list can't have null elements");
        }
        msg(target, message.toArray(new String[0]));
    }

    /**
     * Returns a translated string.
     *
     * @param msg The message to be translated
     * @return A translated message
     */
    public static @NotNull String colorize(final String msg) {
        String unEditedMessage = msg;
        if (unEditedMessage == null) {
            throw new IllegalArgumentException("The string can't be null!");
        }
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(unEditedMessage);
        while (matcher.find()) {
            String hexCode = unEditedMessage.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            unEditedMessage = unEditedMessage.replace(hexCode, builder.toString());
            matcher = pattern.matcher(unEditedMessage);
        }
        //noinspection deprecation
        return ChatColor.translateAlternateColorCodes('&', unEditedMessage);
    }

    public static String stripColor(final String msg) {
        if (msg == null) {
            throw new IllegalArgumentException("The string can't be null!");
        }
        //noinspection deprecation
        return ChatColor.stripColor(msg);
    }

    /**
     * Returns a translated string array.
     *
     * @param array Array of messages
     * @return A translated message array
     */
    public static String @NotNull [] colorize(final String... array) {
        if (array == null) {
            throw new IllegalArgumentException("The string array can't be null!");
        }
        if (Arrays.stream(array).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The string array can't have null elements!");
        }
        final String[] newarr = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = colorize(newarr[i]);
        }
        return newarr;
    }

    public static String @NotNull [] stripColor(final String... array) {
        if (array == null) {
            throw new IllegalArgumentException("The string array can't be null!");
        }
        if (Arrays.stream(array).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The string array can't have null elements!");
        }
        final String[] newarr = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = stripColor(newarr[i]);
        }
        return newarr;
    }

    /**
     * Returns a translated string collection.
     *
     * @param coll The collection to be translated
     * @return A translated message
     */
    public static @NotNull List<String> colorize(final List<String> coll) {
        if (coll == null) {
            throw new IllegalArgumentException("The string collection can't be null!");
        }
        if (coll.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The string collection can't have null elements!");
        }
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(BukkitMinecraftUtils::colorize);
        return newColl;
    }

    /**
     * Converts a String List that contains color codes to Color List
     *
     * @param list the String List that contains the color codes
     * @return the new Color List with the colors of the input Color String List
     */
    public static @NotNull List<Color> colorsStringListToColorList(@NotNull List<String> list) {
        return colorsStringListToColorList(list.toArray(new String[0]));
    }

    /**
     * Converts a String Array that contains color codes to Color List
     *
     * @param array the String Array that contains the color codes
     * @return the new Color List with the colors of the input Color String Array
     */
    public static @NotNull List<Color> colorsStringListToColorList(String @NotNull ... array) {
        List<Color> colorList = Lists.newArrayList();
        for (String str : array) {
            colorList.add(Color.from(str));
        }
        return colorList;
    }

    public static @NotNull List<String> stripColor(final List<String> coll) {
        if (coll == null) {
            throw new IllegalArgumentException("The string collection can't be null!");
        }
        if (coll.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("The string collection can't have null elements!");
        }
        final List<String> newColl = Lists.newArrayList(coll);
        newColl.replaceAll(BukkitMinecraftUtils::stripColor);
        return newColl;
    }

    public static void debug(final String name, String version, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            BukkitMinecraftUtils.printMsg(Utils.placeHolder("[" + name + "] [Debug] [Version: " + version + "] " + msg, map, false));
        }
    }

    public static void debug(final String name, String version, String... messages) {
        debug(name, version, new HashObjectMap<>(), messages);
    }

    public static void debug(final String name, String version, @NotNull List<String> messages) {
        debug(name, version, new HashObjectMap<>(), messages.toArray(new String[0]));
    }

    public static void debug(final JavaPlugin plugin, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            //noinspection deprecation
            BukkitMinecraftUtils.printMsg(Utils.placeHolder("[" + plugin.getDescription().getName() + "] [Debug] [Version: " + plugin.getDescription().getVersion() + "] " + msg, map, false));
        }
    }

    public static void debug(final JavaPlugin plugin, String... messages) {
        debug(plugin, new HashObjectMap<>(), messages);
    }

    public static void debug(final JavaPlugin plugin, @NotNull List<String> messages) {
        debug(plugin, new HashObjectMap<>(), messages.toArray(new String[0]));
    }

    public static ItemStack @NotNull [] getItems(final @NotNull ItemStack item, int amount) {

        final int maxSize = item.getMaxStackSize();
        if (amount <= maxSize) {
            item.setAmount(Math.max(amount, 1));
            return new ItemStack[]{item};
        }
        final List<ItemStack> resultItems = Lists.newArrayList();
        do {
            item.setAmount(Math.min(amount, maxSize));
            resultItems.add(new ItemStack(item));
            amount = amount >= maxSize ? amount - maxSize : 0;
        } while (amount != 0);
        return resultItems.toArray(new ItemStack[0]);
    }

    public static @NotNull String getProgressBar(double current, double max, int totalBars, String symbol, String completedColor,
                                                 String notCompletedColor) {
        final double percent = (float) Math.min(current, max) / max;
        final int progressBars = (int) (totalBars * percent);
        final int leftOver = totalBars - progressBars;

        return colorize(completedColor) +
                String.valueOf(symbol).repeat(Math.max(0, progressBars)) +
                colorize(notCompletedColor) +
                String.valueOf(symbol).repeat(Math.max(0, leftOver));
    }

    public static @NotNull ItemStack resetItemMeta(final @NotNull ItemStack item) {
        final ItemStack copy = item.clone();
        copy.setItemMeta(Bukkit.getItemFactory().getItemMeta(copy.getType()));
        return copy;
    }

    /**
     * Register listeners
     *
     * @param listeners Class that implements Listener interface
     */
    public static void registerListeners(Plugin plugin, Listener @NotNull ... listeners) {
        final PluginManager pm = Bukkit.getPluginManager();
        for (final Listener listener : listeners) {
            pm.registerEvents(listener, plugin);
        }
    }

    /**
     * Run the commands from config
     *
     * @param s Command to run
     * @since v5.0
     */
    public static void runCommand(Plugin plugin, String s) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (s == null)
                return;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        });
    }

    /**
     * Kick all players.
     *
     * @param kickMessage The kick message to display.
     * @since v5.0
     */
    public static void kickAll(Plugin plugin, String kickMessage) {
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(colorize(kickMessage))));
    }

    /**
     * Disallow or allow the player login to the server with a custom message.
     *
     * @param b       True -> disallow player login. False -> allow player login.
     * @param message The message to display when the player is disallowed to login.
     * @since v5.0
     */
    public static void disallowLogin(boolean b, String message) {
        join = b;
        disableJoinMessage = message;
    }

    /**
     * @return true if the player login is disallowed or false if the player login is allowed.
     * @since v5.0
     */
    public static boolean isLoginDisallowed() {
        return join;
    }

    /**
     * @return The message to display when the player is disallowed to login.
     * @since v5.0
     */
    public static String getDisallowLoginMessage() {
        return disableJoinMessage;
    }

    /**
     * Gets a list of ItemStacks from Base64 string.
     *
     * @param data Base64 string to convert to ItemStack list.
     * @return ItemStack list created from the Base64 string.
     */
    @Contract("null -> new")
    public static @Nullable List<ItemStack> itemStackListFromBase64(String data) {
        if (data == null || data.isEmpty()) {
            return Lists.newArrayList();
        }
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return Arrays.asList(items);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A method to serialize an {@link ItemStack} list to Base64 String.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     */
    public static @NotNull String itemStackListToBase64(List<ItemStack> items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.size());

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Check if a username belongs to a premium account
     *
     * @param username player name
     * @return boolean
     */
    public static boolean isUsernamePremium(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return !result.toString().isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param target     target for the placeholders.
     * @param str        the input string to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string with the placeholders replaced
     */
    public static String placeholderAPI(final ServerOperator target, String str, final Map<String, String> map, final boolean ignoreCase) {
        if (target == null) {
            throw new IllegalArgumentException("The target can't be null");
        }
        if (str == null) {
            throw new IllegalArgumentException("The string can't be null!");
        }
        if (map == null) {
            try {
                if (target instanceof OfflinePlayer offlinePlayer) {
                    return me.clip.placeholderapi.PlaceholderAPI.setBracketPlaceholders(offlinePlayer, str);
                }
                return str;
            } catch (Throwable error) {
                return str;
            }
        }
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            str = ignoreCase ? Utils.replaceIgnoreCase(str, entry.getKey(), entry.getValue())
                    : str.replace(entry.getKey(), entry.getValue());
        }
        try {
            if (target instanceof OfflinePlayer offlinePlayer) {
                return me.clip.placeholderapi.PlaceholderAPI.setBracketPlaceholders(offlinePlayer, str);
            }
            return str;
        } catch (Throwable error) {
            return str;
        }
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param target     target for the placeholders.
     * @param array      the input array of string to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string array with the placeholders replaced
     */
    public static String @NotNull [] placeholderAPI(final ServerOperator target, final String[] array, final Map<String, String> map, final boolean ignoreCase) {
        if (array == null) throw new IllegalArgumentException("The string array can't be null!");
        if (Arrays.stream(array).anyMatch(Objects::isNull))
            throw new IllegalArgumentException("The string array can't have null elements!");
        final String[] newArray = Arrays.copyOf(array, array.length);
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = placeholderAPI(target, newArray[i], map, ignoreCase);
        }
        return newArray;
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param target     target for the placeholders.
     * @param coll       the input string list to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string list with the placeholders replaced
     */
    public static List<String> placeholderAPI(final ServerOperator target, final List<String> coll, final Map<String, String> map,
                                              final boolean ignoreCase) {
        if (coll == null) throw new IllegalArgumentException("The string collection can't be null!");
        if (coll.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("The string collection can't have null elements!");
        return coll.stream().map(str -> placeholderAPI(target, str, map, ignoreCase)).collect(Collectors.toList());
    }


    /**
     * Compares two Chunks to check if they are the same.
     *
     * @param chunkA The first Chunk to compare.
     * @param chunkB The second Chunk to compare.
     * @return `true` if the Chunks are equal, otherwise `false`.
     */
    public static boolean compareChunks(final @NotNull Chunk chunkA, final @NotNull Chunk chunkB) {
        if (!chunkA.getWorld().equals(chunkB.getWorld())) {
            return false;
        }
        if (chunkA.getX() != chunkB.getX()) {
            return false;
        }
        return chunkA.getZ() == chunkB.getZ();
    }

    /**
     * Compares two ItemStacks to check if they are the same.
     *
     * @param item1 The first ItemStack to compare.
     * @param item2 The second ItemStack to compare.
     * @return `true` if the ItemStacks are equal, otherwise `false`.
     */
    public static boolean compareItemStacks(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) {
            return false;
        }

        if (item1 == item2) {
            return true;
        }

        if (item1.getType() != item2.getType()) {
            return false;
        }

        if (item1.getDurability() != item2.getDurability()) {
            return false;
        }

        if (item1.hasItemMeta() != item2.hasItemMeta()) {
            return false;
        }

        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            ItemMeta meta1 = item1.getItemMeta();
            ItemMeta meta2 = item2.getItemMeta();

            if (!Bukkit.getItemFactory().equals(meta1, meta2)) {
                return false;
            }
        }

        return item1.getAmount() == item2.getAmount();
    }

    /**
     * Checks if the chunk containing the specified `location` is loaded in the world.
     *
     * @param loc The location to check.
     * @return `true` if the chunk is loaded, otherwise `false`.
     */
    public static boolean isChunkLoaded(final Location loc) {
        if (loc == null) {
            return false;
        }
        if (loc.getWorld() == null) {
            return false;
        }
        return loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }

    /**
     * Checks if the current environment is "Folia" by attempting to load the "io.papermc.paper.threadedregions.RegionizedServer" class.
     *
     * @return `true` if the environment is "Folia," otherwise `false`.
     */
    public static boolean isFolia() {
        return hasClass("io.papermc.paper.threadedregions.RegionizedServer");
    }

    /**
     * Checks if the current environment is "Paper" by attempting to load the "com.destroystokyo.paper.PaperConfig" or "io.papermc.paper.configuration.Configuration" class.
     *
     * @return `true` if the environment is "Paper," otherwise `false`.
     */
    public static boolean isPaper() {
        return (hasClass("com.destroystokyo.paper.PaperConfig")
                || hasClass("io.papermc.paper.configuration.Configuration"));
    }

    /**
     * Represents a Minecraft server version using a numeric format (major.minor.patch).
     */
    public static final class MinecraftVersion implements Comparable<MinecraftVersion> {

        private final int major;
        private final int minor;
        private final int patch;
        private final String nmsPackage;

        /**
         * The current server version, parsed once during class initialization.
         */
        private static final MinecraftVersion CURRENT;

        static {
            CURRENT = parse(Bukkit.getServer().getBukkitVersion());
        }

        /**
         * Constructs a new {@link MinecraftVersion}.
         *
         * @param major the major version (e.g. 1 or 26)
         * @param minor the minor version (e.g. 21 or 1)
         * @param patch the patch version (e.g. 4 or 11)
         */
        public MinecraftVersion(int major, int minor, int patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.nmsPackage = parseNMSPackage();
        }

        public MinecraftVersion(int major, int minor, int patch, String nmsPackage) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.nmsPackage = nmsPackage;
        }

        /**
         * Returns the current Minecraft server version.
         *
         * @return the parsed server version
         */
        public static MinecraftVersion getCurrent() {
            return CURRENT;
        }

        /**
         * Parses a Bukkit version string into a {@link MinecraftVersion}.
         * <p>
         * Examples of supported formats:
         * <ul>
         *     <li>{@code 1.21.4-R0.1-SNAPSHOT}</li>
         *     <li>{@code 26.1.1}</li>
         * </ul>
         *
         * @param bukkitVersion the raw version string from Bukkit
         * @return a parsed {@link MinecraftVersion}, or {@code 0.0.0} if parsing fails
         */
        @Contract("_ -> new")
        public static @NonNull MinecraftVersion parse(String bukkitVersion) {
            try {
                String versionPart = bukkitVersion.split("-")[0];
                String[] parts = versionPart.split("\\.");

                int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
                int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

                return new MinecraftVersion(major, minor, patch, parseNMSPackage());
            } catch (Exception e) {
                return new MinecraftVersion(0, 0, 0, "unknown");
            }
        }

        private static String parseNMSPackage() {
            try {
                String packageName = Bukkit.getServer()
                        .getClass()
                        .getPackage()
                        .getName();

                String[] parts = packageName.split("\\.");

                return parts[parts.length - 1];
            } catch (Exception e) {
                return "unknown";
            }
        }

        /**
         * Compares this version to another version.
         *
         * @param other the other version
         * @return a negative value if lower, positive if higher, 0 if equal
         */
        @Override
        public int compareTo(@NonNull MinecraftVersion other) {
            if (this.major != other.major) {
                return Integer.compare(this.major, other.major);
            }
            if (this.minor != other.minor) {
                return Integer.compare(this.minor, other.minor);
            }
            return Integer.compare(this.patch, other.patch);
        }

        /**
         * Checks if this version is greater than or equal to the given version.
         * Patch is ignored (assumes 0).
         *
         * @param major the major version
         * @param minor the minor version
         * @return {@code true} if this version is >= given version
         */
        public boolean isAtLeast(int major, int minor) {
            return compareTo(new MinecraftVersion(major, minor, 0)) >= 0;
        }

        /**
         * Checks if this version is greater than or equal to the given version.
         *
         * @param major the major version
         * @param minor the minor version
         * @param patch the patch version
         * @return {@code true} if this version is >= given version
         */
        public boolean isAtLeast(int major, int minor, int patch) {
            return compareTo(new MinecraftVersion(major, minor, patch)) >= 0;
        }

        /**
         * Checks if this version is strictly lower than the given version.
         * Patch is ignored (assumes 0).
         *
         * @param major the major version
         * @param minor the minor version
         * @return {@code true} if this version is < given version
         */
        public boolean isBelow(int major, int minor) {
            return isBelow(major, minor, 0);
        }


        public boolean isBelow(int major, int minor, int patch) {
            return compareTo(new MinecraftVersion(major, minor, patch)) < 0;
        }

        /**
         * Checks if this version is within a range:
         * {@code [min, max)} (inclusive lower bound, exclusive upper bound).
         *
         * @param minMajor minimum major version
         * @param minMinor minimum minor version
         * @param maxMajor maximum major version
         * @param maxMinor maximum minor version
         * @return {@code true} if within the specified range
         */
        public boolean isBetween(
                int minMajor, int minMinor,
                int maxMajor, int maxMinor
        ) {
            return isAtLeast(minMajor, minMinor)
                    && isBelow(maxMajor, maxMinor);
        }

        public boolean isBetween(int minMajor, int minMinor, int minPatch, int maxMajor, int maxMinor, int maxPatch) {
            return isAtLeast(minMajor, minMinor, minPatch)
                    && isBelow(maxMajor, maxMinor, maxPatch);
        }

        public boolean isVersion(int major, int minor, Integer patch) {
            return this.major == major
                    && this.minor == minor
                    && (patch == null || this.patch == patch);
        }

        /**
         * @return the major version
         */
        public int getMajor() {
            return major;
        }

        /**
         * @return the minor version
         */
        public int getMinor() {
            return minor;
        }

        /**
         * @return the patch version
         */
        public int getPatch() {
            return patch;
        }

        public String getNMSPackage() {
            return nmsPackage;
        }

        /**
         * Returns the version in {@code major.minor.patch} format.
         *
         * @return string representation of this version
         */
        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }
    }

    public static final class MinecraftReflection {

        private static final String NET_MINECRAFT_PACKAGE = "net.minecraft";
        private static final String OBC_PACKAGE = "org.bukkit.craftbukkit";

        private static final String NMS_SERVER_BASE = NET_MINECRAFT_PACKAGE + ".server";

        private static final MinecraftVersion VERSION = MinecraftVersion.getCurrent();
        private static final boolean REPACKAGED_NMS = VERSION.isAtLeast(1, 17);
        private static final boolean REPACKAGED_OBC = VERSION.isAtLeast(1, 20, 5) && isPaper();

        private static String resolveNmsPackage() {
            return REPACKAGED_NMS ? NMS_SERVER_BASE : NMS_SERVER_BASE + "." + VERSION.getNMSPackage();
        }

        private static String resolveObcPackage() {
            return REPACKAGED_OBC ? OBC_PACKAGE : OBC_PACKAGE + "." + VERSION.getNMSPackage();
        }

        private static ClassLoader loader() {
            return Bukkit.class.getClassLoader();
        }

        @Contract(pure = true)
        public static @NonNull String nmsClassName(String className) {
            return resolveNmsPackage() + "." + className;
        }

        public static @NonNull Class<?> nmsClass(String className) throws ClassNotFoundException {
            return Class.forName(nmsClassName(className), false, loader());
        }

        public static Optional<Class<?>> nmsOptionalClass(String className) {
            return Utils.Reflection.optionalClass(nmsClassName(className), loader());
        }

        public static @NonNull Class<?> nmsClass(String className, String fullName) throws ClassNotFoundException {
            return Class.forName(REPACKAGED_NMS ? fullName : nmsClassName(className), false, loader());
        }

        public static Optional<Class<?>> nmsOptionalClass(String className, String fullName) {
            return Utils.Reflection.optionalClass(
                    REPACKAGED_NMS ? fullName : nmsClassName(className),
                    loader()
            );
        }

        @Contract(pure = true)
        public static @NonNull String obcClassName(String className) {
            return resolveObcPackage() + "." + className;
        }

        public static @NonNull Class<?> obcClass(String className) throws ClassNotFoundException {
            return Class.forName(obcClassName(className), false, loader());
        }

        public static Optional<Class<?>> obcOptionalClass(String className) {
            return Utils.Reflection.optionalClass(obcClassName(className), loader());
        }

        public static boolean isNmsRepackaged() {
            return REPACKAGED_NMS;
        }

        public static boolean isObcRepackaged() {
            return REPACKAGED_OBC;
        }
    }

}
