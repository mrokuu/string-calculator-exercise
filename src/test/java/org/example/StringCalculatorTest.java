package org.example;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


class StringCalculatorTest {

    @Test
    void add_shouldReturnZero_whenInputIsEmpty() {
        //give
        StringCalculator calculator = new StringCalculator();

        //when
        var result = calculator.add(StringUtils.EMPTY);

        //then
        assertThat(result).isZero();
    }

    @Test
    void add_shouldReturnSameNumber_whenInputIsSingleNumber(){
        //give
        String value = "34";
        StringCalculator calculator = new StringCalculator();

        //when
        var result = calculator.add(value);

        //then
        assertThat(result).isEqualTo(Integer.parseInt(value));

    }

    @Test
    void add_shouldReturnSum_whenInputContainsTwoNumbersSeparatedByComma(){
        //given
        String value = "11,22";
        StringCalculator calculator = new StringCalculator();

        //when
        var result = calculator.add(value);

        //then
        assertThat(result).isEqualTo(33);
    }
}