/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import jakarta.inject.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Small {
    String value() default "";
}
