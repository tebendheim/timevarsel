import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.inlineQuery
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.io.FileInputStream
import java.util.*




fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    FileInputStream(fileName).use { fileInput ->
        properties.load(fileInput)
    }
    return properties
}


fun createDynamicKeyboard(options: List<Region>): InlineKeyboardMarkup {
    val buttons = options.map { option ->
        InlineKeyboardButton.CallbackData(text = option.name, callbackData = "regionID|${option.id}")
    }

    return InlineKeyboardMarkup.create(buttons.chunked(3)) // Adjust chunk size as needed
}

// Function to create the follow-up inline keyboard dynamically based on the pressed button
fun createFollowUpKeyboard(options: List<Section>): InlineKeyboardMarkup {
    val sectionButtons = options.map { option ->
        InlineKeyboardButton.CallbackData(text = option.name, callbackData = "sectionId|$option.id")
    }
    val additionalButton = listOf( InlineKeyboardButton.CallbackData(text = "Back", callbackData = "../"))
    val buttons = additionalButton + sectionButtons
    return InlineKeyboardMarkup.create(buttons.chunked(2)) // Adjust chunk size as needed
}

class Bot(controller:Controller) {
    private val properties = loadProperties("gradle.properties")
    private val myApiKey = properties.getProperty("telegram_api")
    private val bot: Bot;
    private val chatListe: MutableList<ChatId> = mutableListOf();

    init {
        bot = bot {
            token = myApiKey
            dispatch {
//                 Handle the "/start" command
                command("start") {
                    val chatId = ChatId.fromId(message.chat.id)
                    val response = bot.sendMessage(
                        chatId = chatId,
                        text = "Hei min kjære! Nå har jeg laget en bot til deg <3 OPPDATERT."
                    )
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

                    } else {

                        val chatId = ChatId.fromId(message.chat.id)
                        val response = bot.sendMessage(chatId = chatId, text = "You said: ${message.text}")

                        response.fold(
                            ifSuccess = { println("Echo message sent successfully") },
                            ifError = { println("Failed to send echo message: ${message}") }
                        )
                    }
                }
                command("test1") {
                    val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = "Test Inline Button",
                                callbackData = "testButton"
                            )
                        ),
                        listOf(InlineKeyboardButton.CallbackData(text = "Show alert", callbackData = "showAlert")),
                    )
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Hello, inline buttons!",
                        replyMarkup = inlineKeyboardMarkup,
                    )
                }
                command("test") {
                    val regions = controller.getRegions()
                    val inlineKeyboardMarkup = createDynamicKeyboard(regions)
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Choose an option:",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }


                callbackQuery("testButton") {
                    val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                    bot.sendMessage(ChatId.fromId(chatId), callbackQuery.data)
                }

                callbackQuery(
                    callbackData = "showAlert",
                    callbackAnswerText = "HelloText",
                    callbackAnswerShowAlert = true,
                ) {
                    val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                    bot.sendMessage(ChatId.fromId(chatId), callbackQuery.data)
                }

                callbackQuery ("regionId"){
                    val chatId = ChatId.fromId(callbackQuery.message?.chat?.id ?: return@callbackQuery)
                    val data = callbackQuery.data

                    when {
                        data.startsWith("regionID") -> {
                            try {
                                val parts = data.split("|")
                                val regionId = parts[1].toInt()

                                withContext(Dispatchers.IO){
                                    bot.sendMessage(chatId, "Jobber med saken")
                                    val sections = controller.getSections(regionId)
                                    println("Har nå sections i bot $sections")
                                    val followUpKeyboard = createFollowUpKeyboard(sections)
                                    bot.editMessageReplyMarkup(
                                        chatId = chatId,
                                        messageId = callbackQuery.message?.messageId ?: return@withContext,
                                        replyMarkup = followUpKeyboard
                                    )

                                }
                                println("har gått videre")
                                 // Debugging output

                            } catch (e: Exception) {
                                println("Error processing regionID callback: ${e.message}")
                            }

                        }


                        data.startsWith("sectionId") -> {
                            try{
                            // Generate follow-up keyboard based on the selected option
                            runBlocking {
                                launch {
                                    val parts = data.split("|")
                                    val sectionId = parts[1]
                                    bot.editMessageReplyMarkup(
                                        chatId = chatId,
                                        messageId = callbackQuery.message?.messageId ?: return@launch,
                                        replyMarkup = InlineKeyboardMarkup.create(
                                            listOf(
                                                InlineKeyboardButton.CallbackData(
                                                    text = "ja",
                                                    callbackData = "varsel|${sectionId}"
                                                ),
                                                InlineKeyboardButton.CallbackData(text = "nei", callbackData = "exitAction")
                                            )
                                        )
                                    )

                                }
                            }
                            }catch(e:Exception){
                                println("Error processing regionID callback: ${e.message}")
                            }
                        }
                        data.startsWith("varsel") -> {
                                val dates = controller.getSections(173)
                                // Generate follow-up keyboard based on the selected option
                                val parts = data.split("|")
                                val sectionId = parts[1]

                                println("finner ledige tidspunkt")
                                bot.sendMessage(chatId = chatId, text = "Ledige dager er: $dates")
                                bot.editMessageReplyMarkup(
                                    chatId = chatId,
                                    messageId = callbackQuery.message?.messageId,
                                    replyMarkup = null
                                )
                            }




                        data == ("../") -> {
                            val regions = controller.getRegions()
                            val inlineKeyboardMarkup = createDynamicKeyboard(regions)
                            bot.editMessageReplyMarkup(
                                chatId = chatId,
                                messageId = callbackQuery.message?.messageId ?: return@callbackQuery,
                                replyMarkup = inlineKeyboardMarkup
                            )
                        }


                    }
                }
                    callbackQuery("exitAction") {
                        val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery

                        // Edit the message to remove the inline keyboard
                        bot.editMessageReplyMarkup(
                            chatId = ChatId.fromId(chatId),
                            messageId = callbackQuery.message?.messageId ?: return@callbackQuery,
                            replyMarkup = null // Remove the keyboard
                        )
                        bot.sendMessage(ChatId.fromId(chatId), text = "You have exited the menu.")
                    }
                }
            }
        }




        fun start() {
            bot.startPolling()
        }

        fun startBot() {
            bot.startPolling()
        }

        fun stopBot() {
            bot.stopPolling()
        }

        fun sendMessage(messageId: ChatId, message: String) {
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

