package com.lucio.expensetracker.data.repository

import com.lucio.expensetracker.data.local.MissionDao
import com.lucio.expensetracker.data.mapper.toMission
import com.lucio.expensetracker.data.mapper.toMissionEntity
import com.lucio.expensetracker.domain.model.Mission
import com.lucio.expensetracker.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MissionRepositoryImpl(
    private val dao: MissionDao
) : MissionRepository {

    override suspend fun insertMission(mission: Mission) {
        dao.insertMission(mission.toMissionEntity())
    }

    override suspend fun deleteMission(mission: Mission) {
        dao.deleteMission(mission.toMissionEntity())
    }

    override fun getAllMissions(): Flow<List<Mission>> {
        return dao.getAllMissions().map { entities ->
            entities.map { it.toMission() }
        }
    }

    override suspend fun getMissionById(id: Long): Mission? {
        return dao.getMissionById(id)?.toMission()
    }
}
