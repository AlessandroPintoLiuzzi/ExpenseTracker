package com.lucio.expensetracker.data.mapper

import com.lucio.expensetracker.data.local.MissionEntity
import com.lucio.expensetracker.domain.model.Mission

fun MissionEntity.toMission(): Mission {
    return Mission(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate
    )
}

fun Mission.toMissionEntity(): MissionEntity {
    return MissionEntity(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate
    )
}
