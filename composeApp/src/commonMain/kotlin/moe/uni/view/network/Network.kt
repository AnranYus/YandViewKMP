package moe.uni.view.network

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class Network {
    companion object {
        const val ROOT_HTTP = "https://yande.re"
        fun obtainClient() = Network()
    }

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.i(message)
                }
            }
            level = LogLevel.ALL
        }
    }.also { Napier.base(DebugAntilog()) }

    suspend fun request(
        subUrl: String,
        params: Map<String, String> = hashMapOf<String, String>()
    ): HttpResponse {
        client.use {
            return it.get(ROOT_HTTP) {
                url {
                    appendPathSegments(subUrl)
                    params.forEach {
                        parameters.append(it.key, it.value)
                    }
                }
            }
        }
    }
}


suspend inline fun <reified T> request(
    subUrl: String,
    params: HashMap<String, String>.() -> Unit
): T {
    val map = HashMap<String, String>()
    params.invoke(map)
    return Network.obtainClient().request(subUrl, map).body<T>()
}
