

import io.ktor.http.cio.*
import java.util.Properties
import java.io.FileInputStream

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.network.fold
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.webhook
import io.ktor.http.HttpStatusCode
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import kotlinx.coroutines.*
import java.io.File


fun main() {

    val controller: Controller = Controller()
    val veg:Vegvesen =  Vegvesen()
try{

    runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            controller.oppdater(true)
            while (true) {
                delay(600_0) // 600_000 10 minutes in milliseconds
                    controller.oppdater(false)
            }
        }
    }
    }catch (e: Exception) {
        println("An error occurred: ${e.message}")
        e.printStackTrace()
    }
}