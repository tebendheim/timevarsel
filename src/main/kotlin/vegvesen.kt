


import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth


class Vegvesen {

    suspend fun getRegions(requestFunction: suspend (String) -> String):List<Region>  {

        try{
            val res = withContext(Dispatchers.IO) {
                requestFunction("https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getregions")
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

    suspend fun getSectionId(regionId: Int, requestFunction: suspend (String) -> String): List<Section>{
        println("I getSectionId med id $regionId")
      val url = "https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getsectionswithservice/438/$regionId";
        val res = withContext(Dispatchers.IO){
            requestFunction(url)
        }
        val json = Json{ignoreUnknownKeys=true}
        val sections:List<Section> = json.decodeFromString(res)
        println("retur er $sections")
        return sections

    }

    suspend fun getAvailDates(sectionId:Int, month:Int, year:Int, requestFunction: suspend (String) -> String): List<String>{
        println("kommet inn i funksjonen")
        val url = "https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getavailabledates?sectionId=$sectionId&serviceId=438&month=$month&year=$year"
        val res = withContext(Dispatchers.IO){
            requestFunction(url)
        }
        try {
            val json = Json { ignoreUnknownKeys = true }
            val dates: List<String> = json.decodeFromString(res)
            println("returnerer datoer")
            return dates;
        }catch (e: SerializationException) {
            // Handle specific JSON parsing errors
//            println("Serialization error occurred: ${e.message}")
            return emptyList()
        }
    }

    suspend fun getAvailTimes(sectionId:Int, date:String, requestFunction: suspend (String) -> String):List<LocalTime> {
        val url =
            "https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/booking/getavailabletime?sectionId=$sectionId&serviceId=438&fromDate=$date"
        val res = withContext(Dispatchers.IO) {
            requestFunction(url)
        }
        try{

//            val jsonResponse = """[{"fromDateTime":"2024-09-02T11:00:00","toDateTime":"2024-09-02T11:15:00"}]"""
            val fromTimes: MutableList<LocalTime> = mutableListOf()

            val json = Json { ignoreUnknownKeys = true }

            // Deserialize the JSON array into a list of TimeSlot objects
            val timeSlots: List<TimeSlot> = json.decodeFromString(res)
            timeSlots.forEach { timeSlot ->
                fromTimes.add(timeSlot.fromDateTime.toLocalTime())
            }

            return fromTimes

        }catch (e: SerializationException) {
            // Handle specific JSON parsing errors
//            println("Serialization error occurred: ${e.message}")
            return emptyList()
        }
    }


    suspend fun finnAlleIRegion(regionId: Int){
        val sections: List<Section> = getSectionId(regionId, ::makeHttpRequest)
        val currentDate = LocalDate.now()
        // Extract current month and year
        val currentMonth = currentDate.monthValue
        val currentYear = currentDate.year

        // Calculate the next month and year
        val nextMonthDate = currentDate.plusMonths(1)
        val nextMonth = nextMonthDate.monthValue
        val nextYear = nextMonthDate.year
        print(nextYear)
        sections.forEach{section ->
            val resm1 = getAvailDates(section.id, month=currentMonth, year = currentYear, ::makeHttpRequest)
            val resm2 = getAvailDates(section.id, month=nextMonth, year=nextYear, ::makeHttpRequest )
            val list = resm1 + resm2
            println(section.name)
            println(list)
        }
    }
    suspend fun finnDatoer(sectionId: Int):List<String>{
        val currentDate = LocalDate.now()
        // Extract current month and year
        val currentMonth = currentDate.monthValue
        val currentYear = currentDate.year

        // Calculate the next month and year
        val nextMonthDate = currentDate.plusMonths(1)
        val nextMonth = nextMonthDate.monthValue
        val nextYear = nextMonthDate.year
        val resm1 = getAvailDates(sectionId, month=currentMonth, year = currentYear, ::makeHttpRequest)
        val resm2 = getAvailDates(sectionId, month=nextMonth, year = nextYear,::makeHttpRequest)
        return resm1

    }
}
