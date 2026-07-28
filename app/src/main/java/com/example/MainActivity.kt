package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainTab
import com.example.ui.MainViewModel
import com.example.ui.screens.AntiToxicCommunityScreen
import com.example.ui.screens.AwarenessDetoxScreen
import com.example.ui.screens.CapybaraPetScreen
import com.example.ui.screens.CbtCounselingScreen
import com.example.ui.screens.HealthyProductivityScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ChillenoughApp()
            }
        }
    }
}

data class NavTabItem(
    val tab: MainTab,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChillenoughApp(viewModel: MainViewModel = viewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val navItems = listOf(
        NavTabItem(MainTab.AWARENESS_DETOX, "Giải Độc", Icons.Default.Psychology, "nav_detox"),
        NavTabItem(MainTab.CBT_COUNSELING, "CBT & SOS", Icons.Default.AutoAwesome, "nav_cbt"),
        NavTabItem(MainTab.HEALTHY_PRODUCTIVITY, "Đủ Tốt", Icons.Default.TaskAlt, "nav_productivity"),
        NavTabItem(MainTab.ANTI_TOXIC_COMMUNITY, "Bức Tường", Icons.Default.Groups, "nav_community"),
        NavTabItem(MainTab.CAPYBARA_PET, "Capybara", Icons.Default.Pets, "nav_capybara")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Đủ - MindSpace THPT",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                navItems.forEach { item ->
                    val isSelected = selectedTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.AWARENESS_DETOX -> AwarenessDetoxScreen(viewModel = viewModel)
                MainTab.CBT_COUNSELING -> CbtCounselingScreen(viewModel = viewModel)
                MainTab.HEALTHY_PRODUCTIVITY -> HealthyProductivityScreen(viewModel = viewModel)
                MainTab.ANTI_TOXIC_COMMUNITY -> AntiToxicCommunityScreen(viewModel = viewModel)
                MainTab.CAPYBARA_PET -> CapybaraPetScreen(viewModel = viewModel)
            }
        }
    }
}
