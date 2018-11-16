package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.model.Item
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import javax.annotation.PostConstruct

class ItemFlux(val itemOps: ReactiveRedisOperations<String, Item>) {

    private lateinit var liveFlux: Flux<Item>

    @PostConstruct
    fun startListener() {
        liveFlux = itemOps
                .listenToChannel(Constants.TOPIC)
                .doOnNext { next -> println("Next on ${next.channel}: ${next.message}") }
                .map { it.message }
                .flatMap { item ->
                    itemOps
                            .opsForList()
                            .leftPush(Constants.ITEMS_LIST, item)
                            .map { item }
                }
    }

}