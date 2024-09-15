

import kotlinx.coroutines.*
import teori.Vegvesen as TVegvesen
import oppkjoring.Vegvesen as OVegvesen
import teori.Controller as TController
import oppkjoring.Controller as OController



fun main() = runBlocking {
    val bot = Bot()
    val scope = CoroutineScope(Dispatchers.Default)

    // Launch the bot within the scope
    val botJob = scope.launch {
        try {
            bot.startBot()          // Start bot logic
            bot.startControll()      // Start bot control logic

            // Continuously perform bot updates
            while (isActive) {
                try {
                    bot.oppdater()
                    delay(300_000)  // Wait for 5 minutes between updates
                } catch (e: Exception) {
                    println("An error occurred during bot update: ${e.message}")
                    e.printStackTrace()
                }
            }
        } finally {
            // Ensure the bot is stopped when the coroutine is cancelled
            bot.stopBot()
            println("Bot has been stopped.")
        }
    }

    // Add shutdown hook to handle program interruption
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutdown hook triggered. Cancelling bot...")
        runBlocking {
            botJob.cancelAndJoin() // Cancel and wait for the bot coroutine to complete
            println("Program has been stopped.")
        }
    })

    // Block main thread until the coroutine is finished
    try {
        botJob.join()
    } catch (e: CancellationException) {
        println("Main job was cancelled: ${e.message}")
    } finally {
        println("Main function is exiting.")
    }
}