package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Entities
@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val id: String,
    val matchId: String,
    val homeCode: String,
    val awayCode: String,
    val homeName: String,
    val awayName: String,
    val homeEmoji: String,
    val awayEmoji: String,
    val dateString: String,
    val venue: String,
    val section: String,
    val row: String,
    val seat: String,
    val ownerName: String,
    val qrCodePayload: String,
    val isTransferred: Boolean = false,
    val transferEmail: String? = null
)

@Entity(tableName = "visited_stadiums")
data class VisitedStadiumEntity(
    @PrimaryKey val stadiumId: String,
    val visitTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "earned_badges")
data class EarnedBadgeEntity(
    @PrimaryKey val badgeId: String,
    val title: String,
    val description: String,
    val category: String, // "Match", "Stadium", "Merch", "Trivia"
    val timestampEarned: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val sender: String, // "USER" or "ASSISTANT"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

// DAO
@Dao
interface FifaDao {
    // Tickets
    @Query("SELECT * FROM tickets ORDER BY id DESC")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)

    @Update
    suspend fun updateTicket(ticket: TicketEntity)

    @Query("DELETE FROM tickets WHERE id = :ticketId")
    suspend fun deleteTicket(ticketId: String)

    // Visited Stadiums
    @Query("SELECT * FROM visited_stadiums")
    fun getVisitedStadiums(): Flow<List<VisitedStadiumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun visitStadium(visited: VisitedStadiumEntity)

    // Badges
    @Query("SELECT * FROM earned_badges ORDER BY timestampEarned DESC")
    fun getEarnedBadges(): Flow<List<EarnedBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun earnBadge(badge: EarnedBadgeEntity)

    // Chat History
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}
