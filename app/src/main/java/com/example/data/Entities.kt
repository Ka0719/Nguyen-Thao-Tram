package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessment_results")
data class AssessmentResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val perfectionismScore: Int, // Out of 100
    val perfectionismType: String, // "Thích nghi" (Adaptive) or "Không thích nghi" (Maladaptive)
    val toxicProductivityScore: Int, // Out of 100
    val socialMediaImpactScore: Int, // Out of 100
    val summaryFeedback: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "trigger_journals")
data class TriggerJournal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val socialMediaContext: String, // e.g. "Thấy bạn A khoe điểm IELTS 8.0"
    val initialEmotion: String, // e.g. "Vô dụng, lo lắng"
    val compulsiveBehavior: String, // e.g. "Ép mình thức đến 2h sáng"
    val aiCognitiveAnalysis: String, // Analysis from CBT model
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "good_enough_tasks")
data class GoodEnoughTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val category: String = "Học tập", // Học tập, Nghỉ ngơi, Bản thân
    val dateCreated: String // YYYY-MM-DD format
)

@Entity(tableName = "to_dont_items")
data class ToDontItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleText: String, // e.g. "Không lướt story học bá sau 10h tối"
    val category: String = "MXH & So sánh",
    val isActive: Boolean = true
)

@Entity(tableName = "failure_posts")
data class FailurePost(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorAlias: String, // e.g. "Mèo lười khối 11", "Học sinh ẩn danh"
    val content: String, // e.g. "Hôm nay kiểm tra 1 tiết Toán được 4.5đ..."
    val tag: String, // "Điểm kém", "Trì hoãn", "Áp lực", "Sai lầm"
    val hugCount: Int = 0,
    val empathyCount: Int = 0,
    val hasUserHugged: Boolean = false,
    val hasUserEmpathized: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chill_records")
data class ChillRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sleepHours: Float,
    val offlineMinutes: Int,
    val breaksTakenCount: Int,
    val dateString: String // YYYY-MM-DD
)

@Entity(tableName = "capybara_pet")
data class CapybaraPetState(
    @PrimaryKey val id: Int = 1,
    val name: String = "Bắp Capybara",
    val happinessLevel: Int = 85, // 0 to 100
    val energyLevel: Int = 90, // 0 to 100
    val isOverworked: Boolean = false, // True if user skips breaks or records late hours
    val restPoints: Int = 120, // Currency earned from resting & completing tasks
    val streakDays: Int = 3,
    val statusText: String = "Capybara đang thong thả tắm suối nước nóng!"
)
