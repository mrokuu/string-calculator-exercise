package org.example.calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class DelimiterParser {
    public static final String DEFAULT_DELIMITERS = ",|\n";
    public static final String COMMA = ",";
    public static final String NEWLINE = "\n";

    public static List<Integer> extractNumbers(ParsedData parsedData) {
        String[] tokens = splitByDelimiter(parsedData);

        return Arrays.stream(tokens)
                .filter(token -> !token.trim().isEmpty())
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

    private static String[] splitByDelimiter(ParsedData parsedData) {
        String delimiter = parsedData.delimiter();
        String input = parsedData.input();

        if (DEFAULT_DELIMITERS.equals(delimiter)) {
            return input.split(DEFAULT_DELIMITERS);
        } else {
            String escapedDelimiter = Pattern.quote(delimiter);
            String combinedDelimiters = escapedDelimiter + "|" + Pattern.quote(NEWLINE);
            return input.split(combinedDelimiters);
        }
    }
}
