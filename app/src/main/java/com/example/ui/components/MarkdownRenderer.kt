package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KigoMutedText
import com.example.ui.theme.KigoNeonRed
import com.example.ui.theme.KigoPrimaryRed
import com.example.ui.theme.KigoSilver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MarkdownContent(
    text: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sections = remember(text) { parseMarkdownSections(text) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (section in sections) {
            when (section) {
                is MarkdownSection.CodeBlock -> {
                    CodeBlockView(
                        language = section.language,
                        code = section.code,
                        context = context
                    )
                }
                is MarkdownSection.Heading -> {
                    Text(
                        text = section.text,
                        fontSize = when (section.level) {
                            1 -> 20.sp
                            2 -> 18.sp
                            else -> 16.sp
                        },
                        fontWeight = FontWeight.Bold,
                        color = if (section.level == 1) KigoNeonRed else KigoSilver,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                is MarkdownSection.BulletItem -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp, end = 8.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(KigoPrimaryRed)
                        )
                        Text(
                            text = parseInlineFormatting(section.text),
                            fontSize = 14.sp,
                            color = KigoSilver,
                            lineHeight = 20.sp
                        )
                    }
                }
                is MarkdownSection.NumberedItem -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${section.number}.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = KigoPrimaryRed,
                            modifier = Modifier
                                .width(22.dp)
                                .padding(top = 1.dp)
                        )
                        Text(
                            text = parseInlineFormatting(section.text),
                            fontSize = 14.sp,
                            color = KigoSilver,
                            lineHeight = 20.sp
                        )
                    }
                }
                is MarkdownSection.Paragraph -> {
                    if (section.text.isNotBlank()) {
                        Text(
                            text = parseInlineFormatting(section.text),
                            fontSize = 14.sp,
                            color = KigoSilver,
                            lineHeight = 21.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlockView(
    language: String,
    code: String,
    context: Context,
    modifier: Modifier = Modifier
) {
    var isCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0C0C12))
            .border(1.dp, Color(0x33FF2442), RoundedCornerShape(8.dp))
    ) {
        Column {
            // Terminal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161522))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5252)))
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFB74D)))
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF81C784)))

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = language.ifBlank { "code" }.lowercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = KigoMutedText
                    )
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("code", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                        isCopied = true
                        scope.launch {
                            delay(2000)
                            isCopied = false
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (isCopied) Color(0xFF00E676) else KigoMutedText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Code Content with horizontal scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    color = Color(0xFFF1F5F9),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

sealed class MarkdownSection {
    data class Paragraph(val text: String) : MarkdownSection()
    data class Heading(val level: Int, val text: String) : MarkdownSection()
    data class BulletItem(val text: String) : MarkdownSection()
    data class NumberedItem(val number: String, val text: String) : MarkdownSection()
    data class CodeBlock(val language: String, val code: String) : MarkdownSection()
}

fun parseMarkdownSections(markdown: String): List<MarkdownSection> {
    val sections = mutableListOf<MarkdownSection>()
    val lines = markdown.lines()

    var inCodeBlock = false
    var codeLang = ""
    val codeBuilder = StringBuilder()
    val paragraphBuilder = StringBuilder()

    fun flushParagraph() {
        if (paragraphBuilder.isNotEmpty()) {
            val content = paragraphBuilder.toString().trim()
            if (content.isNotEmpty()) {
                sections.add(MarkdownSection.Paragraph(content))
            }
            paragraphBuilder.clear()
        }
    }

    for (line in lines) {
        if (line.trim().startsWith("```")) {
            if (inCodeBlock) {
                // End code block
                sections.add(MarkdownSection.CodeBlock(codeLang, codeBuilder.toString().trimEnd()))
                codeBuilder.clear()
                codeLang = ""
                inCodeBlock = false
            } else {
                // Start code block
                flushParagraph()
                codeLang = line.trim().removePrefix("```").trim()
                inCodeBlock = true
            }
            continue
        }

        if (inCodeBlock) {
            codeBuilder.append(line).append("\n")
            continue
        }

        val trimmed = line.trim()
        when {
            trimmed.startsWith("### ") -> {
                flushParagraph()
                sections.add(MarkdownSection.Heading(3, trimmed.removePrefix("### ").trim()))
            }
            trimmed.startsWith("## ") -> {
                flushParagraph()
                sections.add(MarkdownSection.Heading(2, trimmed.removePrefix("## ").trim()))
            }
            trimmed.startsWith("# ") -> {
                flushParagraph()
                sections.add(MarkdownSection.Heading(1, trimmed.removePrefix("# ").trim()))
            }
            trimmed.startsWith("* ") || trimmed.startsWith("- ") -> {
                flushParagraph()
                val itemText = trimmed.drop(2).trim()
                sections.add(MarkdownSection.BulletItem(itemText))
            }
            trimmed.matches(Regex("^\\d+\\.\\s.*")) -> {
                flushParagraph()
                val match = Regex("^(\\d+)\\.\\s(.*)").find(trimmed)
                if (match != null) {
                    val num = match.groupValues[1]
                    val content = match.groupValues[2]
                    sections.add(MarkdownSection.NumberedItem(num, content))
                } else {
                    paragraphBuilder.append(line).append("\n")
                }
            }
            trimmed.isEmpty() -> {
                flushParagraph()
            }
            else -> {
                paragraphBuilder.append(line).append("\n")
            }
        }
    }

    if (inCodeBlock && codeBuilder.isNotEmpty()) {
        sections.add(MarkdownSection.CodeBlock(codeLang, codeBuilder.toString()))
    }
    flushParagraph()

    return sections
}

fun parseInlineFormatting(text: String) = buildAnnotatedString {
    var i = 0
    while (i < text.length) {
        if (text.startsWith("**", i)) {
            val end = text.indexOf("**", i + 2)
            if (end != -1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFFFFFFF))) {
                    append(text.substring(i + 2, end))
                }
                i = end + 2
                continue
            }
        } else if (text.startsWith("`", i)) {
            val end = text.indexOf("`", i + 1)
            if (end != -1) {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        color = KigoNeonRed,
                        background = Color(0xFF22151D)
                    )
                ) {
                    append(" ${text.substring(i + 1, end)} ")
                }
                i = end + 1
                continue
            }
        } else if (text.startsWith("*", i)) {
            val end = text.indexOf("*", i + 1)
            if (end != -1) {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFFCBD5E1))) {
                    append(text.substring(i + 1, end))
                }
                i = end + 1
                continue
            }
        }
        append(text[i])
        i++
    }
}
