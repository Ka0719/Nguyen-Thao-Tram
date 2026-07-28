package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AssessmentResult
import com.example.data.CapybaraPetState
import com.example.data.ChillRecord
import com.example.data.FailurePost
import com.example.data.GeminiCbtService
import com.example.data.GoodEnoughTask
import com.example.data.MindspaceRepository
import com.example.data.ToDontItem
import com.example.data.TriggerJournal
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class MainTab {
    AWARENESS_DETOX,   // 1. "Giải độc" tâm lý & Nhận thức
    CBT_COUNSELING,    // 2. Can thiệp Tâm lý CBT & Self-Compassion Audio
    HEALTHY_PRODUCTIVITY, // 3. Định nghĩa lại Năng suất Lành mạnh (Good enough task, Pomodoro)
    ANTI_TOXIC_COMMUNITY, // 4. Mạng xã hội thu nhỏ "Khu vườn thất bại"
    CAPYBARA_PET       // 5. Nuôi thú ảo Capybara
}

data class QuizQuestion(
    val id: Int,
    val text: String,
    val options: List<Pair<String, Int>> // Text to points (1 to 5)
)

data class SelfCompassionTrack(
    val title: String,
    val speaker: String,
    val durationText: String,
    val audioCategory: String,
    val description: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MindspaceRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MindspaceRepository(db)
        viewModelScope.launch {
            repository.seedDefaultsIfEmpty()
        }
    }

    // --- State Flows from Repository ---
    val allAssessments = repository.allAssessments
    val latestAssessment = repository.latestAssessment
    val allTriggers = repository.allTriggers
    val todayTasks = repository.getTodayTasks()
    val allToDontItems = repository.allToDontItems
    val failurePosts = repository.failurePosts
    val chillRecords = repository.chillRecords
    val capybaraState = repository.capybaraState

    // --- UI Navigation State ---
    private val _selectedTab = MutableStateFlow(MainTab.AWARENESS_DETOX)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    // --- Notification Banner State ---
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    // ==========================================
    // 1. FEATURE CLUSTER: AWARENESS & DETOX
    // ==========================================

    // Perfectionism & Toxic Productivity Test Questions
    val quizQuestions = listOf(
        QuizQuestion(
            id = 1,
            text = "Khi điểm số hoặc kết quả bài làm không đạt 100% như kỳ vọng, bạn cảm thấy thế nào?",
            options = listOf(
                "Rất thất vọng, coi mình là kẻ kém cỏi" to 5,
                "Cảm thấy lo lắng và ép mình thức đêm ôn thêm" to 4,
                "Hơi buồn chút nhưng coi đó là cơ hội rút kinh nghiệm" to 2,
                "Thoải mái, biết mình đã nỗ lực hết sức" to 1
            )
        ),
        QuizQuestion(
            id = 2,
            text = "Sau khi xem story bạn bè khoe thành tích học tập (IELTS 8.0, Học bổng, Giải Nhất)...",
            options = listOf(
                "Lập tức cảm thấy bất an, hoảng sợ và bắt đầu ép mình học" to 5,
                "Tự trách bản thân vì sao mình lại chây trì" to 4,
                "Vui cho bạn và tự đi theo nhịp độ riêng của mình" to 2,
                "Chẳng bận tâm, tắt mạng xã hội đi nghỉ ngơi" to 1
            )
        ),
        QuizQuestion(
            id = 3,
            text = "Bạn có dành thời gian nghỉ ngơi trọn vẹn mà không cảm thấy 'tội lỗi' (guilt) không?",
            options = listOf(
                "Luôn thấy tội lỗi nếu ngồi yên không học/làm gì đó" to 5,
                "Hiếm khi, lúc nào cũng vừa nghỉ vừa mở tài liệu" to 4,
                "Thỉnh thoảng vẫn lo nhưng biết cân bằng" to 2,
                "Hoàn toàn thư giãn, nghỉ ngơi là để tái tạo năng lượng" to 1
            )
        ),
        QuizQuestion(
            id = 4,
            text = "Khi gặp thất bại hoặc bài kiểm tra điểm kém, bạn xử lý ra sao?",
            options = listOf(
                "Tự trừng phạt bằng cách cắt giờ ngủ, nhịn ăn/học liên tục" to 5,
                "Nghĩ rằng mọi người sẽ coi thường mình" to 4,
                "Chấp nhận nỗi buồn và tâm sự với người thân/bạn bè" to 2,
                "Ôm lấy bản thân và tự dặn 'Lần sau mình làm tốt hơn'" to 1
            )
        )
    )

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _quizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quizAnswers: StateFlow<Map<Int, Int>> = _quizAnswers.asStateFlow()

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished: StateFlow<Boolean> = _quizFinished.asStateFlow()

    fun answerQuiz(questionId: Int, points: Int) {
        val updated = _quizAnswers.value.toMutableMap()
        updated[questionId] = points
        _quizAnswers.value = updated

        if (_currentQuizIndex.value < quizQuestions.size - 1) {
            _currentQuizIndex.value += 1
        } else {
            // Finish Quiz & calculate score
            _quizFinished.value = true
            calculateAndSaveQuizResult()
        }
    }

    fun resetQuiz() {
        _currentQuizIndex.value = 0
        _quizAnswers.value = emptyMap()
        _quizFinished.value = false
    }

    private fun calculateAndSaveQuizResult() {
        val totalPoints = _quizAnswers.value.values.sum()
        val perfectionismScore = ((totalPoints / 20.0) * 100).toInt()
        val isMaladaptive = totalPoints >= 12
        val perfectionismType = if (isMaladaptive) "Cầu toàn không thích nghi (Maladaptive)" else "Cầu toàn thích nghi (Adaptive)"
        val toxicScore = (perfectionismScore * 0.9).toInt().coerceIn(20, 95)
        val socialImpactScore = (totalPoints * 4.5).toInt().coerceIn(15, 90)

        val summaryText = if (isMaladaptive) {
            "⚠️ Bạn đang ở mức Cầu toàn Không Thích Nghi & Năng suất Độc hại cao. Bạn thường dùng điểm số để định giá bản thân và dễ bị kích hoạt bởi MXH. Hãy dùng tính năng 'Tái cấu trúc nhận thức' và 'Good Enough List' để xoa dịu áp lực nhé!"
        } else {
            "🌿 Bạn có mức Cầu toàn Thích Nghi lành mạnh. Bạn biết nỗ lực và chấp nhận sự 'Đủ tốt'. Hãy duy trì tinh thần này và tận hưởng cuộc sống học đường nhé!"
        }

        viewModelScope.launch {
            repository.saveAssessment(
                AssessmentResult(
                    perfectionismScore = perfectionismScore,
                    perfectionismType = perfectionismType,
                    toxicProductivityScore = toxicScore,
                    socialMediaImpactScore = socialImpactScore,
                    summaryFeedback = summaryText
                )
            )
            // If maladaptive, notify Capybara
            if (isMaladaptive) {
                repository.setCapybaraOverworked(true)
            }
        }
    }

    // --- Trigger Journal State ---
    private val _triggerContextInput = MutableStateFlow("")
    val triggerContextInput: StateFlow<String> = _triggerContextInput.asStateFlow()

    private val _triggerEmotionInput = MutableStateFlow("")
    val triggerEmotionInput: StateFlow<String> = _triggerEmotionInput.asStateFlow()

    private val _triggerBehaviorInput = MutableStateFlow("")
    val triggerBehaviorInput: StateFlow<String> = _triggerBehaviorInput.asStateFlow()

    private val _isAnalyzingTrigger = MutableStateFlow(false)
    val isAnalyzingTrigger: StateFlow<Boolean> = _isAnalyzingTrigger.asStateFlow()

    fun updateTriggerInputs(context: String, emotion: String, behavior: String) {
        _triggerContextInput.value = context
        _triggerEmotionInput.value = emotion
        _triggerBehaviorInput.value = behavior
    }

    fun submitTriggerJournal() {
        val ctx = _triggerContextInput.value.ifBlank { "Lướt story học tập trên TikTok/Instagram" }
        val emo = _triggerEmotionInput.value.ifBlank { "Bất an, tự thấy mình kém cỏi" }
        val beh = _triggerBehaviorInput.value.ifBlank { "Ép mình học đến 2h sáng" }

        viewModelScope.launch {
            _isAnalyzingTrigger.value = true
            val analysis = GeminiCbtService.analyzeTriggerPattern(ctx, emo, beh)
            repository.saveTrigger(
                TriggerJournal(
                    socialMediaContext = ctx,
                    initialEmotion = emo,
                    compulsiveBehavior = beh,
                    aiCognitiveAnalysis = analysis
                )
            )
            _isAnalyzingTrigger.value = false
            _triggerContextInput.value = ""
            _triggerEmotionInput.value = ""
            _triggerBehaviorInput.value = ""
            showMessage("🌱 Đã ghi nhật ký Kích hoạt & Phân tích mô thức!")
        }
    }

    // --- Screen-Time Detox Mode State ---
    private val _detoxActive = MutableStateFlow(false)
    val detoxActive: StateFlow<Boolean> = _detoxActive.asStateFlow()

    private val _detoxSecondsRemaining = MutableStateFlow(25 * 60)
    val detoxSecondsRemaining: StateFlow<Int> = _detoxSecondsRemaining.asStateFlow()

    private var detoxTimerJob: Job? = null

    fun startDetox(minutes: Int = 25) {
        detoxTimerJob?.cancel()
        _detoxSecondsRemaining.value = minutes * 60
        _detoxActive.value = true

        detoxTimerJob = viewModelScope.launch {
            while (_detoxSecondsRemaining.value > 0 && _detoxActive.value) {
                delay(1000)
                _detoxSecondsRemaining.value -= 1
            }
            if (_detoxActive.value && _detoxSecondsRemaining.value <= 0) {
                _detoxActive.value = false
                repository.logSleepAndOffline(0f, minutes)
                showMessage("🎉 Hoàn thành phiên Screen-Time Detox! Đã khóa các app gây nhiễu và bảo vệ tâm trí bạn.")
            }
        }
    }

    fun stopDetox() {
        detoxTimerJob?.cancel()
        _detoxActive.value = false
    }

    // ==========================================
    // 2. FEATURE CLUSTER: CBT & SELF-COMPASSION
    // ==========================================

    private val _cbtThoughtInput = MutableStateFlow("")
    val cbtThoughtInput: StateFlow<String> = _cbtThoughtInput.asStateFlow()

    private val _cbtReframedResult = MutableStateFlow<String?>(null)
    val cbtReframedResult: StateFlow<String?> = _cbtReframedResult.asStateFlow()

    private val _isCbtLoading = MutableStateFlow(false)
    val isCbtLoading: StateFlow<Boolean> = _isCbtLoading.asStateFlow()

    fun updateCbtThought(text: String) {
        _cbtThoughtInput.value = text
    }

    fun runCognitiveReframing() {
        val input = _cbtThoughtInput.value
        if (input.isBlank()) {
            showMessage("Vui lòng nhập suy nghĩ cầu toàn cần tái cấu trúc!")
            return
        }

        viewModelScope.launch {
            _isCbtLoading.value = true
            val result = GeminiCbtService.reframePerfectionistThought(input)
            _cbtReframedResult.value = result
            _isCbtLoading.value = false
        }
    }

    // --- Self-Compassion Audio Player Simulation ---
    val podcastList = listOf(
        SelfCompassionTrack(
            title = "Xoa Dụ Áp Lực Điểm Số",
            speaker = "Chuyên viên Tâm lý Ngọc Hà",
            durationText = "05:20",
            audioCategory = "Audio Thiền",
            description = "Bài tập hít thở chánh niệm và gỡ bỏ ảo tưởng về sự hoàn hảo."
        ),
        SelfCompassionTrack(
            title = "Vượt Qua Peer Pressure (Áp lực đồng trang lứa)",
            speaker = "Podcast Góc Nhìn Học Đường",
            durationText = "08:15",
            audioCategory = "Podcast Chữa Lành",
            description = "Học cách ngừng so sánh bản thân với story rực rỡ của bạn bè."
        ),
        SelfCompassionTrack(
            title = "Bao Dung Với Lần Thất Bại Lớp 12",
            speaker = "ThS. Tâm lý Minh Triết",
            durationText = "06:45",
            audioCategory = "Chăm Sóc Bản Thân",
            description = "Thất bại không định nghĩa con người bạn. Hãy cho phép mình vấp ngã."
        )
    )

    private val _activeTrack = MutableStateFlow<SelfCompassionTrack?>(podcastList[0])
    val activeTrack: StateFlow<SelfCompassionTrack?> = _activeTrack.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _audioProgress = MutableStateFlow(0.35f)
    val audioProgress: StateFlow<Float> = _audioProgress.asStateFlow()

    fun playTrack(track: SelfCompassionTrack) {
        _activeTrack.value = track
        _isPlayingAudio.value = true
    }

    fun toggleAudioPlayPause() {
        _isPlayingAudio.value = !_isPlayingAudio.value
    }

    // --- Breathing Exercise Guide ---
    private val _breathingPhase = MutableStateFlow("Hít vào (4s)")
    val breathingPhase: StateFlow<String> = _breathingPhase.asStateFlow()

    private val _isBreathingActive = MutableStateFlow(false)
    val isBreathingActive: StateFlow<Boolean> = _isBreathingActive.asStateFlow()

    private var breathingJob: Job? = null

    fun toggleBreathingExercise() {
        if (_isBreathingActive.value) {
            breathingJob?.cancel()
            _isBreathingActive.value = false
        } else {
            _isBreathingActive.value = true
            breathingJob = viewModelScope.launch {
                while (_isBreathingActive.value) {
                    _breathingPhase.value = "🌸 Hít vào chậm (4s)..."
                    delay(4000)
                    _breathingPhase.value = "🌾 Giữ khí lại (7s)..."
                    delay(7000)
                    _breathingPhase.value = "🍃 Thở ra nhẹ nhàng (8s)..."
                    delay(8000)
                }
            }
        }
    }

    // ==========================================
    // 3. FEATURE CLUSTER: HEALTHY PRODUCTIVITY
    // ==========================================

    private val _newTaskTitle = MutableStateFlow("")
    val newTaskTitle: StateFlow<String> = _newTaskTitle.asStateFlow()

    private val _taskWarningDialog = MutableStateFlow<String?>(null)
    val taskWarningDialog: StateFlow<String?> = _taskWarningDialog.asStateFlow()

    fun updateNewTaskTitle(title: String) {
        _newTaskTitle.value = title
    }

    fun addGoodEnoughTask() {
        val title = _newTaskTitle.value.trim()
        if (title.isBlank()) return

        viewModelScope.launch {
            val (success, message) = repository.addGoodEnoughTask(title)
            if (!success) {
                _taskWarningDialog.value = message
            } else {
                _newTaskTitle.value = ""
                showMessage(message)
            }
        }
    }

    fun dismissTaskWarning() {
        _taskWarningDialog.value = null
    }

    fun toggleTask(task: GoodEnoughTask) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTask(id)
        }
    }

    // --- Mandatory Rest Pomodoro Timer ---
    private val _pomodoroState = MutableStateFlow("STUDY") // "STUDY", "MANDATORY_REST", "IDLE"
    val pomodoroState: StateFlow<String> = _pomodoroState.asStateFlow()

    private val _pomodoroSeconds = MutableStateFlow(25 * 60)
    val pomodoroSeconds: StateFlow<Int> = _pomodoroSeconds.asStateFlow()

    private val _restSeconds = MutableStateFlow(5 * 60)
    val restSeconds: StateFlow<Int> = _restSeconds.asStateFlow()

    private var pomodoroJob: Job? = null

    fun startPomodoro() {
        pomodoroJob?.cancel()
        _pomodoroState.value = "STUDY"
        _pomodoroSeconds.value = 25 * 60

        pomodoroJob = viewModelScope.launch {
            while (_pomodoroSeconds.value > 0 && _pomodoroState.value == "STUDY") {
                delay(1000)
                _pomodoroSeconds.value -= 1
            }
            if (_pomodoroState.value == "STUDY") {
                // Shift to MANDATORY REST phase
                _pomodoroState.value = "MANDATORY_REST"
                _restSeconds.value = 5 * 60
                showMessage("⏰ Đã hết 25 phút học! Bây giờ là GIỜ NGHỈ BẮT BUỘC. Hãy rời khỏi bàn học 5 phút để nhận điểm thưởng nhé!")
                runMandatoryRestTimer()
            }
        }
    }

    private fun runMandatoryRestTimer() {
        pomodoroJob = viewModelScope.launch {
            while (_restSeconds.value > 0 && _pomodoroState.value == "MANDATORY_REST") {
                delay(1000)
                _restSeconds.value -= 1
            }
            if (_pomodoroState.value == "MANDATORY_REST") {
                _pomodoroState.value = "IDLE"
                repository.recordPomodoroBreakBonus(40)
                showMessage("🎉 Hoàn thành lượt nghỉ! Capybara cộng thêm 40 điểm thưởng vì bạn đã biết bảo vệ sức khỏe!")
            }
        }
    }

    fun skipPomodoroRest() {
        pomodoroJob?.cancel()
        _pomodoroState.value = "IDLE"
        viewModelScope.launch {
            repository.setCapybaraOverworked(true)
        }
        showMessage("🚫 Bạn đã bỏ qua giờ nghỉ! App không cộng điểm thưởng và Capybara sẽ bị mệt mỏi vì bạn vắt kiệt sức đấy.")
    }

    // --- To-Don't List State ---
    private val _newToDontText = MutableStateFlow("")
    val newToDontText: StateFlow<String> = _newToDontText.asStateFlow()

    fun updateToDontText(text: String) {
        _newToDontText.value = text
    }

    fun addToDontItem() {
        val text = _newToDontText.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.addToDontItem(text, "Bảo vệ tâm trí")
            _newToDontText.value = ""
            showMessage("Đã thêm điều KHÔNG LÀM vào danh sách bảo vệ bản thân!")
        }
    }

    fun toggleToDont(item: ToDontItem) {
        viewModelScope.launch {
            repository.toggleToDontActive(item)
        }
    }

    fun deleteToDont(id: Long) {
        viewModelScope.launch {
            repository.deleteToDontItem(id)
        }
    }

    // ==========================================
    // 4. FEATURE CLUSTER: ANTI-TOXIC COMMUNITY
    // ==========================================

    private val _postContentInput = MutableStateFlow("")
    val postContentInput: StateFlow<String> = _postContentInput.asStateFlow()

    private val _postTagInput = MutableStateFlow("Điểm kém")
    val postTagInput: StateFlow<String> = _postTagInput.asStateFlow()

    fun updatePostInputs(content: String, tag: String) {
        _postContentInput.value = content
        _postTagInput.value = tag
    }

    fun submitFailurePost() {
        val content = _postContentInput.value.trim()
        if (content.isBlank()) {
            showMessage("Vui lòng viết vài dòng chia sẻ nỗi niềm!")
            return
        }

        viewModelScope.launch {
            repository.addFailurePost("Cầu thủ ẩn danh", content, _postTagInput.value)
            _postContentInput.value = ""
            showMessage("🌸 Đã gửi bài viết ẩn danh vào Khu Vườn Thất Bại. Mọi người đang ôm và đồng cảm cùng bạn!")
        }
    }

    fun reactToPost(post: FailurePost, isHug: Boolean) {
        viewModelScope.launch {
            repository.reactToPost(post, isHug)
        }
    }

    // ==========================================
    // 5. FEATURE CLUSTER: CAPYBARA VIRTUAL PET
    // ==========================================

    fun feedOrCarePet(itemType: String) {
        viewModelScope.launch {
            repository.feedOrCareCapybara(itemType)
        }
    }

    fun logSleepRecord(sleepHours: Float, offlineMins: Int) {
        viewModelScope.launch {
            repository.logSleepAndOffline(sleepHours, offlineMins)
            showMessage("✨ Đã ghi nhận $sleepHours giờ ngủ và $offlineMins phút offline! Capybara của bạn tràn đầy năng lượng.")
        }
    }
}
