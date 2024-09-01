import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

// Create an HTTP client
val client = HttpClient()

// Define the actual request function
suspend fun makeHttpRequest(url: String): String {
    return client.get(url).bodyAsText()
}