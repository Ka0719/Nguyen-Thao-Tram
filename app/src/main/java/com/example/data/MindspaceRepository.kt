package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MindspaceRepository(private val db: AppDatabase) {

    val allAssessments: Flow<List<AssessmentResult>> = db.assessmentDao().getAllAssessments()
    val latestAssessment: Flow<AssessmentResult?> = db.assessmentDao().getLatestAssessment()

    val allTriggers: Flow<List<TriggerJournal>> = db.triggerDao().getAllTriggers()

    val allToDontItems: Flow<List<ToDontItem>> = db.toDontDao().getAllToDontItems()

    val failurePosts: Flow<List<FailurePost>> = db.failureDao().getAllPosts()

    val chillRecords: Flow<List<ChillRecord>> = db.chillDao().getAllRecords()

    val capybaraState: Flow<CapybaraPetState?> = db.capybaraDao().getPetState()

    fun getTodayTasks(): Flow<List<GoodEnoughTask>> {
        val todayStr = getTodayDateString()
        return db.taskDao().getTasksForDate(todayStr)
    }

    suspend fun saveAssessment(result: AssessmentResult) {
        db.assessmentDao().insertAssessment(result)
        // Award rest points for completing self-reflection
        addRestPointsToCapybara(30)
    }

    suspend fun saveTrigger(trigger: TriggerJournal) {
        db.triggerDao().insertTrigger(trigger)
        addRestPointsToCapybara(20)
    }

    suspend fun addGoodEnoughTask(taskTitle: String, category: String = "Học tập"): Pair<Boolean, String> {
        val todayStr = getTodayDateString()
        val count = db.taskDao().getTaskCountForDate(todayStr)
        if (count >= 5) {
            // Warn and do not add
            return Pair(false, "⚠️ Bạn đang ép bản thân quá mức đấy! Danh sách 'Đủ Tốt' giới hạn tối đa 5 nhiệm vụ quan trọng nhất mỗi ngày.")
        }
        val task = GoodEnoughTask(
            title = taskTitle,
            isCompleted = false,
            category = category,
            dateCreated = todayStr
        )
        db.taskDao().insertTask(task)
        return Pair(true, "Đã thêm nhiệm vụ vào danh sách 'Đủ Tốt'!")
    }

    suspend fun toggleTaskCompleted(task: GoodEnoughTask) {
        val updated = task.copy(isCompleted = !task.isCompleted)
        db.taskDao().updateTask(updated)
        if (updated.isCompleted) {
            addRestPointsToCapybara(15)
        }
    }

    suspend fun deleteTask(id: Long) {
        db.taskDao().deleteTask(id)
    }

    suspend fun addToDontItem(ruleText: String, category: String) {
        val item = ToDontItem(ruleText = ruleText, category = category, isActive = true)
        db.toDontDao().insertToDontItem(item)
    }

    suspend fun toggleToDontActive(item: ToDontItem) {
        val updated = item.copy(isActive = !item.isActive)
        db.toDontDao().updateToDontItem(updated)
    }

    suspend fun deleteToDontItem(id: Long) {
        db.toDontDao().deleteToDontItem(id)
    }

    suspend fun addFailurePost(alias: String, content: String, tag: String) {
        val post = FailurePost(
            authorAlias = if (alias.isBlank()) "Cầu thủ ẩn danh" else alias,
            content = content,
            tag = tag,
            hugCount = 1,
            empathyCount = 1,
            timestamp = System.currentTimeMillis()
        )
        db.failureDao().insertPost(post)
        addRestPointsToCapybara(25) // Courage award
    }

    suspend fun reactToPost(post: FailurePost, isHug: Boolean) {
        val updated = if (isHug) {
            val newHug = if (post.hasUserHugged) post.hugCount - 1 else post.hugCount + 1
            post.copy(hugCount = newHug.coerceAtLeast(0), hasUserHugged = !post.hasUserHugged)
        } else {
            val newEmpathy = if (post.hasUserEmpathized) post.empathyCount - 1 else post.empathyCount + 1
            post.copy(empathyCount = newEmpathy.coerceAtLeast(0), hasUserEmpathized = !post.hasUserEmpathized)
        }
        db.failureDao().updatePost(updated)
    }

    suspend fun recordPomodoroBreakBonus(points: Int) {
        addRestPointsToCapybara(points)
        // Record chill stat
        val todayStr = getTodayDateString()
        val current = db.chillDao().getRecordForDate(todayStr)
        val updated = if (current != null) {
            current.copy(breaksTakenCount = current.breaksTakenCount + 1)
        } else {
            ChillRecord(sleepHours = 7.5f, offlineMinutes = 45, breaksTakenCount = 1, dateString = todayStr)
        }
        db.chillDao().insertOrUpdateRecord(updated)
    }

    suspend fun logSleepAndOffline(sleepHours: Float, offlineMins: Int) {
        val todayStr = getTodayDateString()
        val current = db.chillDao().getRecordForDate(todayStr)
        val updated = if (current != null) {
            current.copy(sleepHours = sleepHours, offlineMinutes = current.offlineMinutes + offlineMins)
        } else {
            ChillRecord(sleepHours = sleepHours, offlineMinutes = offlineMins, breaksTakenCount = 0, dateString = todayStr)
        }
        db.chillDao().insertOrUpdateRecord(updated)
        addRestPointsToCapybara((sleepHours * 5 + offlineMins / 10).toInt())
    }

    suspend fun feedOrCareCapybara(itemType: String) {
        val currentState = db.capybaraDao().getPetStateOnce() ?: CapybaraPetState()
        when (itemType) {
            "Bát Cam Nóng" -> {
                if (currentState.restPoints >= 20) {
                    val newState = currentState.copy(
                        happinessLevel = (currentState.happinessLevel + 15).coerceAtMost(100),
                        energyLevel = (currentState.energyLevel + 10).coerceAtMost(100),
                        restPoints = currentState.restPoints - 20,
                        isOverworked = false,
                        statusText = "Capybara vừa ăn bát cam nóng giòn ngon tuyệt!"
                    )
                    db.capybaraDao().updatePetState(newState)
                }
            }
            "Nón Tắm Onsen" -> {
                if (currentState.restPoints >= 35) {
                    val newState = currentState.copy(
                        happinessLevel = (currentState.happinessLevel + 25).coerceAtMost(100),
                        restPoints = currentState.restPoints - 35,
                        statusText = "Capybara đang ngâm mình thư thái trong suối nước nóng!"
                    )
                    db.capybaraDao().updatePetState(newState)
                }
            }
            "Giường Mây Ngủ Tốt" -> {
                if (currentState.restPoints >= 50) {
                    val newState = currentState.copy(
                        happinessLevel = 100,
                        energyLevel = 100,
                        isOverworked = false,
                        restPoints = currentState.restPoints - 50,
                        statusText = "Capybara đã ngủ một giấc thật ngon và hoàn toàn hồi phục!"
                    )
                    db.capybaraDao().updatePetState(newState)
                }
            }
        }
    }

    suspend fun setCapybaraOverworked(isOverworked: Boolean) {
        val currentState = db.capybaraDao().getPetStateOnce() ?: CapybaraPetState()
        val newState = currentState.copy(
            isOverworked = isOverworked,
            energyLevel = if (isOverworked) 35 else currentState.energyLevel,
            statusText = if (isOverworked) "Capybara mệt mỏi với đôi mắt thâm quầng... Cần bạn nghỉ ngơi và ngủ đủ giấc ngay!" else "Capybara thong thả tự tại!"
        )
        db.capybaraDao().updatePetState(newState)
    }

    private suspend fun addRestPointsToCapybara(points: Int) {
        val current = db.capybaraDao().getPetStateOnce() ?: CapybaraPetState()
        val updated = current.copy(
            restPoints = current.restPoints + points,
            happinessLevel = (current.happinessLevel + 5).coerceAtMost(100)
        )
        db.capybaraDao().updatePetState(updated)
    }

    suspend fun seedDefaultsIfEmpty() {
        val petState = db.capybaraDao().getPetStateOnce()
        if (petState == null) {
            db.capybaraDao().updatePetState(CapybaraPetState())
        }

        // Seed default To-Don't items
        val toDonts = db.toDontDao().getAllToDontItems()
        // Check if empty via single query or insert defaults directly
        db.toDontDao().insertToDontItem(ToDontItem(ruleText = "Không lướt story của hội học bá sau 10h tối", category = "MXH & So sánh"))
        db.toDontDao().insertToDontItem(ToDontItem(ruleText = "Không so sánh điểm bài kiểm tra với bạn ngồi bên cạnh", category = "Áp lực học tập"))
        db.toDontDao().insertToDontItem(ToDontItem(ruleText = "Không thức quá 11h30 đêm để ép mình nhồi nhét lý thuyết", category = "Sức khỏe tâm thần"))

        // Seed default Failure Posts (Wall of Oops)
        val defaultPosts = listOf(
            FailurePost(authorAlias = "Mèo lười khối 11", content = "Hôm nay làm bài 1 tiết Lý mình tính nhầm công thức nên chỉ được 4.0 điểm. Cảm thấy thật tệ nhưng tự dặn lòng lần sau cẩn thận hơn.", tag = "Điểm kém", hugCount = 12, empathyCount = 18),
            FailurePost(authorAlias = "Sâu ngủ A1", content = "Lên kế hoạch dậy 5h sáng ôn thi Sử, kết quả ngủ quên đến 7h15 suýt trễ giờ học. Nhưng thôi ngủ đủ lại thấy khỏe hơn hẳn!", tag = "Trì hoãn", hugCount = 25, empathyCount = 30),
            FailurePost(authorAlias = "Chủ nhân Capybara", content = "Thấy bạn cùng lớp khoe chứng chỉ IELTS 8.0 làm mình hoảng sợ. Nhưng tự nhắc bản thân: Mỗi người có một vạch xuất phát riêng.", tag = "Áp lực đồng trang lứa", hugCount = 19, empathyCount = 22)
        )
        for (post in defaultPosts) {
            db.failureDao().insertPost(post)
        }

        // Seed initial Good Enough Tasks
        val todayStr = getTodayDateString()
        db.taskDao().insertTask(GoodEnoughTask(title = "Ôn 20 từ vựng Tiếng Anh cơ bản", isCompleted = true, category = "Học tập", dateCreated = todayStr))
        db.taskDao().insertTask(GoodEnoughTask(title = "Uống đủ 2L nước & nghỉ mắt 15 phút", isCompleted = false, category = "Nghỉ ngơi", dateCreated = todayStr))
        db.taskDao().insertTask(GoodEnoughTask(title = "Viết 1 dòng cảm ơn bản thân", isCompleted = false, category = "Bản thân", dateCreated = todayStr))

        // Seed initial Chill Record
        db.chillDao().insertOrUpdateRecord(ChillRecord(sleepHours = 8.0f, offlineMinutes = 60, breaksTakenCount = 3, dateString = todayStr))
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
