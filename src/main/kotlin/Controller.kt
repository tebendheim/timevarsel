import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId

class Controller (){
    private val bot:Bot = Bot(this)
    private val veg:Vegvesen
    private val abonnenter:MutableMap<Section, User> = mutableMapOf()
    private val res: MutableMap<Section, TimeSlot> = mutableMapOf()
    private val regioner:MutableList<Region> = mutableListOf()
    private val sections: MutableMap<Long, Section> = mutableMapOf()

    init {
        veg  =  Vegvesen()
        bot.startBot()
    }

    suspend fun oppdater() {
        val newRegions: List<Region> = getRegions()
        regioner.clear()
        regioner.addAll(newRegions)

//        @todo: For hver region. søke opp seksjoner. og sjekke om de er like eller ulike, og deretter sjekke datoer på seksjonen
//        @todo: Deretter Sjekke abon på secsjonen og sende melding.
//        @todo: Deretter gå videre til neste seksjon - for så neste region osv.

        regioner.forEach{ region ->
            getSections(region.id).forEach{section ->
                val dates: List<String> = getAvailDates(section.id)

            }
        }


//        // Clear the current list and add all elements from the new list
//
//        regions.forEach{region ->
//            getSections(region.id).forEach{section -> sections[]}
//    }



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
    fun leggTilVarsel(user:User, sectionid: Int): String{
        val section = sections[sectionid.toLong()]
        if (section != null){
            abonnenter.put(section, user )
            return "success"
        }
        else{
            println("Noe feil med å legge til abonnemnet.")
            return "error"
        }
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