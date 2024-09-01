import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dates(
    @SerialName("date") val date: String
)