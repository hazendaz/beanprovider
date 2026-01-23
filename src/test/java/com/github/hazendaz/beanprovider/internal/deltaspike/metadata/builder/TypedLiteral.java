/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import jakarta.enterprise.inject.Typed;
import jakarta.enterprise.util.AnnotationLiteral;

/**
 * Literal for {@link jakarta.enterprise.inject.Typed}
 */
public class TypedLiteral extends AnnotationLiteral<Typed> implements Typed {
    private static final long serialVersionUID = 6805980497117269525L;

    private final Class<?>[] value;

    public TypedLiteral() {
        value = new Class<?>[0];
    }

    public TypedLiteral(Class<?>[] value) {
        this.value = value;
    }

    @Override
    public Class<?>[] value() {
        return value;
    }
}
