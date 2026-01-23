/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import jakarta.enterprise.inject.spi.AnnotatedCallable;
import jakarta.enterprise.inject.spi.AnnotatedParameter;

import java.lang.reflect.Type;

/**
 * Implementation of {@link AnnotatedParameter}.
 */
class AnnotatedParameterImpl<X> extends AnnotatedImpl implements AnnotatedParameter<X> {

    private final int position;
    private final AnnotatedCallable<X> declaringCallable;

    /**
     * Constructor
     */
    AnnotatedParameterImpl(AnnotatedCallable<X> declaringCallable, Class<?> type, int position,
            AnnotationStore annotations, Type genericType, Type typeOverride) {
        super(type, annotations, genericType, typeOverride);
        this.declaringCallable = declaringCallable;
        this.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotatedCallable<X> getDeclaringCallable() {
        return declaringCallable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition() {
        return position;
    }

}
