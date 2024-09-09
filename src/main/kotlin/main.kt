

import kotlinx.coroutines.*


fun main() {

    val controller: Controller = Controller()
    val veg:Vegvesen =  Vegvesen()
try{

    runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            controller.start()
            while (true) {
                delay(300_000) // 600_000 10 minutes in milliseconds
                    controller.oppdater()
            }
        }
    }
    }catch (e: Exception) {
        println("An error occurred: ${e.message}")
        e.printStackTrace()
    }
}