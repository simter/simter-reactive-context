package tech.simter.reactive.context

import reactor.core.publisher.Mono
import tech.simter.exception.PermissionDeniedException
import tech.simter.exception.UnauthenticatedException
import java.util.*

/**
 * The reactive system-context.
 *
 * See ["8.8. Adding a Context to a Reactive Sequence"](http://projectreactor.io/docs/core/release/reference/#context)
 *
 * @author RJ
 */
object SystemContext {
  const val SYSTEM_CONTEXT_KEY: String = "ST_SYSTEM_CONTEXT"

  /**
   * Get system-context data-holder.
   *
   * Return [Mono.empty] if without a system-context.
   */
  private fun getDataHolder(): Mono<DataHolder> {
    return Mono.subscriberContext()
      .flatMap {
        if (it.hasKey(SYSTEM_CONTEXT_KEY)) Mono.just(it.get<DataHolder>(SYSTEM_CONTEXT_KEY))
        else Mono.empty()
      }
  }

  /**
   * Get the authenticated user info.
   *
   * Return a mono instance with the authenticated user info if has a authenticated system-context,
   * otherwise return Mono.just([Optional.empty]).
   */
  fun getAuthenticatedUser(): Mono<Optional<User>> {
    return getDataHolder()
      .map { Optional.of(it.user) }
      .switchIfEmpty(Mono.just(Optional.empty()))
  }

  /**
   * Determine whether the system-context has any specified [roles].
   *
   * Return `Mono.just(true)` if has a authenticated system-context and it has any specified [roles],
   * otherwise return `Mono.just(false)`.
   */
  fun hasAnyRole(vararg roles: String): Mono<Boolean> {
    return getDataHolder().map { it.hasAnyRole(*roles) }.switchIfEmpty(Mono.just(false))
  }

  /**
   * Determine whether the system-context has all specified [roles].
   *
   * Return `Mono.just(true)` if has a authenticated system-context and it has all specified [roles],
   * otherwise return `Mono.just(false)`.
   */
  fun hasAllRole(vararg roles: String): Mono<Boolean> {
    return getDataHolder().map { it.hasAllRole(*roles) }.switchIfEmpty(Mono.just(false))
  }

  /**
   * Verify whether the system-context has any specified [roles].
   *
   * Return a [Mono.error] with [UnauthenticatedException] if without a authenticated system-context.
   * Or return a [Mono.error] with [PermissionDeniedException] if has a authenticated system-context but it has'ont any specified [roles].
   * Otherwise return [Mono.empty].
   */
  fun verifyHasAnyRole(vararg roles: String): Mono<Void> {
    return getDataHolder()
      .switchIfEmpty(Mono.error(UnauthenticatedException("Without system-context")))
      .flatMap {
        if (it.hasAnyRole(*roles)) Mono.empty<Void>()
        else Mono.error(PermissionDeniedException("Verify has any roles failed: $roles"))
      }
  }

  /**
   * Verify whether the system-context has all specified [roles].
   *
   * Return a [Mono.error] with [UnauthenticatedException] if without a authenticated system-context.
   * Or return a [Mono.error] with [PermissionDeniedException] if has a authenticated system-context but it has'ont all specified [roles].
   * Otherwise return [Mono.empty].
   */
  fun verifyHasAllRole(vararg roles: String): Mono<Void> {
    return getDataHolder()
      .switchIfEmpty(Mono.error(UnauthenticatedException("Without system-context")))
      .flatMap {
        if (it.hasAllRole(*roles)) Mono.empty<Void>()
        else Mono.error(PermissionDeniedException("Verify has all roles failed: $roles"))
      }
  }

  /**
   * Hold the system-context data.
   */
  data class DataHolder(
    val user: User,
    private val roles: List<String>
  ) {
    fun hasAnyRole(vararg roles: String): Boolean {
      return roles.any { this.roles.contains(it) }
    }

    fun hasAllRole(vararg roles: String): Boolean {
      return roles.all { this.roles.contains(it) }
    }
  }

  /**
   * Hold the authenticated user info.
   */
  data class User(
    val id: Int,
    val account: String,
    val name: String
  )
}