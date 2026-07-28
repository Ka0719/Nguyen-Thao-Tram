package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AssessmentResult::class,
        TriggerJournal::class,
        GoodEnoughTask::class,
        ToDontItem::class,
        FailurePost::class,
        ChillRecord::class,
        CapybaraPetState::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assessmentDao(): AssessmentDao
    abstract fun triggerDao(): TriggerDao
    abstract fun taskDao(): TaskDao
    abstract fun toDontDao(): ToDontDao
    abstract fun failureDao(): FailureDao
    abstract fun chillDao(): ChillDao
    abstract fun capybaraDao(): CapybaraDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chillenough_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
