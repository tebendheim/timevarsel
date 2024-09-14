import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.FileInputStream
import java.util.*

// Create an HTTP client
val client = HttpClient()

// Define the actual request function
suspend fun makeHttpRequest(url: String): String {
    return client.get(url).bodyAsText()
}

fun loadProperties(fileName: String): Properties? {
    try {
        val properties = Properties()
        FileInputStream(fileName).use { fileInput ->
            properties.load(fileInput)
        }
        return properties
    }catch(e:Exception){
        return null
    }
}