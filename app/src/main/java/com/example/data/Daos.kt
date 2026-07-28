package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessment_results ORDER BY timestamp DESC")
    fun getAllAssessments(): Flow<List<AssessmentResult>>

    @Query("SELECT * FROM assessment_results ORDER BY timestamp DESC LIMIT 1")
    fun getLatestAssessment(): Flow<AssessmentResult?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentResult)
}

@Dao
interface TriggerDao {
    @Query("SELECT * FROM trigger_journals ORDER BY timestamp DESC")
    fun getAllTriggers(): Flow<List<TriggerJournal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrigger(trigger: TriggerJournal)

    @Query("DELETE FROM trigger_journals WHERE id = :id")
    suspend fun deleteTrigger(id: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM good_enough_tasks WHERE dateCreated = :dateStr ORDER BY id DESC")
    fun getTasksForDate(dateStr: String): Flow<List<GoodEnoughTask>>

    @Query("SELECT COUNT(*) FROM good_enough_tasks WHERE dateCreated = :dateStr")
    suspend fun getTaskCountForDate(dateStr: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: GoodEnoughTask): Long

    @Update
    suspend fun updateTask(task: GoodEnoughTask)

    @Query("DELETE FROM good_enough_tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}

@Dao
interface ToDontDao {
    @Query("SELECT * FROM to_dont_items ORDER BY id DESC")
    fun getAllToDontItems(): Flow<List<ToDontItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDontItem(item: ToDontItem)

    @Update
    suspend fun updateToDontItem(item: ToDontItem)

    @Query("DELETE FROM to_dont_items WHERE id = :id")
    suspend fun deleteToDontItem(id: Long)
}

@Dao
interface FailureDao {
    @Query("SELECT * FROM failure_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<FailurePost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: FailurePost)

    @Update
    suspend fun updatePost(post: FailurePost)
}

@Dao
interface ChillDao {
    @Query("SELECT * FROM chill_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<ChillRecord>>

    @Query("SELECT * FROM chill_records WHERE dateString = :dateStr LIMIT 1")
    suspend fun getRecordForDate(dateStr: String): ChillRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: ChillRecord)
}

@Dao
interface CapybaraDao {
    @Query("SELECT * FROM capybara_pet WHERE id = 1")
    fun getPetState(): Flow<CapybaraPetState?>

    @Query("SELECT * FROM capybara_pet WHERE id = 1")
    suspend fun getPetStateOnce(): CapybaraPetState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePetState(state: CapybaraPetState)
}
