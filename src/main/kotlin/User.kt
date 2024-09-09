import com.github.kotlintelegrambot.entities.ChatId

data class User(
    val chatId: ChatId,
    val id: Long?,
    val isBot: Boolean?,
    val firstName: String?,
    val lastName: String? = null,
    val username: String? = null,
    val languageCode: String? = null
)