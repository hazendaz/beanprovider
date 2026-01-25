/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright 2011-2026 Hazendaz
 */
package com.github.hazendaz.beanprovider;

import jakarta.enterprise.context.ApplicationScoped;

import lombok.Data;

/**
 * The Class DummyInjection.
 */
@ApplicationScoped
@Data
public class DummyInjection {

    /** The name. */
    private String name;

    /**
     * Instantiates a new dummy injection.
     */
    public DummyInjection() {
        this.name = "DummyInjection";
    }

}
