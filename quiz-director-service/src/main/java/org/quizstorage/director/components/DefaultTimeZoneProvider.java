package org.quizstorage.director.components;

import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class DefaultTimeZoneProvider implements TimeZoneProvider {
    @Override
    public ZoneId getZoneId() {
        return ZoneId.systemDefault();
    }

}
