package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.api.ItemsApiDelegate
import com.github.jesty.webfluxreactive.model.Item
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct


@Service
class ItemsApiDelegateImpl_V4(val itemOps: ReactiveRedisOperations<String, Item>) : ItemsApiDelegate {


    //HOT
    private lateinit var hotSource: DirectProcessor<Item>

    @PostConstruct
    fun startListener() {
        hotSource = DirectProcessor.create<Item>()
        itemOps
                .listenToChannel(Constants.TOPIC)
                .doOnNext { next -> println("Next on ${next.channel}: ${next.message}") }
                .map { it.message }
                .flatMap { item ->
                    itemOps
                            .opsForList()
                            .leftPush(Constants.ITEMS_LIST, item)
                            .map { item }
                            .doOnNext { hotSource.onNext(it) }
                }
                .subscribe()
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
                                .filter { item -> if (!filter.isNullOrEmpty()) item.name.contains(filter.toString()) else true }))
    }

}