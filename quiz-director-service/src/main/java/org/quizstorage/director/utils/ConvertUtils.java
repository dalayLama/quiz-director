package org.quizstorage.director.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@UtilityClass
@Slf4j
public class ConvertUtils {

    public static <T, V> V convert(T object, Function<T, V> convertFunction, V errorValue) {
        try {
            return convertFunction.apply(object);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return errorValue;
        }
    }

    public static <T, V> V convert(T object, Function<T, V> convertFunction) {
        return convert(object, convertFunction, null);
    }



}
