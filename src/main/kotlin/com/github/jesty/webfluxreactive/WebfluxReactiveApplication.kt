package com.github.jesty.webfluxreactive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class WebfluxReactiveApplication

fun main(args: Array<String>) {
    runApplication<WebfluxReactiveApplication>(*args)
}
