package com.github.jesty.webfluxreactive

import com.github.jesty.webfluxreactive.model.Item
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class RedisTest {

    @Autowired
    private lateinit var itemOps: ReactiveRedisOperations<String, Item>

    @Test
    fun testSet() {
        val opsForValue = itemOps.opsForValue()
        val item = Item()
        item.id = 1
        item.name = "Davide"
        opsForValue.set("a", item).block()
        Assert.assertEquals(item, opsForValue.get("a").block())
    }

}