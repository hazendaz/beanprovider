/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Named;

/**
 * Literal for {@link jakarta.inject.Named} qualifier.
 */
public class NamedLiteral extends AnnotationLiteral<Named> implements Named {
    private static final long serialVersionUID = -1457223276475846060L;

    private final String value;

    public NamedLiteral(String value) {
        this.value = value;
    }

    public NamedLiteral() {
        value = "";
    }

    @Override
    public String value() {
        return value;
    }
}
