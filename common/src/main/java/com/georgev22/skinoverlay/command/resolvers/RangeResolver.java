package com.georgev22.skinoverlay.command.resolvers;

import com.georgev22.skinoverlay.command.CommandIssuer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves numeric ranges (integers or decimals) into lists of string values.
 * <p>
 * Supported formats:
 * <ul>
 *   <li><b>Integer range</b>: {@code 1-5} → {@code ["1", "2", "3", "4", "5"]}</li>
 *   <li><b>Decimal range</b>: {@code 1.1-2.0} → {@code ["1.1", "1.2", ..., "2.0"]}
 *       <br>(step auto-detected based on decimal places)</li>
 *   <li><b>Range with explicit step</b>: {@code 1-5:0.5} → {@code ["1.0", "1.5", "2.0", ..., "5.0"]}</li>
 *   <li><b>Negative numbers</b>: {@code -3--1} → {@code ["-3", "-2", "-1"]}</li>
 *   <li><b>Descending ranges</b>: {@code 5-1} → {@code ["5", "4", "3", "2", "1"]}</li>
 * </ul>
 * <p>
 * Notes:
 * <ul>
 *   <li>Decimals are handled with {@link BigDecimal} to avoid floating-point errors.</li>
 *   <li>Output is formatted to match the maximum decimal places of the inputs or the step.</li>
 *   <li>If no explicit step is provided, it defaults to {@code 1} for integers or
 *       {@code 0.1}, {@code 0.01}, etc. for decimals (depending on precision).</li>
 * </ul>
 */
public class RangeResolver implements ArgumentResolver {

    /**
     * Regex supporting:
     * <ul>
     *   <li>{@code start-end}</li>
     *   <li>{@code start-end:step}</li>
     *   <li>Optional +/- signs</li>
     * </ul>
     */
    private static final Pattern RANGE_PATTERN =
            Pattern.compile("\\s*([+-]?\\d+(?:\\.\\d+)?)\\s*-\\s*([+-]?\\d+(?:\\.\\d+)?)(?::([+-]?\\d+(?:\\.\\d+)?))?\\s*");

    /**
     * Resolves a range string (like "1-5", "1.1-2.0", "1-5:0.5") into a list of values.
     *
     * @param commandIssuer the command issuer (unused here, but required by interface)
     * @param args          the arguments, where the first element is expected to be a range expression
     * @return list of numbers as strings, or empty list if input is invalid
     */
    @Override
    public List<String> resolve(@NotNull CommandIssuer commandIssuer, String @NotNull ... args) {
        if (args.length == 0) return List.of();

        Matcher m = RANGE_PATTERN.matcher(args[0]);
        if (!m.matches()) return List.of();

        String left = m.group(1);
        String right = m.group(2);
        String stepStr = m.group(3);

        BigDecimal start = new BigDecimal(left);
        BigDecimal end = new BigDecimal(right);

        int decimals = Math.max(getDecimalPlaces(left), getDecimalPlaces(right));

        BigDecimal step;
        if (stepStr != null) {
            step = new BigDecimal(stepStr);
            decimals = Math.max(decimals, getDecimalPlaces(stepStr));
        } else {
            step = BigDecimal.ONE.movePointLeft(decimals);
        }

        if (step.compareTo(BigDecimal.ZERO) <= 0) return List.of();

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.ROOT);
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(symbols);
        df.setGroupingUsed(false);
        df.setMinimumFractionDigits(decimals);
        df.setMaximumFractionDigits(decimals);

        List<String> out = new ArrayList<>();

        if (start.compareTo(end) <= 0) {
            for (BigDecimal cur = start; cur.compareTo(end) <= 0; cur = cur.add(step)) {
                out.add(df.format(cur));
            }
        } else {
            for (BigDecimal cur = start; cur.compareTo(end) >= 0; cur = cur.subtract(step)) {
                out.add(df.format(cur));
            }
        }

        return out;
    }

    private int getDecimalPlaces(@NotNull String s) {
        int idx = s.indexOf('.');
        return idx < 0 ? 0 : s.length() - idx - 1;
    }
}
