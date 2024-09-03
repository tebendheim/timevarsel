import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    @SerialName("chatId") val chatId: Long,
    @SerialName("userId") val userId: Long
)