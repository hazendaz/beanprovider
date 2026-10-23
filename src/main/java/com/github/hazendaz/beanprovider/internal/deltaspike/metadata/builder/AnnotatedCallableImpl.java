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

import jakarta.enterprise.inject.spi.AnnotatedCallable;
import jakarta.enterprise.inject.spi.AnnotatedParameter;
import jakarta.enterprise.inject.spi.AnnotatedType;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link AnnotatedCallable}.
 *
 * @param <X>
 *            the generic type
 * @param <Y>
 *            the generic type
 */
abstract class AnnotatedCallableImpl<X, Y extends Member> extends AnnotatedMemberImpl<X, Y>
        implements AnnotatedCallable<X> {

    /** The parameters. */
    private final List<AnnotatedParameter<X>> parameters;

    /**
     * Instantiates a new annotated callable impl.
     *
     * @param declaringType
     *            the declaring type
     * @param member
     *            the member
     * @param memberType
     *            the member type
     * @param parameterTypes
     *            the parameter types
     * @param genericTypes
     *            the generic types
     * @param annotations
     *            the annotations
     * @param parameterAnnotations
     *            the parameter annotations
     * @param genericType
     *            the generic type
     * @param parameterTypeOverrides
     *            the parameter type overrides
     */
    protected AnnotatedCallableImpl(AnnotatedType<X> declaringType, Y member, Class<?> memberType,
            Class<?>[] parameterTypes, Type[] genericTypes, AnnotationStore annotations,
            Map<Integer, AnnotationStore> parameterAnnotations, Type genericType,
            Map<Integer, Type> parameterTypeOverrides) {
        super(declaringType, member, memberType, annotations, genericType, null);
        parameters = getAnnotatedParameters(this, parameterTypes, genericTypes, parameterAnnotations,
                parameterTypeOverrides);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AnnotatedParameter<X>> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    /**
     * Gets the parameter.
     *
     * @param index
     *            the index
     *
     * @return the parameter
     */
    public AnnotatedParameter<X> getParameter(int index) {
        return parameters.get(index);

    }

    /**
     * Gets the annotated parameters.
     *
     * @param <X>
     *            the generic type
     * @param <Y>
     *            the generic type
     * @param callable
     *            the callable
     * @param parameterTypes
     *            the parameter types
     * @param genericTypes
     *            the generic types
     * @param parameterAnnotations
     *            the parameter annotations
     * @param parameterTypeOverrides
     *            the parameter type overrides
     *
     * @return the annotated parameters
     */
    private static <X, Y extends Member> List<AnnotatedParameter<X>> getAnnotatedParameters(
            AnnotatedCallableImpl<X, Y> callable, Class<?>[] parameterTypes, Type[] genericTypes,
            Map<Integer, AnnotationStore> parameterAnnotations, Map<Integer, Type> parameterTypeOverrides) {
        List<AnnotatedParameter<X>> parameters = new ArrayList<>();
        int len = parameterTypes.length;

        for (int i = 0; i < len; ++i) {
            AnnotationBuilder builder = new AnnotationBuilder();
            if (parameterAnnotations != null && parameterAnnotations.containsKey(i)) {
                builder.addAll(parameterAnnotations.get(i));
            }
            Type over = null;
            if (parameterTypeOverrides != null) {
                over = parameterTypeOverrides.get(i);
            }
            AnnotatedParameterImpl<X> p = new AnnotatedParameterImpl<>(callable, parameterTypes[i], i, builder.create(),
                    genericTypes[i], over);

            parameters.add(p);
        }
        return parameters;
    }

}
