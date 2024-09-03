

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

fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    FileInputStream(fileName).use { fileInput ->
        properties.load(fileInput)
    }
    return properties
}

fun main() {
    val properties = loadProperties("gradle.properties")
    val myApiKey = properties.getProperty("telegram_api")

    val bot: Bot = Bot(myApiKey)
    bot.startBot()

    val veg:Vegvesen =  Vegvesen()
try{
    runBlocking {
        val res = veg.getRegions(::makeHttpRequest)
        println("response: $res")
//        println("dette er en testprint")
//        val section = veg.getSectionId(173, ::makeHttpRequest)
//        println("sections: $section")
//        val dates = veg.getAvailDates(1693, 9,2024, ::makeHttpRequest)
//        println("dates: $dates")
//        val times = veg.getAvailTimes(1693, "2024-09-02", ::makeHttpRequest)
//        println("Times: $times")
        val alt = veg.finnAlleIRegion(172)

        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(600_0) // 10 minutes in milliseconds
                    println("nå skal det gå en melding")
            }
        }




    }
    }catch (e: Exception) {
        println("An error occurred: ${e.message}")
        e.printStackTrace()
    }
}