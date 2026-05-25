/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider.internal.deltaspike.metadata.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.hazendaz.beanprovider.internal.deltaspike.literal.AlternativeLiteral;
import com.github.hazendaz.beanprovider.internal.deltaspike.literal.NamedLiteral;

import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Named;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.jupiter.api.Test;

class AnnotationBuilderTest {

    @Deprecated
    static class DeprecatedType {
        // marker class
    }

    @Test
    void defaultAnnotationStoreIsEmpty() {
        final AnnotationStore store = new AnnotationStore();
        assertThat(store.getAnnotations()).isEmpty();
        assertThat(store.isAnnotationPresent(Named.class)).isFalse();
        assertThat(store.getAnnotation(Named.class)).isNull();
    }

    @Test
    void annotationBuilderSupportsAllMutationPaths() {
        final AnnotationBuilder source = new AnnotationBuilder();
        source.add(new NamedLiteral("source"));

        final AnnotationBuilder builder = new AnnotationBuilder();
        builder.add(new AlternativeLiteral());
        builder.addAll(List.<Annotation> of(new NamedLiteral("collection")));
        builder.addAll(source.create());
        builder.addAll(DeprecatedType.class);
        builder.remove(Deprecated.class);

        assertThat(builder.isAnnotationPresent(Named.class)).isTrue();
        assertThat(builder.getAnnotation(Named.class).value()).isEqualTo("source");
        assertThat(builder.isAnnotationPresent(Alternative.class)).isTrue();
        assertThat(builder.toString()).contains("source");
    }

    @Test
    void nullInputsAreRejected() {
        final AnnotationBuilder builder = new AnnotationBuilder();
        assertThatThrownBy(() -> builder.add(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.remove(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
