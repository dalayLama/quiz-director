package org.quizstorage.director.utils;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@UtilityClass
public class DateTimeUtil {

    public static OffsetDateTime toLocalDateTime(Date date) {
        return Optional.ofNullable(date)
                .map(d -> OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()))
                .orElse(null);
    }

}
