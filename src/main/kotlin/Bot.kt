import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.inlineQuery
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.CallbackQuery
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
        InlineKeyboardButton.CallbackData(text = option.name, callbackData = "getSections|${option.id}")
    }

    return InlineKeyboardMarkup.create(buttons.chunked(3)) // Adjust chunk size as needed
}

// Function to create the follow-up inline keyboard dynamically based on the pressed button
fun createFollowUpKeyboard(options: List<Section>): InlineKeyboardMarkup {
    val sectionButtons = options.map { option ->
        InlineKeyboardButton.CallbackData(text = option.name, callbackData = "getDates|${option.id}")
    }
    val additionalButton = listOf( InlineKeyboardButton.CallbackData(text = "Back", callbackData = "start"))
    val buttons =  sectionButtons + additionalButton
    return InlineKeyboardMarkup.create(buttons.chunked(2)) // Adjust chunk size as needed
}

class Bot(kontroll:Controller) {
    private val control = kontroll
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
                    val regions = control.getRegions()
                    val inlineKeyboardMarkup = createDynamicKeyboard(regions)
                    println("i Test")
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Velg ett område:",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }


                callbackQuery("testButton") {
                    val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                    bot.sendMessage(ChatId.fromId(chatId), callbackQuery.data)
                }
                callbackQuery {
                    val chatId = ChatId.fromId(callbackQuery.message?.chat?.id ?: return@callbackQuery)
                    val data = callbackQuery.data

                    when {
                        data.startsWith("getSection") -> {
                            getSectionCallback(data, chatId, callbackQuery)
                        }

                        data.startsWith("getDates") -> {
                            getDatesCallback(data, chatId, callbackQuery)
                        }

                        data.startsWith("varsel") -> {
                            handleVarselCallback(data, chatId, callbackQuery)
                        }

                        data == "start" -> {
                            handleBackToRegionsCallback(chatId, callbackQuery)
                        }

                        data == "exitAction" -> {
                            handleExitActionCallback(chatId, callbackQuery)
                        }
                    }
                }
            }
        }
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

    private suspend fun getSectionCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            val parts = data.split("|")
            val regionId = parts[1].toInt()
            // Perform operations in IO context
                val sections = control.getSections(regionId)
                val followUpKeyboard = createFollowUpKeyboard(sections)
                bot.sendMessage(
                    chatId = chatId,
                    text = "Velg ett sted: ",
                    replyMarkup = followUpKeyboard
                )
        } catch (e: Exception) {
            println("Error processing regionId callback: ${e.message}")
        }
    }


    private suspend fun getDatesCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            val parts = data.split("|")
            val sectionId = parts[1]

            println("i getDates med SectionId: $sectionId")
            val dates = control.getAvailDates(sectionId.toInt())
//            bot.sendMessage(chatId = chatId, text="Ledige datoer er: $dates")
//            bot.editMessageReplyMarkup(
            bot.sendMessage(
                chatId = chatId,
//                messageId = callbackQuery.message?.messageId,
                text =  if (dates.isEmpty()) {
                    "Det er dessverre ingen ledige datoer."
                }
                else {
                    "Ledige datoer er:\n${dates.joinToString("\n")}"
                }
                        +
                        "\n\nØnsker du å få varslinger når det kommer ledige timer?",
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
        } catch (e: Exception) {
            println("Error processing sectionId callback: ${e.message}")
        }
    }

    private suspend fun handleVarselCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            val dates = control.getSections(173)
            val parts = data.split("|")
            val sectionId = parts[1]
            val user = User(callbackQuery.from.id,callbackQuery.from.isBot, callbackQuery.from.firstName, callbackQuery.from.lastName, callbackQuery.from.username, callbackQuery.from.languageCode)
            control.leggTilVarsel(chatId, user, sectionId.toInt())

            println("finner ledige tidspunkt")
            bot.sendMessage(chatId = chatId, text = "Ledige dager er: $dates")
            bot.editMessageReplyMarkup(
                chatId = chatId,
                messageId = callbackQuery.message?.messageId,
                replyMarkup = null
            )
        } catch (e: Exception) {
            println("Error processing varsel callback: ${e.message}")
        }
    }

    private suspend fun handleBackToRegionsCallback(chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            val regions = control.getRegions()
            val inlineKeyboardMarkup = createDynamicKeyboard(regions)
            bot.editMessageReplyMarkup(
                chatId = chatId,
                messageId = callbackQuery.message?.messageId,
                replyMarkup = inlineKeyboardMarkup
            )
        } catch (e: Exception) {
            println("Error processing back to regions callback: ${e.message}")
        }
    }

    private suspend fun handleExitActionCallback(chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            bot.editMessageReplyMarkup(
                chatId = chatId,
                messageId = callbackQuery.message?.messageId ?: return,
                replyMarkup = null // Remove the keyboard
            )
            bot.sendMessage(chatId, text = "You have exited the menu.")
        } catch (e: Exception) {
            println("Error processing exit action callback: ${e.message}")
        }
    }
}

