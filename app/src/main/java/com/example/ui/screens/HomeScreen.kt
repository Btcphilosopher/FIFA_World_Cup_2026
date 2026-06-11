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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.FifaViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FifaViewModel,
    modifier: Modifier = Modifier
) {
    val matches by viewModel.matches.collectAsState()
    val liveMatches = matches.filter { it.status == MatchStatus.LIVE }
    val fanEvents by viewModel.fanEvents.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming & Countdown Header
        item {
            HeaderSection(viewModel)
        }

        // Live Matches Ticker
        if (liveMatches.isNotEmpty()) {
            item {
                SectionTitle(title = "LIVE NOW", icon = Icons.Default.FiberManualRecord, tint = LiveRed)
            }
            items(liveMatches) { match ->
                LiveMatchCard(match = match, onClick = {
                    viewModel.selectedMatchId = match.id
                    viewModel.currentNavRoute = "matches"
                })
            }
        } else {
            // Placeholder/Finished matches header
            item {
                SectionTitle(title = "TODAY'S MATCHES", icon = Icons.Default.SportsSoccer, tint = ArenaPrimary)
            }
            items(matches) { match ->
                MatchRowCard(match = match, onClick = {
                    viewModel.selectedMatchId = match.id
                    viewModel.currentNavRoute = "matches"
                })
            }
        }

        // Quick Travel & Host City Customize Info Banner
        item {
            CustomizationQuickBanner(viewModel)
        }

        // Tournament News Section
        item {
            SectionTitle(title = "LATEST WORLD CUP NEWS", icon = Icons.Default.Feed, tint = ArenaTertiary)
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(viewModel.news) { article ->
                    NewsRowItem(article = article)
                }
            }
        }

        // Recommended Fan Festivals
        item {
            SectionTitle(title = "RECOMMENDED FAN FESTIVALS", icon = Icons.Default.ConfirmationNumber, tint = ArenaSecondary)
        }
        items(fanEvents.take(2)) { event ->
            FanEventHomeItem(event = event, onRegister = {
                viewModel.registerForEvent(event)
            })
        }

        // Official Merch Callout Card
        item {
            MerchandiseTeaserCard(onClick = {
                viewModel.currentNavRoute = "more" // this will navigate and show the utilities
            })
        }
    }
}

