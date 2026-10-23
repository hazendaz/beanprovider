/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Implementation of {@link AnnotatedMethod} to be used in CDI life cycle events and
 * {@link com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder.AnnotatedTypeBuilder}.
 *
 * @param <X>
 *            the generic type
 */
class AnnotatedMethodImpl<X> extends AnnotatedCallableImpl<X, Method> implements AnnotatedMethod<X> {

    /**
     * Constructor.
     *
     * @param type
     *            the type
     * @param method
     *            the method
     * @param annotations
     *            the annotations
     * @param parameterAnnotations
     *            the parameter annotations
     * @param parameterTypeOverrides
     *            the parameter type overrides
     */
    AnnotatedMethodImpl(AnnotatedType<X> type, Method method, AnnotationStore annotations,
            Map<Integer, AnnotationStore> parameterAnnotations, Map<Integer, Type> parameterTypeOverrides) {
        super(type, method, method.getReturnType(), method.getParameterTypes(), method.getGenericParameterTypes(),
                annotations, parameterAnnotations, method.getGenericReturnType(), parameterTypeOverrides);
    }
}
