package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.model.Item
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import javax.annotation.PostConstruct

@RestController
@RequestMapping("/coroutine")
@FlowPreview
class ItemsApiDelegateImpl_V5(val itemOps: ReactiveRedisOperations<String, Item>) {


    private lateinit var hot: Flow<Item>

    @PostConstruct
    fun startListener() {
        hot = itemOps
                .listenToChannel(Constants.TOPIC)
                .doOnNext { next -> println("Next on ${next.channel}: ${next.message}") }
                .map { it.message }
                .flatMap { item ->
                    itemOps
                            .opsForList()
                            .leftPush(Constants.ITEMS_LIST, item)
                            .map { item }
                            .doOnNext { println(it) }
                }
                .asFlow()
    }

    @RequestMapping(value = ["/items"], produces = ["text/event-stream"], method = [RequestMethod.GET])
    @MessageMapping("items")
    fun listItems(filter: String?, exchange: ServerWebExchange?): Flow<Item> {
        val fluxAll = itemOps
                .opsForList()
                .range(Constants.ITEMS_LIST, 0, -1)
                .asFlow()

        return flow {
            fluxAll.collect { emit(it) }
            hot.collect { emit(it) }
        }.filter {
            filter(filter, it)
        }
    }

    @RequestMapping(value = ["/live"], produces = ["text/event-stream"], method = [RequestMethod.GET])
    @MessageMapping("live")
    fun liveItems(filter: String?, exchange: ServerWebExchange?): Flow<Item> {
        return hot.filter {
            filter(filter, it)
        }
    }

    private fun filter(filter: String?, it: Item) =
            if (!filter.isNullOrEmpty()) it.name.contains(filter.toString()) else true

}



