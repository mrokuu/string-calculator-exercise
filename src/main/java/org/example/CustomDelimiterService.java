package org.example;

class CustomDelimiterService {

    private static final String DELIMITER_PREFIX = "//";
    private static final String DELIMITER_SUFFIX = "\n";

    public static boolean hasCustomDelimiter(String input) {
        return input.startsWith(DELIMITER_PREFIX) && input.contains(DELIMITER_SUFFIX);
    }

    public static CalculationData parseCustomDelimiter(String input) {
        int newlineIndex = input.indexOf(DELIMITER_SUFFIX);

        if (newlineIndex == -1) {
            throw new InputException("Missing newline after delimiter declaration");
        }

        String delimiter = extractDelimiter(input, newlineIndex);
        String numbersInput = extractNumbersInput(input, newlineIndex);

        return new CalculationData(numbersInput, delimiter);
    }

    private static String extractDelimiter(String input, int newlineIndex) {
        String delimiter = input.substring(DELIMITER_PREFIX.length(), newlineIndex);

        if (delimiter.isEmpty()) {
            throw new InputException("Delimiter cannot be empty");
        }

        return delimiter;
    }

    private static String extractNumbersInput(String input, int newlineIndex) {
        return input.substring(newlineIndex + DELIMITER_SUFFIX.length());
    }
}
