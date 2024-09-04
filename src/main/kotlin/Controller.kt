import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId

class Controller (){
    private val bot:Bot = Bot(this)
    private val veg:Vegvesen
    private val abonnenter:MutableMap<Long, MutableList<User>> = mutableMapOf()
    private val res: MutableMap<Section, TimeSlot> = mutableMapOf()
    private val regioner:MutableList<Region> = mutableListOf()
    private val sections: MutableMap<Long, Section> = mutableMapOf()
    private val sectionDates:MutableMap<Section, List<String>> = mutableMapOf()

    init {
        veg  =  Vegvesen()
        bot.startBot()
    }

    suspend fun oppdater(init:Boolean) {
        println("oppdaterer")
        val newRegions: List<Region> = getRegions()
        regioner.clear()
        regioner.addAll(newRegions)

//        @todo: For hver region. søke opp seksjoner. og sjekke om de er like eller ulike, og deretter sjekke datoer på seksjonen
//        @todo: Deretter Sjekke abon på secsjonen og sende melding.
//        @todo: Deretter gå videre til neste seksjon - for så neste region osv.

        regioner.forEach{ region ->
            val allSections = getSections(region.id)
            val newSections = getNewItems(allSections, sections.values.toList())
            newSections.forEach{section -> sections.put(section.id.toLong(), section)}
            val deletedSections = getDeletedItems(allSections, sections.values.toList())
            deletedSections.forEach{section -> sections.remove(section.id.toLong())}
                allSections.forEach{section ->
                val dates: List<String> = getAvailDates(section.id)
                    val newDates = getNewItems(dates, sectionDates[section])
                    val deletedDates = getDeletedItems(dates, sectionDates[section])
                    if (newDates.isNotEmpty()){
                        if (!init) {
//                        @todo: Send message- new dates available with all dates and section
                            val users = abonnenter[section.id]
                        }
                        sectionDates.replace(section, dates)
                    }else if (deletedDates.isNotEmpty()){
                        sectionDates.replace(section, dates)
                    }
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
    suspend fun getSections(regionid: Long):List<Section>{
        val retur = veg.getSectionId(regionid, ::makeHttpRequest)
        return retur
    }
    suspend fun getAvailDates(sectionid: Long):List<String>{
        val retur =  veg.finnDatoer(sectionid)
        return retur
    }
    fun leggTilVarsel(user:User, sectionid: Long): String{
        try {
        if (abonnenter[sectionid] == null){
            abonnenter.put(sectionid, mutableListOf(user) )
            println(abonnenter)
            return "success"
        }

            val users = abonnenter.get(sectionid)!!
            if (!users.contains(user))
            users.add(user)
            println(abonnenter)
            return "success"
        }catch (e: Exception){
            return "error"
        }
    }
}

fun <E> getNewItems(newList: List<E>, oldList: List<E>?):List<E>{
    if (oldList != null) {
        val oldSet = oldList.toSet()
        val newSet = newList.toSet()
        return (newSet - oldSet).toList()
    }
    return newList
}
fun <E> getDeletedItems(newList: List<E>, oldList: List<E>?):List<E>{
    if (oldList != null){
    val oldSet = oldList.toSet()
    val newSet = newList.toSet()
    return (oldSet - newSet).toList()
    }
    return emptyList()
}

fun <E> compareLists(oldList: List<E>, newList: List<E>) {
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