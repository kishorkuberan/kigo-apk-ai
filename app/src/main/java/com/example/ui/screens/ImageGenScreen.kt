package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.ChatViewModel
import com.example.ui.base64ToBitmap
import com.example.ui.components.KigoLogoEmblem
import com.example.ui.theme.KigoBackground
import com.example.ui.theme.KigoMutedText
import com.example.ui.theme.KigoNeonRed
import com.example.ui.theme.KigoPrimaryRed
import com.example.ui.theme.KigoSilver

val sampleImagePrompts = listOf(
    "Cinematic sunset over the ocean with crimson sky",
    "Futuristic cyberpunk city with neon red holographic signs in rain",
    "3D metallic chrome robot holding glowing red crystal",
    "Mythological phoenix rising from red ashes, ultra-detailed"
)

val aspectRatios = listOf("1:1", "16:9", "4:3", "9:16")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGenScreen(
    viewModel: ChatViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prompt by viewModel.imageGenPrompt.collectAsState()
    val aspectRatio by viewModel.imageGenAspectRatio.collectAsState()
    val isGenerating by viewModel.isGeneratingImage.collectAsState()
    val errorMessage by viewModel.imageGenError.collectAsState()
    val gallery by viewModel.generatedImagesList.collectAsState()

    var showEnlargedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    showEnlargedBitmap?.let { bmp ->
        Dialog(onDismissRequest = { showEnlargedBitmap = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0F16))
                    .border(1.5.dp, KigoPrimaryRed, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Full artwork",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(KigoBackground),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        KigoLogoEmblem(size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Image Generation",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = KigoSilver
                            )
                            Text(
                                text = "Gemini Flash Native Image Model",
                                fontSize = 10.sp,
                                color = KigoMutedText
                            )
                        }
                    }
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
                .imePadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF200C14), Color(0xFF141322))
                        )
                    )
                    .border(1.dp, Color(0x55FF2442), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = KigoNeonRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "KIGO Creative Studio",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Enter detailed prompts to generate high-fidelity AI artwork with native multimodal Gemini models.",
                            fontSize = 11.5.sp,
                            color = KigoMutedText,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Prompt Input Field
            Column {
                Text(
                    text = "ARTWORK PROMPT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = KigoMutedText,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { viewModel.setImageGenPrompt(it) },
                    placeholder = {
                        Text(
                            text = "e.g. \"Generate a cinematic sunset over the ocean.\"",
                            color = Color(0xFF64748B),
                            fontSize = 13.5.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = KigoSilver,
                        focusedBorderColor = KigoNeonRed,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedContainerColor = Color(0xFF141320),
                        unfocusedContainerColor = Color(0xFF141320)
                    ),
                    trailingIcon = {
                        if (prompt.isNotBlank()) {
                            IconButton(onClick = { viewModel.setImageGenPrompt("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = KigoMutedText
                                )
                            }
                        }
                    }
                )
            }

            // Quick Prompt Ideas
            Column {
                Text(
                    text = "QUICK INSPIRATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = KigoMutedText,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sampleImagePrompts) { sample ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1B1A2A))
                                .border(0.5.dp, Color(0x44FFFFFF), RoundedCornerShape(8.dp))
                                .clickable { viewModel.setImageGenPrompt(sample) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = sample,
                                fontSize = 11.5.sp,
                                color = KigoSilver,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Aspect Ratio Selector
            Column {
                Text(
                    text = "ASPECT RATIO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = KigoMutedText,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (ratio in aspectRatios) {
                        val isSelected = ratio == aspectRatio
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setImageGenAspectRatio(ratio) },
                            label = { Text(text = ratio, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF330C16),
                                selectedLabelColor = KigoNeonRed,
                                containerColor = Color(0xFF161524),
                                labelColor = KigoMutedText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = KigoNeonRed,
                                borderColor = Color(0x33FFFFFF)
                            )
                        )
                    }
                }
            }

            // Error Display
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF330B12))
                        .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFFFFD9DF)
                        )
                    }
                }
            }

            // Generate Button
            Button(
                onClick = { viewModel.triggerImageGeneration(prompt, aspectRatio) },
                enabled = prompt.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (prompt.isNotBlank() && !isGenerating) KigoNeonRed else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A0A12),
                    disabledContainerColor = Color(0xFF181520)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = KigoNeonRed,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Synthesizing Artwork...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (prompt.isNotBlank()) KigoNeonRed else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate Artwork",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (prompt.isNotBlank()) Color.White else Color(0xFF64748B)
                    )
                }
            }

            // Gallery of Generated Artwork
            if (gallery.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "GENERATED IN THIS SESSION (${gallery.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = KigoMutedText,
                    letterSpacing = 1.sp
                )

                for (item in gallery) {
                    val bitmap = remember(item.imageBase64) {
                        base64ToBitmap(item.imageBase64)
                    }
                    if (bitmap != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF141320))
                                .border(1.dp, Color(0x44FF2442), RoundedCornerShape(14.dp))
                                .clickable { showEnlargedBitmap = bitmap }
                        ) {
                            Column {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Generated Artwork",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(260.dp),
                                    contentScale = ContentScale.Crop
                                )
                                if (!item.caption.isNullOrBlank()) {
                                    Text(
                                        text = item.caption,
                                        fontSize = 12.sp,
                                        color = KigoSilver,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
