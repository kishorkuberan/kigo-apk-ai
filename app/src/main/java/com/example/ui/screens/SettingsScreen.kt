package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.EducationMode
import com.example.ai.GeminiModel
import com.example.ui.ChatViewModel
import com.example.ui.components.KigoBrandHeader
import com.example.ui.components.KigoLogoEmblem
import com.example.ui.theme.KigoBackground
import com.example.ui.theme.KigoDeepRedContainer
import com.example.ui.theme.KigoMutedText
import com.example.ui.theme.KigoNeonRed
import com.example.ui.theme.KigoPrimaryRed
import com.example.ui.theme.KigoSilver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ChatViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedModel by viewModel.selectedModel.collectAsState()
    val educationMode by viewModel.educationMode.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    val autoSpeak by viewModel.autoSpeak.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = "Clear All Conversations?",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all stored chat sessions from your device's local database.",
                    color = KigoSilver,
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllConversations()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))
                ) {
                    Text("Clear Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = KigoSilver)
                }
            },
            containerColor = Color(0xFF141320)
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(KigoBackground),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & AI Config",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = KigoSilver
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer",
                            tint = KigoSilver
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0C0C14)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // AI Model Selection Section
            SettingsSectionHeader(title = "AI INTELLIGENCE MODEL", icon = Icons.Default.Psychology)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (model in listOf(GeminiModel.GEMINI_3_5_FLASH, GeminiModel.GEMINI_3_1_PRO)) {
                    val isSelected = model == selectedModel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF261019) else Color(0xFF141320))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) KigoNeonRed else Color(0x33FFFFFF),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.updateModel(model) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, if (isSelected) KigoNeonRed else Color(0xFF64748B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(KigoNeonRed)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = model.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else KigoSilver
                            )
                            Text(
                                text = model.description,
                                fontSize = 11.sp,
                                color = KigoMutedText
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)

            // Education Mode Section
            SettingsSectionHeader(title = "EDUCATION & REASONING MODE", icon = Icons.Default.School)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (mode in EducationMode.values()) {
                    val isSelected = mode == educationMode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF261019) else Color(0xFF141320))
                            .border(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) KigoNeonRed else Color(0x22FFFFFF),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.updateEducationMode(mode) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else KigoSilver
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = KigoNeonRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)

            // Temperature / Creativity Slider
            SettingsSectionHeader(title = "CREATIVITY & PRECISION", icon = Icons.Default.Tune)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141320))
                    .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Model Temperature",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KigoSilver
                        )
                        Text(
                            text = String.format("%.2f", temperature),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = KigoNeonRed
                        )
                    }

                    Slider(
                        value = temperature,
                        onValueChange = { viewModel.updateTemperature(it) },
                        valueRange = 0.0f..1.0f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = KigoNeonRed,
                            activeTrackColor = KigoPrimaryRed,
                            inactiveTrackColor = Color(0xFF262638)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Precise / Deterministic", fontSize = 10.sp, color = KigoMutedText)
                        Text(text = "Creative / Open", fontSize = 10.sp, color = KigoMutedText)
                    }
                }
            }

            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)

            // Voice & Speech
            SettingsSectionHeader(title = "VOICE & SPEECH", icon = Icons.Default.VolumeUp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141320))
                    .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto Read Aloud",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KigoSilver
                    )
                    Text(
                        text = "Automatically speak KIGO AI text responses using Text-to-Speech",
                        fontSize = 11.sp,
                        color = KigoMutedText
                    )
                }

                Switch(
                    checked = autoSpeak,
                    onCheckedChange = { viewModel.toggleAutoSpeak() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = KigoNeonRed,
                        uncheckedTrackColor = Color(0xFF262638)
                    )
                )
            }

            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)

            // Future Video Architecture (Mandated by section 14)
            SettingsSectionHeader(title = "FUTURE VIDEO GENERATION", icon = Icons.Default.Movie)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141320))
                    .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB74D))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Veo Architecture: Staged",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB74D)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The modular provider layer is configured and ready. In accordance with strict guidelines, video generation will activate upon project video-model API activation.",
                        fontSize = 11.5.sp,
                        color = KigoMutedText,
                        lineHeight = 16.sp
                    )
                }
            }

            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)

            // API Security & Environment
            SettingsSectionHeader(title = "SECURITY & SECRETS", icon = Icons.Default.Security)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141320))
                    .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = KigoNeonRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Server-Side Secrets Injection",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = KigoSilver
                        )
                        Text(
                            text = if (viewModel.aiService.geminiProvider.isKeyConfigured) {
                                "GEMINI_API_KEY is securely configured via BuildConfig."
                            } else {
                                "Key not detected. Add GEMINI_API_KEY in the AI Studio Secrets panel."
                            },
                            fontSize = 11.sp,
                            color = if (viewModel.aiService.geminiProvider.isKeyConfigured) Color(0xFF81C784) else Color(0xFFFF8A80)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)

            // Data Management
            SettingsSectionHeader(title = "DATA & STORAGE", icon = Icons.Default.DeleteSweep)

            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF260B12)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Clear All Chat Conversations",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF8A80)
                )
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KigoNeonRed,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = KigoMutedText,
            letterSpacing = 1.sp
        )
    }
}
