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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.hazendaz.beanprovider.internal.deltaspike.literal.AlternativeLiteral;
import com.github.hazendaz.beanprovider.internal.deltaspike.literal.AnyLiteral;
import com.github.hazendaz.beanprovider.internal.deltaspike.literal.ApplicationScopedLiteral;
import com.github.hazendaz.beanprovider.internal.deltaspike.literal.NamedLiteral;
import com.github.hazendaz.beanprovider.internal.deltaspike.literal.TypedLiteral;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Typed;
import jakarta.enterprise.inject.spi.AnnotatedConstructor;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedParameter;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Named;
import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class AnnotatedTypeBuilderTest {

    @Test
    void testTypeLevelAnnotationRedefinition() {
        AnnotatedTypeBuilder<Cat> builder = new AnnotatedTypeBuilder<>();
        builder.readFromType(Cat.class);

        AnnotatedType<Cat> cat = builder.create();

        assertThat(cat).isNotNull();
        assertThat(cat.getAnnotation(Named.class)).isNotNull();
        assertThat(cat.getAnnotation(Named.class).value()).isEqualTo("cat");

        builder.addToClass(new AlternativeLiteral()).addToClass(new ApplicationScopedLiteral())
                .removeFromClass(Named.class).addToClass(new NamedLiteral("tomcat"));

        cat = builder.create();
        assertThat(cat).isNotNull();

        assertThat(cat.getAnnotations()).hasSize(3);
        assertThat(cat.isAnnotationPresent(Named.class)).isTrue();
        assertThat(cat.isAnnotationPresent(Alternative.class)).isTrue();
        assertThat(cat.isAnnotationPresent(ApplicationScoped.class)).isTrue();
        assertThat(cat.getAnnotation(Named.class).value()).isEqualTo("tomcat");

        AnnotatedMethod observerMethod = null;
        for (AnnotatedMethod m : cat.getMethods()) {
            if ("doSomeObservation".equals(m.getJavaMember().getName())) {
                observerMethod = m;
                break;
            }
        }
        assertThat(observerMethod).isNotNull();
        boolean hasObserves = false;
        for (Object paramObj : observerMethod.getParameters()) {
            AnnotatedParameter<?> param = (AnnotatedParameter<?>) paramObj;
            if (param.getAnnotation(Observes.class) != null) {
                hasObserves = true;
                break;
            }
        }
        assertThat(hasObserves).isTrue();

        {
            // test reading from an AnnotatedType
            AnnotatedTypeBuilder<Cat> builder2 = new AnnotatedTypeBuilder<>();
            builder2.readFromType(cat);
            builder2.removeFromAll(Named.class);

            final AnnotatedType<Cat> noNameCat = builder2.create();
            assertThat(noNameCat.isAnnotationPresent(Named.class)).isFalse();
            assertThat(noNameCat.getAnnotations()).hasSize(2);
        }

        {

            // test reading from an AnnotatedType in non-overwrite mode
            AnnotatedTypeBuilder<Cat> builder3 = new AnnotatedTypeBuilder<>();
            builder3.readFromType(cat, true);
            builder3.removeFromAll(Named.class);

            builder3.readFromType(cat, false);

            final AnnotatedType<Cat> namedCat = builder3.create();
            assertThat(namedCat.isAnnotationPresent(Named.class)).isTrue();
            assertThat(namedCat.getAnnotations()).hasSize(3);
        }
    }

    @Test
    void testAdditionOfAnnotation() {
        final AnnotatedTypeBuilder<Cat> builder = new AnnotatedTypeBuilder<>();
        builder.readFromType(Cat.class, true);
        builder.addToClass(new TypedLiteral());

        final AnnotatedType<Cat> catAnnotatedType = builder.create();
        assertThat(catAnnotatedType.isAnnotationPresent(Typed.class)).isTrue();
    }

    @Test
    void modifyAnnotationsOnConstructorParameter() throws NoSuchMethodException {
        final AnnotatedTypeBuilder<Cat> builder = new AnnotatedTypeBuilder<>();
        builder.readFromType(Cat.class, true);
        builder.removeFromConstructorParameter(Cat.class.getConstructor(String.class, String.class), 1, Default.class);
        builder.addToConstructorParameter(Cat.class.getConstructor(String.class, String.class), 1, new AnyLiteral());

        final AnnotatedType<Cat> catAnnotatedType = builder.create();
        Set<AnnotatedConstructor<Cat>> catCtors = catAnnotatedType.getConstructors();

        assertThat(catCtors).hasSize(2);

        for (AnnotatedConstructor<Cat> ctor : catCtors) {
            if (ctor.getParameters().size() == 2) {
                List<AnnotatedParameter<Cat>> ctorParams = ctor.getParameters();

                assertThat(ctorParams.get(1).getAnnotations()).hasSize(1);
                assertThat((AnyLiteral) ctorParams.get(1).getAnnotations().toArray()[0]).isEqualTo(new AnyLiteral());
            }
        }
    }

    @Test
    void buildValidAnnotationAnnotatedType() {
        final AnnotatedTypeBuilder<Small> builder = new AnnotatedTypeBuilder<Small>();
        builder.readFromType(Small.class);
        final AnnotatedType<Small> smallAnnotatedType = builder.create();

        assertThat(smallAnnotatedType.getMethods()).hasSize(1);
        assertThat(smallAnnotatedType.getConstructors()).isEmpty();
        assertThat(smallAnnotatedType.getFields()).isEmpty();
    }

    @Test
    void testCtWithMultipleParams() {
        final AnnotatedTypeBuilder<TypeWithParamsInCt> builder = new AnnotatedTypeBuilder<TypeWithParamsInCt>();
        builder.readFromType(TypeWithParamsInCt.class);
        builder.addToClass(new AnnotationLiteral<Default>() {
        });

        AnnotatedType<TypeWithParamsInCt> newAt = builder.create();
        assertThat(newAt).isNotNull();
    }

    @Test
    void testEnumWithParam() {
        final AnnotatedTypeBuilder<EnumWithParams> builder = new AnnotatedTypeBuilder<EnumWithParams>();
        builder.readFromType(EnumWithParams.class);
        builder.addToClass(new AnnotationLiteral<Default>() {
        });

        AnnotatedType<EnumWithParams> newAt = builder.create();
        assertThat(newAt).isNotNull();
    }

    @Test
    void testOverloadsAndTypeOverrides() throws Exception {
        final AnnotatedType<Cat> sourceType = new AnnotatedTypeBuilder<Cat>().readFromType(Cat.class).create();

        final AnnotatedField<? super Cat> colorField = sourceType.getFields().stream()
                .filter(f -> "color".equals(f.getJavaMember().getName())).findFirst().orElseThrow();
        final AnnotatedMethod<? super Cat> setupMethod = sourceType.getMethods().stream()
                .filter(m -> "setup".equals(m.getJavaMember().getName())).findFirst().orElseThrow();
        final AnnotatedMethod<? super Cat> observerMethod = sourceType.getMethods().stream()
                .filter(m -> "doSomeObservation".equals(m.getJavaMember().getName())).findFirst().orElseThrow();
        final AnnotatedConstructor<Cat> injectCtor = sourceType.getConstructors().stream()
                .filter(c -> c.getParameters().size() == 2).findFirst().orElseThrow();
        final AnnotatedParameter<? super Cat> observerParameter = observerMethod.getParameters().get(0);
        final AnnotatedParameter<? super Cat> constructorParameter = injectCtor.getParameters().get(1);

        final AnnotatedTypeBuilder<Cat> builder = new AnnotatedTypeBuilder<>();
        builder.readFromType(Cat.class, true);
        builder.addToField(colorField, new NamedLiteral("field-name"));
        builder.removeFromField(colorField, Named.class);
        builder.addToMethod(setupMethod, new NamedLiteral("method-name"));
        builder.removeFromMethod(setupMethod, Named.class);
        builder.addToMethodParameter(observerMethod.getJavaMember(), 1, new AnyLiteral());
        builder.removeFromMethodParameter(observerMethod.getJavaMember(), 1, Any.class);
        builder.addToConstructor(injectCtor, new jakarta.enterprise.util.AnnotationLiteral<Inject>() {
        });
        builder.removeFromConstructor(injectCtor, Inject.class);
        builder.addToConstructorParameter(injectCtor.getJavaMember(), 1, new AnyLiteral());
        builder.removeFromConstructorParameter(injectCtor.getJavaMember(), 1, Any.class);
        builder.addToParameter(observerParameter, new NamedLiteral("observer-parameter"));
        builder.removeFromParameter(observerParameter, Named.class);
        builder.addToParameter(constructorParameter, new AnyLiteral());
        builder.removeFromParameter(constructorParameter, Any.class);
        builder.overrideFieldType(colorField, Object.class);
        builder.overrideParameterType(observerParameter, Object.class);
        builder.overrideParameterType(constructorParameter, Object.class);

        assertThat(builder.getJavaClass()).isEqualTo(Cat.class);
        assertThat(builder.setJavaClass(Cat.class)).isSameAs(builder);

        final AnnotatedType<Cat> updatedType = builder.create();
        final AnnotatedField<? super Cat> updatedColorField = updatedType.getFields().stream()
                .filter(f -> "color".equals(f.getJavaMember().getName())).findFirst().orElseThrow();
        final AnnotatedMethod<? super Cat> updatedObserverMethod = updatedType.getMethods().stream()
                .filter(m -> "doSomeObservation".equals(m.getJavaMember().getName())).findFirst().orElseThrow();
        final AnnotatedConstructor<Cat> updatedInjectCtor = updatedType.getConstructors().stream()
                .filter(c -> c.getParameters().size() == 2).findFirst().orElseThrow();

        assertThat(updatedColorField.getBaseType()).isEqualTo(Object.class);
        assertThat(updatedObserverMethod.getParameters().get(0).getBaseType()).isEqualTo(Object.class);
        assertThat(updatedInjectCtor.getParameters().get(1).getBaseType()).isEqualTo(Object.class);
    }

    @Test
    void testValidationAndMissingMembers() throws Exception {
        final AnnotatedTypeBuilder<Cat> builder = new AnnotatedTypeBuilder<>();
        final Method toStringMethod = Object.class.getDeclaredMethod("toString");
        final Method observerMethod = Cat.class.getDeclaredMethod("doSomeObservation", Cat.class, BeanManager.class);
        final Constructor<Cat> injectCtor = Cat.class.getConstructor(String.class, String.class);
        final Field colorField = Cat.class.getDeclaredField("color");
        final Field valueField = String.class.getDeclaredField("value");

        assertThatThrownBy(() -> builder.readFromType((Class<Cat>) null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.readFromType((AnnotatedType<Cat>) null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.removeFromAll(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.removeFromField(valueField, Named.class))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.removeFromMethod(toStringMethod, Named.class))
                .isInstanceOf(IllegalArgumentException.class);

        builder.readFromType(Cat.class, true);
        assertThatThrownBy(() -> builder.removeFromMethodParameter(observerMethod, 2, Named.class))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> builder.overrideFieldType((Field) null, Object.class))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.overrideFieldType(colorField, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.overrideMethodParameterType(null, 0, Object.class))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.overrideMethodParameterType(observerMethod, 0, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.overrideConstructorParameterType(null, 0, Object.class))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.overrideConstructorParameterType(injectCtor, 0, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    public static class TypeWithParamsInCt {
        public TypeWithParamsInCt(String a, int b, String c) {
            // all fine
        }
    }

    public enum EnumWithParams {
        VALUE("A");

        EnumWithParams(String val) {
            // all fine
        }
    }

    @Test
    void testExceptionPerformance() {
        long start = System.nanoTime();
        long val = -230349823423L;
        Exception e = new Exception("static");
        for (int i = 0; i < 10_000_000; i++) {
            try {
                val += 19;
                throw e;
            } catch (Exception e2) {
                // do nothing
            }
        }
        long end = System.nanoTime();
        System.out.println("Exeptions took ms " + TimeUnit.NANOSECONDS.toMillis(end - start));
    }

}
