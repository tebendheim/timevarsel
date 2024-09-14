import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId

interface BotInterface {
    fun handleStartCommand(message: Message)
    fun handleHelpCommand(message: Message)
    suspend fun handleLeggTilCommand(message: Message)
    suspend fun handleSlettCommand(message: Message)
    suspend fun getSectionCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery)
    suspend fun getDatesCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery)
    suspend fun handleVarselCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery):String
    fun handleFjernCallback(data: String, chatId: ChatId, callbackQuery: CallbackQuery)
    suspend fun handleBackToRegionsCallback(chatId: ChatId, callbackQuery: CallbackQuery)
    fun handleExitActionCallback(chatId: ChatId, callbackQuery: CallbackQuery)
}