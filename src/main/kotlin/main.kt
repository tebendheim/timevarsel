

import kotlinx.coroutines.*
import teori.Vegvesen as TVegvesen
import oppkjoring.Vegvesen as OVegvesen
import teori.Controller as TController
import oppkjoring.Controller as OController


fun main() {
   val bot = Bot()


try{

    runBlocking {
        CoroutineScope(Dispatchers.IO).launch {
            bot.startBot()
            bot.startControll()
            while (true) {
                delay(300_000) // 600_000 10 minutes in milliseconds
                bot.oppdater()
            }
        }
    }
    }catch (e: Exception) {
        println("An error occurred: ${e.message}")
        e.printStackTrace()
    }
}