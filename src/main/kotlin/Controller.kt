import com.github.kotlintelegrambot.entities.ChatId
import kotlinx.coroutines.*
import java.util.Properties
import java.io.FileInputStream
class Controller (){
    private val bot:Bot;
    private val veg:Vegvesen
    private val abonnenter:MutableList<ChatId> = mutableListOf()
    private val
    init {
        bot = Bot
        veg  =  Vegvesen()
        bot.startBot()
    }

    fun oppdater(){

    }
}