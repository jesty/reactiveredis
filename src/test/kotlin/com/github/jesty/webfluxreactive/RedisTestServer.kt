package com.github.jesty.webfluxreactive

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import redis.embedded.RedisExecProvider
import redis.embedded.RedisServer
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class RedisTestServer {


    private var server: RedisServer? = null

    @Value("\${spring.redis.port:6379}")
    private val redisPort: Int = 0

    private val isWindows: Boolean
        get() = System.getProperty("os.name").toLowerCase().indexOf("windows") > -1

    @PostConstruct
    fun startConnection() {
        killPreviousProcess()
        this.startConnection(redisPort)
    }

    fun startConnection(redisPort: Int) {
        val builder = RedisServer.builder()
                .redisExecProvider(RedisExecProvider.defaultProvider())
        if (this.isWindows) {
            builder.setting("maxheap 128M")
        }
        server = builder
                .port(redisPort)
                .build()
        if (!server!!.isActive) {
            server!!.start()
        }
    }

    fun killPreviousProcess() {
        if (isWindows) {
            killRedisOnWindows()
        } else {
            killOnUnixLike()
        }
    }

    @PreDestroy
    fun stopConnection() {
        server!!.stop()
    }

    private fun killRedisOnWindows() {
        try {
            Runtime.getRuntime().exec("taskkill /F /IM redis-server-2.8.19.exe")
        } catch (e: IOException) {
            System.err.println("Cannot kill Redis server on Windows")
        }

    }

    private fun killOnUnixLike() {
        try {
            Runtime.getRuntime().exec("kill $(ps -efw | grep redis-server | grep -v grep | awk '{print $2}')")
        } catch (e: IOException) {
            System.err.println("Cannot kill Redis server on Unix like system")
        }

    }

}




