package com.ssanchozzz.telebreak.rest.helper

import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import java.time.Duration
import java.time.temporal.ChronoUnit

object RestHelper {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val restTemplate = RestTemplateBuilder()
        .setConnectTimeout(Duration.of(60, ChronoUnit.SECONDS))
        .setReadTimeout(Duration.of(60, ChronoUnit.SECONDS))
        .build()

    @Suppress("UNCHECKED_CAST")
    fun <R> invokeGet(url: String): R = invokeHttp(url, HttpMethod.GET, null)

    @Suppress("UNCHECKED_CAST")
    fun <R, T> invokePost(url: String, body: T): R = invokeHttp(url, HttpMethod.POST, body)

    @Suppress("UNCHECKED_CAST")
    private fun <R, T> invokeHttp(
        url: String,
        httpMethod: HttpMethod,
        body: T?
    ): R {
        log.info("Making a request to $url")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val httpEntity = if (body == null) {
            HttpEntity(headers)
        } else {
            HttpEntity(body, headers)
        }

        val response = restTemplate.exchange(
            url,
            httpMethod,
            httpEntity,
            object : ParameterizedTypeReference<HashMap<String, Any>>() {}
        )
        log.debug(response.toString())
        log.info("Got a response from $url, ok = ${response.body!!["ok"]}")

        return response.body!!["result"] as R
    }
}

