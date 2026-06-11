package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.FifaViewModel
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketingScreen(
    viewModel: FifaViewModel,
    modifier: Modifier = Modifier
) {
    val tickets by viewModel.myTickets.collectAsState()
    val matches by viewModel.matches.collectAsState()

    var activeViewTab by remember { mutableStateOf("wallet") } // wallet, buy

    // Buy fields
    var selectMatchIndex by remember { mutableStateOf(0) }
    var inputSection by remember { mutableStateOf("Sec 114") }
    var inputRow by remember { mutableStateOf("Row E") }
    var inputSeat by remember { mutableStateOf("Seat 12") }
    var inputName by remember { mutableStateOf("Tommy Ah") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaDarkBg)
    ) {
        // Mode selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ArenaCardBg)
                .padding(8.dp)
        ) {
            Button(
                onClick = { activeViewTab = "wallet" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeViewTab == "wallet") ArenaPrimary else ArenaBorder
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CardMembership, null, tint = if (activeViewTab == "wallet") ArenaDarkBg else Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Digital Wallet (${tickets.size})", color = if (activeViewTab == "wallet") ArenaDarkBg else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Button(
                onClick = { activeViewTab = "buy" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeViewTab == "buy") ArenaPrimary else ArenaBorder
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddShoppingCart, null, tint = if (activeViewTab == "buy") ArenaDarkBg else Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buy Passports", color = if (activeViewTab == "buy") ArenaDarkBg else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            if (activeViewTab == "wallet") {
                if (tickets.isEmpty()) {
                    item {
                        EmptyWalletState(onNavigateBuy = { activeViewTab = "buy" })
                    }
                } else {
                    item {
                        Text(
                            text = "SECURE MOBILE ECO-TICKETS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ArenaPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    items(tickets) { ticket ->
                        WalletTicketCard(ticket = ticket, onTransfer = { email ->
                            viewModel.transferTicket(ticket.id, email)
                        }, onDelete = {
                            viewModel.deleteTicket(ticket.id)
                        })
                    }
                }
            } else {
                // Buy View
                item {
                    Text(
                        text = "ACQUIRE MATCH PASSES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = ArenaPrimary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
                        border = BorderStroke(1.dp, ArenaBorder),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Select Match fixture:", fontSize = 12.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
                            
                            // Simple match selectors
                            matches.forEachIndexed { index, m ->
                                val isSelected = selectMatchIndex == index
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) ArenaBorder else ArenaDarkBg),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, if (isSelected) ArenaPrimary else Color.Transparent),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectMatchIndex = index }
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectMatchIndex = index },
                                            colors = RadioButtonDefaults.colors(selectedColor = ArenaPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(m.homeTeam.flagEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${m.homeTeam.code} vs ${m.awayTeam.code}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(m.stage.substringBefore("Stage").trim(), fontSize = 10.sp, color = ArenaTextSecondary)
                                    }
                                }
                            }

                            // Input fields
                            Text("Stadium Section Details:", fontSize = 12.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = inputSection,
                                    onValueChange = { inputSection = it },
                                    label = { Text("Section", fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                                    modifier = Modifier.weight(1.2f)
                                )
                                OutlinedTextField(
                                    value = inputRow,
                                    onValueChange = { inputRow = it },
                                    label = { Text("Row", fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = inputSeat,
                                    onValueChange = { inputSeat = it },
                                    label = { Text("Seat", fontSize = 11.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = inputName,
                                onValueChange = { inputName = it },
                                label = { Text("Legal Ticket Holder Name") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    viewModel.purchaseTicket(matches[selectMatchIndex], inputSection, inputRow, inputSeat, inputName)
                                    // Reset active tab to wallet
                                    activeViewTab = "wallet"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ArenaPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = ArenaDarkBg)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Acquire Ticket & Verify Identity", color = ArenaDarkBg, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EMPTY_QR() {
    Canvas(
        modifier = Modifier
            .size(110.dp)
            .background(Color.White)
            .border(2.dp, ArenaDarkBg)
    ) {
        val sizeVal = size.width
        val blockCount = 8
        val blockSize = sizeVal / blockCount
        
        // Render stylized mock QR grid blocks
        for (i in 0 until blockCount) {
            for (j in 0 until blockCount) {
                // Corner positioning squares
                val isFinderPattern = (i < 3 && j < 3) || (i >= blockCount - 3 && j < 3) || (i < 3 && j >= blockCount - 3)
                if (isFinderPattern) {
                    drawRect(
                        color = ArenaDarkBg,
                        topLeft = Offset(i * blockSize, j * blockSize),
                        size = Size(blockSize, blockSize)
                    )
                } else if ((i + j) % 2 == 0 && (i * j) % 3 != 0) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(i * blockSize, j * blockSize),
                        size = Size(blockSize, blockSize)
                    )
                }
            }
        }
    }
}

@Composable
fun WalletTicketCard(ticket: TicketEntity, onTransfer: (String) -> Unit, onDelete: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    var transferEmailInput by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, if (ticket.isTransferred) ArenaBorder else ArenaPrimary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, null, tint = ArenaSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SECURE IDENTITY VERIFIED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (ticket.isTransferred) ArenaBorder else ArenaAccentRed.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (ticket.isTransferred) "TRANSFERRED" else "ACTIVE ENTRY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = if (ticket.isTransferred) ArenaTextSecondary else ArenaAccentRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Match description
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ticket.homeEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(ticket.homeCode, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(" vs ", fontSize = 16.sp, color = ArenaTextSecondary)
                Text(ticket.awayCode, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(ticket.awayEmoji, fontSize = 24.sp)
            }
            Text(ticket.venue, fontSize = 11.sp, color = ArenaTextSecondary, maxLines = 1)
            Text(ticket.dateString, fontSize = 11.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            // Seating indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArenaBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SECTION", fontSize = 8.sp, color = ArenaTextSecondary)
                    Text(ticket.section, fontSize = 13.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ROW", fontSize = 8.sp, color = ArenaTextSecondary)
                    Text(ticket.row, fontSize = 13.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SEAT", fontSize = 8.sp, color = ArenaTextSecondary)
                    Text(ticket.seat, fontSize = 13.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expand link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Hide QR Entrance Code" else "Tap to Show QR & Transfer Options",
                    fontSize = 11.sp,
                    color = ArenaPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = ArenaPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Divider(color = ArenaBorder)
                    
                    // The QR code canvas representation
                    EMPTY_QR()

                    Text(
                        text = "ID: ${ticket.id} • Holder: ${ticket.ownerName}",
                        fontSize = 11.sp,
                        color = ArenaTextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    if (!ticket.isTransferred) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ArenaDarkBg),
                            border = BorderStroke(1.dp, ArenaBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Transfer Ticket to Friend's Email", fontSize = 11.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = transferEmailInput,
                                        onValueChange = { transferEmailInput = it },
                                        placeholder = { Text("friend@example.com", fontSize = 12.sp) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArenaPrimary, unfocusedBorderColor = ArenaBorder),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (transferEmailInput.isNotBlank()) {
                                                onTransfer(transferEmailInput)
                                                transferEmailInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ArenaPrimary),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Text("Sent", color = ArenaDarkBg, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Transferred in safe custody to: ${ticket.transferEmail}",
                            color = ArenaSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Delete pass button
                    TextButton(onClick = onDelete) {
                        Text("Remove Ticket from Wallet", color = ArenaAccentRed, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyWalletState(onNavigateBuy: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(ArenaCardBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.CardMembership, null, tint = ArenaTertiary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Digital Ticket Wallet Empty", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Text("Secure matching pass keys are stored in this device sandbox when acquired.", color = ArenaTextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateBuy,
                colors = ButtonDefaults.buttonColors(containerColor = ArenaPrimary)
            ) {
                Text("Browse Matches", color = ArenaDarkBg, fontWeight = FontWeight.Bold)
            }
        }
    }
}
