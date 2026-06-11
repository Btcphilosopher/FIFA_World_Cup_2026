package com.example.data

import java.util.UUID

// Matches & Teams
enum class MatchStatus {
    UPCOMING, LIVE, FINISHED
}

data class Team(
    val name: String,
    val code: String,
    val flagEmoji: String,
    val group: String
)

data class Match(
    val id: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int,
    val awayScore: Int,
    val status: MatchStatus,
    val minute: Int = 0,
    val stage: String,
    val dateTime: String, // e.g. "17:00 local"
    val venueName: String,
    val expectedGoalsHome: Double = 0.0,
    val expectedGoalsAway: Double = 0.0,
    val possessionHome: Int = 50,
    val shotsHome: Int = 0,
    val shotsAway: Int = 0,
    val passesHome: Int = 0,
    val passesAway: Int = 0,
    val foulsHome: Int = 0,
    val foulsAway: Int = 0,
    val yellowHome: Int = 0,
    val yellowAway: Int = 0,
    val redHome: Int = 0,
    val redAway: Int = 0,
    val lineupsHome: List<PlayerPosition> = emptyList(),
    val lineupsAway: List<PlayerPosition> = emptyList(),
    val commentary: List<CommentaryItem> = emptyList(),
    val highlightsUrl: String = "https://example.com/highlights.mp4"
)

data class PlayerPosition(
    val name: String,
    val number: Int,
    val position: String, // GK, DEF, MID, FWD
    val rating: Double,
    val x: Float, // 0.0 to 1.0 on a direct coordinate map
    val y: Float
)

data class CommentaryItem(
    val id: String = UUID.randomUUID().toString(),
    val minute: Int,
    val eventType: String, // GOAL, CARD, SUBSTITUTION, TEXT
    val text: String
)

// News & Articles
data class NewsArticle(
    val id: String,
    val title: String,
    val category: String, // e.g., "TEAM NEWS", "HOST CITIES", "TICKETING"
    val snippet: String,
    val imageUrl: String,
    val date: String
)

// Merchandise
data class MerchandiseItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String, // "Jerseys", "Accessories", "Balls", "Collectibles"
    val imageUrl: String,
    val description: String,
    val isCustomizable: Boolean = false
)

// Stadium info
data class Stadium(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val capacity: String,
    val openingMatch: Boolean = false,
    val elevationInfo: String = "Sea Level",
    val description: String,
    val gates: List<String> = emptyList(),
    val concessions: List<ConcessionItem> = emptyList(),
    val transportTransit: List<TransportOption> = emptyList(),
    val coordinates: String // e.g. "40.8135° N, 74.0743° W"
)

data class ConcessionItem(
    val id: String,
    val name: String,
    val category: String, // "Food", "Beverage", "Merchandise"
    val price: Double,
    val rating: Double,
    val waitTimeMinutes: Int,
    val description: String
)

data class TransportOption(
    val type: String, // "Metro", "Bus", "Rideshare", "Shuttle"
    val lineName: String,
    val etaMinutes: Int,
    val cost: String,
    val details: String
)

// Travel Companion Planner
data class RouteOption(
    val type: String, // "Fastest", "Cheapest", "Eco"
    val stages: List<RouteStage>
)

data class RouteStage(
    val mode: String, // "Walk", "Metro", "Rideshare", "Bus"
    val durMinutes: Int,
    val details: String,
    val iconName: String
)

// Fan Festival / Events
data class FanEvent(
    val id: String,
    val name: String,
    val city: String,
    val location: String,
    val category: String, // "Watch Party", "Concert", "Cultural", "Food"
    val headliner: String = "",
    val dateTime: String,
    val capacity: String,
    val description: String,
    val rating: Double,
    val isRegistered: Boolean = false
)

// Tournament Standings
data class GroupStandings(
    val groupLetter: String,
    val teams: List<StandingRow>
)

data class StandingRow(
    val team: Team,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val gd: Int,
    val points: Int
)

// Static Data Fixtures Provider
object Fixtures {
    val Teams = mapOf(
        "USA" to Team("United States", "USA", "🇺🇸", "Group A"),
        "MEX" to Team("Mexico", "MEX", "🇲🇽", "Group A"),
        "CAN" to Team("Canada", "CAN", "🇨🇦", "Group B"),
        "ARG" to Team("Argentina", "ARG", "🇦🇷", "Group B"),
        "BRA" to Team("Brazil", "BRA", "🇧🇷", "Group C"),
        "FRA" to Team("France", "FRA", "🇫🇷", "Group C"),
        "ENG" to Team("England", "ENG", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "Group D"),
        "ESP" to Team("Spain", "ESP", "🇪🇸", "Group D"),
        "GER" to Team("Germany", "GER", "🇩🇪", "Group E"),
        "ITA" to Team("Italy", "ITA", "🇮🇹", "Group E"),
        "POR" to Team("Portugal", "POR", "🇵🇹", "Group F"),
        "NED" to Team("Netherlands", "NED", "🇳🇱", "Group F"),
        "JPN" to Team("Japan", "JPN", "🇯🇵", "Group G"),
        "SEN" to Team("Senegal", "SEN", "🇸🇳", "Group G")
    )

