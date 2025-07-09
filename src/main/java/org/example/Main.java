package org.example;

import org.example.calculator.StringCalculatorFacade;

public class Main {
    public static void main(String[] args) {
        StringCalculatorFacade facade = new StringCalculatorFacade();


        System.out.println(facade.add("1,2,3"));
    }
}