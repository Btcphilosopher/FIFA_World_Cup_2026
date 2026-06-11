package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class AiState {
    object Idle : AiState()
    object Loading : AiState()
    data class Success(val response: String) : AiState()
    data class Error(val error: String) : AiState()
}

class FifaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = FifaRepository(db.fifaDao())
    private val geminiService = GeminiService()

    // Screen navigation
    var currentNavRoute by mutableStateOf("home")
    var selectedMatchId by mutableStateOf<String?>("m_usa_eng") // default selected match for Match Centre

    // Interactive custom state (to keep things dynamic)
    var favoriteTeam by mutableStateOf("USA")
    var selectedLocationCity by mutableStateOf("New York/New Jersey")
    
    // Live Match Center - Periodic Simulator
    private val _matches = MutableStateFlow<List<Match>>(Fixtures.UpcomingMatches)
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    // Visual lists
    val stadiums = Fixtures.Stadiums
    val news = Fixtures.News
    val merchandise = Fixtures.Merchandise
    
    private val _fanEvents = MutableStateFlow<List<FanEvent>>(Fixtures.Events)
    val fanEvents: StateFlow<List<FanEvent>> = _fanEvents.asStateFlow()

    // Room Persistent States
    val myTickets: StateFlow<List<TicketEntity>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myVisitedStadiums: StateFlow<List<VisitedStadiumEntity>> = repository.visitedStadiums
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myBadges: StateFlow<List<EarnedBadgeEntity>> = repository.earnedBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiChatHistory: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Assistant State
    var aiTextQuery by mutableStateOf("")
    var aiState by mutableStateOf<AiState>(AiState.Idle)

    // Merchandise customizer
    var customJerseyName by mutableStateOf("PULISIC")
    var customJerseyNumber by mutableStateOf("10")
    var isMerchOrdered by mutableStateOf(false)

    // Travel finder query
    var travelFromCity by mutableStateOf("Times Square, NYC")
    var travelToStadium by mutableStateOf("MetLife Stadium")
    var matchedRoutes by mutableStateOf<List<RouteOption>>(emptyList())

    // Notification toast alert
    var activeNotificationAlert by mutableStateOf<String?>(null)

    init {
        // Seed initial tickets and badge if empty when first launched!
        viewModelScope.launch {
            repository.allTickets.first().let { currentTickets ->
                if (currentTickets.isEmpty()) {
                    // Seed standard opening ticket for user
                    repository.insertTicket(
                        TicketEntity(
                            id = "TKT-OPENING",
                            matchId = "m_usa_eng",
                            homeCode = "USA",
                            awayCode = "ENG",
                            homeName = "United States",
                            awayName = "England",
                            homeEmoji = "🇺🇸",
                            awayEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
                            dateString = "June 11, 2026 - 20:00 Local",
                            venue = "MetLife Stadium, East Rutherford",
                            section = "Sec 124",
                            row = "Row 15",
                            seat = "Seat 4",
                            ownerName = "Tommy Ah",
                            qrCodePayload = "FIFA2026|TKT-OPENING|m_usa_eng|124|15|4"
                        )
                    )
                    // Earn introductory badge!
                    repository.earnBadge(
                        badgeId = "FIRST_TICKET",
                        title = "PASSIONATE SUPPORTER",
                        description = "Acclaimed your inaugural tickets for World Cup 2026 digital wallet!",
                        category = "Match"
                    )
                }
            }
            
            // Starts the periodic Live Match Centre Simulator
            startLiveScoreSimulator()
        }
        
        // Match default travel options immediately
        findRoutes()
    }

    private fun startLiveScoreSimulator() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(8000) // update scores/comments every 8 seconds
                val currentList = _matches.value
                val updated = currentList.map { match ->
                    if (match.status == MatchStatus.LIVE) {
                        val newMin = if (match.minute < 90) match.minute + 1 else 90
                        val oldHome = match.homeScore
                        val oldAway = match.awayScore
                        var newHome = match.homeScore
                        var newAway = match.awayScore
                        
                        // Small chance of scoring
                        val homeScored = (1..100).random() > 95
                        val awayScored = (1..100).random() > 95
                        
                        val newComm = match.commentary.toMutableList()
                        
                        if (homeScored && newMin < 90) {
                            newHome += 1
                            val scorers = listOf("Christian Pulisic", "Tim Weah", "Weston McKennie")
                            val scorer = scorers.random()
                            newComm.add(0, CommentaryItem(
                                minute = newMin,
                                eventType = "GOAL",
                                text = "GOAL USA!!! $scorer converts a lightning volley inside the penalty box! Absolute pandemonium in MetLife!"
                            ))
                            pushAlert("ALERT: GOAL FOR USA! ${match.homeTeam.flagEmoji} USA $newHome - $newAway ENG ${match.awayTeam.flagEmoji} (${newMin}')")
                        } else if (awayScored && newMin < 90) {
                            newAway += 1
                            val scorers = listOf("Jude Bellingham", "Harry Kane", "Phil Foden")
                            val scorer = scorers.random()
                            newComm.add(0, CommentaryItem(
                                minute = newMin,
                                eventType = "GOAL",
                                text = "GOAL ENGLAND!!! $scorer leaps high above the defense to nod in an exceptional corner! Stunning response."
                            ))
                            pushAlert("ALERT: GOAL FOR ENGLAND! ${match.homeTeam.flagEmoji} USA $newHome - $newAway ENG ${match.awayTeam.flagEmoji} (${newMin}')")
                        } else if (newMin == 90) {
                            pushAlert("MATCH ALERT: Full-time in New Jersey. USA and England split points in a breathtaking 1st game.")
                            newComm.add(0, CommentaryItem(
                                minute = 90,
                                eventType = "TEXT",
                                text = "= FULL TIME = The referee blows the final whistle! Match ends USA $newHome - $newAway ENG. Brilliant spectacle."
                            ))
                        }
                        
                        // Live stats fluctuate
                        match.copy(
                            minute = newMin,
                            homeScore = newHome,
                            awayScore = newAway,
                            possessionHome = if (newMin % 2 == 0) (45..55).random() else match.possessionHome,
                            shotsHome = match.shotsHome + if ((1..2).random() > 1) 1 else 0,
                            shotsAway = match.shotsAway + if ((1..3).random() > 2) 1 else 0,
                            passesHome = match.passesHome + (4..12).random(),
                            passesAway = match.passesAway + (3..10).random(),
                            expectedGoalsHome = match.expectedGoalsHome + if (homeScored) 0.72 else 0.05,
                            expectedGoalsAway = match.expectedGoalsAway + if (awayScored) 0.61 else 0.03,
                            commentary = newComm,
                            status = if (newMin >= 90) MatchStatus.FINISHED else MatchStatus.LIVE
                        )
                    } else {
                        match
                    }
                }
                _matches.value = updated
            }
        }
    }

    private fun pushAlert(text: String) {
        viewModelScope.launch {
            activeNotificationAlert = text
            delay(5000)
            if (activeNotificationAlert == text) {
                activeNotificationAlert = null
            }
        }
    }

    fun dismissAlert() {
        activeNotificationAlert = null
    }

    // Ticketing actions
    fun purchaseTicket(match: Match, section: String, row: String, seat: String, name: String) {
        viewModelScope.launch {
            repository.buyTicket(match, section, row, seat, name)
            pushAlert("TICKET ORDERED! Added direct match entrance pass to your World Cup Wallet!")
        }
    }

    fun transferTicket(ticketId: String, email: String) {
        viewModelScope.launch {
            // Retrieve actual list
            val tickets = myTickets.value
            val target = tickets.find { it.id == ticketId }
            if (target != null) {
                val updated = target.copy(isTransferred = true, transferEmail = email)
                repository.updateTicket(updated)
                pushAlert("TICKET TRANSFERRED! Entry QR code shared securely with $email.")
                repository.earnBadge("TRANS_TKT_$ticketId", "SHARING MOMENTS", "Transferred a ticket to share football magic with family!", "Match")
            }
        }
    }

    fun deleteTicket(ticketId: String) {
        viewModelScope.launch {
            repository.deleteTicket(ticketId)
            pushAlert("Ticket removed from Wallet.")
        }
    }

    // Passport actions
    fun visitStadium(stadiumId: String, name: String) {
        viewModelScope.launch {
            val currentVisits = myVisitedStadiums.value
            if (currentVisits.none { it.stadiumId == stadiumId }) {
                repository.claimStadiumVisit(stadiumId, name)
                pushAlert("CHECK-IN SUCCESS! Stamp added to your Fan Passport. Earned '${name}' Aura Badge!")
            } else {
                pushAlert("You have already checked in at $name in your Fan Passport!")
            }
        }
    }

    // AI Assistant chat actions
    fun askAssistant() {
        val query = aiTextQuery.trim()
        if (query.isEmpty()) return

        aiTextQuery = ""
        aiState = AiState.Loading

        viewModelScope.launch {
            // 1. Store USER prompt in Room
            repository.addChatMessage("USER", query)

            // 2. Fetch active chat logs from repository
            val history = repository.chatMessages.first()

            // 3. Make Gemini REST call
            val response = geminiService.askAssistant(query, history)

            // 4. Store ASSISTANT feedback in Room
            repository.addChatMessage("ASSISTANT", response)
            
            // 5. Update state
            aiState = AiState.Success(response)
            
            // 6. Push helper alert & earn helper badge!
            repository.earnBadge(
                badgeId = "BOT_ASSIST_" + UUID.randomUUID().toString().take(4),
                title = "ORACLE EXPLORER",
                description = "Engaged with the FIFA World Cup AI Fan assistant to navigate stadium layouts or host city routes.",
                category = "Trivia"
            )
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
            aiState = AiState.Idle
        }
    }

    // Fan festival registration
    fun registerForEvent(event: FanEvent) {
        viewModelScope.launch {
            val updatedList = _fanEvents.value.map { e ->
                if (e.id == event.id) {
                    e.copy(isRegistered = true)
                } else {
                    e
                }
            }
            _fanEvents.value = updatedList
            repository.earnBadge(
                badgeId = "EVENT_" + event.id,
                title = "FESTIVAL ENTHUSIAST",
                description = "Secured your reserved spot for the watch parties or concerts in ${event.city}!",
                category = "Merch"
            )
            pushAlert("REGISTRATION GRANTED! Digital entry voucher saved to your passport profile.")
        }
    }

    // Custom personalized jersey order
    fun orderCustomJersey(itemName: String, name: String, number: String) {
        viewModelScope.launch {
            isMerchOrdered = true
            pushAlert("JERSEY DESIGNED! Pre-order registered. Size XL with printed '$name - $number' will be ready for express in-stadium pickup!")
            repository.earnBadge(
                badgeId = "SHOP_CHAMP",
                title = "SQUAD SOLIDARITY",
                description = "Customized and purchased your stylized official Jersey ($name - $number) for live matches!",
                category = "Merch"
            )
            delay(4000)
            isMerchOrdered = false
        }
    }

    // Route finding matching
    fun findRoutes() {
        viewModelScope.launch {
            // Fake matcher logic based on inputs
            val results = mutableListOf<RouteOption>()
            if (travelToStadium.contains("MetLife", ignoreCase = true)) {
                results.add(
                    RouteOption("Fastest", listOf(
                        RouteStage("Walk", 3, "Depart towards NYC Port Authority Terminal", "directions_walk"),
                        RouteStage("Bus", 18, "Express Shuttle Matchday Bus Coach 351", "directions_bus"),
                        RouteStage("Walk", 2, "Arrive MetLife Gate A Lot", "directions_walk")
                    ))
                )
                results.add(
                    RouteOption("Cheapest", listOf(
                        RouteStage("Walk", 6, "Walk to Penn Station Subway Hub", "directions_walk"),
                        RouteStage("Metro", 10, "NJ Transit Meadowlands Rail Link (Sector 4)", "train"),
                        RouteStage("Walk", 4, "Walk to East Entrance MetLife Hub", "directions_walk")
                    ))
                )
            } else if (travelToStadium.contains("Azteca", ignoreCase = true)) {
                results.add(
                    RouteOption("Fastest", listOf(
                        RouteStage("Rideshare", 20, "Didi Express Matchday Safe Lane", "directions_car"),
                        RouteStage("Walk", 5, "Walk from Insurgentes Gate 1 Dropoff", "directions_walk")
                    ))
                )
                results.add(
                    RouteOption("Metro", listOf(
                        RouteStage("Walk", 4, "Walk to Metro Tasqueña Junction", "directions_walk"),
                        RouteStage("Metro", 12, "Tren Ligero Direct Line to Estadio Azteca", "train"),
                        RouteStage("Walk", 3, "Enter through north turnstile plaza", "directions_walk")
                    ))
                )
            } else {
                results.add(
                    RouteOption("Fastest", listOf(
                        RouteStage("Walk", 4, "Walk to Stadium-Chinatown Station", "directions_walk"),
                        RouteStage("Metro", 6, "Expo Line SkyTrain direct transit", "train"),
                        RouteStage("Walk", 2, "Arrive BC Place Plaza Robson Gate", "directions_walk")
                    ))
                )
            }
            matchedRoutes = results
        }
    }
}
