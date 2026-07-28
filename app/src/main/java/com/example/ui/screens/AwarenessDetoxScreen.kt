package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel

@Composable
fun AwarenessDetoxScreen(viewModel: MainViewModel) {
    val latestAssessment by viewModel.latestAssessment.collectAsState(initial = null)
    val allTriggers by viewModel.allTriggers.collectAsState(initial = emptyList())
    val detoxActive by viewModel.detoxActive.collectAsState()
    val detoxSeconds by viewModel.detoxSecondsRemaining.collectAsState()

    var selectedSubSection by remember { mutableStateOf(0) } // 0: Quiz, 1: Trigger Journal, 2: Detox Mode

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_mindfulness),
                        contentDescription = "Mindfulness banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(70.dp))
                        Text(
                            text = "Giải Độc Tâm Lý & Nhận Thức",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "Đo lường mức độ cầu toàn & Nhận diện cạm bẫy so sánh trên MXH",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Sub-navigation selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val tabs = listOf("Nhiệt Kế Cầu Toàn", "Trigger Journal", "Detox Mode")
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedSubSection == index
                    Surface(
                        onClick = { selectedSubSection = index },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sub_tab_$index")
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(vertical = 10.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        when (selectedSubSection) {
            0 -> {
                // Section 1: Quiz "Nhiệt kế cầu toàn" & "Năng suất độc hại"
                item {
                    PerfectionismQuizCard(viewModel = viewModel, latestAssessment = latestAssessment)
                }
            }
            1 -> {
                // Section 2: Trigger Journal
                item {
                    TriggerTrackerSection(viewModel = viewModel)
                }
                item {
                    Text(
                        text = "Lịch Sử Kích Hoạt & Cạm Bẫy Đã Ghi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (allTriggers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "Chưa có nhật ký trigger nào. Hãy ghi lại cảm xúc sau khi lướt MXH để AI phân tích nhé!",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(allTriggers) { trigger ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Trigger",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = trigger.socialMediaContext,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• Cảm xúc: ${trigger.initialEmotion}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "• Phản ứng ép bản thân: ${trigger.compulsiveBehavior}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(
                                    text = trigger.aiCognitiveAnalysis,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Section 3: Screen-Time Detox
                item {
                    ScreenTimeDetoxSection(
                        detoxActive = detoxActive,
                        detoxSeconds = detoxSeconds,
                        onStartDetox = { mins -> viewModel.startDetox(mins) },
                        onStopDetox = { viewModel.stopDetox() }
                    )
                }
            }
        }
    }
}

@Composable
fun PerfectionismQuizCard(
    viewModel: MainViewModel,
    latestAssessment: com.example.data.AssessmentResult?
) {
    val quizFinished by viewModel.quizFinished.collectAsState()
    val currentIndex by viewModel.currentQuizIndex.collectAsState()
    val questions = viewModel.quizQuestions

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("perfectionism_quiz_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Quiz",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đo Lường 'Nhiệt Kế Cầu Toàn'",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!quizFinished) {
                Text(
                    text = "Câu ${currentIndex + 1} / ${questions.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / questions.size },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                val q = questions[currentIndex]
                Text(
                    text = q.text,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                q.options.forEach { (optionText, points) ->
                    OutlinedButton(
                        onClick = { viewModel.answerQuiz(q.id, points) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("quiz_option_$points"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                // Display Quiz Result Summary
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Done",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hoàn Thành Đánh Giá Tâm Lý!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    latestAssessment?.let { result ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Phân loại: ${result.perfectionismType}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "• Điểm Cầu Toàn: ${result.perfectionismScore}/100")
                                Text(text = "• Mức Năng Suất Độc Hại: ${result.toxicProductivityScore}/100")
                                Text(text = "• Mức Ảnh Hưởng Mạng Xã Hội: ${result.socialMediaImpactScore}/100")
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(
                                    text = result.summaryFeedback,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.resetQuiz() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Làm Lại Trắc Nghiệm")
                    }
                }
            }
        }
    }
}

@Composable
fun TriggerTrackerSection(viewModel: MainViewModel) {
    val contextInput by viewModel.triggerContextInput.collectAsState()
    val emotionInput by viewModel.triggerEmotionInput.collectAsState()
    val behaviorInput by viewModel.triggerBehaviorInput.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingTrigger.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trigger_tracker_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Trigger Tracker",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nhật Ký Cảm Xúc & Kích Hoạt (Trigger Tracker)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ghi lại những khoảnh khắc bạn thấy bất an sau khi lướt MXH để AI bóc tách mô thức độc hại:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = contextInput,
                onValueChange = { viewModel.updateTriggerInputs(it, emotionInput, behaviorInput) },
                label = { Text("1. Nhìn thấy điều gì trên MXH?") },
                placeholder = { Text("VD: Bạn A khoe điểm IELTS 8.0 trên story...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trigger_context_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = emotionInput,
                onValueChange = { viewModel.updateTriggerInputs(contextInput, it, behaviorInput) },
                label = { Text("2. Cảm xúc bộc phát của bạn là gì?") },
                placeholder = { Text("VD: Thấy mình kém cỏi, vô dụng, lo sợ...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trigger_emotion_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = behaviorInput,
                onValueChange = { viewModel.updateTriggerInputs(contextInput, emotionInput, it) },
                label = { Text("3. Bạn ép bản thân phản ứng thế nào?") },
                placeholder = { Text("VD: Ép mình thức đến 2h sáng để học nhồi nhét...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trigger_behavior_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.submitTriggerJournal() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_trigger_button"),
                enabled = !isAnalyzing,
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Đang Phân Tích Mô Thức...")
                } else {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ghi Lại & Nhận Phân Tích AI")
                }
            }
        }
    }
}

@Composable
fun ScreenTimeDetoxSection(
    detoxActive: Boolean,
    detoxSeconds: Int,
    onStartDetox: (Int) -> Unit,
    onStopDetox: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("screen_time_detox_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (detoxActive) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LockClock,
                    contentDescription = "Detox",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chế Độ 'Screen-Time Detox'",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tạm thời phong tỏa các ứng dụng MXH gây xao nhãng để trả lại khoảng không nghỉ ngơi đích thực cho tâm trí.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (detoxActive) {
                val mins = detoxSeconds / 60
                val secs = detoxSeconds % 60
                val timeFormatted = String.format("%02d:%02d", mins, secs)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔒 ĐANG KHÓA APP MXH",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hãy cất điện thoại, hít thở và thưởng thức một tách trà ấm.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onStopDetox,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Dừng Detox Ngay")
                    }
                }
            } else {
                Text(
                    text = "Chọn thời gian khóa app:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 25, 45).forEach { mins ->
                        Button(
                            onClick = { onStartDetox(mins) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("detox_start_$mins"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Text("$mins Phút")
                        }
                    }
                }
            }
        }
    }
}
