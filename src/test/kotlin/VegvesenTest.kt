// src/test/kotlin/your/package/name/VegvesenTest.kt


import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import teori.Vegvesen
import java.time.LocalTime

class VegvesenTest {

    @Test
    fun `test getAvailTimes with valid response`() = runBlocking {
        // Mock the request function to return a valid JSON response
        val mockRequestFunction = mockk<suspend (String) -> String>()
        val validJsonResponse = """[{"fromDateTime":"2024-09-02T11:00:00","toDateTime":"2024-09-02T11:15:00"}]"""

        coEvery { mockRequestFunction(any()) } returns validJsonResponse

        // Create an instance of teori.Vegvesen
        val vegvesen = Vegvesen()

        // Call the function under test
        val result = vegvesen.getAvailTimes(sectionId = 123, date = "2024-09-02", requestFunction = mockRequestFunction)
        println(result)
        // Assertions to verify the results
        assertEquals(1, result.size)
        assertEquals(LocalTime.of(11, 0), result[0])  // Compare to LocalTime directly
    }
//
    @Test
    fun `test getTimeSlots with empty response`() = runBlocking {
        val mockRequestFunction = mockk<suspend (String) -> String>()
        val emptyJsonResponse = """[]"""

        coEvery { mockRequestFunction(any()) } returns emptyJsonResponse

        val vegvesen = Vegvesen()
        val result = vegvesen.getAvailTimes(sectionId = 123, date = "2024-09-02", requestFunction = mockRequestFunction)

        println(assertEquals(0, result.size))
    }

//    @Test
//    fun `test getTimeSlots with malformed response`() = runBlocking {
//        val mockRequestFunction = mockk<suspend (String) -> String>()
//        val malformedJsonResponse = """{malformed}"""
//
//        coEvery { mockRequestFunction(any()) } returns malformedJsonResponse
//
//        val vegvesen = teori.Vegvesen()
//        val result = vegvesen.getAvailTimes(sectionId = 123, date = "2024-09-02", requestFunction = mockRequestFunction)
//
//        assertEquals(null, result)
//    }
}
