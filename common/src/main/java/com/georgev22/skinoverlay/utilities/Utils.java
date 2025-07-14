package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.exceptions.NotFoundException;
import com.georgev22.skinoverlay.exceptions.ReflectionException;
import com.georgev22.skinoverlay.maps.HashObjectMap;
import com.georgev22.skinoverlay.maps.ObjectMap;
import com.georgev22.skinoverlay.maps.TreeObjectMap;
import com.google.gson.Gson;
import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class Utils {

    /**
     * Converts a given number of seconds into a formatted time string using provided labels.
     * If the provided array does not contain exactly 9 elements, default values are used.
     *
     * @param input  The number of seconds to convert.
     * @param inputs An array containing labels for singular/plural time units and an invalid input message.
     * @return A formatted time string.
     */
    public static String convertSeconds(long input, String @NotNull ... inputs) {
        String[] defaultInputs = {"second", "seconds", "minute", "minutes", "hour", "hours", "day", "days", "invalid"};
        String[] processedInputs = new String[9];

        System.arraycopy(inputs, 0, processedInputs, 0, inputs.length);

        System.arraycopy(defaultInputs, inputs.length, processedInputs, inputs.length, 9 - inputs.length);

        return convertSeconds(
                input,
                processedInputs[0],
                processedInputs[1],
                processedInputs[2],
                processedInputs[3],
                processedInputs[4],
                processedInputs[5],
                processedInputs[6],
                processedInputs[7],
                processedInputs[8]
        );
    }

    /**
     * Converts a given number of seconds into a formatted time string using provided labels.
     *
     * @param input        The number of seconds to convert.
     * @param secondInput  Singular form of "second".
     * @param secondsInput Plural form of "seconds".
     * @param minuteInput  Singular form of "minute".
     * @param minutesInput Plural form of "minutes".
     * @param hourInput    Singular form of "hour".
     * @param hoursInput   Plural form of "hours".
     * @param dayInput     Singular form of "day".
     * @param daysInput    Plural form of "days".
     * @param invalidInput Message to return if the input is invalid.
     * @return A formatted time string.
     */
    public static String convertSeconds(long input, String secondInput,
                                        String secondsInput,
                                        String minuteInput,
                                        String minutesInput,
                                        String hourInput,
                                        String hoursInput,
                                        String dayInput,
                                        String daysInput,
                                        String invalidInput) {
        if (input < 0) {
            System.out.println(
                    "An attempt to convert a negative number was made for: " + input + ", making the number absolute.");
            input = Math.abs(input);
        }

        final StringBuilder builder = new StringBuilder();

        boolean comma = false;

        /* Days */
        final long days = TimeUnit.SECONDS.toDays(input);
        if (days > 0) {
            builder.append(days).append(" ").append(days == 1 ? dayInput : daysInput);
            comma = true;
        }

        /* Hours */
        final long hours = (TimeUnit.SECONDS.toHours(input) - TimeUnit.DAYS.toHours(days));
        if (hours > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(hours).append(" ").append(hours == 1 ? hourInput : hoursInput);
            comma = true;
        }

        /* Minutes */
        final long minutes = (TimeUnit.SECONDS.toMinutes(input) - TimeUnit.HOURS.toMinutes(hours)
                - TimeUnit.DAYS.toMinutes(days));
        if (minutes > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(minutes).append(" ").append(minutes == 1 ? minuteInput : minutesInput);
            comma = true;
        }

        /* Seconds */
        final long seconds = (TimeUnit.SECONDS.toSeconds(input) - TimeUnit.MINUTES.toSeconds(minutes)
                - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days));
        if (seconds > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(seconds).append(" ").append(seconds == 1 ? secondInput : secondsInput);
        }

        /* Result */
        final String result = builder.toString();
        return result.isEmpty() ? invalidInput : result;
    }

    /**
     * Converts a given number of seconds into a formatted time string using default labels.
     *
     * @param input The number of seconds to convert.
     * @return A formatted time string.
     */
    public static String convertSeconds(long input) {
        return convertSeconds(input, "second", "seconds", "minute", "minutes",
                "hour", "hours", "day", "days",
                "invalid time");
    }

    /**
     * Checks if the input is of type long
     *
     * @param input input to check if it is long
     * @return <code>true</code> if the input is long,
     * <code>false</code> if it is not
     */
    public static boolean isLong(final @NotNull String input) {
        try {
            Long.parseLong(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the input is of type double
     *
     * @param input input to check if it is double
     * @return <code>true</code> if the input is double,
     * <code>false</code> if it is not
     */
    public static boolean isDouble(final @NotNull String input) {
        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the input is of type int
     *
     * @param input input to check if it is int
     * @return <code>true</code> if the input is int,
     * <code>false</code> if it is not
     */
    public static boolean isInt(final @NotNull String input) {
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the input is a List
     *
     * @param input input to check if it is a List
     * @return <code>true</code> if the input is a List,
     * <code>false</code> if it is not
     */
    public static boolean isList(final Object input) {
        return input instanceof List;
    }

    public static boolean isList(final @NotNull FileConfiguration file, final String path) {
        return Utils.isList(file.get(path));
    }

    /**
     * Converts an object to int
     *
     * @param object Object to convert.
     * @return the converted object as int.
     */
    public static int toInt(Object object) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }

        try {
            return Integer.parseInt(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return 0;
    }

    /**
     * Converts an object to double
     *
     * @param object Object to convert.
     * @return the converted object as double.
     */
    public static double toDouble(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }

        try {
            return Double.parseDouble(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {
        }

        return 0;
    }

    /**
     * Converts an object to long
     *
     * @param object Object to convert.
     * @return the converted object as long.
     */
    public static long toLong(Object object) {
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }

        try {
            return Long.parseLong(object.toString());
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return 0;
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param str        the input string to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string with the placeholders replaced
     */
    public static String placeHolder(String str, final Map<String, String> map, final boolean ignoreCase) {
        if (str == null) throw new IllegalArgumentException("str cannot be null");
        if (map == null) {
            return str;
        }
        for (final Entry<String, String> entry : map.entrySet()) {
            str = ignoreCase ? replaceIgnoreCase(str, entry.getKey(), entry.getValue())
                    : str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }

    /**
     * Replaces a string, without distinguishing between lowercase and uppercase
     *
     * @param text         the input string
     * @param searchString the string to be replaced
     * @param replacement  the replacement of the string
     * @return the new string with the replacement
     */
    public static String replaceIgnoreCase(final String text, String searchString, final String replacement) {

        if (text == null || text.isEmpty()) {
            return text;
        }
        if (searchString == null || searchString.isEmpty()) {
            return text;
        }
        if (replacement == null) {
            return text;
        }

        int max = -1;

        final String searchText = text.toLowerCase();
        searchString = searchString.toLowerCase();
        int start = 0;
        int end = searchText.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        final int replacementLength = searchString.length();
        int increase = replacement.length() - replacementLength;
        increase = Math.max(increase, 0);
        increase *= 16;

        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text, start, end).append(replacement);
            start = end + replacementLength;
            if (--max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        return buf.append(text, start, text.length()).toString();
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param array      the input array of string to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string array with the placeholders replaced
     */
    public static String @NotNull [] placeHolder(final String[] array, final Map<String, String> map, final boolean ignoreCase) {
        if (array == null) throw new IllegalArgumentException("array cannot be null");
        if (Arrays.stream(array).anyMatch(Objects::isNull))
            throw new IllegalArgumentException("array cannot have null elements");

        final String[] newArray = Arrays.copyOf(array, array.length);
        if (map == null) {
            return newArray;
        }
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = placeHolder(newArray[i], map, ignoreCase);
        }
        return newArray;
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param coll       the input string list to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string list with the placeholders replaced
     */
    public static List<String> placeHolder(final List<String> coll, final Map<String, String> map,
                                           final boolean ignoreCase) {
        if (coll == null) throw new IllegalArgumentException("coll cannot be null");
        if (coll.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("coll cannot have null elements");
        return map == null ? coll
                : coll.stream().map(str -> placeHolder(str, map, ignoreCase)).collect(Collectors.toList());
    }

    /**
     * Formats a number to a specific Locale
     *
     * @param lang  the desired locale
     * @param input the input number
     * @return the formatted String
     */
    public static String formatNumber(java.util.Locale lang, double input) {
        if (lang == null) throw new IllegalArgumentException("lang cannot be null");
        return NumberFormat.getInstance(lang).format(input);
    }

    /**
     * Formats a number to the US Locale
     *
     * @param input the input number
     * @return the formatted String
     */
    public static String formatNumber(double input) {
        return formatNumber(java.util.Locale.US, input);
    }

    /**
     * Finds and retrieves the greatest values of a map.
     *
     * @param map The map to get the greatest values
     * @param n   The number of values you want to get
     * @param <K> The map key
     * @param <V> The map value
     * @return Map
     */
    public static <K, V extends Comparable<? super V>> @NotNull List<Entry<K, V>> findGreatest(@NotNull Map<K, V> map, int n) {
        Comparator<? super Entry<K, V>> comparator = (Comparator<Entry<K, V>>) (e0, e1) -> {
            V v0 = e0.getValue();
            V v1 = e1.getValue();
            return v1.compareTo(v0);
        };
        PriorityQueue<Entry<K, V>> highest = new PriorityQueue<>(n, comparator);
        for (Entry<K, V> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > n) {
                highest.poll();
            }
        }

        List<Entry<K, V>> result = new ArrayList<>();
        while (!highest.isEmpty()) {
            result.add(highest.poll());
        }
        return result;
    }

    public static @NotNull String getArgumentsToString(String @NotNull [] args, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

    public static String @NotNull [] getArgumentsToArray(String @NotNull [] args, int num) {
        if (args.length == 0) {
            return args;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim().split(" ");
    }

    public static String @NotNull [] reverse(String[] a) {
        List<String> list = Arrays.asList(a);
        Collections.reverse(list);
        return list.toArray(new String[0]);
    }

    /**
     * Serialize Object to string using google Gson
     *
     * @param object object to serialize
     * @return string output of the serialized object
     * @since v5.0.1
     */
    public static <T> String serialize(Object object) {
        ByteArrayOutputStream byteaOut = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = null;
        try {
            gzipOut = new GZIPOutputStream(byteaOut);
            gzipOut.write(new Gson().toJson(object).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipOut != null) try {
                gzipOut.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return byteaOut.toString();
    }

    /**
     * Deserialize a string back to object
     * see {@link #serialize(Object)}
     *
     * @param string serialized string
     * @param <T>    the original object type (eg: {@code deserialize(stringToDeserialize, new TypeToken<ObjectMap<String, Integer>>(){}.getType())})
     * @return the deserialized object
     * @since v5.0.1
     */
    public static <T> T deserialize(@NotNull String string, @NotNull Type type) {
        ByteArrayOutputStream byteaOut = new ByteArrayOutputStream();
        GZIPInputStream gzipIn = null;
        try {
            gzipIn = new GZIPInputStream(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
            for (int data; (data = gzipIn.read()) > -1; ) {
                byteaOut.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipIn != null) try {
                gzipIn.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return new Gson().fromJson(byteaOut.toString(), type);
    }

    /**
     * Serializes an object and saves it to the specified file path.
     * <p>
     * Serializes the specified object using the {@link ObjectOutputStream} and saves the serialized data to the file
     * specified by the given file path.
     * </p>
     *
     * @param obj      the object to be serialized
     * @param filePath the file path where the serialized data will be saved
     * @throws IOException if there is an error accessing or writing to the file
     */
    public static void serializeObject(@NotNull Object obj, @NotNull String filePath) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.close();
    }

    /**
     * Serializes an object and returns the serialized data as a string.
     * <p>
     * Serializes the specified object using the {@link ObjectOutputStream} and returns the serialized data as a string.
     *
     * @param obj the object to be serialized
     * @return the serialized data as a string
     * @throws IOException if there is an error during serialization
     */
    @Contract("_ -> new")
    public static @NotNull String serializeObjectToString(@NotNull Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();
        byteArrayOutputStream.close();

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Serializes an object and returns the serialized data as a byte array.
     * <p>
     * Serializes the specified object using the {@link ObjectOutputStream} and returns the serialized data as a byte array.
     *
     * @param obj the object to be serialized
     * @return the serialized data as a  byte array
     * @throws IOException if there is an error during serialization
     */
    public static byte @NotNull [] serializeObjectToBytes(@NotNull Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Deserializes an object from the specified file path.
     * <p>
     * Deserializes the object
     * stored in the file specified by the given file path using the {@link ObjectInputStream} and
     * returns the deserialized object.
     *
     * @param filePath the file path where the serialized object is stored
     * @return the deserialized object
     * @throws IOException            if there is an error accessing or reading from the file
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public static Object deserializeObject(@NotNull String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        return obj;
    }

    /**
     * Deserializes an object from the specified serialized data.
     * <p>
     * Deserializes the object from the specified serialized data using the {@link ObjectInputStream} and returns the
     * deserialized object.
     *
     * @param serializedData the serialized object data as a string
     * @return the deserialized object
     * @throws IOException            if there is an error accessing or reading from the serialized data
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public static Object deserializeObjectFromString(@NotNull String serializedData) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(serializedData);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();

        return obj;
    }

    /**
     * Deserializes an object from the specified serialized data.
     * <p>
     * Deserializes the object from the specified serialized data using the {@link ObjectInputStream} and returns the
     * deserialized object.
     *
     * @param byteArray the serialized object data as a byte array
     * @return the deserialized object
     * @throws IOException            if there is an error accessing or reading from the serialized data
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public static Object deserializeObjectFromBytes(byte @NotNull [] byteArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return obj;
    }

    /**
     * Converts a string list to string.
     *
     * @param stringList The String List to convert.
     * @return a String that contains the String List contents.
     */
    public static @NotNull String stringListToString(@NotNull List<String> stringList) {
        return stringList.toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    /**
     * Convert a string to string list.
     *
     * @param string The string to convert.
     * @return a String List that contains the String contents.
     */
    public static @NotNull List<String> stringToStringList(@NotNull String string) {
        return string.replace(" ", "").isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(string.split(",")));
    }

    /**
     * Converts a map to string list.
     *
     * @param objectMap The {@link ObjectMap} to convert.
     * @return a String List with the {@link ObjectMap} contents.
     */
    public static <K, V> @NotNull List<String> objectMapToStringList(@NotNull ObjectMap<K, V> objectMap) {
        return mapToStringList(objectMap);
    }

    /**
     * Converts a map to string list.
     *
     * @param map The {@link Map} to convert.
     * @return a String List with the {@link Map} contents.
     */
    public static <K, V> @NotNull List<String> mapToStringList(@NotNull Map<K, V> map) {
        List<String> stringList = new ArrayList<>();
        for (Entry<K, V> entry : map.entrySet()) {
            stringList.add(entry.getKey() + "=" + entry.getValue());
        }
        return stringList;
    }

    /**
     * Converts a String List to {@link ObjectMap}.
     *
     * @param stringList the String List to convert.
     * @param clazz      The class type of the value.
     * @param <T>        The class type.
     * @param <K>        Type of the key.
     * @param <V>        Type of the value.
     * @return a {@link ObjectMap#newHashObjectMap(Map)} with the String List contents.
     */
    public static <K, V, T> @NotNull ObjectMap<K, V> stringListToObjectMap(List<String> stringList, final Class<T> clazz) {
        return new HashObjectMap<>(stringListToHashMap(stringList, clazz));
    }

    /**
     * Converts a String List to {@link HashMap}.
     *
     * @param stringList the String List to convert.
     * @param clazz      The class type of the value.
     * @param <T>        The class type.
     * @param <K>        Type of the key.
     * @param <V>        Type of the value.
     * @return a {@link HashMap} with the String List contents.
     */
    public static <K, V, T> @NotNull Map<K, V> stringListToMap(List<String> stringList, final Class<T> clazz) {
        return new HashMap<>(stringListToHashMap(stringList, clazz));
    }

    /**
     * Converts a String List to {@link Map}.
     *
     * @param stringList the String List to convert.
     * @param clazz      The class type of the value.
     * @param <T>        The class type.
     * @param <K>        Type of the key.
     * @param <V>        Type of the value.
     * @return a {@link HashMap} with the String List contents.
     */
    public static <K, V, T> @NotNull Map<K, V> stringListToHashMap(@NotNull List<String> stringList, final Class<T> clazz) {
        Map<K, V> map = new HashMap<>();

        if (stringList.isEmpty()) {
            return map;
        }

        for (String string : stringList) {
            if (!string.contains("=")) continue;
            String[] arguments = string.split("=");
            if (clazz != null) {
                Method method;
                try {
                    method = Reflection.fetchDeclaredMethod(clazz, "valueOf", String.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    map.put((K) arguments[0], (V) method.invoke(null, arguments[1]));
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    ex.printStackTrace();
                    System.out.println("Failure: " + arguments[1] + " is not of type " + clazz.getName());
                }
            } else {
                map.put((K) arguments[0], (V) arguments[1]);
            }

        }

        return map;
    }

    /**
     * Generates a specific amount of random HEX colors
     *
     * @param amount the amount of the colors to be generated
     * @return a String List that contains the generated color codes
     */
    public static @NotNull List<String> randomColors(int amount) {
        List<String> randomColorList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            randomColorList.add(String.format("#%06x", new Random().nextInt(0xffffff + 1)));
        }
        return randomColorList;
    }

    /**
     * Converts a number to Roman Number format
     *
     * @param number number to convert
     * @return a string of the number at Roman Number format
     */
    public static String toRoman(int number) {
        if (number <= 0) {
            return String.valueOf(number);
        }
        TreeObjectMap<Integer, String> map = ObjectMap.newTreeObjectMap();
        map
                .append(1000, "M")
                .append(900, "CM")
                .append(500, "D")
                .append(400, "CD")
                .append(100, "C")
                .append(90, "XC")
                .append(50, "L")
                .append(40, "XL")
                .append(10, "X")
                .append(9, "IX")
                .append(5, "V")
                .append(4, "IV")
                .append(1, "I")
        ;
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number - l);
    }

    /**
     * Returns element closest to target in arr[]
     */
    public static int findClosest(@NotNull List<Integer> list, int target) {
        Integer[] arr = list.toArray(new Integer[0]);
        int n = arr.length;
        if (target <= arr[0])
            return arr[0];
        if (target >= arr[n - 1])
            return arr[n - 1];
        int i = 0, j = n, mid = 0;
        while (i < j) {
            mid = (i + j) / 2;
            if (arr[mid] == target)
                return arr[mid];
            if (target < arr[mid]) {
                if (mid > 0 && target > arr[mid - 1])
                    return getClosest(arr[mid - 1],
                            arr[mid], target);
                j = mid;
            } else {
                if (mid < n - 1 && target < arr[mid + 1])
                    return getClosest(arr[mid],
                            arr[mid + 1], target);
                i = mid + 1;
            }
        }

        return arr[mid];
    }

    /**
     * Method to compare which one is the more close
     */
    public static int getClosest(int val1, int val2,
                                 int target) {
        if (target - val1 >= val2 - target)
            return val2;
        else
            return val1;
    }

    /**
     * Generate a random alphanumeric string
     *
     * @param n string length
     * @return a random alphanumeric string
     */
    public static @NotNull String generateAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    /**
     * Returns an element at random from a List
     *
     * @param list List to select a random element
     * @return a random element from the specified List
     */
    public static Object getRandomElement(@NotNull List<String> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    /**
     * Returns an element at random from a Set
     *
     * @param set Set to select a random element
     * @return a random element from the specified Set
     */
    public static Object getRandomElement(@NotNull Set<String> set) {
        List<String> b = new ArrayList<>();
        b.addAll(set);
        return getRandomElement(b);
    }

    public static void saveResource(@NotNull String resourcePath, boolean replace, File dataFolder, Class<?> clazz) throws Exception {
        if (resourcePath.isEmpty()) {
            throw new Exception("ResourcePath cannot be null or empty");
        }

        //noinspection DuplicatedCode
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath, clazz);
        if (in == null) {
            throw new Exception("The embedded resource '" + resourcePath + "' cannot be found in " + new File(clazz.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName());
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = Files.newOutputStream(outFile.toPath());
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                Utils.getLogger().warning("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            throw new Exception("Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public static Logger getLogger() {
        return Logger.getGlobal();
    }

    @Nullable
    public static InputStream getResource(@NotNull String filename, @NotNull Class<?> clazz) {
        //noinspection DuplicatedCode
        try {
            URL url = clazz.getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public static void debug(final String name, String version, Logger logger, final Map<String, String> map, String @NotNull ... messages) {
        for (final String msg : messages) {
            printMsg(Utils.placeHolder("[" + name + "] [Debug] [Version: " + version + "] " + msg, map, true), logger);
        }
    }

    public static void debug(final String name, String version, final Logger logger, String... messages) {
        debug(name, version, logger, new HashObjectMap<>(), messages);
    }

    public static void debug(final String name, String version, final Logger logger, @NotNull List<String> messages) {
        debug(name, version, logger, new HashObjectMap<>(), messages.toArray(new String[0]));
    }

    public static void printMsg(final String input, Logger logger) {
        logger.info(input);
    }


    /**
     * Converts the given double `input` to a string representation.
     * If the input value has no decimal part, it returns the integer part as a string.
     * Otherwise, it returns the full double value as a string.
     *
     * @param input The double value to be converted.
     * @return The string representation of the given double value.
     */
    @Contract(pure = true)
    public static @NotNull String convertDoubleInt(final double input) {
        return input % 1d == 0 ? String.valueOf((int) input) : String.valueOf(input);
    }

    /**
     * Splits the given `value` of type BigDecimal into a list of BigDecimal values.
     * Each element in the list represents a portion of the original value that does not exceed Double.MAX_VALUE.
     *
     * @param value The BigDecimal value to split.
     * @return A list of BigDecimal values representing portions of the original value.
     */
    @NotNull
    public static List<BigDecimal> splitBigDecimal(@NotNull BigDecimal value) {
        List<BigDecimal> splitValues = new ArrayList<>();

        if (value.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) <= 0) {
            splitValues.add(value);
        } else {
            BigDecimal maxValue = BigDecimal.valueOf(Double.MAX_VALUE);
            BigDecimal remainingValue = value;

            while (remainingValue.compareTo(maxValue) > 0) {
                splitValues.add(maxValue);
                remainingValue = remainingValue.subtract(maxValue);
            }

            if (remainingValue.compareTo(BigDecimal.ZERO) > 0) {
                splitValues.addAll(splitBigDecimal(remainingValue));
            }
        }

        return splitValues;
    }

    /**
     * Formats the given `number` into a human-readable string representation of money using the US locale.
     *
     * @param number The number to be formatted as money.
     * @return The formatted money string.
     */
    @NotNull
    public static String formatMoney(Number number) {
        return formatMoney(number, java.util.Locale.US);
    }

    /**
     * Formats the given `amount` into a human-readable string representation of money using the specified `locale`.
     *
     * @param amount The number to be formatted as money.
     * @param locale The locale to use for formatting the money string.
     * @return The formatted money string.
     */
    @NotNull
    public static String formatMoney(Number amount, java.util.Locale locale) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        return numberFormat.format(amount);
    }

    public static @NotNull String formatNumber(Number number, String format) {
        return formatNumber(number, java.util.Locale.US, format);
    }

    public static @NotNull String formatNumber(Number number, java.util.Locale locale, String format) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        symbols.setGroupingSeparator(' ');

        DecimalFormat decimalFormat = new DecimalFormat(format, symbols);
        return decimalFormat.format(number);
    }

    /**
     * Generates a deterministic UUID from a given seed using the SHA-256 hash function.
     *
     * @param seed the input seed used to generate the UUID
     * @return a UUID generated from the seed
     */
    @Contract("_ -> new")
    public static @NotNull UUID generateUUID(@NotNull String seed) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(seed.getBytes(StandardCharsets.UTF_8));
            byte[] hash = md.digest();
            long msb = 0;
            long lsb = 0;
            for (int i = 0; i < 8; i++)
                msb = (msb << 8) | (hash[i] & 0xff);
            for (int i = 8; i < 16; i++)
                lsb = (lsb << 8) | (hash[i] & 0xff);
            return new UUID(msb, lsb);
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Splits the given `value` of type BigInteger into a list of BigInteger values.
     * Each element in the list represents a portion of the original value that does not exceed Integer.MAX_VALUE.
     *
     * @param value The BigInteger value to split.
     * @return A list of BigInteger values representing portions of the original value.
     */
    public static @NotNull List<BigInteger> splitBigInteger(@NotNull BigInteger value) {
        List<BigInteger> splitValues = new ArrayList<>();

        if (value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
            splitValues.add(value);
        } else {
            BigInteger maxValue = BigInteger.valueOf(Integer.MAX_VALUE);
            BigInteger remainingValue = value;

            while (remainingValue.compareTo(maxValue) > 0) {
                splitValues.add(maxValue);
                remainingValue = remainingValue.subtract(maxValue);
            }

            if (remainingValue.compareTo(BigInteger.ZERO) > 0) {
                splitValues.addAll(splitBigInteger(remainingValue));
            }
        }

        return splitValues;
    }

    /**
     * Splits the given `value` of type BigInteger into a list of BigInteger values.
     * Each element in the list represents a portion of the original value that does not exceed the limit.
     *
     * @param value The BigInteger value to split.
     * @param limit The limit to split the value into.
     * @return A list of BigInteger values representing portions of the original value.
     */
    public static @NotNull List<BigInteger> splitBigIntegerTo(@NotNull BigInteger value, int limit) {
        List<BigInteger> splitValues = new ArrayList<>();

        if (value.compareTo(BigInteger.valueOf(limit)) <= 0) {
            splitValues.add(value);
        } else {
            BigInteger maxValue = BigInteger.valueOf(limit);
            BigInteger remainingValue = value;

            while (remainingValue.compareTo(maxValue) > 0) {
                splitValues.add(maxValue);
                remainingValue = remainingValue.subtract(maxValue);
            }

            if (remainingValue.compareTo(BigInteger.ZERO) > 0) {
                splitValues.addAll(splitBigIntegerTo(remainingValue, limit));
            }
        }

        return splitValues;
    }

    /**
     * Returns the floor of the given double value.
     *
     * @param num The double value to get the floor of.
     * @return The floor of the given double value.
     */
    public static int floor(double num) {
        int floor = (int) num;
        return (double) floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Decrypts a Base64-encoded string that was encrypted using AES encryption with a salt prepended.
     *
     * @param encryptedText the Base64-encoded string to decrypt (including the prepended 16-byte salt)
     * @param secret        the secret password used for key derivation
     * @return the decrypted plaintext string
     * @throws NoSuchAlgorithmException  if the PBKDF2WithHmacSHA256 algorithm is not available
     * @throws InvalidKeySpecException   if the key specification is invalid
     * @throws NoSuchPaddingException    if the padding mechanism is not available
     * @throws InvalidKeyException       if the key is invalid
     * @throws IllegalBlockSizeException if the encrypted data length is incorrect
     * @throws BadPaddingException       if the padding of the data is incorrect
     */
    @Contract("_, _ -> new")
    public static @NotNull String decrypt(String encryptedText, @NotNull String secret) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        byte[] salt = Arrays.copyOfRange(Base64.getDecoder().decode(encryptedText), 0, 16);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] encryptedBytes = Arrays.copyOfRange(Base64.getDecoder().decode(encryptedText), 16, Base64.getDecoder().decode(encryptedText).length);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    /**
     * Encrypts a string using AES encryption with a randomly generated salt and returns the result as a Base64-encoded string.
     * The resulting string includes the salt prepended to the encrypted data.
     *
     * @param plaintext the plaintext string to encrypt
     * @param secret    the secret password used for key derivation
     * @return the Base64-encoded string containing the salt and encrypted data
     * @throws NoSuchAlgorithmException  if the PBKDF2WithHmacSHA256 algorithm is not available
     * @throws InvalidKeySpecException   if the key specification is invalid
     * @throws NoSuchPaddingException    if the padding mechanism is not available
     * @throws InvalidKeyException       if the key is invalid
     * @throws IllegalBlockSizeException if the data size is invalid
     * @throws BadPaddingException       if the data is improperly padded
     */
    public static @NotNull String encrypt(@NotNull String plaintext, @NotNull String secret) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[16 + encryptedBytes.length];
        System.arraycopy(salt, 0, combined, 0, 16);
        System.arraycopy(encryptedBytes, 0, combined, 16, encryptedBytes.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    public static final class Assertions {

        /**
         * Throw IllegalArgumentException if the value is null.
         *
         * @param name  the parameter name
         * @param value the value that should not be null
         * @param <T>   the value type
         * @return the value
         * @throws IllegalArgumentException if value is null
         */
        @Contract(value = "_, null -> fail; _, !null -> param2", pure = true)
        public static <T> @NotNull T notNull(final String name, final T value) {
            if (value == null) {
                throw new IllegalArgumentException(name + " can not be null");
            }
            return value;
        }

        /**
         * Throw IllegalStateException if the condition if false.
         *
         * @param name      the name of the state that is being checked
         * @param condition the condition about the parameter to check
         * @throws IllegalStateException if the condition is false
         */
        public static void isTrue(final String name, final boolean condition) {
            if (!condition) {
                throw new IllegalStateException("state should be: " + name);
            }
        }

        /**
         * Throw IllegalArgumentException if the condition if false.
         *
         * @param name      the name of the state that is being checked
         * @param condition the condition about the parameter to check
         * @throws IllegalArgumentException if the condition is false
         */
        public static void isTrueArgument(final String name, final boolean condition) {
            if (!condition) {
                throw new IllegalArgumentException("state should be: " + name);
            }
        }

        /**
         * Throw IllegalArgumentException if the condition if false, otherwise return the value.  This is useful when arguments must be checked
         * within an expression, as when using {@code this} to call another constructor, which must be the first line of the calling
         * constructor.
         *
         * @param <T>       the value type
         * @param name      the name of the state that is being checked
         * @param value     the value of the argument
         * @param condition the condition about the parameter to check
         * @return the value
         * @throws IllegalArgumentException if the condition is false
         */
        public static <T> T isTrueArgument(final String name, final T value, final boolean condition) {
            if (!condition) {
                throw new IllegalArgumentException("state should be: " + name);
            }
            return value;
        }

        /**
         * Cast an object to the given class and return it, or throw IllegalArgumentException if it's not assignable to that class.
         *
         * @param clazz        the class to cast to
         * @param value        the value to cast
         * @param errorMessage the error message to include in the exception
         * @param <T>          the Class type
         * @return value cast to clazz
         * @throws IllegalArgumentException if value is not assignable to clazz
         */
        public static <T> @NotNull T convertToType(final @NotNull Class<T> clazz, final @NotNull Object value, final String errorMessage) {
            if (!clazz.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException(errorMessage);
            }
            return (T) value;
        }

        public static void notEmpty(Object object, String message) {
            if (object instanceof Collection<?>) {
                if (((Collection<?>) object).isEmpty()) {
                    throw new IllegalArgumentException(message);
                }
            } else if (object instanceof Map<?, ?>) {
                if (((Map<?, ?>) object).isEmpty()) {
                    throw new IllegalArgumentException(message);
                }
            } else if (object instanceof String) {
                if (((String) object).isEmpty()) {
                    throw new IllegalArgumentException(message);
                }
            } else
                throw new IllegalArgumentException("Object must be a Collection, Map or String");
        }

        private Assertions() {
        }
    }

    public static class Reflection {

        private static volatile Object theUnsafe;
        private static final ObjectMap<Class<?>, Class<?>> classClassMap = new HashObjectMap<>();

        static {
            try {
                synchronized (Reflection.class) {
                    if (theUnsafe == null) {
                        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                        Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
                        theUnsafeField.setAccessible(true);
                        theUnsafe = theUnsafeField.get(null);
                    }
                    classClassMap
                            .append(Integer.class, Integer.TYPE)
                            .append(Long.class, Long.TYPE)
                            .append(Double.class, Double.TYPE)
                            .append(Float.class, Float.TYPE)
                            .append(Boolean.class, Boolean.TYPE)
                            .append(Character.class, Character.TYPE)
                            .append(Byte.class, Byte.TYPE)
                            .append(Short.class, Short.TYPE);
                }
            } catch (Exception e) {
                theUnsafe = null;
            }
        }

        public static boolean isUnsafeAvailable() {
            return theUnsafe != null;
        }

        /**
         * Returns the {@link Class} object associated with the class or
         * interface with the given string name, using the given class loader.
         * Given the fully qualified name for a class or interface (in the same
         * format returned by {@link Class#getName}) this method attempts to
         * locate, load, and link the class or interface.  The specified class
         * loader is used to load the class or interface.  If the parameter
         * {@code loader} is null, the class is loaded through the bootstrap
         * class loader.  The class is initialized only if it has
         * not been initialized earlier.
         *
         * @param name   fully qualified name of the desired class
         * @param loader class loader from which the class must be loaded
         * @return class object representing the desired class
         * @throws LinkageError                if the linkage fails
         * @throws ExceptionInInitializerError if the initialization provoked
         *                                     by this method fails
         * @throws ReflectionException         if the class cannot be located by
         *                                     the specified class loader
         * @see Class#forName(String)
         * @see ClassLoader
         */
        public static @NotNull Class<?> getClass(String name, ClassLoader loader) throws ReflectionException {
            try {
                return Class.forName(name, true, loader);
            } catch (ClassNotFoundException exception) {
                throw new ReflectionException("Class not found " + name, exception);
            }
        }

        /**
         * Returns the {@link Class} object associated with the class or
         * interface with the given string name, using the given class loader.
         * Given the fully qualified name for a class or interface (in the same
         * format returned by {@link Class#getName}) this method attempts to
         * locate, load, and link the class or interface.  The specified class
         * loader is used to load the class or interface.  If the parameter
         * {@code loader} is null, the class is loaded through the bootstrap
         * class loader.  The class is initialized only if it has
         * not been initialized earlier.
         *
         * @param name   fully qualified name of the desired class
         * @param loader class loader from which the class must be loaded
         * @return class object representing the desired class,
         * If a {@link ClassNotFoundException} occurs the return value is {@link Optional#empty()}
         * @see Class#forName(String)
         * @see ClassLoader
         */
        public static Optional<Class<?>> optionalClass(String name, ClassLoader loader) {
            try {
                return Optional.of(Class.forName(name, true, loader));
            } catch (ClassNotFoundException e) {
                return Optional.empty();
            }
        }

        public static @NotNull Object enumValueOf(@NotNull Class<?> enumClass, String ordinal) {
            return Enum.valueOf(enumClass.asSubclass(Enum.class), ordinal);
        }

        public static Object enumValueOf(Class<?> enumClass, String constant, int fallbackOrdinal) throws NotFoundException {
            try {
                return enumValueOf(enumClass, constant);
            } catch (IllegalArgumentException e) {
                Object[] constants = enumClass.getEnumConstants();
                if (constants.length > fallbackOrdinal) {
                    return constants[fallbackOrdinal];
                }
                throw new NotFoundException("Enum constant not found " + constant, e);
            }
        }

        public static @NotNull Enum<?> getEnum(@NotNull Class<?> clazz, String constant) throws NotFoundException {
            Enum<?>[] enumConstants = (Enum<?>[]) clazz.getEnumConstants();

            for (Enum<?> e : enumConstants)
                if (e.name().equalsIgnoreCase(constant))
                    return e;

            throw new NotFoundException("Enum constant not found " + constant);
        }

        public static Enum<?> getEnum(@NotNull Class<?> clazz, int ordinal) throws NotFoundException {
            try {
                return (Enum<?>) clazz.getEnumConstants()[ordinal];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NotFoundException("Enum constant not found " + ordinal);
            }
        }

        public static @NotNull Enum<?> getEnum(Class<?> clazz, String enumName, String constant) throws NotFoundException, ReflectionException {
            return getEnum(getSubClass(clazz, enumName), constant);
        }

        public static Enum<?> getEnum(Class<?> clazz, String enumName, int ordinal) throws NotFoundException, ReflectionException {
            return getEnum(getSubClass(clazz, enumName), ordinal);
        }

        public static @NotNull Class<?> getSubClass(@NotNull Class<?> clazz, String className) throws ReflectionException {
            for (Class<?> subClass : clazz.getDeclaredClasses()) {
                if (subClass.getSimpleName().equals(className))
                    return subClass;
            }

            for (Class<?> subClass : clazz.getClasses()) {
                if (subClass.getSimpleName().equals(className))
                    return subClass;
            }

            throw new ReflectionException("Sub class " + className + " of " + clazz.getSimpleName() + " not found!");
        }

        public static @NotNull Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initArgs) throws ReflectionException {
            try {
                return getConstructor(clazz, args).newInstance(initArgs);
            } catch (Exception e) {
                throw new ReflectionException(e);
            }
        }

        public static @NotNull Object invokeConstructor(Class<?> clazz, Object... initArgs) throws ReflectionException {
            try {
                return getConstructorByArgs(clazz, initArgs).newInstance(initArgs);
            } catch (Exception e) {
                throw new ReflectionException(e);
            }
        }

        public static @NotNull Constructor<?> getConstructor(@NotNull Class<?> clazz, Class<?>... args) throws NoSuchMethodException {
            Constructor<?> c = clazz.getConstructor(args);
            c.setAccessible(true);

            return c;
        }

        public static @NotNull Constructor<?> getConstructorByArgs(@NotNull Class<?> clazz, Object... args) throws ReflectionException {
            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (constructor.getParameterTypes().length != args.length)
                    continue;

                int i = 0;
                for (Class<?> parameter : constructor.getParameterTypes()) {
                    if (!isAssignable(parameter, args[i]))
                        break;

                    i++;
                }

                if (i == args.length)
                    return constructor;
            }

            throw new ReflectionException("Could not find constructor with args " + Arrays.stream(args).map(Object::getClass).map(Class::getSimpleName).collect(Collectors.joining(", ")) + " in " + clazz.getSimpleName());
        }

        public static boolean isAssignable(Class<?> clazz, Object obj) {
            clazz = convertToPrimitive(clazz);

            return clazz.isInstance(obj) || clazz == convertToPrimitive(obj.getClass());
        }

        public static Class<?> convertToPrimitive(Class<?> clazz) {
            return classClassMap.getOrDefault(clazz, clazz);
        }

        public static Object getFieldByType(Object obj, String typeName) throws ReflectionException {
            return getFieldByType(obj, obj.getClass(), typeName);
        }

        public static Object getFieldByType(Object obj, Class<?> superClass, String typeName) throws ReflectionException {
            return getFieldByTypeList(obj, superClass, typeName).get(0);
        }

        public static @NotNull List<Object> getFieldByTypeList(Object obj, String typeName) throws ReflectionException {
            return getFieldByTypeList(obj, obj.getClass(), typeName);
        }

        public static @NotNull List<Object> getFieldByTypeList(Object obj, Class<?> superClass, String typeName) throws ReflectionException {
            List<Object> fields = new ArrayList<>();

            try {
                for (Field f : superClass.getDeclaredFields()) {
                    if (f.getType().getSimpleName().equalsIgnoreCase(typeName)) {
                        f.setAccessible(true);

                        fields.add(f.get(obj));
                    }
                }

                if (superClass.getSuperclass() != null) {
                    fields.addAll(getFieldByTypeList(obj, superClass.getSuperclass(), typeName));
                }

                if (fields.isEmpty() && obj.getClass() == superClass) {
                    throw new ReflectionException("Could not find field of type " + typeName + " in " + obj.getClass().getSimpleName());
                } else {
                    return fields;
                }
            } catch (Exception e) {
                throw new ReflectionException(e);
            }
        }

        /**
         * Returns the inner-{@link Class} object associated with the class or
         * interface with the given parent class, using the given class predicate.
         *
         * @param parentClass    the parent class
         * @param classPredicate the class predicate to test for the inner class
         * @return class object representing the desired inner-class,
         * @throws ReflectionException if the inner-class does not exist
         */
        static Class<?> innerClass(@NotNull Class<?> parentClass, Predicate<Class<?>> classPredicate) throws ReflectionException {
            for (Class<?> innerClass : parentClass.getDeclaredClasses()) {
                if (classPredicate.test(innerClass)) {
                    return innerClass;
                }
            }
            throw new ReflectionException("No class in " + parentClass.getCanonicalName() + " matches the predicate.");
        }

        public static @NotNull Utils.Reflection.Constructor0 findConstructor(Class<?> clazz, MethodHandles.@NotNull Lookup lookup) throws NoSuchMethodException, IllegalAccessException {
            if (isUnsafeAvailable()) {
                MethodType allocateMethodType = MethodType.methodType(Object.class, Class.class);
                MethodHandle allocateMethod = lookup.findVirtual(theUnsafe.getClass(), "allocateInstance", allocateMethodType);
                return () -> allocateMethod.invoke(theUnsafe, clazz);
            }
            try {
                MethodHandle constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class));
                return constructor::invoke;
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Returns the value of the field represented by this {@code Field}, on
         * the specified object. The value is automatically wrapped in an
         * object if it has a primitive type.
         *
         * <p>The underlying field's value is obtained as follows:
         *
         * <p>If the underlying field is a static field, the {@code object} argument
         * is ignored; it may be null.
         *
         * <p>Otherwise, the underlying field is an instance field.  If the
         * specified {@code object} argument is null, the method throws a
         * {@code NullPointerException}. If the specified object is not an
         * instance of the class or interface declaring the underlying
         * field, the method throws an {@code IllegalArgumentException}.
         *
         * <p>If this {@code Field} object is enforcing Java language access control, and
         * the underlying field is inaccessible, the method throws an
         * {@code IllegalAccessException}.
         * If the underlying field is static, the class that declared the
         * field is initialized if it has not already been initialized.
         *
         * <p>Otherwise, the value is retrieved from the underlying instance
         * or static field.  If the field has a primitive type, the value
         * is wrapped in an object before being returned, otherwise it is
         * returned as is.
         *
         * <p>If the field is hidden in the type of {@code object},
         * the field's value is obtained according to the preceding rules.
         *
         * @param clazz  class that contains the field
         * @param object object from which the represented field's value is
         *               to be extracted
         * @param name   field name
         * @return the value of the represented field in object
         * {@code object}; primitive values are wrapped in an appropriate
         * object before being returned
         * @throws IllegalAccessException      if this {@code Field} object
         *                                     is enforcing Java language access control and the underlying
         *                                     field is inaccessible.
         * @throws IllegalArgumentException    if the specified object is not an
         *                                     instance of the class or interface declaring the underlying
         *                                     field (or a subclass or implementor thereof).
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the field is an instance field.
         * @throws ExceptionInInitializerError if the initialization provoked
         *                                     by this method fails.
         */
        public static Object fetchDeclaredField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            if (isUnsafeAvailable() & object != null) {
                Field field = clazz.getDeclaredField(name);
                long offset = (long) fetchDeclaredMethodAndInvoke(theUnsafe.getClass(), "objectFieldOffset", theUnsafe, new Object[]{field}, new Class[]{Field.class});
                return fetchDeclaredMethodAndInvoke(theUnsafe.getClass(), "getObject", theUnsafe, new Object[]{object, offset}, new Class[]{Object.class, long.class});
            }
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Sets the field represented by this {@code Field} object on the
         * specified object argument to the specified new value. The new
         * value is automatically unwrapped if the underlying field has a
         * primitive type.
         *
         * <p>The operation proceeds as follows:
         *
         * <p>If the underlying field is static, the {@code object} argument is
         * ignored; it may be null.
         *
         * <p>Otherwise the underlying field is an instance field.  If the
         * specified object argument is null, the method throws a
         * {@code NullPointerException}.  If the specified object argument is not
         * an instance of the class or interface declaring the underlying
         * field, the method throws an {@code IllegalArgumentException}.
         *
         * <p>If this {@code Field} object is enforcing Java language access control, and
         * the underlying field is inaccessible, the method throws an
         * {@code IllegalAccessException}.
         *
         * <p>If the underlying field is final, the method throws an
         * {@code IllegalAccessException} unless {@code setAccessible(true)}
         * has succeeded for this {@code Field} object
         * and the field is non-static. Setting a final field in this way
         * is meaningful only during deserialization or reconstruction of
         * instances of classes with blank final fields, before they are
         * made available for access by other parts of a program. Use in
         * any other context may have unpredictable effects, including cases
         * in which other parts of a program continue to use the original
         * value of this field.
         *
         * <p>If the underlying field is of a primitive type, an unwrapping
         * conversion is attempted to convert the new value to a value of
         * a primitive type.  If this attempt fails, the method throws an
         * {@code IllegalArgumentException}.
         *
         * <p>If, after possible unwrapping, the new value cannot be
         * converted to the type of the underlying field by an identity or
         * widening conversion, the method throws an
         * {@code IllegalArgumentException}.
         *
         * <p>If the underlying field is static, the class that declared the
         * field is initialized if it has not already been initialized.
         *
         * <p>The field is set to the possibly unwrapped and widened new value.
         *
         * <p>If the field is hidden in the type of {@code object},
         * the field's value is set according to the preceding rules.
         *
         * @param clazz  class that contains the specific field
         * @param object object whose field should be modified
         * @param name   field name
         * @param value  new value for the field of {@code object}
         *               being modified
         * @throws IllegalAccessException      if this {@code Field} object
         *                                     is enforcing Java language access control and the underlying
         *                                     field is either inaccessible or final.
         * @throws IllegalArgumentException    if the specified object is not an
         *                                     instance of the class or interface declaring the underlying
         *                                     field (or a subclass or implementor thereof),
         *                                     or if an unwrapping conversion fails.
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the field is an instance field.
         * @throws ExceptionInInitializerError if the initialization provoked
         *                                     by this method fails.
         */
        public static void setDeclaredFieldValue(final Class<?> clazz, final Object object, final String name, Object value) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
            if (isUnsafeAvailable()) {
                Field field = clazz.getDeclaredField(name);
                long offset = (long) fetchDeclaredMethodAndInvoke(theUnsafe.getClass(), "objectFieldOffset", theUnsafe, new Object[]{field}, new Class[]{Field.class});
                fetchDeclaredMethodAndInvoke(theUnsafe.getClass(), "putObject", theUnsafe, new Object[]{object, offset, value}, new Class[]{Object.class, long.class, Object.class});
            }

            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                field.set(object, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Returns the value of the field represented by this {@code Field}, on
         * the specified object. The value is automatically wrapped in an
         * object if it has a primitive type.
         *
         * <p>The underlying field's value is obtained as follows:
         *
         * <p>If the underlying field is a static field, the {@code object} argument
         * is ignored; it may be null.
         *
         * <p>Otherwise, the underlying field is an instance field.  If the
         * specified {@code object} argument is null, the method throws a
         * {@code NullPointerException}. If the specified object is not an
         * instance of the class or interface declaring the underlying
         * field, the method throws an {@code IllegalArgumentException}.
         *
         * <p>If this {@code Field} object is enforcing Java language access control, and
         * the underlying field is inaccessible, the method throws an
         * {@code IllegalAccessException}.
         * If the underlying field is static, the class that declared the
         * field is initialized if it has not already been initialized.
         *
         * <p>Otherwise, the value is retrieved from the underlying instance
         * or static field.  If the field has a primitive type, the value
         * is wrapped in an object before being returned, otherwise it is
         * returned as is.
         *
         * <p>If the field is hidden in the type of {@code object},
         * the field's value is obtained according to the preceding rules.
         *
         * @param clazz  class that contains the field
         * @param object object from which the represented field's value is
         *               to be extracted
         * @param name   field name
         * @return the value of the represented field in object
         * {@code object}; primitive values are wrapped in an appropriate
         * object before being returned
         * @throws IllegalAccessException      if this {@code Field} object
         *                                     is enforcing Java language access control and the underlying
         *                                     field is inaccessible.
         * @throws IllegalArgumentException    if the specified object is not an
         *                                     instance of the class or interface declaring the underlying
         *                                     field (or a subclass or implementor thereof).
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the field is an instance field.
         * @throws ExceptionInInitializerError if the initialization provoked
         *                                     by this method fails.
         */
        public static Object fetchField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            if (isUnsafeAvailable() & object != null) {
                Field field = clazz.getField(name);
                long offset = (long) fetchDeclaredMethodAndInvoke(theUnsafe.getClass(), "objectFieldOffset", theUnsafe, new Object[]{field}, new Class[]{Field.class});
                return fetchDeclaredMethodAndInvoke(theUnsafe.getClass(), "getObject", theUnsafe, new Object[]{object, offset}, new Class[]{Object.class, long.class});
            }
            try {
                Field field = clazz.getField(name);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Sets the field represented by this {@code Field} object on the
         * specified object argument to the specified new value. The new
         * value is automatically unwrapped if the underlying field has a
         * primitive type.
         *
         * <p>The operation proceeds as follows:
         *
         * <p>If the underlying field is static, the {@code object} argument is
         * ignored; it may be null.
         *
         * <p>Otherwise the underlying field is an instance field.  If the
         * specified object argument is null, the method throws a
         * {@code NullPointerException}.  If the specified object argument is not
         * an instance of the class or interface declaring the underlying
         * field, the method throws an {@code IllegalArgumentException}.
         *
         * <p>If this {@code Field} object is enforcing Java language access control, and
         * the underlying field is inaccessible, the method throws an
         * {@code IllegalAccessException}.
         *
         * <p>If the underlying field is final, the method throws an
         * {@code IllegalAccessException} unless {@code setAccessible(true)}
         * has succeeded for this {@code Field} object
         * and the field is non-static. Setting a final field in this way
         * is meaningful only during deserialization or reconstruction of
         * instances of classes with blank final fields, before they are
         * made available for access by other parts of a program. Use in
         * any other context may have unpredictable effects, including cases
         * in which other parts of a program continue to use the original
         * value of this field.
         *
         * <p>If the underlying field is of a primitive type, an unwrapping
         * conversion is attempted to convert the new value to a value of
         * a primitive type.  If this attempt fails, the method throws an
         * {@code IllegalArgumentException}.
         *
         * <p>If, after possible unwrapping, the new value cannot be
         * converted to the type of the underlying field by an identity or
         * widening conversion, the method throws an
         * {@code IllegalArgumentException}.
         *
         * <p>If the underlying field is static, the class that declared the
         * field is initialized if it has not already been initialized.
         *
         * <p>The field is set to the possibly unwrapped and widened new value.
         *
         * <p>If the field is hidden in the type of {@code object},
         * the field's value is set according to the preceding rules.
         *
         * @param clazz  class that contains the specific field
         * @param object object whose field should be modified
         * @param name   field name
         * @param value  new value for the field of {@code object}
         *               being modified
         * @throws IllegalAccessException      if this {@code Field} object
         *                                     is enforcing Java language access control and the underlying
         *                                     field is either inaccessible or final.
         * @throws IllegalArgumentException    if the specified object is not an
         *                                     instance of the class or interface declaring the underlying
         *                                     field (or a subclass or implementor thereof),
         *                                     or if an unwrapping conversion fails.
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the field is an instance field.
         * @throws ExceptionInInitializerError if the initialization provoked
         *                                     by this method fails.
         */
        public static void setFieldValue(final Class<?> clazz, final Object object, final String name, Object value) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
            if (isUnsafeAvailable()) {
                Field field = clazz.getField(name);
                long offset = (long) fetchDeclaredMethodAndInvoke(theUnsafe.getClass(), "objectFieldOffset", theUnsafe, new Object[]{field}, new Class[]{Field.class});
                fetchDeclaredMethodAndInvoke(theUnsafe.getClass(), "putObject", theUnsafe, new Object[]{object, offset, value}, new Class[]{Object.class, long.class, Object.class});
            }

            try {
                Field field = clazz.getField(name);
                field.setAccessible(true);
                field.set(object, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Returns a {@code Method} object that reflects the specified
         * declared method of the class or interface represented by this
         * {@code Class} object. The {@code name} parameter is a
         * {@code String} that specifies the simple name of the desired
         * method, and the {@code parameterTypes} parameter is an array of
         * {@code Class} objects that identify the method's formal parameter
         * types, in declared order.  If more than one method with the same
         * parameter types is declared in a class, and one of these methods has a
         * return type that is more specific than any of the others, that method is
         * returned; otherwise one of the methods is chosen arbitrarily.  If the
         * name is "&lt;init&gt;"or "&lt;clinit&gt;" a {@code NoSuchMethodException}
         * is raised.
         *
         * <p> If this {@code Class} object represents an array type, then this
         * method does not find the {@code clone()} method.
         *
         * @param clazz          the class that contains the method
         * @param name           the name of the method
         * @param parameterTypes the parameter array
         * @return the {@code Method} object for the method of this class
         * matching the specified name and parameters
         * @throws NoSuchMethodException if a matching method is not found.
         * @throws NullPointerException  if {@code name} is {@code null}
         */
        public static @NotNull Method fetchDeclaredMethod(final @NotNull Class<?> clazz, final String name, Class<?>... parameterTypes) throws NoSuchMethodException {
            return clazz.getDeclaredMethod(name, parameterTypes);
        }

        /**
         * Check {@link Reflection#fetchDeclaredMethod(Class, String, Class...)} for
         * the fetch method.
         * Invokes the underlying method represented by this {@code Method}
         * object, on the specified object with the specified parameters.
         * Individual parameters are automatically unwrapped to match
         * primitive formal parameters, and both primitive and reference
         * parameters are subject to method invocation conversions as
         * necessary.
         *
         * <p>If the underlying method is static, then the specified {@code obj}
         * argument is ignored. It may be null.
         *
         * <p>If the number of formal parameters required by the underlying method is
         * 0, the supplied {@code args} array may be of length 0 or null.
         *
         * <p>If the underlying method is an instance method, it is invoked
         * using dynamic method lookup as documented in The Java Language
         * Specification, Second Edition, section 15.12.4.4; in particular,
         * overriding based on the runtime type of the target object will occur.
         *
         * <p>If the underlying method is static, the class that declared
         * the method is initialized if it has not already been initialized.
         *
         * <p>If the method completes normally, the value it returns is
         * returned to the caller of invoke; if the value has a primitive
         * type, it is first appropriately wrapped in an object. However,
         * if the value has the type of array of a primitive type, the
         * elements of the array are <i>not</i> wrapped in objects; in
         * other words, an array of primitive type is returned.  If the
         * underlying method return type is void, the invocation returns
         * null.
         *
         * @param clazz          the class that contains the method
         * @param name           the name of the method
         * @param parameterTypes the parameter array
         * @param obj            the object the underlying method is invoked from
         * @param arguments      the arguments used for the method call
         * @return the result of dispatching the method represented by
         * this object on {@code obj} with parameters
         * {@code args}
         * @throws IllegalAccessException      if this {@code Method} object
         *                                     is enforcing Java language access control and the underlying
         *                                     method is inaccessible.
         * @throws IllegalArgumentException    if the method is an
         *                                     instance method and the specified object argument
         *                                     is not an instance of the class or interface
         *                                     declaring the underlying method (or of a subclass
         *                                     or implementor thereof); if the number of actual
         *                                     and formal parameters differ; if an unwrapping
         *                                     conversion for primitive arguments fails; or if,
         *                                     after possible unwrapping, a parameter value
         *                                     cannot be converted to the corresponding formal
         *                                     parameter type by a method invocation conversion.
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the method is an instance method.
         * @throws ExceptionInInitializerError if the initialization
         *                                     provoked by this method fails.
         */
        public static Object fetchDeclaredMethodAndInvoke(final Class<?> clazz, final String name, Object obj, Object[] arguments, Class<?>[] parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Method method = fetchDeclaredMethod(clazz, name, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, arguments);
        }

        /**
         * Returns a {@code Method} object that reflects the specified
         * method of the class or interface represented by this
         * {@code Class} object. The {@code name} parameter is a
         * {@code String} that specifies the simple name of the desired
         * method, and the {@code parameterTypes} parameter is an array of
         * {@code Class} objects that identify the method's formal parameter
         * types, in declared order.  If more than one method with the same
         * parameter types is declared in a class, and one of these methods has a
         * return type that is more specific than any of the others, that method is
         * returned; otherwise one of the methods is chosen arbitrarily.  If the
         * name is "&lt;init&gt;"or "&lt;clinit&gt;" a {@code NoSuchMethodException}
         * is raised.
         *
         * <p> If this {@code Class} object represents an array type, then this
         * method does not find the {@code clone()} method.
         *
         * @param clazz          the class that contains the method
         * @param name           the name of the method
         * @param parameterTypes the parameter array
         * @return the {@code Method} object for the method of this class
         * matching the specified name and parameters
         * @throws NoSuchMethodException if a matching method is not found.
         * @throws NullPointerException  if {@code name} is {@code null}
         */
        public static @NotNull Method fetchMethod(final @NotNull Class<?> clazz, final String name, Class<?>... parameterTypes) throws NoSuchMethodException {
            return clazz.getMethod(name, parameterTypes);
        }

        /**
         * Check {@link Reflection#fetchMethod(Class, String, Class...)} for
         * the fetch method.
         * Invokes the underlying method represented by this {@code Method}
         * object, on the specified object with the specified parameters.
         * Individual parameters are automatically unwrapped to match
         * primitive formal parameters, and both primitive and reference
         * parameters are subject to method invocation conversions as
         * necessary.
         *
         * <p>If the underlying method is static, then the specified {@code obj}
         * argument is ignored. It may be null.
         *
         * <p>If the number of formal parameters required by the underlying method is
         * 0, the supplied {@code args} array may be of length 0 or null.
         *
         * <p>If the underlying method is an instance method, it is invoked
         * using dynamic method lookup as documented in The Java Language
         * Specification, Second Edition, section 15.12.4.4; in particular,
         * overriding based on the runtime type of the target object will occur.
         *
         * <p>If the underlying method is static, the class that declared
         * the method is initialized if it has not already been initialized.
         *
         * <p>If the method completes normally, the value it returns is
         * returned to the caller of invoke; if the value has a primitive
         * type, it is first appropriately wrapped in an object. However,
         * if the value has the type of array of a primitive type, the
         * elements of the array are <i>not</i> wrapped in objects; in
         * other words, an array of primitive type is returned.  If the
         * underlying method return type is void, the invocation returns
         * null.
         *
         * @param clazz          the class that contains the method
         * @param name           the name of the method
         * @param parameterTypes the parameter array
         * @param obj            the object the underlying method is invoked from
         * @param arguments      the arguments used for the method call
         * @return the result of dispatching the method represented by
         * this object on {@code obj} with parameters
         * {@code args}
         * @throws IllegalAccessException      if this {@code Method} object
         *                                     is enforcing Java language access control and the underlying
         *                                     method is inaccessible.
         * @throws IllegalArgumentException    if the method is an
         *                                     instance method and the specified object argument
         *                                     is not an instance of the class or interface
         *                                     declaring the underlying method (or of a subclass
         *                                     or implementor thereof); if the number of actual
         *                                     and formal parameters differ; if an unwrapping
         *                                     conversion for primitive arguments fails; or if,
         *                                     after possible unwrapping, a parameter value
         *                                     cannot be converted to the corresponding formal
         *                                     parameter type by a method invocation conversion.
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the method is an instance method.
         * @throws ExceptionInInitializerError if the initialization
         *                                     provoked by this method fails.
         */
        public static Object fetchMethodAndInvoke(final Class<?> clazz, final String name, Object obj, Object[] arguments, Class<?>[] parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            Method method = fetchMethod(clazz, name, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, arguments);
        }

        @FunctionalInterface
        interface Constructor0 {
            Object invoke() throws Throwable;
        }
    }

    public static class Cooldown {
        private static final ObjectMap<String, Cooldown> cooldownManagerObjectMap = ObjectMap.newHashObjectMap();
        private long start;
        private final int timeInSeconds;
        private final UUID id;
        private final String cooldownName;

        public Cooldown(UUID id, String cooldownName, int timeInSeconds) {
            this.id = id;
            this.cooldownName = cooldownName;
            this.timeInSeconds = timeInSeconds;
        }

        public static boolean isInCooldown(UUID id, String cooldownName) {
            if (Cooldown.getTimeLeft(id, cooldownName) >= 1) {
                return true;
            }
            Cooldown.stop(id, cooldownName);
            return false;
        }

        private static void stop(UUID id, String cooldownName) {
            cooldownManagerObjectMap.remove(id + cooldownName);
        }

        private static Cooldown getCooldown(@NotNull UUID id, String cooldownName) {
            return cooldownManagerObjectMap.get(id + cooldownName);
        }

        public static int getTimeLeft(UUID id, String cooldownName) {
            Cooldown cooldown = Cooldown.getCooldown(id, cooldownName);
            int f = -1;
            if (cooldown != null) {
                long now = System.currentTimeMillis();
                long cooldownTime = cooldown.start;
                int r = (int) (now - cooldownTime) / 1000;
                f = (r - cooldown.timeInSeconds) * -1;
            }
            return f;
        }

        public void start() {
            this.start = System.currentTimeMillis();
            cooldownManagerObjectMap.put(this.id.toString() + this.cooldownName, this);
        }

        public static @NotNull String getTimeLeft(int secondTime) {
            TimeZone tz = Calendar.getInstance().getTimeZone();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            df.setTimeZone(tz);
            return df.format(new Date(secondTime * 1000L));
        }

        public static ObjectMap<String, Cooldown> getCooldowns() {
            return cooldownManagerObjectMap;
        }

        public static ObjectMap<String, Cooldown> appendToCooldowns(ObjectMap<String, Cooldown> cooldownObjectMap) {
            return cooldownManagerObjectMap.append(cooldownObjectMap);
        }

    }

    public static abstract class Callback<T> {

        public abstract T onSuccess();

        public abstract T onFailure();

        public T onFailure(Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }


    /**
     * The Request class provides methods for making HTTPS requests.
     */
    public static class Request {
        /**
         * The address of the HTTP(S) endpoint.
         */
        private String address;

        /**
         * The bytes returned from the HTTP(S) response.
         */
        private byte[] bytes;

        /**
         * The HTTP status code returned by the endpoint.
         */
        private int httpCode;

        /**
         * The HTTPS URL connection used to make the request.
         */
        private HttpsURLConnection httpsURLConnection;

        /**
         * Sets the HTTP request method to "GET".
         *
         * @return This Request object.
         * @throws ProtocolException If the request method could not be set.
         */
        public Request getRequest() throws ProtocolException {
            this.httpsURLConnection.setRequestMethod("GET");
            this.httpsURLConnection.setRequestProperty("User-Agent", "SkinOverlay");
            return this;
        }

        /**
         * Sets the HTTP request method to "POST" and sets several request properties.
         *
         * @return This Request object.
         * @throws ProtocolException If the request method could not be set.
         */
        public Request postRequest() throws ProtocolException {
            this.httpsURLConnection.setRequestMethod("POST");
            this.httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
            this.httpsURLConnection.setRequestProperty("Cache-Control", "no-cache");
            this.httpsURLConnection.setRequestProperty("User-Agent", "SkinOverlay");
            this.httpsURLConnection.setDoOutput(true);
            return this;
        }

        /**
         * Opens a connection to the specified HTTP(S) endpoint.
         *
         * @param address The address of the endpoint.
         * @return This Request object.
         * @throws IOException If a connection to the endpoint could not be established.
         */
        public Request openConnection(String address) throws IOException {
            this.address = address;
            final URL url = new URL(address);
            this.httpsURLConnection = (HttpsURLConnection) url.openConnection();
            return this;
        }

        /**
         * Sets a request property to the specified key-value pair.
         *
         * @param key   The key for the property.
         * @param value The value for the property.
         * @return This Request object.
         */
        public Request setRequestProperty(String key, String value) {
            this.httpsURLConnection.setRequestProperty(key, value);
            return this;
        }

        /**
         * Writes one or more strings to the request output stream.
         *
         * @param data The strings to write.
         * @return This Request object.
         * @throws IOException If an I/O error occurs.
         */
        public Request writeToOutputStream(final String @NotNull ... data) throws IOException {
            for (final String str : data) {
                this.httpsURLConnection.getOutputStream().write(str.getBytes());
            }
            return this;
        }

        /**
         * Writes one or more byte arrays to the request output stream.
         *
         * @param data The byte arrays to write.
         * @return This Request object.
         * @throws IOException If an I/O error occurs.
         */
        public Request writeToOutputStream(final byte @NotNull []... data) throws IOException {
            for (final byte[] bytes : data) {
                this.httpsURLConnection.getOutputStream().write(bytes);
            }
            return this;
        }

        /**
         * Closes the request output stream.
         *
         * @return This Request object.
         * @throws IOException If an I/O error occurs.
         */
        public Request closeOutputStream() throws IOException {
            this.httpsURLConnection.getOutputStream().close();
            return this;
        }

        /**
         * Finalizes the request by getting the HTTP response code and reading the response body bytes.
         *
         * @return The updated Request object.
         * @throws IOException If an I/O error occurs while finalizing the request.
         */
        public Request finalizeRequest() throws IOException {
            this.httpCode = this.httpsURLConnection.getResponseCode();
            this.bytes = this.httpsURLConnection.getInputStream().readAllBytes();
            return this;
        }

        /**
         * Gets the HTTP response code of the request.
         *
         * @return The HTTP response code.
         */
        public int getHttpCode() {
            return this.httpCode;
        }

        /**
         * Gets the bytes of the response body of the request.
         *
         * @return The response body bytes.
         */
        public byte[] getBytes() {
            return this.bytes;
        }

        /**
         * Gets the address of the request.
         *
         * @return The request address.
         */
        public String getAddress() {
            return this.address;
        }

        /**
         * Gets the underlying HTTPS connection object.
         *
         * @return The HTTPS connection object.
         */
        public HttpsURLConnection getHttpsURLConnection() {
            return httpsURLConnection;
        }
    }
}
