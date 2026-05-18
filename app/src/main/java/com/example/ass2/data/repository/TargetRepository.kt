package com.example.ass2.data.repository

import com.example.ass2.data.local.DailyTargetDao
import com.example.ass2.data.local.DailyTargetLog
import kotlinx.coroutines.flow.Flow

class TargetRepository(private val dao: DailyTargetDao) {

    suspend fun upsert(log: DailyTargetLog) = dao.upsert(log)

    suspend fun getForDay(email: String, dayStart: Long): DailyTargetLog? =
        dao.getForDay(email, dayStart)

    fun observeRecent(email: String): Flow<List<DailyTargetLog>> =
        dao.observeRecent(email)
}