    val Stadiums = listOf(
        Stadium(
            id = "metlife",
            name = "MetLife Stadium",
            city = "East Rutherford, NJ",
            country = "USA",
            capacity = "82,500",
            description = "Selected to host the grand FIFA World Cup 2026 Final! A state-of-the-art super-venue located just outside New York City.",
            gates = listOf("Verizon Gate A", "Pepsi Gate B", "MetLife Gate C", "Honduras Gate D"),
            coordinates = "40.8135° N, 74.0743° W",
            concessions = listOf(
                ConcessionItem("c1", "Empire State Burgers", "Food", 12.50, 4.8, 10, "Double beef patties, melted cheddar, signature sauce"),
                ConcessionItem("c2", "Liberty Brew House", "Beverage", 9.00, 4.5, 5, "Local craft IPAs, premium lager, sodas"),
                ConcessionItem("c3", "MetLife Merchandise Depot", "Merchandise", 0.00, 4.9, 15, "Official jerseys, final souvenirs, scarves")
            ),
            transportTransit = listOf(
                TransportOption("Metro", "Meadowlands Rail Line", 15, "$4.25", "Frequent service directly to NJ Penn Station"),
                TransportOption("Rideshare", "Uber Lot G Hub", 8, "Varies", "Direct pickup and drop-off zone"),
                TransportOption("Shuttle", "Fan Festival Express Bus", 20, "Free", "Every 5 mins from NYC Times Square Hub")
            )
        ),
        Stadium(
            id = "azteca",
            name = "Estadio Azteca",
            city = "Mexico City",
            country = "Mexico",
            capacity = "87,523",
            openingMatch = true,
            elevationInfo = "2,240m Above Sea Level",
            description = "Historic temple of world football hosting the opening match! The first stadium in history to host matches in three FIFA World Cups.",
            gates = listOf("Puerta 1 - Insurgentes", "Puerta 2 - Tlalpan", "Acceso General Norte", "Palcos Preferente"),
            coordinates = "19.3029° N, 99.1505° W",
            concessions = listOf(
                ConcessionItem("c4", "Azteca Taco Spot", "Food", 6.50, 4.9, 8, "Pastor, carne asada, handmade salsas"),
                ConcessionItem("c5", "Techolote Cantina", "Beverage", 5.00, 4.6, 4, "Fresh horchata, local mexican beers, lime margaritas"),
                ConcessionItem("c6", "Tienda Oficial Estadio Azteca", "Merchandise", 0.00, 4.7, 12, "Mexico 2026 flags, opening ceremony caps")
            ),
            transportTransit = listOf(
                TransportOption("Metro", "Tren Ligero (Estación Estadio Azteca)", 12, "$3.00 MXN", "Connects to Metro Linea 2 MetroTasqueña"),
                TransportOption("Bus", "Metrobús Linea 1", 22, "$6.00 MXN", "Direct shuttle along Insurgentes Sur"),
                TransportOption("Rideshare", "Didi/Uber Safe Drop Zone", 10, "Varies", "Designated security lot near Gate 3")
            )
        ),
        Stadium(
            id = "bcplace",
            name = "BC Place",
            city = "Vancouver, BC",
            country = "Canada",
            capacity = "54,500",
            description = "Vancouver's world-famous retractable roof stadium nestled beautifully on the skyline of False Creek.",
            gates = listOf("Gate A (Robson)", "Gate C (Pacific)", "Gate E", "Suite VIP Entrance"),
            coordinates = "49.2767° N, 123.1120° W",
            concessions = listOf(
                ConcessionItem("c7", "Maple Bacon Grill", "Food", 14.00, 4.7, 12, "Fresh poutine with smoked bacon, local wild salmon burger"),
                ConcessionItem("c8", "Pacific Coastal Lounge", "Beverage", 10.50, 4.4, 6, "Pacific northwest craft lagers, cider, premium coffee"),
                ConcessionItem("c9", "BC Place Fan Wear Center", "Merchandise", 0.00, 4.8, 8, "Maple leaf shirts, pins, official match balls")
            ),
            transportTransit = listOf(
                TransportOption("Metro", "SkyTrain - Stadium-Chinatown Station", 5, "$3.15 CAD", "Expo Line connects to downtown Vancouver and suburbs"),
                TransportOption("Ferry", "False Creek Aquabus Ferry", 15, "$5.00 CAD", "Scenic water taxi boarding at Plaza of Nations Dock"),
                TransportOption("Rideshare", "Rideshare Zone (Expo Blvd)", 12, "Varies", "Dedicated lane along Pacific Boulevard")
            )
        )
    )

