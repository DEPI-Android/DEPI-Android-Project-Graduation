package com.hfad.egypttour.data.local

object IdMapper {
    private val idMap = mutableMapOf<String, Int>()
    
    fun getIntegerId(localId: String): Int {
        return idMap.getOrPut(localId) {
            localId.toDoubleOrNull()?.toInt() ?: localId.hashCode()
        }
    }
}
