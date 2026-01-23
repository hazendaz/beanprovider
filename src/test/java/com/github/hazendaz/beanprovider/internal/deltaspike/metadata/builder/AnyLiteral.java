/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.util.AnnotationLiteral;

/**
 * Literal for the {@link jakarta.enterprise.inject.Any} annotation.
 */
public class AnyLiteral extends AnnotationLiteral<Any> implements Any {
    private static final long serialVersionUID = -8623640277155878657L;
}
