package org.codeberg.chromatic.vignettetweaks.data;

import org.polyfrost.compose.render.PolyColor;

// I felt like an AI when writing this
// but ehhhh seemed like the easiest option
public record VignetteResult(PolyColor color, float strength) {
    public boolean isActive() {
        return color != null && strength > 0f;
    }
}
