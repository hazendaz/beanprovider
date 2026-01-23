/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.util.AnnotationLiteral;

/**
 * Literal for {@link ApplicationScoped}
 */
public class ApplicationScopedLiteral extends AnnotationLiteral<ApplicationScoped> implements ApplicationScoped {
    private static final long serialVersionUID = 6582580975876369665L;
}
