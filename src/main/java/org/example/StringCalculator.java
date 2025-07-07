package org.example;

class StringCalculator {
    private static final String DEFAULT_DELIMITER = ",";

    public static int add(String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }

        if (input.contains(",")) {
            String[] parts = input.split(DEFAULT_DELIMITER);
            var result = 0;
            for (var part : parts) {
                result += Integer.parseInt(part);
            }
            return result;
        }
        return Integer.parseInt(input.trim());
    }
}
