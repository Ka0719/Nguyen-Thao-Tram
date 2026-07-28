package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@Composable
fun HealthyProductivityScreen(viewModel: MainViewModel) {
    val tasks by viewModel.todayTasks.collectAsState(initial = emptyList())
    val newTaskTitle by viewModel.newTaskTitle.collectAsState()
    val taskWarningDialog by viewModel.taskWarningDialog.collectAsState()

    val pomodoroState by viewModel.pomodoroState.collectAsState()
    val pomodoroSeconds by viewModel.pomodoroSeconds.collectAsState()
    val restSeconds by viewModel.restSeconds.collectAsState()

    val toDontList by viewModel.allToDontItems.collectAsState(initial = emptyList())
    val newToDontText by viewModel.newToDontText.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("healthy_productivity_hero"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = "Healthy Productivity",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Năng Suất Lành Mạnh (Good Enough)",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Giới hạn nhiệm vụ, Pomodoro bắt buộc nghỉ & Danh sách To-Don't",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Section 1: To-Do List "Đủ Tốt" (Max 3-5 Tasks)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("good_enough_list_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Good Enough List",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Danh Sách 'Đủ Tốt' Hôm Nay",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${tasks.size}/5 Task",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Khác với các app thông thường, app này CHỈ cho phép tối đa 5 nhiệm vụ quan trọng để bảo vệ bạn khỏi kiệt sức.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { viewModel.updateNewTaskTitle(it) },
                            placeholder = { Text("Nhập nhiệm vụ quan trọng...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("new_task_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.addGoodEnoughTask() },
                            modifier = Modifier.testTag("add_task_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (tasks.isEmpty()) {
                        Text(
                            text = "Hôm nay chưa có nhiệm vụ nào. Nhập tối đa 3-5 việc quan trọng nhất thôi nhé!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        tasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(
                                        if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { viewModel.toggleTask(task) },
                                        modifier = Modifier.testTag("task_checkbox_${task.id}")
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteTask(task.id) },
                                    modifier = Modifier.testTag("delete_task_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Đồng Hồ Pomodoro "Bắt Buộc Nghỉ"
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pomodoro_mandatory_rest_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (pomodoroState == "MANDATORY_REST") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Pomodoro",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đồng Hồ Pomodoro 'Bắt Buộc Nghỉ'",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "App tặng điểm thưởng cho Capybara khi bạn CHỊU NGHỈ NGƠI giữa các hiệp. Nếu bỏ qua giờ nghỉ để nhồi nhét, app sẽ giữ lại điểm thưởng!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val activeSeconds = if (pomodoroState == "MANDATORY_REST") restSeconds else pomodoroSeconds
                    val mins = activeSeconds / 60
                    val secs = activeSeconds % 60
                    val formattedTime = String.format("%02d:%02d", mins, secs)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (pomodoroState == "MANDATORY_REST") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = when (pomodoroState) {
                                    "STUDY" -> "📖 Đang Học Tập Tập Trung (25p)"
                                    "MANDATORY_REST" -> "☕ GIỜ NGHỈ BẮT BUỘC (5p) - Hãy rời bàn học!"
                                    else -> "☕ Hãy sẵn sàng cho phiên học cân bằng"
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (pomodoroState == "MANDATORY_REST") MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = formattedTime,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (pomodoroState == "IDLE") {
                                Button(
                                    onClick = { viewModel.startPomodoro() },
                                    modifier = Modifier.testTag("start_pomodoro_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Bắt Đầu Học (25p)")
                                }
                            } else if (pomodoroState == "MANDATORY_REST") {
                                Button(
                                    onClick = { /* Auto running rest timer */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.FreeBreakfast, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Đang Thư Giãn (Nhận +40đ)")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.skipPomodoroRest() },
                                    modifier = Modifier.testTag("skip_rest_button"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Bỏ Nghỉ (Không Cộng Điểm)")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: To-Don't List (Những Việc KHÔNG Làm)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("to_dont_list_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DoNotDisturbOn,
                            contentDescription = "To Don't List",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tính Năng 'To-Don't List' (Những Việc KHÔNG Làm)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Khuyến khích bạn thiết lập ranh giới để bảo vệ sức khỏe tâm thần (VD: Không lướt story học bá sau 10h tối, Không so sánh điểm Toán):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newToDontText,
                            onValueChange = { viewModel.updateToDontText(it) },
                            placeholder = { Text("VD: Không xem điểm bạn bên cạnh...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("new_to_dont_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.addToDontItem() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.testTag("add_to_dont_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    toDontList.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Block",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.ruleText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteToDont(item.id) },
                                modifier = Modifier.testTag("delete_to_dont_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Task Warning Alert Dialog
    taskWarningDialog?.let { warningText ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissTaskWarning() },
            icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.error) },
            title = { Text("Cảnh Báo Năng Suất Độc Hại!", fontWeight = FontWeight.Bold) },
            text = { Text(warningText) },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissTaskWarning() },
                    modifier = Modifier.testTag("dismiss_warning_button")
                ) {
                    Text("Mình Hiểu Rồi")
                }
            }
        )
    }
}
