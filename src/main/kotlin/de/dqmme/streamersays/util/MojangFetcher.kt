package de.dqmme.streamersays.util

import com.google.gson.JsonParser
import okhttp3.*
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.io.IOException
import java.util.*
import javax.security.auth.callback.Callback

object MojangFetcher {
    private val client: OkHttpClient = OkHttpClient()
    fun fetchOfflinePlayerFromNameAsync(name: String, offlinePlayerCallback: OfflinePlayerCallback) {
        fetchUuidFromNameAsync(name, object : UuidCallback {
            override fun call(uuid: UUID?) {
                if (uuid == null) {
                    offlinePlayerCallback.call(null)
                } else {
                    offlinePlayerCallback.call(Bukkit.getOfflinePlayer(uuid))
                }
            }
        })
    }

    fun fetchUuidFromNameAsync(name: String, uuidCallback: UuidCallback) {
        val request: Request = Request.Builder()
            .url("https://api.mojang.com/users/profiles/minecraft/$name")
            .build()
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback, okhttp3.Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val responseBody: String = response.body!!.string()
                val jsonParser = JsonParser()
                if (jsonParser.parse(responseBody).isJsonObject) {
                    val jsonTree = jsonParser.parse(responseBody).asJsonObject
                    val uuidElement = jsonTree["id"]
                    uuidCallback.call(UUID.fromString(formatUuid(uuidElement.asString)))
                } else {
                    uuidCallback.call(null)
                }
            }

            override fun onFailure(call: Call, e: IOException) {}
        })
    }

    private fun formatUuid(uuid: String): String {
        return uuid.replaceFirst(
            "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)".toRegex(),
            "$1-$2-$3-$4-$5"
        )
    }

    interface UuidCallback {
        fun call(uuid: UUID?)
    }

    interface OfflinePlayerCallback {
        fun call(offlinePlayer: OfflinePlayer?)
    }
}