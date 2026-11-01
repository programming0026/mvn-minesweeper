package com.juniverse.game.util;

import java.time.Year;

public final class DateUtil {

    private DateUtil() {
    }


    public static int getYear() {
        return Year.now().getValue();
    }
}
