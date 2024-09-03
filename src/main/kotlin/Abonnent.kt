import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Abonnent(
    @SerialName("chatId") val chatId: Long,
    @SerialName("userId") val userId: Long,
    val region: MutableList<Region> = mutableListOf()
)