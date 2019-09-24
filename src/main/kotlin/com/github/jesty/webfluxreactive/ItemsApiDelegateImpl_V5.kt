package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.model.Item
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/coroutine")
@FlowPreview
class ItemsApiDelegateImpl_V5(val itemOps: ItemService) {

    @RequestMapping(value = ["/items"], produces = ["text/event-stream"], method = [RequestMethod.GET])
    fun listItems(filter: String?, exchange: ServerWebExchange?): Flow<Item> = itemOps.listItems(filter)

    @RequestMapping(value = ["/live"], produces = ["text/event-stream"], method = [RequestMethod.GET])
    fun liveItems(filter: String?, exchange: ServerWebExchange?): Flow<Item> = itemOps.liveItems(filter)

}



