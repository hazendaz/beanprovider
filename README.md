# beanprovider #

[![Java CI](https://github.com/hazendaz/beanprovider/actions/workflows/ci.yaml/badge.svg)](https://github.com/hazendaz/beanprovider/actions/workflows/ci.yaml)
[![Coveralls](https://coveralls.io/repos/github/hazendaz/beanprovider/badge.svg?branch=master)](https://coveralls.io/github/hazendaz/beanprovider?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.hazendaz.cdi/beanprovider)](https://central.sonatype.com/artifact/com.github.hazendaz.cdi/beanprovider)
[![License](https://img.shields.io/github/license/hazendaz/beanprovider)](https://www.apache.org/licenses/LICENSE-2.0)

![hazendaz](src/site/resources/images/hazendaz-banner.jpg)

See site page [here](https://hazendaz.github.io/beanprovider/)

# Introduction

A small utility library that provides a customized “BeanProvider”-style helper for Jakarta CDI.

It’s based on Apache DeltaSpike’s injection utilities, with two project-specific features:

## Background / origin

This project restores and adapts functionality that existed in Apache DeltaSpike (pre-2.x) and was removed as part of the DeltaSpike 2.x migration. It’s maintained here independently (not an official DeltaSpike module).

- **Inject into non-CDI-managed objects** (field injection on an existing instance).
- **Customize injection points at runtime** by:
  - removing specific annotations from specific fields (via an `ignoreMap`)
  - supporting a custom `@PostInject` marker that is treated as `@Inject` only when you explicitly invoke injection

This is useful when you have objects that are created by frameworks outside CDI (listeners, filters, legacy code, etc.) but you still want CDI field injection in a controlled way.

## Requirements

- Java 11+
- Jakarta CDI API (compile-time)
- Apache DeltaSpike Core (runtime)

Tests use Weld + `weld-junit5`.

## Dependency

Add the dependency to your project (coordinates shown here match the current POM):

- GroupId: `com.github.hazendaz.cdi`
- ArtifactId: `beanprovider`

(If you consume a released version, use the released version instead of `1.0.0-SNAPSHOT`.)

## How it works

The main entry point is:

- `com.github.hazendaz.beanprovider.BeanProvider`

It takes an existing instance and asks CDI to inject its fields. Before injection happens, it can alter the effective `AnnotatedType` used for injection:

- remove an annotation from a named field (useful to prevent injection of certain fields)
- add `@Inject` to any field annotated with `@PostInject`

### `@PostInject`

`@PostInject` is a simple marker annotation you can put on fields (or methods/constructors), typically as a *replacement for `@Inject`* when you **don’t** want CDI to inject automatically.

When you call `BeanProvider.injectFields(...)`, any field annotated with `@PostInject` will be treated as if it also had `@Inject`.

## Usage

### 1) Mark fields for "late" injection

```java
import com.github.hazendaz.beanprovider.PostInject;

public class SomeLegacyObject {

  @PostInject
  private MyService myService;

  // created manually / by another framework
}
```

### 2) Trigger injection explicitly

```java
import com.github.hazendaz.beanprovider.BeanProvider;
import java.util.Map;

SomeLegacyObject obj = new SomeLegacyObject();

// Don’t remove any annotations from fields:
BeanProvider.injectFields(obj, Map.of());
```

### 3) Optionally suppress injection for specific fields

If you want to strip an annotation from a field before injection runs, pass it via `ignoreMap`:

```java
import com.github.hazendaz.beanprovider.BeanProvider;
import jakarta.inject.Inject;
import java.util.Map;

SomeLegacyObject obj = new SomeLegacyObject();

// Example: remove @Inject from the field named "myService" before injection runs.
// (This is just an example; you can remove other annotations too.)
BeanProvider.injectFields(obj, Map.of("myService", Inject.class));
```

## Notes / gotchas

- `BeanProvider.injectFields(...)` requires an active CDI container. Internally it uses `CDI.current().getBeanManager()`.
- The injected instance is **not** managed by CDI after injection; it’s still whatever lifecycle your other framework uses.
- This utility is intended for runtime use; using it during CDI bootstrap in extensions can lead to non-portable behavior.

## Building and testing

This project uses the Maven Wrapper.

```bash
./mvnw --quiet test
```

On Windows:

```bat
mvnw.cmd --quiet test
```

## License

Apache License 2.0. See `LICENSE`.
