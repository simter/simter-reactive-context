package tech.simter.reactive.reactor

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
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
  fun thenManyLostFluxData() {
    Mono.empty<Int>()
      .thenMany<Int> { Flux.fromIterable(listOf(1, 2)) }
      .subscribe { println("b:$it") } // why not output item 1, 2
  }

  @Test
  fun thenManyRunForever() {
    Mono.empty<Int>()
      .thenMany<Int> { Flux.fromIterable(listOf(1, 2)) }
      .blockLast() // why not output item 1, 2
  }

  // this method output item 1, 2
  @Test
  @Disabled
  fun flatMapIterableTest() {
    Mono.empty<Int>()
      .then(Mono.just(listOf(1, 2)))
      .flatMapIterable { it.asIterable() }
      .subscribe { println(it) } // output item 1, 2
  }

  @Test
  fun tt() {
    Flux.range(1, 3)
      .subscribe { println(it) }
  }
}