@Composable
fun HeaderSection(viewModel: FifaViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(ArenaCardBg, ArenaDarkBg)
                )
            )
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "FIFA WORLD CUP 2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArenaPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "North America 2026™",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArenaAccentRed)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "LIVE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Unified host nations statement
            Text(
                text = "United States  |  Mexico  |  Canada",
                style = MaterialTheme.typography.bodyMedium,
                color = ArenaTextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown timer simulation or status statement
            Card(
                colors = CardDefaults.cardColors(containerColor = ArenaBorder.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Event",
                        tint = ArenaPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "TOURNAMENT HAS OFFICIALLY BEGUN!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ArenaTertiary
                        )
                        Text(
                            text = "June 11 - July 19, 2026",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMatchCard(match: Match, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E40AF).copy(alpha = 0.8f), ArenaCardBg)
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(LiveRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE • ${match.minute}' MIN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LiveRed
                    )
                }
                Text(
                    text = match.stage,
                    fontSize = 11.sp,
                    color = ArenaTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Home Team
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(match.homeTeam.flagEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = match.homeTeam.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Scores
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = match.homeScore.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = ArenaPrimary
                    )
                    Text(
                        text = " - ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = match.awayScore.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = ArenaPrimary
                    )
                }

                // Away Team
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(match.awayTeam.flagEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = match.awayTeam.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stadium description ticker
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = ArenaTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = match.venueName,
                    fontSize = 11.sp,
                    color = ArenaTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchRowCard(match: Match, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = match.stage,
                    fontSize = 10.sp,
                    color = ArenaTextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(match.homeTeam.flagEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${match.homeTeam.name} vs ${match.awayTeam.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(match.awayTeam.flagEmoji, fontSize = 20.sp)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ScoreBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                if (match.status == MatchStatus.FINISHED) {
                    Text(
                        text = "${match.homeScore} - ${match.awayScore} (FT)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = ArenaPrimary
                    )
                } else {
                    Text(
                        text = match.dateTime.substringBefore("local").trim(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun CustomizationQuickBanner(viewModel: FifaViewModel) {
    var editDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SettingsSuggest,
                        contentDescription = null,
                        tint = ArenaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "YOUR MATCHDAY PROFILE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                TextButton(onClick = { editDialog = true }) {
                    Text("Customize", color = ArenaPrimary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("FAVORITE SQUAD", fontSize = 10.sp, color = ArenaTextSecondary)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = if (viewModel.favoriteTeam == "USA") "🇺🇸 United States"
                            else if (viewModel.favoriteTeam == "MEX") "🇲🇽 Mexico"
                            else "🇨🇦 Canada",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("ACTIVE HOST CITY", fontSize = 10.sp, color = ArenaTextSecondary)
                    Text(
                        text = viewModel.selectedLocationCity,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }

    if (editDialog) {
        AlertDialog(
            onDismissRequest = { editDialog = false },
            title = { Text("Personalise World Cup Hub", color = Color.White, fontWeight = FontWeight.Bold) },
            containerColor = ArenaCardBg,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select your favourite standard team:", fontSize = 12.sp, color = ArenaTextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("USA", "MEX", "CAN").forEach { code ->
                            val isSelected = viewModel.favoriteTeam == code
                            Button(
                                onClick = { viewModel.favoriteTeam = code },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) ArenaPrimary else ArenaBorder
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (code == "USA") "🇺🇸 USA" else if (code == "MEX") "🇲🇽 MEX" else "🇨🇦 CAN",
                                    color = if (isSelected) ArenaDarkBg else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text("Select active host city:", fontSize = 12.sp, color = ArenaTextSecondary)
                    val cities = listOf("New York/New Jersey", "Mexico City", "Vancouver")
                    cities.forEach { city ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectedLocationCity = city }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = viewModel.selectedLocationCity == city,
                                onClick = { viewModel.selectedLocationCity = city },
                                colors = RadioButtonDefaults.colors(selectedColor = ArenaPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(city, color = Color.White, fontSize = 13.dp.value.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { editDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ArenaPrimary)
                ) {
                    Text("Done", color = ArenaDarkBg)
                }
            }
        )
    }
}

@Composable
fun NewsRowItem(article: NewsArticle) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .width(260.dp)
            .height(200.dp)
    ) {
        Column {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = article.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaPrimary
                    )
                    Text(
                        text = article.date,
                        fontSize = 9.sp,
                        color = ArenaTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = article.snippet,
                    fontSize = 10.sp,
                    color = ArenaTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FanEventHomeItem(event: FanEvent, onRegister: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArenaCardBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "${event.category.uppercase()} EVENT • ${event.city}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = ArenaSecondary
                    )
                    Text(
                        text = event.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Button(
                    onClick = onRegister,
                    enabled = !event.isRegistered,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArenaSecondary,
                        disabledContainerColor = ArenaBorder
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (event.isRegistered) "Registered" else "Book Free",
                        color = if (event.isRegistered) ArenaTextSecondary else ArenaDarkBg,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = ArenaTextSecondary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.dateTime, fontSize = 10.sp, color = ArenaTextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = ArenaTextSecondary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cap. ${event.capacity}", fontSize = 10.sp, color = ArenaTextSecondary)
                }
            }
        }
    }
}

@Composable
fun MerchandiseTeaserCard(onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ScoreBg),
        border = BorderStroke(1.dp, ArenaBorder),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "OFFICIAL STORE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArenaPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Personalise Your Team Jersey Now",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Type custom name, select matchday badge, checkout for rapid stadium collect.",
                    fontSize = 11.sp,
                    color = ArenaTextSecondary
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = ArenaPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
