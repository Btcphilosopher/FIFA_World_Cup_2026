package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AiState
import com.example.ui.FifaViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilitiesScreen(
    viewModel: FifaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("ai") } // ai, passport, travel, festival, store, ar

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaDarkBg)
    ) {
        // Horizontal scrolling chips for the six main sub-features!
        LazyRow(
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().background(ArenaCardBg)
        ) {
            val chips = listOf(
                "ai" to Pair("AI Assistant", Icons.Default.SmartToy),
                "passport" to Pair("Fan Passport", Icons.Default.Public),
                "travel" to Pair("Travel Comp", Icons.Default.Navigation),
                "festival" to Pair("Festivals", Icons.Default.MusicNote),
                "store" to Pair("Custom Shop", Icons.Default.Storefront),
                "ar" to Pair("AR Guide", Icons.Default.CameraAlt)
            )
            items(chips) { (route, pair) ->
                val isSelected = selectedTab == route
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTab = route },
                    label = { Text(pair.first, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(pair.second, null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ArenaPrimary,
                        selectedLabelColor = ArenaDarkBg,
                        selectedLeadingIconColor = ArenaDarkBg,
                        containerColor = ArenaBorder,
                        labelColor = Color.White,
                        iconColor = ArenaPrimary
                    ),
                    border = BorderStroke(1.dp, if (isSelected) ArenaPrimary else Color.Transparent)
                )
            }
        }

        Divider(color = ArenaBorder, thickness = 1.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedTab) {
                "ai" -> AiAssistantTab(viewModel)
                "passport" -> FanPassportTab(viewModel)
                "travel" -> TravelCompanionTab(viewModel)
                "festival" -> FanFestivalTab(viewModel)
                "store" -> CustomShopTab(viewModel)
                "ar" -> ArGuideTab()
            }
        }
    }
}

// --- TAB 1: AI Assistant ---
@Composable
fun AiAssistantTab(viewModel: FifaViewModel) {
    val chatHistory by viewModel.aiChatHistory.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(ArenaDarkBg)
        ) {
            if (chatHistory.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.SmartToy, null, tint = ArenaPrimary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("FIFA AI Oracle Assistant", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Ask me anything about flights to LA, security checkpoints at Azteca, transit schedules at MetLife, or match kickoff schedules!",
                        color = ArenaTextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatHistory) { msg ->
                        ChatBubble(message = msg)
                    }
                    if (viewModel.aiState is AiState.Loading) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
                                CircularProgressIndicator(color = ArenaPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Gemini 3.5 Flash holds the field...", color = ArenaPrimary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Divider(color = ArenaBorder, modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.aiTextQuery,
                onValueChange = { viewModel.aiTextQuery = it },
                placeholder = { Text("How do I get to MetLife Stadium?", fontSize = 12.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.askAssistant() },
                colors = IconButtonDefaults.iconButtonColors(containerColor = ArenaPrimary)
            ) {
                Icon(Icons.Default.Send, null, tint = ArenaDarkBg)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessageEntity) {
    val isUser = message.sender == "USER"
    val align = if (isUser) Alignment.End else Alignment.Start
    val containerBg = if (isUser) ArenaBorder else ArenaCardBg
    val label = if (isUser) "YOU" else "FIFA ASSISTANT"
    val labelColor = if (isUser) ArenaTertiary else ArenaPrimary

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isUser) Icons.Default.Person else Icons.Default.SmartToy,
                contentDescription = null,
                tint = labelColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Black, color = labelColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(containerBg)
                .padding(12.dp)
        ) {
            Text(message.content, color = Color.White, fontSize = 12.sp)
        }
    }
}

