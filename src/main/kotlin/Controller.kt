import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId

class Controller (){
    private val bot:Bot = Bot(this)
    private val veg:Vegvesen
    private val abonnenter:MutableList<Abonnent> = mutableListOf()
    private val res: MutableMap<Section, TimeSlot> = mutableMapOf()
    private val regAb: MutableMap<Section, Abonnent> = mutableMapOf()
    private val regions: MutableList<Region> = mutableListOf()
    init {
        veg  =  Vegvesen()
        bot.startBot()
    }

    suspend fun oppdater(){
        val newRegions: List<Region> = getRegions()

        // Clear the current list and add all elements from the new list
        regions.clear()
        regions.addAll(newRegions)

    }
    suspend fun getRegions():List<Region>{
        return veg.getRegions(::makeHttpRequest)
    }
    suspend fun getSections(regionid: Int):List<Section>{
        println(regionid)
        val retur = veg.getSectionId(regionid, ::makeHttpRequest)
        println("retur fra controller er $retur")
        return retur
    }
    suspend fun getAvailDates(sectionid: Int):List<String>{
        val retur =  veg.finnDatoer(sectionid)
        println(retur)
        return retur
    }
    fun leggTilVarsel(chatId: ChatId, user:User, sectionid: Int){

    }

    fun compareLists(oldList: List<Region>, newList: List<Region>) {
        // Convert lists to sets for easier comparison
        val oldSet = oldList.toSet()
        val newSet = newList.toSet()

        // Determine the items that are in the new list but not in the old list (added items)
        val addedItems = newSet - oldSet
        println("Added items: $addedItems")

        // Determine the items that are in the old list but not in the new list (deleted items)
        val removedItems = oldSet - newSet
        println("Removed items: $removedItems")
    }
}