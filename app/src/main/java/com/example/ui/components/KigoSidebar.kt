package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConversationEntity
import com.example.ui.AppDestination
import com.example.ui.theme.KigoMutedText
import com.example.ui.theme.KigoNeonRed
import com.example.ui.theme.KigoPrimaryRed
import com.example.ui.theme.KigoRedNeonBrush
import com.example.ui.theme.KigoSilver
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KigoSidebarContent(
    currentDestination: AppDestination,
    conversations: List<ConversationEntity>,
    selectedConversationId: String?,
    onNavigate: (AppDestination) -> Unit,
    onNewChat: () -> Unit,
    onSelectConversation: (String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onTriggerImageUpload: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color(0xFF09090F))
            .padding(16.dp)
    ) {
        // Top Brand Header
        KigoBrandHeader(isCompact = false)

        Spacer(modifier = Modifier.height(20.dp))

        // "New Chat" primary glowing button
        Button(
            onClick = {
                onNewChat()
                onCloseDrawer()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, KigoNeonRed, RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF260B12)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Chat",
                    tint = KigoNeonRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Chat",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation list
        SidebarNavItem(
            label = "Home / Chat",
            icon = Icons.Default.Chat,
            isSelected = currentDestination == AppDestination.CHAT && selectedConversationId == null,
            onClick = {
                onNavigate(AppDestination.CHAT)
                onCloseDrawer()
            }
        )

        SidebarNavItem(
            label = "Image Generation",
            icon = Icons.Default.Image,
            isSelected = currentDestination == AppDestination.IMAGE_GENERATION,
            onClick = {
                onNavigate(AppDestination.IMAGE_GENERATION)
                onCloseDrawer()
            }
        )

        SidebarNavItem(
            label = "Image Upload",
            icon = Icons.Default.Upload,
            isSelected = false,
            onClick = {
                onNavigate(AppDestination.CHAT)
                onTriggerImageUpload()
                onCloseDrawer()
            }
        )

        SidebarNavItem(
            label = "Voice Assistant",
            icon = Icons.Default.Mic,
            isSelected = false,
            onClick = {
                onNavigate(AppDestination.CHAT)
                onCloseDrawer()
            }
        )

        SidebarNavItem(
            label = "Settings",
            icon = Icons.Default.Settings,
            isSelected = currentDestination == AppDestination.SETTINGS,
            onClick = {
                onNavigate(AppDestination.SETTINGS)
                onCloseDrawer()
            }
        )

        SidebarNavItem(
            label = "About KIGO AI",
            icon = Icons.Default.Info,
            isSelected = currentDestination == AppDestination.ABOUT,
            onClick = {
                onNavigate(AppDestination.ABOUT)
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Chat History Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                tint = KigoMutedText,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "CHAT HISTORY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = KigoMutedText,
                letterSpacing = 0.8.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // History items
        if (conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved conversations yet.",
                    fontSize = 12.sp,
                    color = Color(0xFF555566)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(conversations, key = { it.id }) { conv ->
                    val isSelected = conv.id == selectedConversationId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) Color(0xFF261019) else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) Color(0x55FF2442) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onSelectConversation(conv.id)
                                onCloseDrawer()
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = conv.title,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else KigoSilver,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = dateFormat.format(Date(conv.updatedAt)),
                                fontSize = 9.5.sp,
                                color = KigoMutedText
                            )
                        }

                        IconButton(
                            onClick = { onDeleteConversation(conv.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete chat",
                                tint = Color(0xFF888899),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Mandatory Footer:
        // KIGO AI
        // "Turning Ideas Into Visual Stories"
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KIGO AI",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = KigoSilver
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "\"Turning Ideas Into Visual Stories\"",
                fontSize = 10.5.sp,
                color = KigoMutedText,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun SidebarNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFF261019) else Color.Transparent)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) Color(0x55FF2442) else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) KigoNeonRed else KigoMutedText,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else KigoSilver
        )
    }
}
