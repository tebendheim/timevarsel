import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Section(
    @SerialName("sectionId") val id: Int,
    @SerialName("sectionName") val name: String,
    val region:Region,
    val dates:List<Dates>
)