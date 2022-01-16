# What is telegram-bot-starter?
Telegram-bot-starter helps to create a basic Telegram bot. 
The bot can operate in 2 modes:
* long polling
* webhook

In long polling mode, the bot querying Telegram API to get new messages.
In webhook mode, the bot registers a webhook URL via Telegram API on startup. Telegram will send updates to this webhook.

To implement your own bot you just need to provide your own implementation of MessagesProcessor interface. 
You can find an example of MessagesProcessor implementation in bot-example module.

# Before start
Before implementing your bot, you need to create it in Telegram.
Please follow the instruction from Telegram: https://core.telegram.org/bots#3-how-do-i-create-a-bot.
Bot Father will give you a bot token.

# Build&Run using Docker
To build your bot locally, please do:
1. *cd* to the root of the project.
2. Execute *docker build . --tag="telegram-bot-starter:v1.0"*. 

To run in *longpolling* mode: 
1. Execute *docker run telegram-bot-starter:v1.0 -env TELEGRAM_BOT_TOKEN=bot_token -env TELEGRAM_BOT_PROTOCOL=longpolling*.

To run in *webhook* mode:
1. Execute *docker run telegram-bot-starter:v1.0 -env TELEGRAM_BOT_TOKEN=bot_token -env TELEGRAM_BOT_PROTOCOL=webhook -env TELEGRAM_BOT_WEBHOOK=url_of_your_weebhook*.
For webhook mode you need to have a webhook with permanent URL to your service.

# Build&Run Maven
To run using Maven, please do:
1. *cd* to the root of the project.
2. Execute: *./mvnw spring-boot:run -DTELEGRAM_BOT_TOKEN=bot_token -DTELEGRAM_BOT_PROTOCOL=longpolling*

To test webhook locally you can extend TeleBreakApplicationTest.kt Spring Boot test with your scenarios. It uses MockServer to mock Telegram server. 