package com.juniverse.game.util;

import java.time.Year;

/**
 * The Class DateUtil.
 */
public final class DateUtil {

    /**
     * Instantiates a new date util.
     */
    private DateUtil() {
    }


    /**
     * Gets the year.
     *
     * @return the year
     */
    public static int getYear() {
        return Year.now().getValue();
    }
}
