package com.example.data

import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {
    val conversations: Flow<List<ConversationEntity>> = chatDao.getAllConversations()

    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForConversation(conversationId)
    }

    suspend fun getConversation(id: String): ConversationEntity? {
        return chatDao.getConversationById(id)
    }

    suspend fun saveConversation(conversation: ConversationEntity) {
        chatDao.insertConversation(conversation)
    }

    suspend fun updateConversation(conversation: ConversationEntity) {
        chatDao.updateConversation(conversation)
    }

    suspend fun deleteConversation(id: String) {
        chatDao.deleteMessagesForConversation(id)
        chatDao.deleteConversation(id)
    }

    suspend fun deleteAll() {
        chatDao.deleteAllMessages()
        chatDao.deleteAllConversations()
    }

    suspend fun saveMessage(message: ChatMessageEntity) {
        chatDao.insertMessage(message)
        // Update conversation timestamp
        val conv = chatDao.getConversationById(message.conversationId)
        if (conv != null) {
            chatDao.updateConversation(conv.copy(updatedAt = message.timestamp))
        }
    }
}
