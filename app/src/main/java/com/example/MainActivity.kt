package com.example

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.graphics.ImageDecoder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.ui.AppDestination
import com.example.ui.ChatViewModel
import com.example.ui.components.KigoSidebarContent
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ImageGenScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KigoApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KigoApp(viewModel: ChatViewModel) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentDestination by viewModel.currentDestination.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val selectedConvId by viewModel.currentConversationId.collectAsState()

    // Activity launcher for photo picker from sidebar
    val sidebarPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                viewModel.setSelectedImage(bitmap)
                viewModel.navigateTo(AppDestination.CHAT)
                Toast.makeText(context, "Image loaded. Type a question or send to analyze.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF09090F)
            ) {
                KigoSidebarContent(
                    currentDestination = currentDestination,
                    conversations = conversations,
                    selectedConversationId = selectedConvId,
                    onNavigate = { dest ->
                        viewModel.navigateTo(dest)
                    },
                    onNewChat = {
                        viewModel.startNewChat()
                    },
                    onSelectConversation = { id ->
                        viewModel.selectConversation(id)
                    },
                    onDeleteConversation = { id ->
                        viewModel.deleteConversation(id)
                    },
                    onTriggerImageUpload = {
                        sidebarPhotoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestination.CHAT -> {
                ChatScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            AppDestination.IMAGE_GENERATION -> {
                ImageGenScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            AppDestination.SETTINGS -> {
                SettingsScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            AppDestination.ABOUT -> {
                AboutScreen(
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
