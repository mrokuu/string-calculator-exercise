package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class DelimiterService {
    public static final String DEFAULT_DELIMITERS = ",|\n";
    public static final String COMMA = ",";
    public static final String NEWLINE = "\n";

    public static List<Integer> extractNumbers(CalculationData calculationData) {
        String[] tokens = splitByDelimiter(calculationData);

        return Arrays.stream(tokens)
                .filter(token -> !token.trim().isEmpty())
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

    private static String[] splitByDelimiter(CalculationData calculationData) {
        String delimiter = calculationData.delimiter();
        String input = calculationData.input();

        if (DEFAULT_DELIMITERS.equals(delimiter)) {
            return input.split(DEFAULT_DELIMITERS);
        } else {
            String escapedDelimiter = Pattern.quote(delimiter);
            String combinedDelimiters = escapedDelimiter + "|" + Pattern.quote(NEWLINE);
            return input.split(combinedDelimiters);
        }
    }
}
