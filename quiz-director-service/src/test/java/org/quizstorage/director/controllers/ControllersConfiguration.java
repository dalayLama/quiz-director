package org.quizstorage.director.controllers;

import org.quizstorage.director.configurations.properties.AuthenticationHeadersProperties;
import org.quizstorage.director.configurations.SecurityConfiguration;
import org.quizstorage.director.security.QuizUserHeaderTokenGenerator;
import org.quizstorage.director.security.QuizUserSecurityFacade;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@EnableConfigurationProperties
@Import({AuthenticationHeadersProperties.class, SecurityConfiguration.class, QuizUserSecurityFacade.class,
        QuizUserHeaderTokenGenerator.class})
public class ControllersConfiguration {

}
