package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.KigoLogoEmblem
import com.example.ui.theme.KigoBackground
import com.example.ui.theme.KigoMutedText
import com.example.ui.theme.KigoNeonRed
import com.example.ui.theme.KigoPrimaryRed
import com.example.ui.theme.KigoSilver

data class CapabilityFeature(val title: String, val desc: String, val icon: ImageVector)

val kigoCapabilities = listOf(
    CapabilityFeature(
        "Chat & Deep Reasoning",
        "High-intelligence conversational AI tailored for nuanced understanding, synthesis, and creative exploration.",
        Icons.Default.Psychology
    ),
    CapabilityFeature(
        "Mathematics & Science",
        "Step-by-step rigorous problem solving with formulas, derivations, and verifiable calculations.",
        Icons.Default.Calculate
    ),
    CapabilityFeature(
        "Code Intelligence",
        "Syntax-highlighted terminal generation, bug triage, and multi-language implementation across modern stacks.",
        Icons.Default.Code
    ),
    CapabilityFeature(
        "Generative Visual Studio",
        "Direct connection to Gemini multimodal image models for ultra-high-definition artwork synthesis.",
        Icons.Default.Image
    ),
    CapabilityFeature(
        "Multimodal Vision",
        "Upload diagrams, screenshots, or physical photos for real-time visual analysis and problem solving.",
        Icons.Default.AutoAwesome
    ),
    CapabilityFeature(
        "Global Multilingual",
        "Comprehensive language translation and cross-cultural explanation, including Tamil, Spanish, French, and Hindi.",
        Icons.Default.Translate
    ),
    CapabilityFeature(
        "Voice Interaction",
        "Speech recognition dictation and neural text-to-speech audio playback.",
        Icons.Default.Mic
    ),
    CapabilityFeature(
        "Zero-Leakage Security",
        "Architected with strict server-side secrets injection; API keys are never exposed in client source.",
        Icons.Default.Security
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(KigoBackground),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About KIGO AI",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 3D Metallic Emblem
            KigoLogoEmblem(size = 80.dp, showGlow = true)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "KIGO AI",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )

            Text(
                text = "\"Turning Ideas Into Visual Stories\"",
                fontSize = 13.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = KigoNeonRed,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Version 1.0.0 • Production Engine",
                fontSize = 11.sp,
                color = KigoMutedText
            )

            // Mission Statement Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B1424), Color(0xFF100E1A))
                        )
                    )
                    .border(1.dp, Color(0x44FF2442), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "MISSION & ARCHITECTURE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KigoNeonRed,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "KIGO AI is a high-performance intelligence assistant engineered to unify multi-turn conversational reasoning, mathematical precision, code architecture, image generation, and multimodal vision in a singular, futuristic dark interface.",
                        fontSize = 12.5.sp,
                        color = KigoSilver,
                        lineHeight = 18.sp
                    )
                }
            }

            HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.5.dp)

            // Capabilities Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(KigoNeonRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CORE CAPABILITIES",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = KigoMutedText,
                    letterSpacing = 1.sp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (cap in kigoCapabilities) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF12111E))
                            .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF261019))
                                .border(1.dp, KigoNeonRed.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cap.icon,
                                contentDescription = null,
                                tint = KigoPrimaryRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cap.title,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cap.desc,
                                fontSize = 11.5.sp,
                                color = KigoMutedText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Powered by Google Gemini 3.5 Flash & Gemini Generative Image Models.\nBuilt with Jetpack Compose & Clean Architecture.",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}
