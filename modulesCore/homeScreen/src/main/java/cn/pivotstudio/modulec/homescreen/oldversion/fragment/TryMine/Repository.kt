package cn.pivotstudio.modulec.homescreen.oldversion.fragment.TryMine

interface Repository {
    suspend fun getMyData() : MyData
}