package org.quizstorage.director.mappers;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface DateTimeMapper {

    default Instant toInstant(Date date) {
        return Optional.ofNullable(date)
                .map(Date::toInstant)
                .orElse(null);
    }

}
