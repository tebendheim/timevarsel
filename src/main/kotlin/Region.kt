import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Region(
    @SerialName("regionId") val id: Long,
    @SerialName("regionName") val name: String,
)