// --- TAB 2: Fan Passport ---
@Composable
fun FanPassportTab(viewModel: FifaViewModel) {
    val visits by viewModel.myVisitedStadiums.collectAsState()
    val badges by viewModel.myBadges.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Passport Cover header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaBorder),
                border = BorderStroke(1.dp, ArenaPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Public, null, tint = ArenaPrimary, modifier = Modifier.size(44.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("DIGITAL FIFA PASSPORT 2026", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("Digital Identity Sandbox • Fan ID #4872-WC", fontSize = 11.sp, color = ArenaTextSecondary)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("STADIUMS VISITED", fontSize = 9.sp, color = ArenaTextSecondary)
                            Text("${visits.size} / 3", fontSize = 18.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BADGES RECLAIMED", fontSize = 9.sp, color = ArenaTextSecondary)
                            Text(badges.size.toString(), fontSize = 18.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Earned Badges section
        item {
            Text("EARNED ACHIEVEMENTS & MILESTONES", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
        }

        if (badges.isEmpty()) {
            item {
                Text("Preorder jerseys, check in at stadiums, or purchase tickets to unlock prestigious World Cup achievements!", fontSize = 11.sp, color = ArenaTextSecondary)
            }
        } else {
            items(badges) { badge ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(0.5.dp, ArenaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when (badge.category) {
                                        "Match" -> ArenaAccentRed.copy(alpha = 0.15f)
                                        "Stadium" -> ArenaSecondary.copy(alpha = 0.15f)
                                        else -> ArenaTertiary.copy(alpha = 0.15f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (badge.category) {
                                    "Match" -> Icons.Default.SportsSoccer
                                    "Stadium" -> Icons.Default.Place
                                    "Merch" -> Icons.Default.ShoppingBag
                                    else -> Icons.Default.SmartToy
                                },
                                contentDescription = null,
                                tint = when (badge.category) {
                                    "Match" -> ArenaAccentRed
                                    "Stadium" -> ArenaSecondary
                                    else -> ArenaTertiary
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(badge.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(badge.description, fontSize = 11.sp, color = ArenaTextSecondary)
                            Text(
                                text = "Earned " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(badge.timestampEarned)),
                                fontSize = 9.sp,
                                color = ArenaPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 3: Travel Companion ---
@Composable
fun TravelCompanionTab(viewModel: FifaViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("MULTI-REGION TRAVEL SEARCH ENGINE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)

        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
            border = BorderStroke(1.dp, ArenaBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = viewModel.travelFromCity,
                    onValueChange = { viewModel.travelFromCity = it },
                    label = { Text("Starting Location", fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.travelToStadium,
                    onValueChange = { viewModel.travelToStadium = it },
                    label = { Text("Destination Stadium", fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { viewModel.findRoutes() },
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = ArenaDarkBg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calculate Optimal Transit Legs", color = ArenaDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Text("OPTIMIZED TRANSIT LEGS MATCHED", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaTertiary)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(viewModel.matchedRoutes) { route ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(0.5.dp, ArenaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(ArenaBorder).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(route.type.uppercase(), color = ArenaPrimary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                            Text("Est Duration: ${route.stages.sumOf { it.durMinutes }} mins", color = ArenaSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        route.stages.forEachIndexed { idx, stage ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                Box(
                                    modifier = Modifier.size(24.dp).clip(CircleShape).background(ArenaBorder),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (stage.iconName) {
                                            "directions_walk" -> Icons.Default.DirectionsWalk
                                            "directions_bus" -> Icons.Default.DirectionsBus
                                            "train" -> Icons.Default.Train
                                            else -> Icons.Default.DirectionsCar
                                        },
                                        null,
                                        tint = ArenaPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("${stage.mode} (${stage.durMinutes}m)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(stage.details, fontSize = 10.sp, color = ArenaTextSecondary)
                                }
                            }
                            if (idx < route.stages.size - 1) {
                                Box(modifier = Modifier.padding(start = 11.dp).width(2.dp).height(12.dp).background(ArenaBorder))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 4: Festivals ---
@Composable
fun FanFestivalTab(viewModel: FifaViewModel) {
    val events by viewModel.fanEvents.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("FIFA FAN CHAMP FESTIVALS & WATCH CONCERTS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
        }

        items(events) { event ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                border = BorderStroke(1.dp, ArenaBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(ArenaBorder).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(event.category.uppercase(), fontSize = 9.sp, color = ArenaSecondary, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = GoldStar, modifier = Modifier.size(14.dp))
                            Text(event.rating.toString(), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(event.name, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("📍 Location: ${event.location} (${event.city})", fontSize = 11.sp, color = ArenaTextSecondary)
                    Text("🎤 Live Headliner: ${event.headliner}", fontSize = 11.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)

                    Text(event.description, fontSize = 11.sp, color = Color.White)

                    Divider(color = ArenaBorder)

                    Button(
                        onClick = { viewModel.registerForEvent(event) },
                        enabled = !event.isRegistered,
                        colors = ButtonDefaults.buttonColors(containerColor = ArenaSecondary, disabledContainerColor = ArenaBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (event.isRegistered) "✓ Reserved Seat Pass Saved" else "Secure Free Entry Ticket",
                            color = if (event.isRegistered) ArenaTextSecondary else ArenaDarkBg,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

// --- TAB 5: Store ---
@Composable
fun CustomShopTab(viewModel: FifaViewModel) {
    var selectMerch by remember { mutableStateOf(viewModel.merchandise.first()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("OFFICIAL MERCHANDISE CUSTOM DESIGN BOOTH", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
        }

        // Product selection row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                items(viewModel.merchandise) { item ->
                    val isSelected = selectMerch.id == item.id
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) ArenaBorder else ArenaCardBg),
                        border = BorderStroke(1.dp, if (isSelected) ArenaPrimary else Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(160.dp).clickable { selectMerch = item }
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(item.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("\$${String.format("%.2f", item.price)}", fontSize = 11.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Customizer Sandbox
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                border = BorderStroke(1.dp, ArenaBorder),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(selectMerch.name, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(selectMerch.description, fontSize = 11.sp, color = ArenaTextSecondary)

                    if (selectMerch.isCustomizable) {
                        Divider(color = ArenaBorder)
                        Text("Personalise Back Shirt Printing", fontSize = 12.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = viewModel.customJerseyName,
                                onValueChange = { viewModel.customJerseyName = it.uppercase() },
                                label = { Text("Name", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                                modifier = Modifier.weight(2f)
                            )
                            OutlinedTextField(
                                value = viewModel.customJerseyNumber,
                                onValueChange = { viewModel.customJerseyNumber = it },
                                label = { Text("No.", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Replica Kit back box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(ArenaAccentRed, ArenaDarkBg)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = viewModel.customJerseyName.ifBlank { "YOUR SQUAD" },
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = viewModel.customJerseyNumber.ifBlank { "26" },
                                    fontSize = 64.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ArenaTertiary,
                                    lineHeight = 64.sp
                                )
                            }
                        }
                    }

                    Divider(color = ArenaBorder)

                    Button(
                        onClick = {
                            viewModel.orderCustomJersey(selectMerch.name, viewModel.customJerseyName, viewModel.customJerseyNumber)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ArenaPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingBag, null, tint = ArenaDarkBg)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Complete Checkout (\$${selectMerch.price})", color = ArenaDarkBg, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    if (viewModel.isMerchOrdered) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ArenaSecondary.copy(alpha = 0.15f))
                                .padding(10.dp)
                        ) {
                            Text("Checkout successfully processed. Receipt index and express QR saved in Passport achievements archive.", color = ArenaSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 6: AR Guide ---
@Composable
fun ArGuideTab() {
    var selectedArMode by remember { mutableStateOf("stadium") } // stadium, trophy

    Column(modifier = Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("AUGMENTED REALITY (AR) GRAPHICS PLATFORM", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { selectedArMode = "stadium" },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedArMode == "stadium") ArenaPrimary else ArenaBorder),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Stadium Pointers", color = if (selectedArMode == "stadium") ArenaDarkBg else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { selectedArMode = "trophy" },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedArMode == "trophy") ArenaPrimary else ArenaBorder),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text("Trophy Photo Op", color = if (selectedArMode == "trophy") ArenaDarkBg else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Divider(color = ArenaBorder)

        // Mock Camera Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, ArenaPrimary, RoundedCornerShape(24.dp))
                .background(Color.Black)
        ) {
            // Simulated camera scanner loop animation represented by Canvas background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val h = size.height
                val w = size.width

                // Scanning grid lines
                drawLine(
                    color = ArenaPrimary.copy(alpha = 0.3f),
                    start = Offset(0f, h * 0.3f),
                    end = Offset(w, h * 0.3f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = ArenaPrimary.copy(alpha = 0.3f),
                    start = Offset(0f, h * 0.7f),
                    end = Offset(w, h * 0.7f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = ArenaPrimary.copy(alpha = 0.3f),
                    start = Offset(w * 0.5f, 0f),
                    end = Offset(w * 0.5f, h),
                    strokeWidth = 2f
                )
            }

            if (selectedArMode == "stadium") {
                // Stadium AR Indicators overlay
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(ArenaDarkBg.copy(alpha = 0.8f))
                            .padding(10.dp)
                    ) {
                        Text("AR LENS: Point your camera at a stadium tower, match pitch or seat cluster to compute holographic HUD directions.", color = Color.White, fontSize = 11.sp)
                    }

                    // Hover tags
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ArHoverTag(label = "CONCESSION COUNTER 4", dist = "14 meters", tint = ArenaSecondary)
                        ArHoverTag(label = "METLIFE GATE A CHECK POINT", dist = "52 meters", tint = ArenaPrimary)
                        ArHoverTag(label = "SECTION 114 ROW F SEATS", dist = "28 meters", tint = ArenaTertiary)
                    }
                }
            } else {
                // 3D Trophy Simulator
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(ArenaDarkBg.copy(alpha = 0.8f))
                            .padding(10.dp)
                    ) {
                        Text("3D TROPHY SCAN: Place the legendary 18k Gold FIFA World Cup Trophy in your living room or next to your seat!", color = Color.White, fontSize = 11.sp)
                    }

                    // Visual representation of trophy in Center
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldStar,
                            modifier = Modifier.size(110.dp)
                        )
                        Text("FIFA WORLD CUP™ 3D OBJECT", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldStar)
                    }
                    
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = ArenaPrimary)
                    ) {
                        Icon(Icons.Default.Camera, null, tint = ArenaDarkBg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Take Holographic Snapshot", color = ArenaDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ArHoverTag(label: String, dist: String, tint: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ArenaDarkBg.copy(alpha = 0.85f))
            .border(1.dp, tint, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(tint))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(ArenaBorder).padding(horizontal = 4.dp, vertical = 2.dp)) {
            Text(dist, fontSize = 9.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
