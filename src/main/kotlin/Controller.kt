class Controller (){
    private val bot:Bot = Bot(this)
    private val veg:Vegvesen
    private val abonnenter:MutableMap<Long, MutableList<User>> = mutableMapOf()
    private val res: MutableMap<Section, TimeSlot> = mutableMapOf()
    private val regioner:MutableList<Region> = mutableListOf()
    private val sections: MutableMap<Long, Section> = mutableMapOf()
    private val sectionDates:MutableMap<Section, MutableList<String>> = mutableMapOf()

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
            newSections.forEach{section -> sections.put(section.id, section)}
            val deletedSections = getDeletedItems(allSections, sections.values.toList())
            deletedSections.forEach{section -> sections.remove(section.id)}
                allSections.forEach{section ->
                val dates: List<String> = getAvailDates(section.id)
                    val oldDates = sectionDates[section]
                    val newDates = getNewItems(dates, oldDates)
                    val deletedDates = getDeletedItems(dates, sectionDates[section])
                    addDates(newDates, section)
                    deleteDates(deletedDates, section)
                    if (newDates.isNotEmpty()){
                        if (!init) {
//                        @todo: Send message- new dates available with all dates and section
                            abonnenter[section.id]?.forEach { user ->
                                bot.sendMessage(
                                    user.chatId,
                                    "${section.name} har nå Fått følgende nye datoer:\n ${dates.joinToString("\n")}"
                                )
                            }
                            println("Section: $section har nye datoer: $newDates")
                        }
                    }
            }
        }
    }
    fun addDates(dates:List<String>, section: Section){
        val storedDates = sectionDates.get(section)
        if (storedDates == null){
            sectionDates.put(section, dates.toMutableList())
        }
        dates.forEach{date -> storedDates?.add(date)}
    }
    fun deleteDates(dates: List<String>, section: Section){
        dates.forEach{
            date -> sectionDates[section]?.remove(date)
        }
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

    fun hentSubs(user: User): List<Section>{
        val subs: MutableList<Section> =  mutableListOf()
        abonnenter.forEach{id:Long, list:MutableList<User> ->

            if (abonnenter[id]?.contains(user)==true){
                if (sections[id] != null){
                    subs.add(sections[id]!!)
                }
            }
        }
        return subs
    }
    fun fjernSubs(user: User, sectionid: Long){

    }
}

fun <E> getNewItems(newList: List<E>, oldList: List<E>?):List<E>{
    if (oldList != null) {
        val oldSet = oldList.toSet()
        val newSet = newList.toSet()
        val res = (newSet - oldSet).toList()
        return res
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