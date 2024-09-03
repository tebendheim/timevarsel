import kotlinx.coroutines.*
import java.util.Properties
import java.io.FileInputStream
class Controller (){
    private val bot:Bot;
    private val veg:Vegvesen
    init {
        bot = Bot
        veg  =  Vegvesen()
        bot.startBot()
    }
}