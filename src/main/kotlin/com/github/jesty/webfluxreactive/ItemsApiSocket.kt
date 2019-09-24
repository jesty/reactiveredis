package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.model.Item
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import javax.annotation.PostConstruct

@Controller
@FlowPreview
class ItemsApiSocket(val itemOps: ItemService) {

    @MessageMapping("items")
    fun listItems(filter: String?): Flow<Item> {
        return itemOps.listItems("")
    }

    @MessageMapping("live")
    fun liveItems(filter: String?): Flow<Item> {
        return itemOps.liveItems(filter)
    }

}



