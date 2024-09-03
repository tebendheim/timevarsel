class Controller (){
    private val bot:Bot = Bot(this)
    private val veg:Vegvesen
    private val abonnenter:MutableList<Abonnent> = mutableListOf()
    private val res: MutableMap<Section, TimeSlot> = mutableMapOf()
    private val regAb: MutableMap<Section, Abonnent> = mutableMapOf()
    private val regions: List<Region>
    init {
        veg  =  Vegvesen()
        bot.startBot()
        regions = mutableListOf()
    }

    fun oppdater(){

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
        println(sectionid)
        return listOf("Test")
//        val retur =  veg.finnDatoer(sectionid)
//        println(retur)
//        return retur
    }
}