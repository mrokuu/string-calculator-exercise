package org.example;

class StringCalculator {

    public int add(String input) {
        if(input.isEmpty()){
            return 0;
        }
        return Integer.parseInt(input.trim());
    }
}
