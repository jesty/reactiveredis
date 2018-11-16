package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.api.TickerApiDelegate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Duration.ofSeconds

@Service
class TickerApiDelegateImpl : TickerApiDelegate {

    override fun tick(exchange: ServerWebExchange?): Mono<ResponseEntity<Flux<BigDecimal>>> {
        val flux = Flux.range(1, 10)
                .map { BigDecimal(it) }
                .doOnNext { println(it) }
                .delayElements(ofSeconds(1))
        return Mono
                .just(ResponseEntity
                        .ok()
                        .body(flux))
    }

}