import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Vegvesen {
    private suspend fun makeGetRequest(url: String): String {
        val client = HttpClient(CIO)

        client.use { client ->
            val response: HttpResponse = client.get(url)
            return response.bodyAsText()
        }
    }

    suspend fun getRegions():List<Region>  {

        try{
            val res = withContext(Dispatchers.IO) {
                makeGetRequest("https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getregions")
            }
//            println("response: $res")
            val json = Json { ignoreUnknownKeys = true }
            val regions: List<Region> = json.decodeFromString(res)
            regions.forEach { println(it) }
            return regions;
        }catch (e: Exception) {
            println("An error occurred: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getSectionId(regionId: Int): List<Section>{
      val url = "https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getsectionswithservice/438/$regionId";
        val res = withContext(Dispatchers.IO){
            makeGetRequest(url)
        }
        val json = Json{ignoreUnknownKeys=true}
        val sections:List<Section> = json.decodeFromString(res)
        return sections

    }

    suspend fun getAvailDates(sectionId:Int, month:Int, year:Int): List<String>{
        val url = "https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getavailabledates?sectionId=$sectionId&serviceId=438&month=$month&year=$year"
        val res = withContext(Dispatchers.IO){
            makeGetRequest(url)
        }
        val jsonResponse = """["2024-09-02"]""" // This is your response
        val json = Json{ignoreUnknownKeys=true}
        val dates:List<String> = json.decodeFromString(res)
        return dates;
    }

    suspend fun getAvailTimes(sectionId:Int, date:String){
        val url = "https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/booking/getavailabletime?sectionId=$sectionId&serviceId=438&fromDate=$date"
        val res = withContext(Dispatchers.IO){
            makeGetRequest(url)
        }
        println(res)
    }
}
