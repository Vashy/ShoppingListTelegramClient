package it.vashykator.shoppinglist

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import com.github.kotlintelegrambot.logging.LogLevel
import com.github.kotlintelegrambot.network.fold

private const val tokenEnvKey = "SHOPPING_LIST_BOT_TOKEN"

fun startPolling(manager: ShoppingListManager) {
    val bot = bot {
        token = readEnv(tokenEnvKey)
        logLevel = LogLevel.All(networkLogLevel = LogLevel.Network.Basic)
        dispatch {
            message { println("Message from ${message.chat.id}: ${message.text}") }
            command("a") { handleAddCommand(manager) }
            command("l") { handleListCommand(manager) }
            command("c") { handleClearCommand(manager) }
        }
    }
    bot.startPolling()
}

private fun CommandHandlerEnvironment.handleClearCommand(manager: ShoppingListManager) {
    manager.deleteAll()
    bot.sendMessage(ChatId.fromId(message.chat.id), text = "Lista cancellata!")
}

private fun CommandHandlerEnvironment.handleListCommand(manager: ShoppingListManager) {
    val items = manager.findAll()
    if (items.isNotEmpty())
        bot.sendMessage(ChatId.fromId(message.chat.id), text = format(items))
    else
        bot.sendMessage(ChatId.fromId(message.chat.id), text = "<vuoto>")
}

private fun CommandHandlerEnvironment.handleAddCommand(manager: ShoppingListManager) {
    val botName = bot.getMe().first?.body()?.result?.username ?: ""
    val text = message.text!!.removePrefix("/a").removePrefix("@$botName").trim()
    manager.create(ShoppingItem(text))
    bot.sendMessage(
        chatId = ChatId.fromId(message.chat.id),
        text = "\"*${text.escaped()}*\" aggiunto\\!",
        parseMode = MARKDOWN_V2,
        replyToMessageId = message.messageId,
    )
}

private fun String.escaped() = this
    .replace("!", "\\!")
    .replace("/", "\\/")

private fun format(items: List<ShoppingItem>): String {
    return items
        .map { it.description }
        .joinToString("\n") { "- $it" }
}
