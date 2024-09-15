import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.network.Response
import com.github.kotlintelegrambot.types.TelegramBotResult
import oppkjoring.OppkjoringBot
import teori.TeoriBot
import java.io.FileInputStream
import java.util.*
import teori.Controller as TController
import oppkjoring.Controller as OController




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



class Bot(){
    private val myBot: Bot
    private val teori: TeoriBot;
    private val oppkjoring: OppkjoringBot;
    private var botMode: BotInterface
    private val tcontroll:teori.Controller;
    private val ocontroll: oppkjoring.Controller;



    init {
        val properties = loadProperties("gradle.properties")
        val myApiKey = properties?.getProperty("telegram_api")
        myBot = bot {
            token = myApiKey ?: System.getenv("telegram_api")
            dispatch {
                commandSetup(this)
                callbackQuerySetup(this)
            }
        }

        tcontroll = TController(this)
        ocontroll = OController(this)
        oppkjoring = OppkjoringBot(ocontroll, this)
        teori = TeoriBot(tcontroll, this)
        botMode = teori
    }

    //@Todo: ocontroll må uvkommenteres før oppkjøring kan brukes.
    suspend fun startControll(){
        tcontroll.start()
//        ocontroll.start()
    }

    //@Todo: ocontroll må uvkommenteres før oppkjøring kan brukes.
    suspend fun oppdater(){
        tcontroll.oppdater()
//        ocontroll.oppdater()
    }

    fun startBot() {
        myBot.startPolling()
    }

    fun stopBot() {
        myBot.stopPolling()
    }


    private fun commandSetup(dispatcher: Dispatcher):Unit {
        dispatcher.command("start") {
            botMode.handleStartCommand(message)
        }

        dispatcher.command("help") {
            botMode.handleHelpCommand(message)
        }

        dispatcher.command("leggtil") {
            botMode.handleLeggTilCommand(message)
        }

        dispatcher.command("slett") {
            botMode.handleSlettCommand(message)
        }
        dispatcher.command("teori"){
            botMode = teori
            sendMessage(ChatId.fromId(message.chat.id), "Du ser nå på ledige teoritimer.")
        }
        dispatcher.command("oppkjoring"){
            oppkjoringCommand(message)
        }
    }

//  @Todo denne må endres når oppkjøring er klar.
    private fun oppkjoringCommand(message: Message){
//            botMode = oppkjoring
//            sendMessage(ChatId.fromId(message.chat.id), "Du ser nå på ledige oppkjøringstimer.")
        sendMessage(ChatId.fromId(message.chat.id), "Oppgjøringsvarsling er ikke mulig ennå.")
    }

    private fun callbackQuerySetup(dispatcher: Dispatcher){
        dispatcher.callbackQuery {
            val chatId = ChatId.fromId(callbackQuery.message?.chat?.id ?: return@callbackQuery)
            val data = callbackQuery.data

            when {
                data.startsWith("getSection") -> {
                    botMode.getSectionCallback(data, chatId, callbackQuery)
                }

                data.startsWith("getDates") -> {
                    botMode.getDatesCallback(data, chatId, callbackQuery)
                }

                data.startsWith("varsel") -> {
                    botMode.handleVarselCallback(data, chatId, callbackQuery)
                }

                data.startsWith("Slett") -> {
                    botMode.handleFjernCallback(data, chatId, callbackQuery)
                }

                data == "start" -> {
                    botMode.handleBackToRegionsCallback(chatId, callbackQuery)
                }

                data == "exitAction" -> {
                    botMode.handleExitActionCallback(chatId, callbackQuery)
                }
            }
        }

    }



    public final fun editMessageReplyMarkup(
        chatId: ChatId? = null,
        messageId: Long? = null,
        inlineMessageId: String? = null,
        replyMarkup: ReplyMarkup? = null
    ): Pair<retrofit2.Response<Response<Message>?>?, Exception?> {
        return (
           myBot.editMessageReplyMarkup(
               chatId,messageId,inlineMessageId,replyMarkup
           )
        )
    }

    public final fun sendMessage(
        chatId: ChatId,
        text: String,
        parseMode: ParseMode? = null,
        disableWebPagePreview: Boolean? = null,
        disableNotification: Boolean? = null,
        protectContent: Boolean? = null,
        replyToMessageId: Long? = null,
        allowSendingWithoutReply: Boolean? = null,
        replyMarkup: ReplyMarkup? = null,
        messageThreadId: Long? = null
    ): TelegramBotResult<Message>{
        return (
            myBot.sendMessage(chatId,text,parseMode,disableWebPagePreview,disableNotification,protectContent,replyToMessageId,allowSendingWithoutReply,replyMarkup,messageThreadId)
        )
    }


}

