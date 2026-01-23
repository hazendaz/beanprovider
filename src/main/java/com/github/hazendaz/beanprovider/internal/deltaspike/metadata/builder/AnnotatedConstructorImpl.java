/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import jakarta.enterprise.inject.spi.AnnotatedConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Implementation of {@link AnnotatedConstructor} to be used in
 * {@link org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder} and other CDI life cycle events.
 */
class AnnotatedConstructorImpl<X> extends AnnotatedCallableImpl<X, Constructor<X>> implements AnnotatedConstructor<X> {

    /**
     * Constructor
     */
    AnnotatedConstructorImpl(AnnotatedTypeImpl<X> type, Constructor<?> constructor, AnnotationStore annotations,
            Map<Integer, AnnotationStore> parameterAnnotations, Map<Integer, Type> typeOverrides) {

        super(type, (Constructor<X>) constructor, constructor.getDeclaringClass(), constructor.getParameterTypes(),
                getGenericArray(constructor), annotations, parameterAnnotations, null, typeOverrides);
    }

    private static Type[] getGenericArray(Constructor<?> constructor) {
        Type[] genericTypes = constructor.getGenericParameterTypes();
        // for inner classes genericTypes and parameterTypes can be different
        // length, this is a hack to fix this.
        // TODO: investigate this behavior further, on different JVM's and
        // compilers
        if (genericTypes.length < constructor.getParameterTypes().length) {
            genericTypes = new Type[constructor.getParameterTypes().length];
            genericTypes[0] = constructor.getParameterTypes()[0];
            for (int i = 0; i < constructor.getGenericParameterTypes().length; ++i) {
                genericTypes[i + 1] = constructor.getGenericParameterTypes()[i];
            }
        }
        return genericTypes;
    }

}
