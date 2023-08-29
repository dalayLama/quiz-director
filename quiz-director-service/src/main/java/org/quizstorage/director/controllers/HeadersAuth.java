package org.quizstorage.director.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.lang.annotation.*;

@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(parameters = {
        @Parameter(name = "${security.auth.headers.id-header-name}", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "${security.auth.headers.name-header-name}", in = ParameterIn.HEADER),
        @Parameter(name = "${security.auth.headers.roles-header-name}", in = ParameterIn.HEADER),
})
public @interface HeadersAuth {
}
