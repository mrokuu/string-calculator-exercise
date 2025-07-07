package org.example;

class StringCalculator {

    public int add(String input) {
        if(input.isEmpty()){
            return 0;
        }

        if(input.contains(",")) {
            String[] parts = input.split(",");
            return Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
        }
        return Integer.parseInt(input.trim());
    }
}
