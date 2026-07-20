package org.codeberg.chromatic.vignettetweaks.util;

import org.polyfrost.compose.render.PolyColor;

public record VignetteResult(PolyColor color, float strength) {
    public boolean isActive() {
        return strength > 0f;
    }
}