    val News = listOf(
        NewsArticle(
            "n1",
            "History Made! Mexico, USA, and Canada kick off World Cup 2026 Today",
            "TOURNAMENT KICKOFF",
            "The historic Opening Match kicks off at Estadio Azteca on June 11, 2026, launching a unified celebration of global football across North America.",
            "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=400&q=80",
            "Today"
        ),
        NewsArticle(
            "n2",
            "Official 2026 Merch drops in stadiums as fans stream into New York and LA",
            "OFFICIAL MERCHANDISE",
            "Custom customizable home shirts and local community scarves are flying off the shelves as the initial matches commence.",
            "https://images.unsplash.com/photo-1579758629938-03607ccdbaba?auto=format&fit=crop&w=400&q=80",
            "2 Hours Ago"
        ),
        NewsArticle(
            "n3",
            "Travel safety and shuttle guidelines for all 16 Host Cities",
            "TRAVEL UPDATE",
            "FIFA and city transit departments release coordinated route maps. Free public transport is available dynamically on game days with a valid mobile ticket.",
            "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=400&q=80",
            "1 Day Ago"
        )
    )

    val Merchandise = listOf(
        MerchandiseItem("m1", "Official Home Jersey 2026", 90.00, "Jerseys", "https://images.unsplash.com/photo-1517747614396-d21a78b850e8?auto=format&fit=crop&w=400&q=80", "High performance fabric featuring official tournament crest. Fully custom printing with direct local in-stadium pickup.", true),
        MerchandiseItem("m2", "Unified Host Nations Scarf", 30.00, "Accessories", "https://images.unsplash.com/photo-1540747737956-37872404a8e1?auto=format&fit=crop&w=400&q=80", "Double-knit high quality scarf featuring United States, Mexico, and Canada crests. Keep warm while showing solidarity.", false),
        MerchandiseItem("m3", "Adidas Al Rihla World Cup 2026 Match Ball", 140.00, "Balls", "https://images.unsplash.com/photo-1511886929837-354d827aae26?auto=format&fit=crop&w=400&q=80", "Innovative speedball technology for high-accuracy aerodynamics. The exact spec used on pitches across North America.", false),
        MerchandiseItem("m4", "Final 2026 MetLife Commemorative Gold Coin", 45.00, "Collectibles", "https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?auto=format&fit=crop&w=400&q=80", "Limited numbered edition plated in 24k gold. Displays MetLife Stadium silhouette alongside final coordinate stamp.", false)
    )

    val Events = listOf(
        FanEvent(
            "e1", "Dallas FIFA Fan Festival Opening Ceremony", "Dallas, TX", "Fair Park Festival Grounds", "Concert",
            "Bizarrap, Becky G", "Today - Gates Open 14:00", "50,000", "The ultimate watch grounds for World Cup fans. Huge 200-meter premium outdoor LED screens, local culinary vendors, and live entertainment.", 4.9
        ),
        FanEvent(
            "e2", "Zócalo Mega Watch Stadium Event", "Mexico City", "Zócalo Plaza", "Watch Party",
            "Mariachi Star Ensemble", "Today - Kickoff Zone 15:00", "120,000", "Join over a hundred thousand fans in the historic center. Experience matching sports broadcast excitement with live culinary acts and interactive exhibits.", 5.0
        ),
        FanEvent(
            "e3", "Vancouver False Creek Food & Football Festival", "Vancouver, BC", "False Creek Plaza", "Food",
            "Vancouver Symphony Big Band", "Starts Today 11:00", "20,000", "Exquisite Pacific Northwest seafood meet culinary delights from all qualifying nations. Includes local craft lager gardens and dynamic visual games.", 4.7
        )
    )

