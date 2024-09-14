package teori

import Bot
import Region
import Section
import TimeSlot
import User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import makeHttpRequest

class Controller (private val bot: Bot){

    private val veg: Vegvesen
    private val abonnenter:MutableMap<Long, MutableList<User>> = mutableMapOf()
    private val res: MutableMap<Section, TimeSlot> = mutableMapOf()
    private val regioner:MutableList<Region> = mutableListOf()
    private val sections: MutableMap<Long, Section> = mutableMapOf()
    private val sectionDates:MutableMap<Section, MutableList<String>> = mutableMapOf()
    private val mutex = Mutex()

    init {
        veg  = Vegvesen()
        bot.startBot()
    }

    suspend fun start(){
        mutex.withLock {
            println("starter")
            val newRegions: List<Region> = getRegions()
            regioner.clear()
            regioner.addAll(newRegions)
            regioner.forEach { region ->
                val regionSections = getSections(region.id)
                regionSections.forEach { section ->
                    sections.put(section.id, section)
                    addDates(getAvailDates(section.id), section)
                }
            }
        }
        println("start er ferdig")
        }



    suspend fun oppdater() {
        println("oppdaterer")
        val newRegions: List<Region> = getRegions()
        regioner.clear()
        regioner.addAll(newRegions)
        val allSections:MutableList<Section> = mutableListOf()
        regioner.forEach{ region ->
            val regionSections = getSections(region.id)
            regionSections.forEach{section ->
                if (!sections.containsKey(section.id)){
                    sections.put(section.id, section)
                }
                allSections.add(section)
                val dates: List<String> = getAvailDates(section.id)
                val oldDates = sectionDates[section]
                val newDates = getNewItems(dates, oldDates)
                val deletedDates = getDeletedItems(dates, sectionDates[section])
                addDates(newDates, section)
                deleteDates(deletedDates, section)
                if (newDates.isNotEmpty()){
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
    suspend fun leggTilVarsel(user: User, sectionid: Long): String{
        return mutex.withLock {
            try {
                if (abonnenter[sectionid] == null) {
                    abonnenter.put(sectionid, mutableListOf(user))
                    return "success"
                }

                val users = abonnenter.get(sectionid)!!
                if (!users.contains(user))
                    users.add(user)
                println("user ${user.id} added to $sectionid")
                "success"
            } catch (e: Exception) {
                "error"
            }
        }
    }

    suspend fun hentSubs(userId: Long?): List<Section>{
        return mutex.withLock {
            val subs: MutableList<Section> = mutableListOf()
            abonnenter.forEach { sectionId, userList ->
                val userExists = userList.any { user -> user.id == userId }
                if (userExists) {
                    sections[sectionId]?.let { section ->
                        // Add the section to subs if it exists
                        subs.add(section)
                    }
                }
            }
            subs
        }
    }


    fun fjernSubsAlle(userId: Long){
        abonnenter.forEach{(sectionid, userList) ->
            val removedUser = userList.removeIf{user -> user.id == userId}
        }
    }

    fun slettSubs(userId: Long, sectionid: Long):Boolean?{
    try {
        val section = abonnenter.get(sectionid)
        val removed: Boolean? = section?.removeIf { user -> user.id == userId }
        println("userid $userId removed from $sectionid")
        return removed
    }catch(e:Exception){
        println(e)
        return false
    }
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


