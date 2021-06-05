package it.vashykator.shoppinglist

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.text.Charsets.UTF_8

interface ShoppingListManager {
    fun create(shoppingItem: ShoppingItem)
    fun findAll(): List<ShoppingItem>
    fun deleteAll()
}

class HttpShoppingListManager(private val httpClient: HttpClient, private val baseUri: String) : ShoppingListManager {
    override fun create(shoppingItem: ShoppingItem) {
        val request = shoppingListItemsEndpoint()
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    """{"description": "${shoppingItem.description}"}""",
                    UTF_8
                )
            )
            .build()

        httpClient.send(request, HttpResponse.BodyHandlers.ofString())
    }

    override fun findAll(): List<ShoppingItem> {
        val request = shoppingListItemsEndpoint()
            .GET()
            .build()

        return httpClient.send(
            request,
            JsonBodyHandler.bodySubscriber(object : TypeToken<List<ShoppingItem>>() {}.type)
        ).body()
    }

    override fun deleteAll() {
        httpClient.send(shoppingListItemsEndpoint().DELETE().build(), HttpResponse.BodyHandlers.discarding())
    }

    private fun shoppingListItemsEndpoint() = HttpRequest.newBuilder()
        .uri(URI.create("$baseUri/api/shopping-list/items"))
        .header("Content-Type", "application/json")
}

object JsonBodyHandler {
    fun bodySubscriber(type: Type): (HttpResponse.ResponseInfo) -> HttpResponse.BodySubscriber<List<ShoppingItem>> =
        { asJSON(type) }

    private inline fun <reified T> asJSON(type: Type): HttpResponse.BodySubscriber<T> {
        val upstream = HttpResponse.BodySubscribers.ofString(UTF_8)
        return HttpResponse.BodySubscribers.mapping(upstream) { body -> Gson().fromJson(body, type) }
    }
}
