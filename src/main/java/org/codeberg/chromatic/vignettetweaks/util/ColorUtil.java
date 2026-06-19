package org.codeberg.chromatic.vignettetweaks.util;


public class ColorUtil {
    public static float invert(float base, float scale, float original) {
        if (scale <= 0f) return original;
        return (1f - base) * scale;
    }

    public static float scale(float percent, float threshold, float min, float max) {
        float t = 1f - (percent / threshold);
        return (min / 100f) + t * ((max - min) / 100f);
    }
}
