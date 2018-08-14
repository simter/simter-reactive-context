# simter-reactive-context

Simter reactive system-context. This package just has one tool class [SystemContext]. 
All the unit test is in the class [SystemContextTest]. Run test by `mvn test`.

The base technique is from reactor's official document ["Adding a Context to a Reactive Sequence"].

## [SystemContext] functions

```
fun getAuthenticatedUser()                : Mono<Optional<User>>
fun hasAnyRole(vararg roles: String)      : Mono<Boolean>
fun hasAllRole(vararg roles: String)      : Mono<Boolean>
fun verifyHasAnyRole(vararg roles: String): Mono<Void>
fun verifyHasAllRole(vararg roles: String): Mono<Void>
```

## Installation

```xml
<dependency>
  <groupId>tech.simter.reactive</groupId>
  <artifactId>simter-reactive-context</artifactId>
  <version>0.5.0</version>
</dependency>
```

## Requirement

- Maven 3.5.2+
- Kotlin 1.2.31+
- Reactor 3+

## Build

```bash
mvn clean package
```

[SystemContext]: https://github.com/simter/simter-reactive-context/blob/master/src/main/kotlin/tech/simter/reactive/context/SystemContext.kt
[SystemContextTest]: https://github.com/simter/simter-reactive-context/blob/master/src/test/kotlin/tech/simter/reactive/context/SystemContextTest.kt
["Adding a Context to a Reactive Sequence"]: http://projectreactor.io/docs/core/release/reference/#context
