package ru.practicum.shareit.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PaginationUtil {

    public static int positionToPage(int from, int size) {
        return from / size;
    }
}
