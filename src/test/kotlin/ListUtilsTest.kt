import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import teori.getDeletedItems
import teori.getNewItems

class ListUtilsTest {

    @Test
    fun `test getNewItems normal`() {
        val oldList = listOf("apple", "banana", "cherry")
        val newList = listOf("banana", "cherry", "date", "elderberry")

        val result = getNewItems(newList, oldList)

        val expected = listOf("date", "elderberry")
        assertEquals(expected, result, "New items should be correctly identified.")
    }

    @Test
    fun `test getNewItems when oldList is null`() {
        val newList = listOf("apple", "banana", "cherry")

        val result = getNewItems(newList, null)

        val expected = newList
        assertEquals(expected, result, "All items should be returned when oldList is null.")
    }

    @Test
    fun `test getDeletedItems normal`() {
        val oldList = listOf("apple", "banana", "cherry")
        val newList = listOf("banana", "cherry", "date", "elderberry")

        val result = getDeletedItems(newList, oldList)

        val expected = listOf("apple")
        assertEquals(expected, result, "Deleted items should be correctly identified.")
    }

    @Test
    fun `test getDeletedItems when oldList is null`() {
        val newList = listOf("apple", "banana", "cherry")

        val result = getDeletedItems(newList, null)

        val expected = emptyList<String>()
        assertEquals(expected, result, "No items should be considered deleted when oldList is null.")
    }
}

