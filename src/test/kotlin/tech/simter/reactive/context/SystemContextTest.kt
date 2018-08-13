package tech.simter.reactive.context

import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import tech.simter.exception.PermissionDeniedException
import tech.simter.exception.UnauthenticatedException
import tech.simter.reactive.context.SystemContext.SYSTEM_CONTEXT_KEY
import java.util.*

/**
 * @author RJ
 */
class SystemContextTest {
  @Test
  fun getAuthenticatedUser_Success() {
    val mono = SystemContext.getAuthenticatedUser()
      .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }

    StepVerifier.create(mono)
      .expectNext(Optional.of(DEFAULT_USER))
      .verifyComplete()
  }

  @Test
  fun getAuthenticatedUser_WithoutSystemContext() {
    val mono = SystemContext.getAuthenticatedUser()

    StepVerifier.create(mono)
      .expectNext(Optional.empty())
      .verifyComplete()
  }

  @Test
  fun hasAnyRole_Success() {
    // one roles
    StepVerifier.create(
      SystemContext.hasAnyRole("ADMIN")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    )
      .expectNext(true)
      .verifyComplete()

    // two roles
    StepVerifier.create(
      SystemContext.hasAnyRole("NOT_EXISTS", "ADMIN")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    )
      .expectNext(true)
      .verifyComplete()
  }

  @Test
  fun hasAnyRole_FailedByWithoutRole() {
    StepVerifier.create(SystemContext.hasAnyRole("NOT_EXISTS"))
      .expectNext(false)
      .verifyComplete()
  }

  @Test
  fun hasAnyRole_FailedByWithoutSystemContext() {
    StepVerifier.create(SystemContext.hasAnyRole("ADMIN"))
      .expectNext(false)
      .verifyComplete()
  }

  @Test
  fun hasAllRole_Success() {
    // one roles
    StepVerifier.create(
      SystemContext.hasAllRole("ADMIN")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    )
      .expectNext(true)
      .verifyComplete()

    // two roles
    StepVerifier.create(
      SystemContext.hasAllRole("COMMON", "ADMIN")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    )
      .expectNext(true)
      .verifyComplete()
  }

  @Test
  fun hasAllRole_FailedByWithoutRole() {
    // one roles
    StepVerifier.create(SystemContext.hasAllRole("NOT_EXISTS"))
      .expectNext(false)
      .verifyComplete()

    // two roles
    StepVerifier.create(SystemContext.hasAllRole("NOT_EXISTS", "ADMIN"))
      .expectNext(false)
      .verifyComplete()
  }

  @Test
  fun hasAllRole_FailedByWithoutSystemContext() {
    StepVerifier.create(SystemContext.hasAllRole("ADMIN"))
      .expectNext(false)
      .verifyComplete()
  }

  @Test
  fun verifyHasAnyRole_Success() {
    // one roles
    StepVerifier.create(
      SystemContext.verifyHasAnyRole("ADMIN")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    ).verifyComplete()

    // two roles
    StepVerifier.create(
      SystemContext.verifyHasAnyRole("COMMON", "ADMIN")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    ).verifyComplete()
  }

  @Test
  fun verifyHasAnyRole_ErrorWithUnauthenticated() {
    StepVerifier.create(SystemContext.verifyHasAnyRole("ANY"))
      .expectError(UnauthenticatedException::class.java)
      .verify()
  }

  @Test
  fun verifyHasAnyRole_ErrorWithPermissionDenied() {
    StepVerifier.create(
      SystemContext.verifyHasAnyRole("NOT_EXISTS")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    )
      .expectError(PermissionDeniedException::class.java)
      .verify()
  }

  @Test
  fun verifyHasAllRole_Success() {
    // one roles
    StepVerifier.create(
      SystemContext.verifyHasAllRole("ADMIN")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    ).verifyComplete()

    // two roles
    StepVerifier.create(
      SystemContext.verifyHasAllRole("COMMON", "ADMIN")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    ).verifyComplete()
  }

  @Test
  fun verifyHasAllRole_ErrorWithUnauthenticated() {
    StepVerifier.create(SystemContext.verifyHasAllRole("ANY"))
      .expectError(UnauthenticatedException::class.java)
      .verify()
  }

  @Test
  fun verifyHasAllRole_ErrorWithPermissionDenied() {
    StepVerifier.create(
      SystemContext.verifyHasAllRole("NOT_EXISTS")
        .subscriberContext { it.put(SYSTEM_CONTEXT_KEY, DEFAULT_SYSTEM_CONTEXT) }
    )
      .expectError(PermissionDeniedException::class.java)
      .verify()
  }

  companion object {
    private val DEFAULT_ROLES = listOf("ADMIN", "COMMON", "TESTER")
    val DEFAULT_USER = SystemContext.User(
      id = 0,
      account = "tester",
      name = "Tester"
    )
    val DEFAULT_SYSTEM_CONTEXT = SystemContext.DataHolder(
      user = DEFAULT_USER,
      roles = DEFAULT_ROLES
    )
  }
}