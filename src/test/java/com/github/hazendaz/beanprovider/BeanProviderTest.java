/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.xml.ws.WebServiceContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@EnableWeld
class BeanProviderTest {

    @Resource
    private WebServiceContext context;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.of(new Weld());

    @Test
    void injectFields_empty_map() {
        final Map<String, Class<? extends Annotation>> ignoreMap = new ConcurrentHashMap<>();
        Assertions.assertSame(this, BeanProvider.injectFields(this, ignoreMap));
    }

    @Test
    void injectFields_full_map() {
        final Map<String, Class<? extends Annotation>> ignoreMap = new ConcurrentHashMap<>();
        ignoreMap.put("context", Resource.class);
        Assertions.assertSame(this, BeanProvider.injectFields(this, ignoreMap));
    }

    @Test
    void injectFields_missing_property() {
        final Map<String, Class<? extends Annotation>> ignoreMap = new ConcurrentHashMap<>();
        ignoreMap.put("inject", Inject.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> BeanProvider.injectFields(this, ignoreMap));
    }

    @Test
    void injectFields_null_instance() {
        final Map<String, Class<? extends Annotation>> ignoreMap = new ConcurrentHashMap<>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> BeanProvider.injectFields(null, ignoreMap));
    }

    @Test
    void injectFields_null_map() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BeanProvider.injectFields(this, null));
    }

    @Test
    void privateConstructorTest() throws Exception {
        final Constructor<?>[] constructors = BeanProvider.class.getDeclaredConstructors();
        constructors[0].setAccessible(true);
        constructors[0].newInstance((Object[]) null);
        for (final Constructor<?> constructor : constructors) {
            Assertions.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }

}
