package ru.practicum.shareit.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class Constants {
    public static final String USER_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "10";
}
