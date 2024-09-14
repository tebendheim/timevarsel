import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class User(
    val chatId: ChatId,
    val id: Long?,
    val isBot: Boolean?,
    val firstName: String?,
    val lastName: String? = null,
    val username: String? = null,
    val languageCode: String? = null
)

@Serializable
data class Abonnent(
    @SerialName("chatId") val chatId: Long,
    @SerialName("userId") val userId: Long,
    val region: MutableList<Region> = mutableListOf()
)

@Serializable
data class Dates(
    @SerialName("date") val date: String
)

@Serializable
data class Region(
    @SerialName("regionId") val id: Long,
    @SerialName("regionName") val name: String,
)


@Serializable
data class Section(
    @SerialName("sectionId") val id: Long,
    @SerialName("sectionName") val name: String,
)

@Serializable
data class TimeSlot(
    @Serializable(with = LocalDateTimeSerializer::class)
    val fromDateTime: LocalDateTime,

    @Serializable(with = LocalDateTimeSerializer::class)
    val toDateTime: LocalDateTime
)
@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val string = value.format(formatter)
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        return LocalDateTime.parse(string, formatter)
    }
}