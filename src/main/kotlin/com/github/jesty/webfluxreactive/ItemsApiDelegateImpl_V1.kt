package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.api.ItemsApiDelegate
import com.github.jesty.webfluxreactive.model.Item
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.ResponseEntity
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct


//@Service
class ItemsApiDelegateImpl_V1 : ItemsApiDelegate {

    @Autowired
    private lateinit var itemOps: ReactiveRedisOperations<String, Item>

    @PostConstruct
    fun startListener() {
        itemOps
                .listenToChannel(Constants.TOPIC)
                .doOnNext { next -> println("Next on ${next.channel}: ${next.message}") }
                .map { it.message }
                .flatMap {
                    itemOps
                            .opsForList()
                            .leftPush(Constants.ITEMS_LIST, it)
                }
                .subscribe()
    }

    override fun listItems(filter: String?, exchange: ServerWebExchange?): Mono<ResponseEntity<Flux<Item>>> {
        val flux = itemOps
                .opsForList()
                .range(Constants.ITEMS_LIST, 0, -1)
        return Mono
                .just(ResponseEntity
                        .ok()
                        .body(flux))
    }

}