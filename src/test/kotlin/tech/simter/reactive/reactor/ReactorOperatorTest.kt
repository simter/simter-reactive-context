package tech.simter.reactive.reactor

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.lang.RuntimeException

/**
 * @author RJ
 */
@Disabled
class ReactorOperatorTest {
  @Test
  fun empty_then() {
    // then 方法只是传输 completion 信号給其参数的结果
    // 也就是说 then 会等待其参数的 mono 实例执行完毕
    // 即 then 方法是肯定会执行的
    Mono.empty<Int>()
      .doOnNext { println("0-$it") } // never invoke
      .then(Mono.just(true).doOnNext { println("1-$it") })
      .doOnNext { println("2-$it") }
      .subscribe()
  }

  @Test
  fun error_then() {
    try {
      // error 的 then 函数不会执行
      // 因此将 data 方法包装在 flatMap 内可以避免 error 时执行
      Mono.error<Int>(RuntimeException())
        .doOnNext { println("0-$it") } // never invoke
        .then(Mono.just(true).flatMap { data(1) }.doOnNext { println("1-$it") })
        .doOnNext { println("2-$it") }
        .thenEmpty { }
        .subscribe()
    } catch (e: Exception) {
      println(e.javaClass)
    }
  }

  private fun data(id: Int): Mono<Int> {
    println("in data: id=$id")
    return Mono.just(id)
  }

  // https://stackoverflow.com/questions/51837156
  @Test
  fun thenManyLostPublisherData() {
    Mono.empty<Int>()
      .thenMany<Int> { Flux.just(1, 2) }
      .subscribe { println(it) } // not output item 1, 2
  }

  // just use ( ) instead of { }
  @Test
  fun thenManyLostFluxData_FixedBySimonbasle() {
    StepVerifier.create(
      Mono.empty<Int>().thenMany(Flux.just(1, 2))
    ).expectNext(1, 2)
      .expectComplete()
      .verify()
  }

  @Test
  fun thenManyRunForever() {
    StepVerifier.create(
      Mono.empty<Int>().thenMany<Int> { Flux.just(1, 2) }
    ).expectNext(1, 2)
      .expectComplete()
      .verify()
  }

  @Test
  fun thenManyRunForever2() {
    Mono.empty<Int>()
      .thenMany<Int> { Flux.just(1, 2) }
      .blockLast() // run forever
  }

  // this method output item 1, 2
  @Test
  fun useFlatMapIterableInsteadThenMany() {
    Mono.empty<Int>()
      .then(Flux.just(1, 2).collectList())
      .flatMapIterable { it.asIterable() }
      .subscribe { println(it) } // output item 1, 2
  }

  // this method output item 1, 2
  @Test
  fun useFlatMapIterableInsteadThenMany2() {
    Mono.empty<Int>()
      .then(Flux.just(1, 2).collectList())
      .flatMapIterable { it.asIterable() }
      .doOnNext { println(it) } // output item 1, 2
      .blockLast()
  }
}