import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.types.TelegramBotResult
import io.ktor.server.engine.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.FileInputStream
import java.util.*


fun loadProperties(fileName: String): Properties? {
    try {
        val properties = Properties()
        FileInputStream(fileName).use { fileInput ->
            properties.load(fileInput)
        }
        return properties
    }catch(e:Exception){
        return null
    }
}


fun createDynamicKeyboard(options: List<Region>, command:String): InlineKeyboardMarkup {
    val buttons = options.map { option ->
        InlineKeyboardButton.CallbackData(text = option.name, callbackData = "$command|${option.id}")
    }

    return InlineKeyboardMarkup.create(buttons.chunked(3)) // Adjust chunk size as needed
}

// Function to create the follow-up inline keyboard dynamically based on the pressed button
fun createFollowUpKeyboard(options: List<Section>, command:String, additionalButton: List<InlineKeyboardButton. CallbackData>): InlineKeyboardMarkup {
    val sectionButtons = options.map { option ->
        InlineKeyboardButton.CallbackData(text = option.name, callbackData = "$command|${option.id}")
    }
    val buttons =  sectionButtons + additionalButton
    return InlineKeyboardMarkup.create(buttons.chunked(2)) // Adjust chunk size as needed
}

class Bot(kontroll:Controller) {
    private val control = kontroll
    private val myBot: Bot
    private val chatListe: MutableList<ChatId> = mutableListOf();


    init {
        val properties = loadProperties("gradle.properties")
        val myApiKey = properties?.getProperty("telegram_api")
        myBot = bot {
            token = myApiKey ?: System.getenv("telegram_api")
            dispatch { dispatchSetup(this) }

        }
    }

