package com.lucio.expensetracker.domain.repository

import com.lucio.expensetracker.domain.model.Mission
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    suspend fun insertMission(mission: Mission)
    suspend fun updateMission(mission: Mission)
    suspend fun deleteMission(mission: Mission)
    fun getAllMissions(): Flow<List<Mission>>
    suspend fun getMissionById(id: Long): Mission?
}
