package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PsychologyAlt
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun CbtCounselingScreen(viewModel: MainViewModel) {
    val cbtInput by viewModel.cbtThoughtInput.collectAsState()
    val cbtResult by viewModel.cbtReframedResult.collectAsState()
    val isCbtLoading by viewModel.isCbtLoading.collectAsState()

    val activeTrack by viewModel.activeTrack.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val audioProgress by viewModel.audioProgress.collectAsState()

    val breathingPhase by viewModel.breathingPhase.collectAsState()
    val isBreathingActive by viewModel.isBreathingActive.collectAsState()

    var showAnonymousCounselingModal by remember { mutableStateOf(false) }
    var counselorMessageText by remember { mutableStateOf("") }

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
                    .testTag("cbt_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PsychologyAlt,
                            contentDescription = "CBT & ACT",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Can Thiệp Tâm Lý (CBT & ACT)",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Tái cấu trúc nhận thức, Lòng trắc ẩn & Hỗ trợ học đường SOS",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Section 1: Công cụ Tái Cấu Trúc Nhận Thức (Cognitive Reframing)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cognitive_reframing_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Cognitive Reframing",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Công Cụ Tái Cấu Trúc Nhận Thức (CBT)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nhập suy nghĩ cầu toàn hoặc lo sợ thất bại. AI CBT sẽ phản biện và hướng dẫn bạn thay thế bằng góc nhìn thực tế, bao dung hơn:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cbtInput,
                        onValueChange = { viewModel.updateCbtThought(it) },
                        placeholder = { Text("VD: 'Nếu mình không được học sinh giỏi, mình là kẻ thất bại...'") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("cbt_thought_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.runCognitiveReframing() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cbt_reframe_button"),
                        enabled = !isCbtLoading,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isCbtLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Đang Tái Cấu Trúc Tư Duy...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chuyển Đổi Suy Nghĩ Cùng AI CBT")
                        }
                    }

                    cbtResult?.let { resultText ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "💡 Phản Biện & Tái Cấu Trúc Nhận Thức",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = resultText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Thư viện "Lòng trắc ẩn với bản thân" (Self-Compassion & Audio)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("self_compassion_library_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Self Compassion",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thư Viện 'Lòng Trắc Ẩn Với Bản Thân'",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Player Bar
                    activeTrack?.let { track ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = track.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            text = "${track.speaker} • ${track.durationText}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleAudioPlayPause() },
                                        modifier = Modifier.testTag("audio_play_pause_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play/Pause",
                                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { if (isPlayingAudio) 0.55f else audioProgress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    viewModel.podcastList.forEach { track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(
                                    if (activeTrack == track) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.playTrack(track) },
                                modifier = Modifier.testTag("track_${track.title}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = track.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Breathing Exercise Widget
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = "Breathing",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bài Tập Hít Thở 4-7-8 Chánh Niệm",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.toggleBreathingExercise() },
                            modifier = Modifier.testTag("breathing_toggle_button")
                        ) {
                            Text(if (isBreathingActive) "Dừng Thở" else "Bắt Đầu")
                        }
                    }

                    if (isBreathingActive) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = breathingPhase,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Góc Tham Vấn (SOS Counseling & Hotlines)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sos_counseling_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "SOS Counseling",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Góc Tham Vấn Tâm Lý Học Đường (SOS)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nếu bạn đang trải qua cảm giác kiệt sức (Burnout) trầm trọng hoặc bế tắc, hãy liên hệ ngay với các chuyên viên hỗ trợ:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hotline list for TP.HCM & Vietnam
                    val hotlines = listOf(
                        "Tổng đài Quốc gia Bảo vệ Trẻ em & Học sinh" to "111",
                        "Đường dây nóng Hỗ trợ Tâm lý TP.HCM" to "1900 6233",
                        "Phòng Tham vấn Tâm lý Học đường THPT" to "Chat Ẩn Danh"
                    )

                    hotlines.forEach { (name, phone) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            if (phone == "Chat Ẩn Danh") {
                                Button(
                                    onClick = { showAnonymousCounselingModal = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("anonymous_counseling_button")
                                ) {
                                    Text("Đặt Lịch")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.showMessage("📞 Đang kết nối cuộc gọi hotline: $phone") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("call_hotline_$phone")
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gọi Ngay")
                                }
                            }
                        }
                    }

                    if (showAnonymousCounselingModal) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🔒 Nhắn tin ẩn danh cho Chuyên viên Tâm lý Trường",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = counselorMessageText,
                                    onValueChange = { counselorMessageText = it },
                                    placeholder = { Text("Hãy chia sẻ những áp lực bạn đang gánh chịu...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .testTag("counselor_message_input")
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = { showAnonymousCounselingModal = false }
                                    ) {
                                        Text("Hủy")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.showMessage("🔒 Lời nhắn ẩn danh đã gửi thành công tới Chuyên viên Tâm lý!")
                                            showAnonymousCounselingModal = false
                                            counselorMessageText = ""
                                        },
                                        modifier = Modifier.testTag("send_counselor_message")
                                    ) {
                                        Text("Gửi Ẩn Danh")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