    val Standings = listOf(
        GroupStandings(
            "Group A",
            listOf(
                StandingRow(Teams["MEX"]!!, 1, 1, 0, 0, 2, 0, 2, 3),
                StandingRow(Teams["USA"]!!, 0, 0, 0, 0, 0, 0, 0, 0),
                StandingRow(Teams["ENG"]!!, 0, 0, 0, 0, 0, 0, 0, 0),
                StandingRow(Teams["SEN"]!!, 1, 0, 0, 1, 0, 2, -2, 0)
            )
        ),
        GroupStandings(
            "Group B",
            listOf(
                StandingRow(Teams["ARG"]!!, 0, 0, 0, 0, 0, 0, 0, 0),
                StandingRow(Teams["CAN"]!!, 0, 0, 0, 0, 0, 0, 0, 0),
                StandingRow(Teams["FRA"]!!, 0, 0, 0, 0, 0, 0, 0, 0),
                StandingRow(Teams["JPN"]!!, 0, 0, 0, 0, 0, 0, 0, 0)
            )
        )
    )

    val UpcomingMatches = listOf(
        Match(
            id = "m_mex_sen",
            homeTeam = Teams["MEX"]!!,
            awayTeam = Teams["SEN"]!!,
            homeScore = 2,
            awayScore = 0,
            status = MatchStatus.FINISHED,
            minute = 90,
            stage = "Group Stage - Group A",
            dateTime = "June 11, 2026 - 15:00 Local",
            venueName = "Estadio Azteca, Mexico City",
            expectedGoalsHome = 1.94,
            expectedGoalsAway = 0.45,
            possessionHome = 62,
            shotsHome = 14,
            shotsAway = 5,
            passesHome = 542,
            passesAway = 310,
            foulsHome = 11,
            foulsAway = 8,
            yellowHome = 1,
            yellowAway = 2,
            commentary = listOf(
                CommentaryItem(minute = 90, eventType = "TEXT", text = "FULL TIME at Estadio Azteca! Mexico takes the opening three points with a convincing 2-0 win!"),
                CommentaryItem(minute = 78, eventType = "GOAL", text = "GOAL MEXICO! Santiago Giménez sweeps a neat pass into the bottom corner. Double lead! Estadio Azteca is bouncing!"),
                CommentaryItem(minute = 54, eventType = "CARD", text = "Yellow Card for Senegal's Koulibaly after a mistimed sliding challenge."),
                CommentaryItem(minute = 45, eventType = "TEXT", text = "Halftime at the Azteca. Mexico dominates possession (64%) and looks sharp leading 1-0."),
                CommentaryItem(minute = 18, eventType = "GOAL", text = "GOAL MEXICO! Chucky Lozano fires an absolute thunderbolt from outside the box on the opening match! Stadium goes wild.")
            ),
            lineupsHome = listOf(
                PlayerPosition("Ochoa", 13, "GK", 7.2, 0.5f, 0.08f),
                PlayerPosition("Montes", 3, "DEF", 7.4, 0.35f, 0.25f),
                PlayerPosition("Vasquez", 5, "DEF", 7.1, 0.65f, 0.25f),
                PlayerPosition("Sanchez", 19, "DEF", 7.6, 0.15f, 0.32f),
                PlayerPosition("Gallardo", 23, "DEF", 7.2, 0.85f, 0.32f),
                PlayerPosition("Alvarez", 4, "MID", 8.1, 0.5f, 0.45f),
                PlayerPosition("Chávez", 18, "MID", 7.8, 0.32f, 0.58f),
                PlayerPosition("Sánchez", 14, "MID", 7.4, 0.68f, 0.58f),
                PlayerPosition("Lozano", 22, "FWD", 8.4, 0.18f, 0.82f),
                PlayerPosition("Antuna", 15, "FWD", 7.3, 0.82f, 0.82f),
                PlayerPosition("Giménez", 11, "FWD", 8.6, 0.5f, 0.88f)
            ),
            lineupsAway = listOf(
                PlayerPosition("Mendy", 16, "GK", 6.8, 0.5f, 0.92f),
                PlayerPosition("Koulibaly", 3, "DEF", 6.2, 0.35f, 0.75f),
                PlayerPosition("Niakhaté", 19, "DEF", 6.5, 0.65f, 0.75f),
                PlayerPosition("Sabaly", 2, "DEF", 6.7, 0.15f, 0.68f),
                PlayerPosition("Jakobs", 14, "DEF", 6.4, 0.85f, 0.68f),
                PlayerPosition("Gueye", 5, "MID", 6.9, 0.5f, 0.55f),
                PlayerPosition("N. Mendy", 6, "MID", 6.6, 0.3f, 0.42f),
                PlayerPosition("Ciss", 11, "MID", 6.8, 0.7f, 0.42f),
                PlayerPosition("Sarr", 18, "FWD", 7.0, 0.2f, 0.18f),
                PlayerPosition("Ndiaye", 13, "FWD", 6.3, 0.8f, 0.18f),
                PlayerPosition("Diallo", 20, "FWD", 6.5, 0.5f, 0.12f)
            )
        ),
        Match(
            id = "m_usa_eng",
            homeTeam = Teams["USA"]!!,
            awayTeam = Teams["ENG"]!!,
            homeScore = 1,
            awayScore = 1,
            status = MatchStatus.LIVE,
            minute = 42,
            stage = "Group Stage - Group A",
            dateTime = "June 11, 2026 - 20:00 Local",
            venueName = "MetLife Stadium, East Rutherford",
            expectedGoalsHome = 0.82,
            expectedGoalsAway = 1.15,
            possessionHome = 47,
            shotsHome = 5,
            shotsAway = 6,
            passesHome = 210,
            passesAway = 245,
            foulsHome = 5,
            foulsAway = 4,
            yellowHome = 1,
            yellowAway = 0,
            commentary = listOf(
                CommentaryItem(minute = 39, eventType = "GOAL", text = "GOAL USA! Christian Pulisic connects beautifully on a flying header from Weah's cross! The entire MetLife stadium erupts! Game tied!"),
                CommentaryItem(minute = 24, eventType = "TEXT", text = "USA close! Weston McKennie launches a long throw-in, Adams hits it on the volley, inches over the crossbar!"),
                CommentaryItem(minute = 12, eventType = "GOAL", text = "GOAL ENGLAND! Harry Kane slides it home on an assist from Jude Bellingham. High precision finish. England strikes early! 1-0."),
                CommentaryItem(minute = 6, eventType = "TEXT", text = "First save! Matt Turner makes an intuitive leap to block a powerful Bukayo Saka drive.")
            ),
            lineupsHome = listOf(
                PlayerPosition("Turner", 1, "GK", 7.4, 0.5f, 0.08f),
                PlayerPosition("Ream", 13, "DEF", 6.9, 0.35f, 0.25f),
                PlayerPosition("Richards", 4, "DEF", 7.0, 0.65f, 0.25f),
                PlayerPosition("Scally", 22, "DEF", 6.8, 0.15f, 0.32f),
                PlayerPosition("Robinson", 5, "DEF", 7.2, 0.85f, 0.32f),
                PlayerPosition("Adams", 4, "MID", 7.5, 0.5f, 0.45f),
                PlayerPosition("McKennie", 8, "MID", 7.9, 0.32f, 0.58f),
                PlayerPosition("Musah", 6, "MID", 7.4, 0.68f, 0.58f),
                PlayerPosition("Weah", 21, "FWD", 8.1, 0.18f, 0.82f),
                PlayerPosition("Pulisic", 10, "FWD", 8.8, 0.82f, 0.82f),
                PlayerPosition("Balogun", 20, "FWD", 7.1, 0.5f, 0.88f)
            ),
            lineupsAway = listOf(
                PlayerPosition("Pickford", 1, "GK", 7.1, 0.5f, 0.92f),
                PlayerPosition("Stones", 5, "DEF", 7.2, 0.35f, 0.75f),
                PlayerPosition("Guehi", 6, "DEF", 7.0, 0.65f, 0.75f),
                PlayerPosition("Walker", 2, "DEF", 7.5, 0.15f, 0.68f),
                PlayerPosition("Shaw", 3, "DEF", 7.1, 0.85f, 0.68f),
                PlayerPosition("Rice", 4, "MID", 7.8, 0.5f, 0.55f),
                PlayerPosition("Bellingham", 10, "MID", 8.5, 0.3f, 0.42f),
                PlayerPosition("Mainoo", 8, "MID", 7.3, 0.7f, 0.42f),
                PlayerPosition("Saka", 7, "FWD", 8.0, 0.2f, 0.18f),
                PlayerPosition("Foden", 11, "FWD", 7.6, 0.8f, 0.18f),
                PlayerPosition("Kane", 9, "FWD", 8.4, 0.5f, 0.12f)
            )
        ),
        Match(
            id = "m_can_arg",
            homeTeam = Teams["CAN"]!!,
            awayTeam = Teams["ARG"]!!,
            homeScore = 0,
            awayScore = 0,
            status = MatchStatus.UPCOMING,
            stage = "Group Stage - Group B",
            dateTime = "June 12, 2026 - 19:00 Local",
            venueName = "BC Place Stadium, Vancouver",
            commentary = listOf(
                CommentaryItem(minute = 0, eventType = "TEXT", text = "Kickoff scheduled for tomorrow. Canada is preparing to face Messi's Argentina in a blockbuster Group B clash!")
            ),
            lineupsHome = emptyList(),
            lineupsAway = emptyList()
        )
    )
}
