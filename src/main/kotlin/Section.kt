import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Section(
    @SerialName("sectionId") val id: Long,
    @SerialName("sectionName") val name: String,
)