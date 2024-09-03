import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.extensions.filters.Filter
import java.io.FileInputStream
import java.util.*

fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    FileInputStream(fileName).use { fileInput ->
        properties.load(fileInput)
    }
    return properties
}

object Bot {
    private val properties = loadProperties("gradle.properties")
    private val myApiKey = properties.getProperty("telegram_api")
    private val bot: Bot;
    private val chatListe: MutableList<ChatId> = mutableListOf();
    init {
        bot = bot {
            token = myApiKey
            dispatch {
                // Handle the "/start" command
                command("start") {
                    val chatId = ChatId.fromId(message.chat.id)
                    val response = bot.sendMessage(chatId = chatId, text = "Hei min kjære! Nå har jeg laget en bot til deg <3 OPPDATERT.")
                    println(message)

                    // Handle the response from Telegram API
                    response.fold(ifSuccess = {
                        println("Message sent successfully")
                    },
                        ifError = {
                            println("Failed to send message: ${message}")
                        }
                    )
                }

                // Handle the "/help" command
                command("help") {
                    val chatId = ChatId.fromId(message.chat.id)
                    val response = bot.sendMessage(chatId = chatId, text = "This is a help message!")

                    response.fold(
                        ifSuccess = {
                            println("Help message sent successfully")
                        },
                        ifError = {
                            println("Failed to send help message: ${message}")
                        }
                    )
                }
                command("varsling") {
                    val chatId = ChatId.fromId(message.chat.id)
                    val response = bot.sendMessage(chatId = chatId, text = "Nå setter jeg deg opp for varsling.")
                    chatListe.add(chatId);

                    response.fold(
                        ifSuccess = {
                            println("Help message sent successfully")

                        },
                        ifError = {
                            println("Failed to send help message: ${message}")
                        }
                    )
                }

                command("hei") {
                    val chatId = ChatId.fromId(message.chat.id)
                    val response = bot.sendMessage(chatId = chatId, text = "Halla")

                    response.fold(
                        ifSuccess = {
                            println("Help message sent successfully")
                        },
                        ifError = {
                            println("Failed to send help message: ${message}")
                        }
                    )
                }

                message(Filter.Text) {
                    // Check if the message is a command or something else
                    if (message.text?.startsWith("/") == true) {
                        // This is a command, ignore it here

                    }else {

                        val chatId = ChatId.fromId(message.chat.id)
                        val response = bot.sendMessage(chatId = chatId, text = "You said: ${message.text}")

                        response.fold(
                            ifSuccess = { println("Echo message sent successfully") },
                            ifError = { println("Failed to send echo message: ${message}") }
                        )
                    }
                }
            }
        }
    }



    fun start() {
        bot.startPolling()
    }
    fun startBot(){
        bot.startPolling()
    }
    fun stopBot(){
        bot.stopPolling()
    }

    fun sendMessage(messageId: ChatId, message:String){
        val response = bot.sendMessage(chatId = messageId, text = message)

        response.fold(
            ifSuccess = {
                println("Help message sent successfully")
            },
            ifError = {
                println("Failed to send help message: ${message}")
            }
        )
    }

}