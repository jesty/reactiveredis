package com.github.jesty.webfluxreactive

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.web.reactive.function.server.RouterFunctions.resources
import org.springframework.web.reactive.function.server.router


@Configuration
class WebConfiguration {

    @Bean
    fun staticResourceRouter() = resources("/**", ClassPathResource("static/"))

    @Bean
    fun indexRouter(@Value("classpath:/static/index.html") indexHtml: Resource) = router {
        GET("/") {
            ok().contentType(TEXT_HTML).syncBody(indexHtml)
        }
    }

}