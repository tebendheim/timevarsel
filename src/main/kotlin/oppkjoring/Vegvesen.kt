package oppkjoring


import Region
import Section
import TimeSlot
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import makeHttpRequest
import java.time.LocalDate
import java.time.LocalTime


class Vegvesen {

    suspend fun getRegions(requestFunction: suspend (String) -> String):List<Region>  {

        try{
            val res = withContext(Dispatchers.IO) {
                requestFunction("https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getregions")
            }
            val json = Json { ignoreUnknownKeys = true }
            val regions: List<Region> = json.decodeFromString(res)
            return regions;
        }catch (e: Exception) {
            println("An error occurred: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getSectionId(regionId: Long, requestFunction: suspend (String) -> String): List<Section>{
      val url = "https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getsectionswithservice/438/$regionId";
        val res = withContext(Dispatchers.IO){
            requestFunction(url)
        }
        val json = Json{ignoreUnknownKeys=true}
        val sections:List<Section> = json.decodeFromString(res)
        return sections

    }

    suspend fun getAvailDates(sectionId:Long, month:Int, year:Int, requestFunction: suspend (String) -> String): List<String>{
        val url = "https://timebestilling-api.atlas.vegvesen.no/api/Timebestilling/getavailabledates?sectionId=$sectionId&serviceId=438&month=$month&year=$year"
        val res = withContext(Dispatchers.IO){
            requestFunction(url)
        }
        try {
            val json = Json { ignoreUnknownKeys = true }
            val dates: List<String> = json.decodeFromString(res)
            return dates;
        }catch (e: SerializationException) {
            // Handle specific JSON parsing errors
//            println("Serialization error occurred: ${e.message}")
            return emptyList()
        }
    }

    suspend fun getAvailTimes(sectionId:Long, date:String, requestFunction: suspend (String) -> String):List<LocalTime> {
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


    suspend fun finnAlleIRegion(regionId: Long){
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
    suspend fun finnDatoer(sectionId: Long):List<String>{
        val currentDate = LocalDate.now()
        // Extract current month and year


        // calculate the 3 month

        val resm1 = getAvailDates(sectionId, month=currentDate.monthValue, year = currentDate.year, ::makeHttpRequest)
        val resm2 = getAvailDates(sectionId, month=finnMaaned(1), year = finnAar(1),::makeHttpRequest)
        val resm3 = getAvailDates(sectionId, month=finnMaaned(2), year=finnAar(2), ::makeHttpRequest)
        return resm1 + resm2 + resm3

    }
    private fun finnMaaned(adder: Long) :Int{
        val currentDate = LocalDate.now()
        return currentDate.plusMonths(adder).monthValue
    }
    private fun finnAar(adder:Long):Int{
        val currentDate = LocalDate.now()
        return currentDate.plusMonths(adder).year
    }
}
