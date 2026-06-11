package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class FifaRepository(private val fifaDao: FifaDao) {

    val allTickets: Flow<List<TicketEntity>> = fifaDao.getAllTickets()
    val visitedStadiums: Flow<List<VisitedStadiumEntity>> = fifaDao.getVisitedStadiums()
    val earnedBadges: Flow<List<EarnedBadgeEntity>> = fifaDao.getEarnedBadges()
    val chatMessages: Flow<List<ChatMessageEntity>> = fifaDao.getChatMessages()

    suspend fun insertTicket(ticket: TicketEntity) = withContext(Dispatchers.IO) {
        fifaDao.insertTicket(ticket)
    }

    suspend fun updateTicket(ticket: TicketEntity) = withContext(Dispatchers.IO) {
        fifaDao.updateTicket(ticket)
    }

    suspend fun deleteTicket(ticketId: String) = withContext(Dispatchers.IO) {
        fifaDao.deleteTicket(ticketId)
    }

    suspend fun buyTicket(match: Match, section: String, row: String, seat: String, ownerName: String) = withContext(Dispatchers.IO) {
        val ticketId = "TKT-${UUID.randomUUID().toString().take(6).uppercase()}"
        val entity = TicketEntity(
            id = ticketId,
            matchId = match.id,
            homeCode = match.homeTeam.code,
            awayCode = match.awayTeam.code,
            homeName = match.homeTeam.name,
            awayName = match.awayTeam.name,
            homeEmoji = match.homeTeam.flagEmoji,
            awayEmoji = match.awayTeam.flagEmoji,
            dateString = match.dateTime,
            venue = match.venueName,
            section = section,
            row = row,
            seat = seat,
            ownerName = ownerName,
            qrCodePayload = "FIFA2026|$ticketId|${match.id}|$section|$row|$seat"
        )
        fifaDao.insertTicket(entity)
        
        // Auto-earn a badge if they buy their first ticket!
        earnBadge("FIRST_TICKET", "TICKET ACQUIRED", "Purchased or claimed your first official ticket for 2026!", "Match")
    }

    suspend fun transferTicket(ticketId: String, email: String) = withContext(Dispatchers.IO) {
        // In our app, ticket can be updated as transferred
        // We find the ticket in a stream or let Dao handle it. We can just load the list and update,
        // but wait! For simplicity of interface, let's let Dao have a transfer query or update.
        // We can just fetch all tickets first, or fetch ticket by ID. Let's write a simple update in DAO.
        // Oh, our DAO has an `updateTicket` method which can take the updated ticket.
    }

    suspend fun claimStadiumVisit(stadiumId: String, name: String) = withContext(Dispatchers.IO) {
        val visit = VisitedStadiumEntity(stadiumId = stadiumId)
        fifaDao.visitStadium(visit)

        // Earn a badge for visiting this stadium
        val title = "AURA OF $name"
        val desc = "Visited and completed check-in at $name"
        earnBadge("STAD_$stadiumId", title, desc, "Stadium")
    }

    suspend fun earnBadge(badgeId: String, title: String, description: String, category: String) = withContext(Dispatchers.IO) {
        val badge = EarnedBadgeEntity(
            badgeId = badgeId,
            title = title,
            description = description,
            category = category,
            timestampEarned = System.currentTimeMillis()
        )
        fifaDao.earnBadge(badge)
    }

    suspend fun addChatMessage(sender: String, message: String) = withContext(Dispatchers.IO) {
        val msg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            sender = sender,
            content = message,
            timestamp = System.currentTimeMillis()
        )
        fifaDao.insertChatMessage(msg)
    }

    suspend fun clearChat() = withContext(Dispatchers.IO) {
        fifaDao.clearChatHistory()
    }
}
