import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Region(
    @SerialName("regionId") val id: Int,
    @SerialName("regionName") val name: String,
    val sections:MutableList<Section>
)