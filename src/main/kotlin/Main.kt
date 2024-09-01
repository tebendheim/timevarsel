

import io.ktor.http.cio.*
import java.util.Properties
import java.io.FileInputStream
import kotlinx.coroutines.runBlocking

fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    FileInputStream(fileName).use { fileInput ->
        properties.load(fileInput)
    }
    return properties
}

fun main() {
//    val properties = loadProperties("gradle.properties")

//    val myApiKey = properties.getProperty("telegram_api")

//    println("API Key: $myApiKey")

    val veg:Vegvesen =  Vegvesen()
try{
    runBlocking {
        val res = veg.getRegions(::makeHttpRequest)
        println("response: $res")
        println("dette er en testprint")
        val section = veg.getSectionId(173, ::makeHttpRequest)
        println("sections: $section")
        val dates = veg.getAvailDates(1693, 9,2024, ::makeHttpRequest)
        println("dates: $dates")
        val times = veg.getAvailTimes(1693, "2024-09-02", ::makeHttpRequest)
        println("Times: $times")
    }
    }catch (e: Exception) {
        println("An error occurred: ${e.message}")
        e.printStackTrace()
    }
}