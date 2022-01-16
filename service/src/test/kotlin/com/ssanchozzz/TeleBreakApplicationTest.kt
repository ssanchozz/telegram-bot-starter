package com.ssanchozzz

import com.ssanchozzz.rest.BotStarter
import org.intellij.lang.annotations.Language
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
internal class TeleBreakApplicationTest {

    @Autowired
    private lateinit var botStarter: BotStarter

    @Value("\${telegram.bot.token}")
    var token: String = "sdf"

    private lateinit var mockServer: ClientAndServer

    @Before
    fun startBot() {
        startMockServer()
        botStarter.startBot()
    }

    fun startMockServer() {
        mockServer = startClientAndServer(80)

        // given
        @Language("JSON")
        val responseBody = """
             {
               "ok": true,
               "response": "OK"
             }
        """.trimIndent()
        mockServer
            .`when`(
                request()
                    .withPath("/bot$token/setWebhook")
                    .withQueryStringParameter("url", "https://localhost/$token")
            )
            .respond(
                response()
                    .withBody(
                        responseBody
                    )
                    .withContentType(MediaType.APPLICATION_JSON)
            )
    }

    @After
    fun stopMockServer() {
        mockServer.stop()
    }

    @Test
    fun `start up a bot and register webhook in mock telegram server`() {
        // nothing to do
    }
}

