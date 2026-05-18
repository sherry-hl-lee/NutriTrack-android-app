package com.example.ass2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTargetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: DailyTargetLog)

    @Query("SELECT * FROM daily_target_log WHERE userEmail = :email AND dayStart = :dayStart LIMIT 1")
    suspend fun getForDay(email: String, dayStart: Long): DailyTargetLog?

    @Query(
        """
        SELECT * FROM daily_target_log 
        WHERE userEmail = :email 
        ORDER BY dayStart DESC 
        LIMIT 7
        """
    )
    fun observeRecent(email: String): Flow<List<DailyTargetLog>>
}