    private fun dispatchSetup(dispatcher: Dispatcher):Unit {

        dispatcher.command("start") {
//                 Handle the "/start" command
            val chatId = ChatId.fromId(message.chat.id)
            val response: TelegramBotResult<Message> = bot.sendMessage(
                chatId = chatId,
                text = "Hei min kjære! Nå har jeg laget en bot til deg <3"
            )

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
                dispatcher.command("help") {
                    val chatId = ChatId.fromId(message.chat.id)
                    val response:TelegramBotResult<Message> = bot.sendMessage(chatId = chatId, text = "This is a help message!")

                    response.fold(
                        ifSuccess = {
                            println("Help message sent successfully")
                        },
                        ifError = {
                            println("Failed to send help message: ${message}")
                        }
                    )
                }

                dispatcher.command("varsling") {
                    val chatId = ChatId.fromId(message.chat.id)
                    val response:TelegramBotResult<Message> = bot.sendMessage(chatId = chatId, text = "Nå setter jeg deg opp for varsling.")
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

                dispatcher.command("hei") {
                    val chatId = ChatId.fromId(message.chat.id)
                    val response:TelegramBotResult<Message> = bot.sendMessage(chatId = chatId, text = "Halla")

                    response.fold(
                        ifSuccess = {
                            println("Help message sent successfully")
                        },
                        ifError = {
                            println("Failed to send help message: ${message}")
                        }
                    )
                }

                dispatcher.message(Filter.Text) {
                    // Check if the message is a command or something else
                    if (message.text?.startsWith("/") == true) {
                        // This is a command, ignore it here

                    } else {

                        val chatId = ChatId.fromId(message.chat.id)
                        val response:TelegramBotResult<Message> = bot.sendMessage(chatId = chatId, text = "You said: ${message.text}")

                        response.fold(
                            ifSuccess = { println("Echo message sent successfully") },
                            ifError = { println("Failed to send echo message: ${message}") }
                        )
                    }
                }
                dispatcher.command("test1") {
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
                dispatcher.command("leggtil") {
                    val regions = control.getRegions()
                    val inlineKeyboardMarkup = createDynamicKeyboard(regions, "getSections")
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Velg ett område:",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }

                dispatcher.command("slett"){
                        val chatId = ChatId.fromId(message.chat.id)
                        val subs = control.hentSubs(message.from?.id)
                        bot.sendMessage(
                            chatId = chatId,
                            text = "Velg sted du vil fjerne varslinger for",
                            replyMarkup = createFollowUpKeyboard(subs, "Slett", listOf( InlineKeyboardButton.CallbackData(text = "Back", callbackData = "exitAction"))
                        )
                        )
//                    val chatId = ChatId.fromId(message.chat.id)
//                    val subs = control.hentSubs(message.from?.id)
//                    bot.sendMessage(
//                        chatId = chatId,
//                        text = "velg sted du vil fjerne varslinger for",
//                        replyMarkup = createSubsList(subs)
//                    )
                }


                dispatcher.callbackQuery("testButton") {
                    val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                    bot.sendMessage(ChatId.fromId(chatId), callbackQuery.data)
                }
                dispatcher.callbackQuery {
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

                        data.startsWith("Slett") -> {
                                handleFjernCallback(data, chatId, callbackQuery)

//                            handleFjernCallback(data, chatId, callbackQuery)
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


    fun startBot() {
        myBot.startPolling()
    }

    fun stopBot() {
        myBot.stopPolling()
    }

    fun sendMessage(messageId: ChatId, message: String) {
        val response:TelegramBotResult<Message> = myBot.sendMessage(chatId = messageId, text = message)

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
                val sections = control.getSections(regionId.toLong())
                val followUpKeyboard = createFollowUpKeyboard(sections, "getDates", listOf( InlineKeyboardButton.CallbackData(text = "Back", callbackData = "start")))
            myBot.sendMessage(
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
            val dates = control.getAvailDates(sectionId.toLong())
//            bot.sendMessage(chatId = chatId, text="Ledige datoer er: $dates")
//            bot.editMessageReplyMarkup(
            myBot.sendMessage(
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

    private suspend fun handleVarselCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery):String {
       return  try {
            val parts = data.split("|")
            val sectionId = parts[1]
            println(sectionId)
            val user = User(chatId, callbackQuery.from.id,callbackQuery.from.isBot, callbackQuery.from.firstName, callbackQuery.from.lastName, callbackQuery.from.username, callbackQuery.from.languageCode)
            val res = control.leggTilVarsel(user, sectionId.toLong())
            if (res.equals("error")){
                myBot.sendMessage(chatId = chatId, text = "Kunne ikke legge deg til som abonnent. Venligst prøv igjen senere.")
            }else{
                myBot.sendMessage(chatId = chatId, text = "Du vil få varslinger på denne trafikkstasjonen")
            }
            myBot.editMessageReplyMarkup(
                chatId = chatId,
                messageId = callbackQuery.message?.messageId,
                replyMarkup = null
            )
           "success"
        } catch (e: Exception) {
            println("Error processing varsel callback: ${e.message}")
           "error"
        }
    }

    private fun handleFjernCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery){
        try {
            val parts = data.split("|")
            val sectionId = parts[1]
            val fjernet = control.slettSubs(callbackQuery.from.id, sectionId.toLong())
            if (fjernet != null && fjernet){
                myBot.sendMessage(
                    chatId = chatId,
                    text = "Abonnement fjernet",
                )
                myBot.editMessageReplyMarkup(
                    chatId = chatId,
                    messageId = callbackQuery.message?.messageId,
                    replyMarkup = null
                )

            }else {
                    myBot.sendMessage(
                    chatId = chatId,
                    text = "En feil har oppstått.",
                    replyMarkup = null
                )
                handleFjernCallback(data, chatId, callbackQuery)
            }
        }catch (e:Exception) {
            println("Error in handleFjernCallback: $e")
        }
    }

    private suspend fun handleBackToRegionsCallback(chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            val regions = control.getRegions()
            val inlineKeyboardMarkup = createDynamicKeyboard(regions,"getSections" )
            myBot.editMessageReplyMarkup(
                chatId = chatId,
                messageId = callbackQuery.message?.messageId,
                replyMarkup = inlineKeyboardMarkup
            )
        } catch (e: Exception) {
            println("Error processing back to regions callback: ${e.message}")
        }
    }

    private fun handleExitActionCallback(chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            myBot.editMessageReplyMarkup(
                chatId = chatId,
                messageId = callbackQuery.message?.messageId ?: return,
                replyMarkup = null // Remove the keyboard
            )
            myBot.sendMessage(chatId, text = "You have exited the menu.")
        } catch (e: Exception) {
            println("Error processing exit action callback: ${e.message}")
        }
    }
}

