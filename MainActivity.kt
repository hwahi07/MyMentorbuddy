package com.example.mymentorbuddy

import ExamGenerationScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mymentorbuddy.ui.theme.MyMentorBuddyTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.core.Context


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase
        setContent {
            MyMentorBuddyTheme {
                // Setup Navigation Controller
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}

@Composable
// Define your navigation helper function
fun navigateToScreen(navController: NavHostController, screen: String, params: Map<String, Any> = emptyMap()) {
    val route = buildString {
        append(screen)
        if (params.isNotEmpty()) {
            append("?")
            params.entries.joinToString("&") { "${it.key}=${it.value}" }.let { append(it) }
        }
    }
    navController.navigate(route)
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "ChatScreen1") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("signup") {
            SignupScreen(navController = navController)
        }
        composable("new_screen") {
            NewScreen(navController = navController)
        }
        composable("input_task_names") {
            Input_task_names(navController = navController)
        }
        composable("ChatScreen"){
            ChatScreen(navController = navController, name = "user")
        }
        composable("ChatScreen1"){
            ChatScreen1(navController = navController, name = "user")
        }
        composable("ExamGenerationScreen"){
            ExamGenerationScreen(navController = navController)
        }
    }
}
