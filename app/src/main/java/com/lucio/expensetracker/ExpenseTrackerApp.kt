package com.lucio.expensetracker

import android.app.Application
import androidx.room.Room
import com.lucio.expensetracker.data.local.AppDatabase
import com.lucio.expensetracker.data.repository.ExpenseRepositoryImpl
import com.lucio.expensetracker.data.repository.MissionRepositoryImpl
import com.lucio.expensetracker.domain.repository.ExpenseRepository
import com.lucio.expensetracker.domain.repository.MissionRepository

class ExpenseTrackerApp : Application() {

    lateinit var expenseRepository: ExpenseRepository
        private set

    lateinit var missionRepository: MissionRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

        expenseRepository = ExpenseRepositoryImpl(database.expenseDao())
        missionRepository = MissionRepositoryImpl(database.missionDao())
    }
}
