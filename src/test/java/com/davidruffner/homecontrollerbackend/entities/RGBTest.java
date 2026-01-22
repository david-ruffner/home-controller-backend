package com.davidruffner.homecontrollerbackend.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RGBTest {

    @Test
    public void testRGBToString() {
        RGB rgb = new RGB(35d, 56d, 112d);
        out.println(rgb.toString());
    }

    @Test
    public void testStringToRGB() {
        RGB rgb = new RGB(35d, 56d, 112d);
        String rgbString = "35.0,56.0,112.0";
        RGB actualRgb = new RGB(rgbString);

        assertTrue(rgb.equals(actualRgb));
    }

    @Test
    public void testAlphaStringRGB() {
        RGB rgb = new RGB(35d, 45d, 122d, 50d);
        String rgbString = rgb.toString();
        RGB actualRgb = new RGB(rgbString);

        assertTrue(rgb.equals(actualRgb));
    }
}
