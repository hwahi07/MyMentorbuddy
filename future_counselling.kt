package com.example.mymentorbuddy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PreferencesHelper1 {

    private const val PREFS_NAME = "ChatPrefs"
    private const val MESSAGES_KEY = "messages"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveChatMessages(context: Context, chatMessages: List<ChatMessage1>) {
        val json = Gson().toJson(chatMessages)
        getSharedPreferences(context).edit().putString(MESSAGES_KEY, json).apply()
    }

    fun loadChatMessages(context: Context): List<ChatMessage1> {
        val json = getSharedPreferences(context).getString(MESSAGES_KEY, "[]")
        val type = object : TypeToken<List<ChatMessage1>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clearChatMessages(context: Context) {
        getSharedPreferences(context).edit().remove(MESSAGES_KEY).apply()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen1(navController: NavHostController, name: String) {
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(PreferencesHelper1.loadChatMessages(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top bar with icons
        TopAppBar(
            title = { Text(text = "Future Counsellor", color = Color.Black) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Go Back",
                        tint = Color.Black
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    PreferencesHelper1.clearChatMessages(context)
                    chatMessages = emptyList()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_refresh_24),
                        contentDescription = "Clear Chat",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
        )

        // Chat messages list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 56.dp) // Reserve space for the input field
        ) {
            LazyColumn {
                items(chatMessages) { message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (message.isUserMessage) Arrangement.End else Arrangement.Start
                    ) {
                        Text(
                            text = message.text,
                            color = if (message.isUserMessage) Color.Black else Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .background(
                                    color = if (message.isUserMessage) Color.LightGray else Color.Blue,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        // Input field and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
                .imePadding(), // Adjusts padding when keyboard is shown
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = { messageText = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (messageText.isNotEmpty()) {
                            val updatedMessages = chatMessages + ChatMessage1(messageText, isUserMessage = true)
                            chatMessages = updatedMessages
                            PreferencesHelper1.saveChatMessages(context, updatedMessages)
                            val scope = CoroutineScope(Dispatchers.Main)
                            scope.launch {
                                val geminiResponse = generate(messageText)
                                val finalMessages = updatedMessages + ChatMessage1(geminiResponse, isUserMessage = false)
                                chatMessages = finalMessages
                                PreferencesHelper1.saveChatMessages(context, finalMessages)
                                messageText = ""
                            }
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Gray, MaterialTheme.shapes.small)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageText.isNotEmpty()) {
                        val updatedMessages = chatMessages + ChatMessage1(messageText, isUserMessage = true)
                        chatMessages = updatedMessages
                        PreferencesHelper1.saveChatMessages(context, updatedMessages)
                        val scope = CoroutineScope(Dispatchers.Main)
                        scope.launch {
                            val geminiResponse = generate(messageText)
                            val finalMessages = updatedMessages + ChatMessage1(geminiResponse, isUserMessage = false)
                            chatMessages = finalMessages
                            PreferencesHelper1.saveChatMessages(context, finalMessages)
                            messageText = ""
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_send_24),
                    contentDescription = "Send",
                    tint = Color.Black
                )
            }
        }
    }
}


data class ChatMessage1(val text: String, val isUserMessage: Boolean)
