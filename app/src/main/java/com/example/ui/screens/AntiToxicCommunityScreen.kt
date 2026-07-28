package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Volcano
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel

@Composable
fun AntiToxicCommunityScreen(viewModel: MainViewModel) {
    val posts by viewModel.failurePosts.collectAsState(initial = emptyList())
    val postContentInput by viewModel.postContentInput.collectAsState()
    val postTagInput by viewModel.postTagInput.collectAsState()

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Khu vườn thất bại, 1: Bảng xếp hạng Chill

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
                    .testTag("failure_garden_hero"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_failure_garden),
                        contentDescription = "Failure Garden",
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
                            text = "Khu Vườn Thất Bại & Anti-Toxic Community",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                        Text(
                            text = "Bình thường hóa sự không hoàn hảo. Không áp lực, không phẫn nộ, chỉ có đồng cảm.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // SubTab Selector
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
                val tabs = listOf("🌸 Khu Vườn Thất Bại", "☕ Bảng Xếp Hạng Chill")
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedSubTab == index
                    Surface(
                        onClick = { selectedSubTab = index },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("community_subtab_$index")
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

        if (selectedSubTab == 0) {
            // Post creation form
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_failure_post_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "🌱 Chia Sẻ Thất Bại / Điểm Kém / Trì Hoãn (Ẩn Danh)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Thất bại là bình thường. Viết ra để nhẹ lòng và nhận vòng tay ấm áp từ cộng đồng:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = postContentInput,
                            onValueChange = { viewModel.updatePostInputs(it, postTagInput) },
                            placeholder = { Text("VD: Hôm nay làm bài 1 tiết Toán được 4.5đ... Lên kế hoạch dậy 5h sáng ôn thi nhưng ngủ quên đến 7h...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("post_content_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Điểm kém", "Trì hoãn", "Áp lực").forEach { tag ->
                                    val isSelected = postTagInput == tag
                                    Surface(
                                        onClick = { viewModel.updatePostInputs(postContentInput, tag) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = { viewModel.submitFailurePost() },
                                modifier = Modifier.testTag("submit_failure_post_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gửi Ẩn Danh")
                            }
                        }
                    }
                }
            }

            // Posts Feed
            items(posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("failure_post_${post.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "👤 ${post.authorAlias}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "#${post.tag}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = post.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // ONLY POSITIVE / EMPATHY REACTIONS (NO HAHA, NO PHẪN NỘ)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.reactToPost(post, isHug = true) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (post.hasUserHugged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (post.hasUserHugged) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("hug_button_${post.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Hug", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gửi Cái Ôm (${post.hugCount})")
                            }

                            Button(
                                onClick = { viewModel.reactToPost(post, isHug = false) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (post.hasUserEmpathized) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (post.hasUserEmpathized) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("empathy_button_${post.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Spa, contentDescription = "Empathy", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Đồng Cảm (${post.empathyCount})")
                            }
                        }
                    }
                }
            }
        } else {
            // Section 2: Bảng Xếp Hạng "Chill" (Honoring Sleep, Offline, Rest)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chill_leaderboard_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = "Chill Leaderboard",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bảng Xếp Hạng 'Chill' Lành Mạnh",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tuyệt đối KHÔNG vinh danh việc học đêm cày cuốc kiệt sức. Bảng này vinh danh những người biết yêu bản thân, ngủ đủ 8 tiếng và nghỉ ngơi!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val mockLeaderboard = listOf(
                            Triple("🥇 Bạn Bắp (Khối 12)", "Ngủ 8.5 giờ • Offline 120p", "Đại Sứ Chill"),
                            Triple("🥈 Mèo Thư Thái (Khối 11)", "Ngủ 8.0 giờ • Offline 90p", "Thần Đèn Sức Khỏe"),
                            Triple("🥉 Cầu Thủ Báo Thủ", "Ngủ 8.0 giờ • Offline 75p", "Master Thở 4-7-8"),
                            Triple("4. Học Sinh Ẩn Danh", "Ngủ 7.5 giờ • Offline 60p", "Học Sinh Đủ Tốt")
                        )

                        mockLeaderboard.forEach { (name, stats, badge) ->
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
                                        text = stats,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = badge,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
