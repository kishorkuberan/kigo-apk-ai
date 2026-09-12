package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.ui.theme.KigoMutedText
import com.example.ui.theme.KigoNeonRed
import com.example.ui.theme.KigoPrimaryRed
import com.example.ui.theme.KigoSilver

data class SuggestionItem(
    val title: String,
    val subtitle: String,
    val prompt: String,
    val icon: ImageVector
)

val defaultSuggestions = listOf(
    SuggestionItem(
        title = "Explain a difficult topic",
        subtitle = "What is photosynthesis in simple terms?",
        prompt = "Explain photosynthesis in simple and clear terms, covering light reactions, Calvin cycle, and why it matters to life on Earth.",
        icon = Icons.Default.Psychology
    ),
    SuggestionItem(
        title = "Solve a math problem",
        subtitle = "Solve 2x + 5 = 15 step-by-step",
        prompt = "Solve 2x + 5 = 15 step-by-step, showing formula, calculation, and clear final answer.",
        icon = Icons.Default.Calculate
    ),
    SuggestionItem(
        title = "Write Python code",
        subtitle = "Calculator with clean functions",
        prompt = "Write clean Python code for an interactive terminal calculator supporting addition, subtraction, multiplication, division, and error handling.",
        icon = Icons.Default.Code
    ),
    SuggestionItem(
        title = "Generate an image",
        subtitle = "Futuristic neon cyberpunk city",
        prompt = "Create a vivid image of a futuristic cyberpunk city with towering glass skyscrapers, neon red holograms, and flying vehicles in rain.",
        icon = Icons.Default.Image
    ),
    SuggestionItem(
        title = "Translate into Tamil",
        subtitle = "Language & multilingual translation",
        prompt = "Translate this into Tamil and explain the meaning: 'Technology empowers humans to solve global challenges.'",
        icon = Icons.Default.Translate
    ),
    SuggestionItem(
        title = "Explain Newton's Laws",
        subtitle = "Physics principles with examples",
        prompt = "Explain Newton's three laws of motion clearly with real-world examples for each law.",
        icon = Icons.Default.AutoAwesome
    )
)

@Composable
fun WelcomeHero(
    onSelectSuggestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large 3D Metallic Emblem
        KigoLogoEmblem(size = 72.dp, showGlow = true)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hello, I'm KIGO AI",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = KigoSilver,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Ask me anything, solve problems, create ideas, or explore something new.",
            fontSize = 13.5.sp,
            color = KigoMutedText,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Section header for suggestions
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
                text = "SUGGESTED CAPABILITIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = KigoMutedText,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Suggestions grid / row
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (item in defaultSuggestions.take(4)) {
                SuggestionCard(item = item, onClick = { onSelectSuggestion(item.prompt) })
            }
        }
    }
}

@Composable
fun SuggestionCard(
    item: SuggestionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF141320), Color(0xFF0F0E18))
                )
            )
            .border(1.dp, Color(0x33FF2442), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF261019))
                .border(1.dp, KigoNeonRed.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = KigoPrimaryRed,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = KigoSilver
            )
            Text(
                text = item.subtitle,
                fontSize = 11.5.sp,
                color = KigoMutedText,
                maxLines = 1
            )
        }
    }
}
