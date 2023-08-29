package org.quizstorage.director.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.auth.headers")
@Getter
@Setter
public class AuthenticationHeadersProperties {

    private String idHeaderName;

    private String nameHeaderName;

    private String rolesHeaderName;

    public String webSocketTokenIdHeaderName;

}
