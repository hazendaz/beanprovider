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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * The Class Cat.
 */
@Named("cat")
public class Cat {

    /** The color. */
    private String color;

    /** The gender. */
    private String gender;

    /**
     * Instantiates a new cat.
     */
    public Cat() {
    }

    /**
     * Instantiates a new cat.
     *
     * @param color
     *            the color
     * @param gender
     *            the gender
     */
    @Inject
    public Cat(String color, String gender) {
        this.color = color;
        this.gender = gender;
    }

    /**
     * Setup.
     */
    @PostConstruct
    public void setup() {
    }

    /**
     * Gets the color.
     *
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color.
     *
     * @param color
     *            the new color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender.
     *
     * @param gender
     *            the new gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Do some observation.
     *
     * @param cat
     *            the cat
     * @param beanManager
     *            the bean manager
     */
    protected void doSomeObservation(@Observes Cat cat, BeanManager beanManager) {
        // whoah, someone fires cats around ^^
        // at least it tests parameter scanning ;)
        color = cat.color;
    }
}
