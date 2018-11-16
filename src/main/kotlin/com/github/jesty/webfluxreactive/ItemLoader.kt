package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.model.Item
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import javax.annotation.PostConstruct


@Component
class ItemLoader(val factory: ReactiveRedisConnectionFactory, val coffeeOps: ReactiveRedisOperations<String, Item>) {


    @PostConstruct
    fun load() {
        factory
                .reactiveConnection
                .serverCommands()
                .flushAll()
                .thenMany(Flux
                        .range(1, 3)
                        .map { it.toLong() }
                        .zipWith(Flux.just("A", "B", "C"))
                        .map { tuple ->
                            val item = Item()
                            item.id = tuple.t1
                            item.name = tuple.t2
                            return@map item
                        }
                        .flatMap { coffeeOps.opsForList().leftPush(Constants.ITEMS_LIST, it) })
                .subscribe()

    }

}