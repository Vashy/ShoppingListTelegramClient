package it.vashykator.shoppinglist

import java.lang.IllegalStateException
import java.net.http.HttpClient

const val serverBasePathEnvKey = "SHOPPING_LIST_SERVER_BASE_URI"

fun main() {
    val client: HttpClient = HttpClient.newBuilder().build()

    val httpShoppingListManager = HttpShoppingListManager(client, readEnv(serverBasePathEnvKey))

    startPolling(httpShoppingListManager)
}

class ShoppingItem(val description: String)

fun readEnv(tokenEnvKey: String): String =
    System.getenv(tokenEnvKey)
        ?: throw IllegalStateException("Please Provide a valid bot token via Env: $tokenEnvKey")
