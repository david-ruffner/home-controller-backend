package com.davidruffner.homecontrollerbackend.services;

import com.davidruffner.homecontrollerbackend.entities.RGB;
import org.springframework.stereotype.Service;

@Service
public class ColorConversionService {

    public record xyToXYZDTO(
        Double X,
        Double Y,
        Double Z
    ) {}

    private xyToXYZDTO xyToXYZ(double x, double y, double Y) {
        if (y == 0) {
            return new xyToXYZDTO(0d, 0d, 0d);
        }

        return new xyToXYZDTO(
            (x * Y) / y,
            Y,
            ((1 - x - y) * Y) / y
        );
    }

    private RGB xyToLinearRgb(double X, double Y, double Z) {
        return new RGB(
            3.2406 * X - 1.5372 * Y - 0.4986 * Z,
            -0.9689 * X + 1.8758 * Y + 0.0415 * Z,
            0.0557 * X - 0.2040 * Y + 1.0570 * Z
        );
    }

    private Double gammaCorrect(Double val) {
        return val <= 0.0031308
            ? 12.92 * val
            : 1.055 * Math.pow(val, 1 / 2.4) - 0.055;
    }

    private RGB toRGB255(RGB rgb) {
        return new RGB(
            (double) Math.round(Math.min(Math.max(gammaCorrect(rgb.getRed()), 0), 1) * 255),
            (double) Math.round(Math.min(Math.max(gammaCorrect(rgb.getGreen()), 0), 1) * 255),
            (double) Math.round(Math.min(Math.max(gammaCorrect(rgb.getBlue()), 0), 1) * 255)
        );
    }

    public record RgbToXyDto(Double x, Double y) {}

    /**
     * Inverse gamma correction for sRGB.
     * Converts gamma-encoded sRGB (0..1) to linear RGB.
     */
    private Double inverseGammaCorrect(Double val) {
        if (val == null) return 0d;

        double v = val;
        return v <= 0.04045
            ? (v / 12.92)
            : Math.pow((v + 0.055) / 1.055, 2.4);
    }

    // Overload so you can call inverseGammaCorrect with primitives too
    private double inverseGammaCorrect(double val) {
        return val <= 0.04045
            ? (val / 12.92)
            : Math.pow((val + 0.055) / 1.055, 2.4);
    }

    /**
     * Converts an RGB color (0–255) back to CIE xy (D65).
     * Note: brightness/luminance is not needed for xy (only affects Y).
     */
    public RgbToXyDto rgbToXy(RGB rgb) {
        if (rgb == null) return new RgbToXyDto(0d, 0d);

        // 1) Normalize to 0..1
        double r = (rgb.getRed() == null ? 0d : rgb.getRed()) / 255.0;
        double g = (rgb.getGreen() == null ? 0d : rgb.getGreen()) / 255.0;
        double b = (rgb.getBlue() == null ? 0d : rgb.getBlue()) / 255.0;

        // 2) sRGB -> linear
        r = inverseGammaCorrect(r);
        g = inverseGammaCorrect(g);
        b = inverseGammaCorrect(b);

        // 3) Linear RGB -> XYZ (D65)
        double X = r * 0.4124 + g * 0.3576 + b * 0.1805;
        double Y = r * 0.2126 + g * 0.7152 + b * 0.0722;
        double Z = r * 0.0193 + g * 0.1192 + b * 0.9505;

        // 4) XYZ -> xy
        double sum = X + Y + Z;
        if (sum == 0.0) return new RgbToXyDto(0d, 0d);

        return new RgbToXyDto(X / sum, Y / sum);
    }

    public RGB xyToRGB(Double x, Double y, Double Y) {
        xyToXYZDTO xyToXyz = xyToXYZ(x, y, Y);
        RGB linearRgb = xyToLinearRgb(xyToXyz.X, xyToXyz.Y, xyToXyz.Z);
        return toRGB255(linearRgb);
    }
}
