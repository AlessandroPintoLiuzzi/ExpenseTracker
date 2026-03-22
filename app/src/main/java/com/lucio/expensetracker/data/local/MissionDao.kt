package com.lucio.expensetracker.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Delete
    suspend fun deleteMission(mission: MissionEntity)

    @Query("SELECT * FROM missions ORDER BY startDate DESC")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions WHERE id = :id")
    suspend fun getMissionById(id: Long): MissionEntity?
}
