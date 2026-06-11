package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.FifaViewModel
import com.example.ui.theme.*

@Composable
fun MatchCentreScreen(
    viewModel: FifaViewModel,
    modifier: Modifier = Modifier
) {
    val matches by viewModel.matches.collectAsState()
    val selectedMatch = matches.find { it.id == viewModel.selectedMatchId } ?: matches.first()

    var activeSubTab by remember { mutableStateOf("centre") } // centre, lineup, stats, commentary

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaDarkBg)
    ) {
        // Horizontally scrolling games ticker
        GamesCompactTicker(matches = matches, selectedId = selectedMatch.id, onSelect = { id ->
            viewModel.selectedMatchId = id
        })

        Divider(color = ArenaBorder, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live broadcast scoreboard panel
            item {
                BroadcastScoreboard(match = selectedMatch)
            }

            // Sub Tab Selector
            item {
                SubTabRow(active = activeSubTab, onTabSelect = { activeSubTab = it })
            }

            when (activeSubTab) {
                "centre" -> {
                    item {
                        MediaReactionsSection(match = selectedMatch)
                    }
                    item {
                        LiveTimelineShort(commentary = selectedMatch.commentary.take(4))
                    }
                }
                "lineup" -> {
                    if (selectedMatch.lineupsHome.isNotEmpty()) {
                        item {
                            FootballPitchLineup(match = selectedMatch)
                        }
                        item {
                            PlayerGrids(match = selectedMatch)
                        }
                    } else {
                        item {
                            EmptyLineupsPlaceholder()
                        }
                    }
                }
                "stats" -> {
                    item {
                        DetailedStatsDashboard(match = selectedMatch)
                    }
                }
                "commentary" -> {
                    items(selectedMatch.commentary) { item ->
                        CommentaryRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun GamesCompactTicker(matches: List<Match>, selectedId: String, onSelect: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(matches) { m ->
            val isSelected = m.id == selectedId
            val borderColor = if (isSelected) ArenaPrimary else ArenaBorder
            val containerColor = if (isSelected) ArenaCardBg else ArenaDarkBg

            Card(
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(180.dp)
                    .clickable { onSelect(m.id) }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(m.homeTeam.flagEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(m.homeTeam.code, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(m.awayTeam.flagEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(m.awayTeam.code, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        if (m.status == MatchStatus.LIVE) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LiveRed)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("${m.minute}'", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${m.homeScore}-${m.awayScore}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
                        } else if (m.status == MatchStatus.FINISHED) {
                            Text("FT", fontSize = 10.sp, color = ArenaTextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${m.homeScore}-${m.awayScore}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ArenaTextSecondary)
                        } else {
                            Text("TOMORROW", fontSize = 8.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(m.dateTime.substringBefore("Local").trim(), fontSize = 10.sp, color = ArenaTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BroadcastScoreboard(match: Match) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(ScoreBg, ArenaCardBg)
                )
            )
            .border(1.dp, ArenaBorder, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Broadcaster top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORLD CUP BROADCAST ACTIVE",
                    color = ArenaPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (match.status == MatchStatus.LIVE) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(LiveRed)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE FEED • ${match.minute}' MIN",
                            color = LiveRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (match.status == MatchStatus.FINISHED) {
                        Text(
                            text = "FINAL SCORE",
                            color = ArenaTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "UPCOMING MATCH",
                            color = ArenaTertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score area
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Home team
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(match.homeTeam.flagEmoji, fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(match.homeTeam.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                    Text(match.homeTeam.code, fontSize = 11.sp, color = ArenaTextSecondary)
                }

                // Figures
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (match.status == MatchStatus.UPCOMING) "-" else match.homeScore.toString(),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = if (match.status == MatchStatus.LIVE) ArenaPrimary else Color.White
                    )
                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaTextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    Text(
                        text = if (match.status == MatchStatus.UPCOMING) "-" else match.awayScore.toString(),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = if (match.status == MatchStatus.LIVE) ArenaPrimary else Color.White
                    )
                }

                // Away team
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(match.awayTeam.flagEmoji, fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(match.awayTeam.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                    Text(match.awayTeam.code, fontSize = 11.sp, color = ArenaTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArenaDarkBg.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Place, null, tint = ArenaTertiary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(match.venueName, fontSize = 11.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun SubTabRow(active: String, onTabSelect: (String) -> Unit) {
    val tabs = listOf(
        "centre" to "Home",
        "lineup" to "Lineups",
        "stats" to "Stats",
        "commentary" to "Text Feed"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ArenaCardBg)
            .padding(4.dp)
    ) {
        tabs.forEach { (route, name) ->
            val isSelected = active == route
            val bg = if (isSelected) ArenaBorder else Color.Transparent
            val fg = if (isSelected) ArenaPrimary else ArenaTextSecondary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bg)
                    .clickable { onTabSelect(route) }
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
}

@Composable
fun MediaReactionsSection(match: Match) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("MATCH STATISTICS HIGHLIGHTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ArenaPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            
            // Expected goals xG slider
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(match.homeTeam.code, fontSize = 12.sp, color = Color.White, modifier = Modifier.width(40.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Expected Goals (xG)", fontSize = 10.sp, color = ArenaTextSecondary)
                        Text("${match.expectedGoalsHome}xG vs ${match.expectedGoalsAway}xG", fontSize = 10.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(ArenaBorder)) {
                        val homeW = if (match.expectedGoalsHome + match.expectedGoalsAway > 0) (match.expectedGoalsHome / (match.expectedGoalsHome + match.expectedGoalsAway)).toFloat() else 0.5f
                        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(homeW).background(ArenaPrimary))
                        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth().background(ArenaAccentRed))
                    }
                }
                Text(match.awayTeam.code, fontSize = 12.sp, color = Color.White, modifier = Modifier.width(40.dp).padding(start = 8.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Play highlight card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0x8038B000), Color(0x301E2E44))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = "play highlight",
                            tint = ArenaPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Stream Match Highlights & Goals (4K)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("In-Stadium multi-cam angles active", fontSize = 9.sp, color = ArenaTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun LiveTimelineShort(commentary: List<CommentaryItem>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("MATCH TIMELINE ALERT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaTertiary)
            Spacer(modifier = Modifier.height(12.dp))

            if (commentary.isEmpty()) {
                Text("Pre-match warmups in progress. Dynamic timeline starts on kickoff.", fontSize = 12.sp, color = ArenaTextSecondary)
            } else {
                commentary.forEach { comm ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (comm.eventType == "GOAL") ArenaSecondary.copy(alpha = 0.2f) else ArenaBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${comm.minute}'",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (comm.eventType == "GOAL") ArenaSecondary else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = comm.text,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FootballPitchLineup(match: Match) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .border(1.dp, ArenaBorder, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PitchGrassDark, PitchGrassLight)
                    )
                )
        ) {
            // Draw field markings
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Center line
                drawLine(
                    color = PitchLines,
                    start = Offset(0f, h / 2),
                    end = Offset(w, h / 2),
                    strokeWidth = 3f
                )

                // Center circle
                drawCircle(
                    color = PitchLines,
                    radius = 45.dp.toPx(),
                    center = Offset(w / 2, h / 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(3f)
                )

                // Penalty boxes
                // Top Goal Box
                drawRect(
                    color = PitchLines,
                    topLeft = Offset(w * 0.2f, 0f),
                    size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.2f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(3f)
                )

                // Bottom Goal Box
                drawRect(
                    color = PitchLines,
                    topLeft = Offset(w * 0.2f, h * 0.8f),
                    size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.2f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(3f)
                )
            }

            // Scatter Home players on top and Away players on bottom
            match.lineupsHome.forEach { p ->
                val scaleX = p.x
                // Map home to top half (0.1 to 0.45)
                val scaleY = 0.1f + p.y * 0.35f
                PlayerFieldPin(player = p, scaleX = scaleX, scaleY = scaleY, color = ArenaPrimary)
            }

            match.lineupsAway.forEach { p ->
                val scaleX = p.x
                // Map away to bottom half (0.55 to 0.9)
                val scaleY = 0.52f + p.y * 0.38f
                PlayerFieldPin(player = p, scaleX = scaleX, scaleY = scaleY, color = ArenaTertiary)
            }
        }
    }
}

@Composable
fun BoxScope.PlayerFieldPin(player: PlayerPosition, scaleX: Float, scaleY: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = 10.dp + scaleX.dp * 260f, y = 10.dp + scaleY.dp * 210f)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(player.number.toString(), fontSize = 8.sp, color = ArenaDarkBg, fontWeight = FontWeight.Black)
        }
        Text(
            player.name,
            fontSize = 9.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .background(ArenaDarkBg.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun PlayerGrids(match: Match) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("ACTIVE SQUAD RATINGS", fontSize = 10.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Home list
                Column(modifier = Modifier.weight(1f)) {
                    Text(match.homeTeam.name.uppercase(), fontSize = 11.sp, color = ArenaTertiary, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    match.lineupsHome.take(6).forEach { p ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Text("${p.number}. ${p.name}", fontSize = 12.sp, color = Color.White)
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(ArenaBorder).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                Text(p.rating.toString(), fontSize = 10.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Away list
                Column(modifier = Modifier.weight(1f)) {
                    Text(match.awayTeam.name.uppercase(), fontSize = 11.sp, color = ArenaAccentRed, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    match.lineupsAway.take(6).forEach { p ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Text("${p.number}. ${p.name}", fontSize = 12.sp, color = Color.White)
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(ArenaBorder).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                Text(p.rating.toString(), fontSize = 10.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyLineupsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ArenaCardBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.SportsSoccer, null, tint = ArenaTertiary, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Lineups Released 1 Hour Before Kickoff", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Real-time formations, positional layouts and manager profiles auto-load dynamically.", color = ArenaTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun DetailedStatsDashboard(match: Match) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("LIVE COMPETITION METRICS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            // Possession
            StatRow(name = "Possession (%)", home = match.possessionHome, away = 100 - match.possessionHome)
            Spacer(modifier = Modifier.height(12.dp))

            // Shots
            StatRow(name = "Total Shots", home = match.shotsHome, away = match.shotsAway)
            Spacer(modifier = Modifier.height(12.dp))

            // Passes
            StatRow(name = "Completed Passes", home = match.passesHome, away = match.passesAway)
            Spacer(modifier = Modifier.height(12.dp))

            // Fouls
            StatRow(name = "Fouls Committed", home = match.foulsHome, away = match.foulsAway)
            Spacer(modifier = Modifier.height(12.dp))

            // Yellow cards
            StatRow(name = "Yellow Cards", home = match.yellowHome, away = match.yellowAway)
        }
    }
}

@Composable
fun StatRow(name: String, home: Int, away: Int) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(home.toString(), fontSize = 12.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
            Text(name, fontSize = 11.sp, color = ArenaTextSecondary)
            Text(away.toString(), fontSize = 12.sp, fontWeight = FontWeight.Black, color = ArenaTertiary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(ArenaBorder)
        ) {
            val total = home + away
            val fraction = if (total > 0) home.toFloat() / total.toFloat() else 0.5f
            Box(modifier = Modifier.fillMaxHeight().weight(fraction).background(ArenaPrimary))
            Box(modifier = Modifier.fillMaxHeight().weight(1f - fraction).background(ArenaTertiary))
        }
    }
}

@Composable
fun CommentaryRow(item: CommentaryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (item.eventType == "GOAL") ArenaSecondary.copy(alpha = 0.2f)
                    else if (item.eventType == "CARD") ArenaAccentRed.copy(alpha = 0.2f)
                    else ArenaBorder
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.minute}'",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (item.eventType == "GOAL") ArenaSecondary
                else if (item.eventType == "CARD") ArenaAccentRed
                else Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (item.eventType == "GOAL") "⚽ GOAL EVENT" else if (item.eventType == "CARD") "⚠️ REF DECISION" else "COMMENTARY",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.eventType == "GOAL") ArenaSecondary else if (item.eventType == "CARD") ArenaTertiary else ArenaTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.text,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}
