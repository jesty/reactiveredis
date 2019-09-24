package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.flow.flow
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class ItemService (val itemOps: ReactiveRedisOperations<String, Item>) {

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

    fun listItems(filter: String?): Flow<Item> {
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

    fun liveItems(filter: String?): Flow<Item> {
        return hot.filter {
            filter(filter, it)
        }
    }

    private fun filter(filter: String?, it: Item) =
            if (!filter.isNullOrEmpty()) it.name.contains(filter.toString()) else true
}