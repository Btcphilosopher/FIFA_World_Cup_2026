package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FifaApp(
    viewModel: FifaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val activeAlert = viewModel.activeNotificationAlert

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaDarkBg),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            colors = listOf(ArenaPrimary, Color.White)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(ArenaDarkBg, shape = RoundedCornerShape(3.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "FWC",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "FIFA 2026",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArenaDarkBg,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Standard polished M3 Bottom Navigation Bar
            NavigationBar(
                containerColor = ArenaCardBg,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding() // Safely offsets system navigation gestured bar
            ) {
                val items = listOf(
                    NavigationItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
                    NavigationItem("matches", "Matches", Icons.Filled.SportsSoccer, Icons.Outlined.SportsSoccer),
                    NavigationItem("hub", "Standings", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard),
                    NavigationItem("tickets", "Wallet", Icons.Filled.CardMembership, Icons.Outlined.CardMembership),
                    NavigationItem("stadium", "Stadium", Icons.Filled.Sensors, Icons.Outlined.Sensors),
                    NavigationItem("more", "Companion", Icons.Filled.Widgets, Icons.Outlined.Widgets)
                )

                items.forEach { item ->
                    val isSelected = viewModel.currentNavRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.currentNavRoute = item.route },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
                                contentDescription = item.label,
                                tint = if (isSelected) ArenaDarkBg else ArenaTextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ArenaPrimary else ArenaTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = ArenaPrimary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main views routing
            when (viewModel.currentNavRoute) {
                "home" -> HomeScreen(viewModel = viewModel)
                "matches" -> MatchCentreScreen(viewModel = viewModel)
                "hub" -> TournamentHubScreen(viewModel = viewModel)
                "tickets" -> TicketingScreen(viewModel = viewModel)
                "stadium" -> StadiumScreen(viewModel = viewModel)
                else -> UtilitiesScreen(viewModel = viewModel)
            }

            // Real-time floating broadcast Goal/Alert Banner
            AnimatedVisibility(
                visible = activeAlert != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                if (activeAlert != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LiveRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.dismissAlert() }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Alert banner",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = activeAlert,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.dismissAlert() }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector
)
