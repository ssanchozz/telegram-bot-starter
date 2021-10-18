package com.ssanchozzz.telebreak.rest.helper

import com.fasterxml.jackson.core.type.TypeReference
import com.ssanchozzz.telebreak.rest.helper.JsonHelper.objectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import java.time.Duration
import java.time.temporal.ChronoUnit

object RestHelper {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val restTemplate = RestTemplateBuilder()
        .setConnectTimeout(Duration.of(60, ChronoUnit.SECONDS))
        .setReadTimeout(Duration.of(60, ChronoUnit.SECONDS))
        .build()

    fun <T> invokeGet(url: String): T {
        log.info("Making a request to $url")
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<HashMap<String, String>>() {}
        )
        log.debug(response.toString())
        log.info("Got a response from $url, ok = ${response.body!!["ok"]}")

        return objectMapper.readValue(
            response.body!!["result"],
            object : TypeReference<T>() {}
        )
    }
}

