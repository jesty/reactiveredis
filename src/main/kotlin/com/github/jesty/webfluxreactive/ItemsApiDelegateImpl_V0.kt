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


//@Service
class ItemsApiDelegateImpl_V0 : ItemsApiDelegate {


    @Autowired
    private lateinit var itemOps: ReactiveRedisOperations<String, Item>

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