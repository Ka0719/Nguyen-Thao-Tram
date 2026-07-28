package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CapybaraPetState
import com.example.ui.MainViewModel

@Composable
fun CapybaraPetScreen(viewModel: MainViewModel) {
    val petState by viewModel.capybaraState.collectAsState(initial = null)
    val state = petState ?: CapybaraPetState()

    var sleepHoursInput by remember { mutableStateOf("8.0") }
    var offlineMinsInput by remember { mutableStateOf("60") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Capybara View
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capybara_pet_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isOverworked) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = "Capybara",
                                tint = if (state.isOverworked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = state.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = "Rest Points",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${state.restPoints} Điểm Chill",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mascot Image display based on overworked vs chill state
                    val capybaraImageRes = if (state.isOverworked) R.drawable.img_capybara_tired else R.drawable.img_capybara_chill

                    Image(
                        painter = painterResource(id = capybaraImageRes),
                        contentDescription = "Capybara Mascot",
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .testTag("capybara_mascot_image"),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Speech bubble status
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (state.isOverworked) "💬 'Thâm quầng mắt rồi... Cứu Capybara bằng cách đi ngủ và nghỉ ngơi đi!'" else "💬 '${state.statusText}'",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (state.isOverworked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vitals Progress Indicators
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Hạnh Phúc:", style = MaterialTheme.typography.bodySmall)
                            Text(text = "${state.happinessLevel}/100", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                        LinearProgressIndicator(
                            progress = { state.happinessLevel / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Năng Lượng Sống:", style = MaterialTheme.typography.bodySmall)
                            Text(text = "${state.energyLevel}/100", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                        LinearProgressIndicator(
                            progress = { state.energyLevel / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        // Section 2: Care Shop for Capybara
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("capybara_care_shop_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Care Shop",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tiệm Chăm Sóc Linh Vật Capybara",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Dùng Điểm Chill tích lũy từ việc hoàn thành bài tập 'Đủ Tốt', nghỉ Pomodoro & hít thở để mua đồ cho Capybara:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val shopItems = listOf(
                        Triple("Bát Cam Nóng", 20, "Phục hồi +15 Hạnh phúc & +10 Năng lượng"),
                        Triple("Nón Tắm Onsen", 35, "Phục hồi +25 Hạnh phúc cho Capybara"),
                        Triple("Giường Mây Ngủ Tốt", 50, "Cứu Capybara khỏi kiệt sức, hồi 100% chỉ số")
                    )

                    shopItems.forEach { (itemName, cost, benefit) ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🍊 $itemName",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = benefit,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { viewModel.feedOrCarePet(itemName) },
                                enabled = state.restPoints >= cost,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("buy_item_$itemName")
                            ) {
                                Text("$cost đ")
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Ghi Nhận Giấc Ngủ & Thời Gian Offline
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sleep_offline_log_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Sleep Log",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ghi Nhận Giấc Ngủ & Thời Gian Offline",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ngủ đủ 8 tiếng là chiếc chìa khóa vàng bảo vệ bạn khỏi burnout và nạp lại sinh lực cho Capybara:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = sleepHoursInput,
                            onValueChange = { sleepHoursInput = it },
                            label = { Text("Số giờ ngủ hôm nay") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sleep_hours_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = offlineMinsInput,
                            onValueChange = { offlineMinsInput = it },
                            label = { Text("Số phút Offline") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("offline_mins_input"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val hours = sleepHoursInput.toFloatOrNull() ?: 8.0f
                            val mins = offlineMinsInput.toIntOrNull() ?: 60
                            viewModel.logSleepRecord(hours, mins)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("log_sleep_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Nightlight, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lưu Giấc Ngủ & Nạp Năng Lượng")
                    }
                }
            }
        }
    }
}
