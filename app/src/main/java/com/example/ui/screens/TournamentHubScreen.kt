package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.FifaViewModel
import com.example.ui.theme.*

@Composable
fun TournamentHubScreen(
    viewModel: FifaViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("groups") } // groups, brackets, teams, stadiums

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaDarkBg)
    ) {
        // Horizontal sub-menu tabs
        TabSelector(active = activeTab, onTabSelect = { activeTab = it })

        Divider(color = ArenaBorder, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (activeTab) {
                "groups" -> {
                    item {
                        Text(
                            text = "OFFICIAL GROUP STANDINGS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ArenaPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    items(Fixtures.Standings) { group ->
                        GroupTableCard(group = group)
                    }
                }
                "brackets" -> {
                    item {
                        KnockoutBracketVisualizer()
                    }
                }
                "teams" -> {
                    item {
                        Text(
                            text = "QUALIFIED TEAM PROFILES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ArenaPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    items(Fixtures.Teams.values.toList().chunked(2)) { pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            pair.forEach { team ->
                                Box(modifier = Modifier.weight(1f)) {
                                    TeamProfileCard(team = team)
                                }
                            }
                            if (pair.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                "stadiums" -> {
                    item {
                        Text(
                            text = "STADIUMS & VENUE INFO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ArenaPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    items(viewModel.stadiums) { stadium ->
                        StadiumGuideCard(stadium = stadium, onCheckIn = {
                            viewModel.visitStadium(stadium.id, stadium.name)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun TabSelector(active: String, onTabSelect: (String) -> Unit) {
    val options = listOf(
        "groups" to "Groups",
        "brackets" to "Bracket",
        "teams" to "Teams",
        "stadiums" to "Venues"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ArenaCardBg)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        options.forEach { (route, label) ->
            val isSelected = active == route
            val borderBrush = if (isSelected) ArenaPrimary else Color.Transparent
            val textStyle = if (isSelected) ArenaPrimary else ArenaTextSecondary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTabSelect(route) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textStyle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(20.dp)
                            .background(borderBrush)
                    )
                }
            }
        }
    }
}

@Composable
fun GroupTableCard(group: GroupStandings) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = group.groupLetter,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ArenaTertiary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Table Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", fontSize = 10.sp, color = ArenaTextSecondary, modifier = Modifier.width(18.dp), fontWeight = FontWeight.Bold)
                Text("TEAM", fontSize = 10.sp, color = ArenaTextSecondary, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.width(120.dp)) {
                    Text("P", fontSize = 10.sp, color = ArenaTextSecondary, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                    Text("GD", fontSize = 10.sp, color = ArenaTextSecondary, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                    Text("PTS", fontSize = 10.sp, color = ArenaTextSecondary, modifier = Modifier.width(32.dp), textAlign = TextAlign.End, fontWeight = FontWeight.Black)
                }
            }

            Divider(color = ArenaBorder, thickness = 0.5.dp, modifier = Modifier.padding(bottom = 4.dp))

            // Body
            group.teams.forEachIndexed { idx, row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text((idx + 1).toString(), fontSize = 12.sp, color = if (idx < 2) ArenaPrimary else ArenaTextSecondary, modifier = Modifier.width(18.dp), fontWeight = FontWeight.Black)
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text(row.team.flagEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(row.team.name, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.width(120.dp)) {
                        Text(row.played.toString(), fontSize = 12.sp, color = Color.White, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                        Text((if (row.gd > 0) "+" else "") + row.gd.toString(), fontSize = 11.sp, color = if (row.gd >= 0) ArenaSecondary else ArenaAccentRed, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                        Text(row.points.toString(), fontSize = 13.sp, color = ArenaPrimary, modifier = Modifier.width(32.dp), textAlign = TextAlign.End, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun KnockoutBracketVisualizer() {
    Column {
        Text("TOURNAMENT BRACKET STAGGER", fontSize = 11.sp, fontWeight = FontWeight.Black, color = ArenaPrimary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
            border = BorderStroke(1.dp, ArenaBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                // Quarter-finals
                Text("QUARTER FINALS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ArenaTertiary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    BracketNode(team1 = "🇺🇸 USA", team2 = "🇫🇷 FRA", score1 = "3", score2 = "2", status = "FT")
                    BracketNode(team1 = "🇲🇽 MEX", team2 = "🇦🇷 ARG", score1 = "1", score2 = "2", status = "FT")
                }

                // Semi-finals
                Text("SEMI FINALS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ArenaAccentRed)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    BracketNode(team1 = "🇺🇸 USA", team2 = "🇦🇷 ARG", score1 = "LIVE", score2 = "-", status = "M84")
                    BracketNode(team1 = "🇧🇷 BRA", team2 = "🇪🇸 ESP", score1 = "TBD", score2 = "TBD", status = "JUL 14")
                }

                // Final
                Text("GRAND FINAL — METLIFE STADIUM", fontSize = 10.sp, fontWeight = FontWeight.Black, color = ArenaPrimary)
                Card(
                    colors = CardDefaults.cardColors(containerColor = ScoreBg),
                    border = BorderStroke(1.dp, ArenaPrimary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Winner Semi 1", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("VS", fontSize = 12.sp, color = ArenaPrimary, fontWeight = FontWeight.ExtraBold)
                            Text("Winner Semi 2", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("July 19, 2026 — 19:00 EST", fontSize = 10.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.BracketNode(team1: String, team2: String, score1: String, score2: String, status: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(team1, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(score1, fontSize = 11.sp, color = ArenaPrimary, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(team2, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(score2, fontSize = 11.sp, color = ArenaPrimary, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(status, fontSize = 8.sp, color = ArenaTertiary, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.End))
        }
    }
}

@Composable
fun TeamProfileCard(team: Team) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(team.flagEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(team.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Qualifying: ${team.group}", fontSize = 10.sp, color = ArenaTextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Key MVP: Pulisic / Davies / Gimenez", fontSize = 10.sp, color = ArenaPrimary, fontWeight = FontWeight.Bold)
            Text("Base Camp: Dallas / CDMX / Toronto", fontSize = 9.sp, color = ArenaTextSecondary)
        }
    }
}

@Composable
fun StadiumGuideCard(stadium: Stadium, onCheckIn: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(stadium.name, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("${stadium.city}, ${stadium.country}", fontSize = 11.sp, color = ArenaTextSecondary)
                }
                
                Button(
                    onClick = onCheckIn,
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaTertiary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardMembership, null, tint = ArenaDarkBg, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check In", color = ArenaDarkBg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(stadium.description, fontSize = 12.sp, color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("CAPACITY", fontSize = 9.sp, color = ArenaTextSecondary, fontWeight = FontWeight.Bold)
                    Text(stadium.capacity, fontSize = 13.sp, color = ArenaPrimary, fontWeight = FontWeight.ExtraBold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("COORDINATES", fontSize = 9.sp, color = ArenaTextSecondary, fontWeight = FontWeight.Bold)
                    Text(stadium.coordinates, fontSize = 11.sp, color = ArenaTertiary, fontWeight = FontWeight.Bold)
                }
                if (stadium.openingMatch) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ArenaAccentRed)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text("OPENING VENUE", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
