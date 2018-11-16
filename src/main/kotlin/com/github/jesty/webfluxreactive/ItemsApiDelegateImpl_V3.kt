package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.api.ItemsApiDelegate
import com.github.jesty.webfluxreactive.model.Item
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct


//@Service
class ItemsApiDelegateImpl_V3 : ItemsApiDelegate {


    @Autowired
    private lateinit var itemOps: ReactiveRedisOperations<String, Item>

    private lateinit var hotSource: Flux<Item>

    //COLD
    @PostConstruct
    fun startListener() {
        hotSource = itemOps
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

    override fun listItems(filter: String?, exchange: ServerWebExchange?): Mono<ResponseEntity<Flux<Item>>> {
        val fluxAll = itemOps
                .opsForList()
                .range(Constants.ITEMS_LIST, 0, -1)

        return Mono
                .just(ResponseEntity
                        .ok()
                        .body(fluxAll
                                .mergeWith(hotSource)
                                .filter { item -> item.name.contains(filter.toString()) }))
    }

}