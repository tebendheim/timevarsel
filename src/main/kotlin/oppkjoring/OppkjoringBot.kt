package oppkjoring

import Bot
import BotInterface
import User
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.types.TelegramBotResult
import createDynamicKeyboard
import createFollowUpKeyboard

class OppkjoringBot (private val control: Controller, private val myBot: Bot): BotInterface {

    override fun handleStartCommand(message: Message){
        val response: TelegramBotResult<Message> = myBot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "Hei min kjære! Nå har jeg laget en bot til deg <3 Oppkjøring"
        )
        response.fold(ifSuccess = {
            println("Message sent successfully")
        },
            ifError = {
                println("Failed to send message: ${message}")
            }
        )
    }

    override fun handleHelpCommand(message: Message){
        val chatId = ChatId.fromId(message.chat.id)
        val response:TelegramBotResult<Message> = myBot.sendMessage(chatId = chatId, text = "This is a help message!")

        response.fold(
            ifSuccess = {
                println("Help message sent successfully")
            },
            ifError = {
                println("Failed to send help message: ${message}")
            }
        )
    }

    override suspend fun handleLeggTilCommand(message: Message){
        val regions = control.getRegions()
        val inlineKeyboardMarkup = createDynamicKeyboard(regions, "getSections")
        myBot.sendMessage(
            chatId = ChatId.fromId(message.chat.id),
            text = "Velg ett område:",
            replyMarkup = inlineKeyboardMarkup
        )
    }

    override suspend fun handleSlettCommand(message: Message){
        val chatId = ChatId.fromId(message.chat.id)
        val subs = control.hentSubs(message.from?.id)
        myBot.sendMessage(
            chatId = chatId,
            text = "Velg sted du vil fjerne varslinger for",
            replyMarkup = createFollowUpKeyboard(
                subs, "Slett", listOf(InlineKeyboardButton.CallbackData(text = "Back", callbackData = "exitAction"))
            )
        )
    }

    override suspend fun getSectionCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            val parts = data.split("|")
            val regionId = parts[1].toInt()
            // Perform operations in IO context
            val sections = control.getSections(regionId.toLong())
            val followUpKeyboard = createFollowUpKeyboard(
                sections,
                "getDates",
                listOf(InlineKeyboardButton.CallbackData(text = "Back", callbackData = "start"))
            )

                myBot.sendMessage(
                    chatId = chatId,
                    text = "Velg ett sted: ",
                    replyMarkup = followUpKeyboard
                )

        } catch (e: Exception) {
            println("Error processing regionId callback: ${e.message}")
        }
    }


    override suspend fun getDatesCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery) {
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

    override suspend fun handleVarselCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery):String {
        return  try {
            val parts = data.split("|")
            val sectionId = parts[1]
            println(sectionId)
            val user = User(
                chatId,
                callbackQuery.from.id,
                callbackQuery.from.isBot,
                callbackQuery.from.firstName,
                callbackQuery.from.lastName,
                callbackQuery.from.username,
                callbackQuery.from.languageCode
            )
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

    override fun handleFjernCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery){
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

    override suspend fun handleBackToRegionsCallback(chatId: ChatId, callbackQuery: CallbackQuery) {
        try {
            val regions = control.getRegions()
            val inlineKeyboardMarkup = createDynamicKeyboard(regions, "getSections")
            myBot.editMessageReplyMarkup(
                chatId = chatId,
                messageId = callbackQuery.message?.messageId,
                replyMarkup = inlineKeyboardMarkup
            )
        } catch (e: Exception) {
            println("Error processing back to regions callback: ${e.message}")
        }
    }

    override fun handleExitActionCallback(chatId: ChatId, callbackQuery: CallbackQuery) {
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
