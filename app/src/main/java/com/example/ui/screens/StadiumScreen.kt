package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.FifaViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StadiumScreen(
    viewModel: FifaViewModel,
    modifier: Modifier = Modifier
) {
    val city = viewModel.selectedLocationCity
    val stadium = when {
        city.contains("New York", ignoreCase = true) -> viewModel.stadiums.first { it.id == "metlife" }
        city.contains("Mexico", ignoreCase = true) -> viewModel.stadiums.first { it.id == "azteca" }
        else -> viewModel.stadiums.first { it.id == "bcplace" }
    }

    var selectedSubSection by remember { mutableStateOf("concessions") } // concessions, map, gates, safety

    // Concession Order simulation
    var orderingItem by remember { mutableStateOf<ConcessionItem?>(null) }
    var orderStatusPhase by remember { mutableStateOf(0) } // 0: Idle, 1: Sent, 2: Preparing, 3: Ready

    LaunchedEffect(orderingItem) {
        if (orderingItem != null) {
            orderStatusPhase = 1
            delay(2000)
            orderStatusPhase = 2
            delay(3000)
            orderStatusPhase = 3
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaDarkBg)
    ) {
        // Active location alert header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(colors = listOf(ScoreBg, ArenaDarkBg))
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = ArenaSecondary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("MATCHDAY MODE ACTIVE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(ArenaPrimary))
                    }
                    Text(text = "NFC Approaching Entrance • ${stadium.name}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Horizontal toggle rows
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ArenaCardBg)
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            val buttons = listOf(
                "concessions" to "Food",
                "gates" to "Gates & Sec",
                "safety" to "Safety Aid"
            )
            buttons.forEach { (mode, name) ->
                val isSelected = selectedSubSection == mode
                val bg = if (isSelected) ArenaBorder else Color.Transparent
                val fg = if (isSelected) ArenaPrimary else ArenaTextSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bg)
                        .clickable { selectedSubSection = mode }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = fg
                    )
                }
            }
        }

        Divider(color = ArenaBorder, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedSubSection) {
                "concessions" -> {
                    // Check if order is in progress
                    if (orderingItem != null) {
                        item {
                            OrderStatusCard(item = orderingItem!!, phase = orderStatusPhase, onCancel = {
                                orderingItem = null
                                orderStatusPhase = 0
                            })
                        }
                    }

                    item {
                        Text(
                            text = "PREMIUM IN-STADIUM FOOD DELIVERY & EXPRESS PICKUP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ArenaPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    items(stadium.concessions) { item ->
                        ConcessionItemRow(item = item, onOrder = {
                            orderingItem = item
                        })
                    }
                }
                "gates" -> {
                    item {
                        SecurityGatesPanel(stadium = stadium)
                    }
                }
                "safety" -> {
                    item {
                        SafetyEmergencyAidsBoard(stadium = stadium)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusCard(item: ConcessionItem, phase: Int, onCancel: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ScoreBg),
        border = BorderStroke(1.dp, ArenaSecondary),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "EXPRESS CONCESSION ORDERING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = ArenaSecondary
                )
                
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ArenaTextSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(onClick = onCancel)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Order: ${item.name}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "Express Pickup: Counter 4 (Section 114 Corridor)", fontSize = 11.sp, color = ArenaTextSecondary)
            
            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar Steps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicator(name = "Received", isActive = phase >= 1, isCompleted = phase > 1)
                Divider(color = if (phase >= 2) ArenaSecondary else ArenaBorder, modifier = Modifier.weight(1f).height(2.dp))
                StepIndicator(name = "Cooking", isActive = phase >= 2, isCompleted = phase > 2)
                Divider(color = if (phase >= 3) ArenaSecondary else ArenaBorder, modifier = Modifier.weight(1f).height(2.dp))
                StepIndicator(name = "Pick Up", isActive = phase >= 3, isCompleted = phase >= 3)
            }

            if (phase == 3) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArenaSecondary.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = ArenaSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ORDER READY! Present order screen at Counter 4 for instant handover.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ArenaSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(name: String, isActive: Boolean, isCompleted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isCompleted) ArenaSecondary
                    else if (isActive) ScoreBg
                    else ArenaBorder
                )
                .border(
                    width = 1.dp,
                    color = if (isActive) ArenaSecondary else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, null, tint = ArenaDarkBg, modifier = Modifier.size(12.dp))
            } else {
                Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(if (isActive) ArenaSecondary else ArenaTextSecondary))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, fontSize = 9.sp, color = if (isActive) ArenaSecondary else ArenaTextSecondary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ConcessionItemRow(item: ConcessionItem, onOrder: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ArenaBorder)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(item.category.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = ArenaPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Star, null, tint = GoldStar, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(item.rating.toString(), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(item.description, fontSize = 11.sp, color = ArenaTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("\$${String.format("%.2f", item.price)}", fontSize = 13.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)
                    Text("Wait: ${item.waitTimeMinutes} mins", fontSize = 11.sp, color = ArenaTextSecondary)
                }
            }

            Button(
                onClick = onOrder,
                colors = ButtonDefaults.buttonColors(containerColor = ArenaPrimary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Preorder", color = ArenaDarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SecurityGatesPanel(stadium: Stadium) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("APPROACHING GATE CHECKPOINTS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
            
            // Gates lists
            StadiumGateStateRow(name = stadium.gates.getOrNull(0) ?: "Gate A - North", status = "LOW WAIT (4 min)", tint = ArenaSecondary)
            StadiumGateStateRow(name = stadium.gates.getOrNull(1) ?: "Gate B - Meadow", status = "MEDIUM (12 min)", tint = ArenaTertiary)
            StadiumGateStateRow(name = stadium.gates.getOrNull(2) ?: "Gate C - South", status = "CLOSED (Staff only)", tint = ArenaTextSecondary)

            Divider(color = ArenaBorder)

            Text("SAFETY REGULATIONS & PROHIBITED LIST", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaAccentRed)
            Text("• Transparent bags of max 12x12x6 inches permitted.\n• Powerbanks permitted under 10,000 mAH.\n• No professional camera lenses, flares, or custom banners over 2 meters.", fontSize = 11.sp, color = Color.White)
        }
    }
}

@Composable
fun StadiumGateStateRow(name: String, status: String, tint: Color) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(tint.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(status, fontSize = 10.sp, color = tint, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SafetyEmergencyAidsBoard(stadium: Stadium) {
    var isAlerted by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("EMERGENCY RESPONSE DESK", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaAccentRed)
            Text("Direct communication line to MetLife & Azteca security commands. Location triangulation is computed securely from on-device sandbox.", fontSize = 11.sp, color = Color.White)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("EMERGENCY STATION", fontSize = 9.sp, color = ArenaTextSecondary)
                    Text("Section 130 Entrance Corridor", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("DIAL HELP", fontSize = 9.sp, color = ArenaTextSecondary)
                    Text("In-Stadium Ext 911-FIFA", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Divider(color = ArenaBorder)

            Button(
                onClick = { isAlerted = true },
                colors = ButtonDefaults.buttonColors(containerColor = if (isAlerted) ArenaBorder else ArenaAccentRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = if (isAlerted) ArenaAccentRed else Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAlerted) "AID RECON DISPATCHED" else "Alert Paramedics (Incident Section)",
                        color = if (isAlerted) ArenaAccentRed else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isAlerted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArenaSecondary.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Text("Emergency beacon transmitted! Coordinated coordinates transmitted to Stadium Command center. Help is approaching your section shortly.", color = ArenaSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
