package com.example.mymentorbuddy

import android.graphics.Shader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text2.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.example.mymentorbuddy.ui.theme.gradient1
import com.example.mymentorbuddy.ui.theme.gradient2
import com.example.mymentorbuddy.ui.theme.gradient3
import com.example.mymentorbuddy.ui.theme.gradient4
import com.example.mymentorbuddy.ui.theme.gradient5
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)

var subjectlist = mutableStateOf(listOf<Subject>())



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScreen(navController: NavController) {
    val context = LocalContext.current
    /*val title: String,
    val description: String,
    val isrelatedtosubject: String,
    val completed: Boolean,
    val date: String*/
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val name = sharedPreferences.getString("username", null)
    val items = listOf(
        NavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
        ),
        NavigationItem(
            title = "AI tutor",
            selectedIcon = Icons.Filled.Edit,
            unselectedIcon = Icons.Outlined.Info,
            badgeCount = 45
        ),
        NavigationItem(
            title = "Future Counsellor",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
        ),
        NavigationItem(
            title = "Exam practice paper generation",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
        ),
        NavigationItem(
            title = "Logout",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
        ),
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Set background color to white
        color = MaterialTheme.colorScheme.background
    ) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var selectedItemIndex by rememberSaveable {
            mutableStateOf(0)
        }
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White // Set drawer background to white
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color.LightGray, // Darkish tint for selected item
                                unselectedContainerColor = Color.White
                            ),
                            label = {
                                Text(text = item.title, color = Color.Black)
                            },
                            selected = index == selectedItemIndex,
                            onClick = {
                                selectedItemIndex = index
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = Color.Black
                                )
                            },
                            badge = {
                                item.badgeCount?.let {
                                    Text(text = it.toString(), color = Color.Black)
                                }
                            },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            },
            drawerState = drawerState
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "My Mentor Buddy", color = Color.Black)
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = Color.White,
                            titleContentColor = Color.Black,
                            navigationIconContentColor = Color.Black
                        ),
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = Color.Black
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxHeight(0.30f)
                            .fillMaxWidth()
                            .padding(20.dp)
                    ){
                        var size by remember { mutableStateOf(Size.Zero) }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { coordinates ->
                                    size = coordinates.size.toSize()
                                }
                                .then(
                                    if (size != Size.Zero) Modifier.background(
                                        color = Color.Black,
                                        shape = RoundedCornerShape(20.dp)
                                    ) else Modifier
                                ) // Conditional application of background modifier
                        ) {
                            var promptResult by remember { mutableStateOf("") }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(30.dp), verticalArrangement = Arrangement.Top
                            ) {
                                Row {
                                    Column {
                                        Text(
                                            "Welcome back,",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 25.sp,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Column {
                                        Text(
                                            "$name!",
                                            fontStyle = FontStyle.Italic,
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 20.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp)
                                        )
                                    }
                                }

                                LaunchedEffect(key1 = Unit) {
                                    promptResult = "Hello World"
                                }
                                Spacer(modifier = Modifier.height(25.dp))
                                Row {
                                    Text(
                                        text = promptResult,
                                        color = Color.White,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Cardfield(modifier = Modifier.fillMaxWidth(), subjectlist = emptyList())
                }
            }
        }
    }
}
suspend fun generate(prompt: String): String {
    return(gemini_generate(prompt))
}

suspend fun gemini_generate(prompt: String): String {
    val apikey = "AIzaSyBcgmDRgwjYfh7FQD2l5ONMLSswbAS18QY"
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apikey
    )
    val response = generativeModel.generateContent(prompt)
    return(response.text.toString())
}

data class Subject(
    val subject: String,
    val completed: String,
    val colors: List<Color>,
    val Subjectid: Int
){
    companion object{
        val cardcolors = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}

@Composable
private fun Cardfield(modifier: Modifier, subjectlist: List<Subject>, emptyalert: String = "You don't have any subjects/Courses. \nPress '+' icon to create one.") {
    // State to manage dialog visibility
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SUBJECTS / COURSES",
                modifier = Modifier
                    .padding(start = 20.dp),
                color = Color.Black
            )
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        if (subjectlist.isEmpty()) {
            Image(
                modifier = modifier.size(80.dp).align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.books),
                contentDescription = emptyalert
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = modifier.fillMaxWidth(),
                text = emptyalert,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectlist) { subject ->
                SubjectCard(
                    subjectname = subject.subject,
                    gradientColors = subject.colors,
                    onclick = {}
                )
            }
        }
    }

    // Show dialog if the state is true
    if (showDialog) {
        InputTaskNamesPopup(onDismiss = { showDialog = false })
    }
}


@Composable
fun SubjectCard(
    subjectname: String,
    gradientColors: List<Color>,
    onclick: () -> Unit
) {
    Box(modifier = Modifier
        .size(150.dp)
        .clickable { onclick() }
        .background(
            brush = Brush.verticalGradient(gradientColors),
            shape = MaterialTheme.shapes.medium
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.books),

            contentDescription = "",
            modifier = Modifier.size(80.dp)
            )
            Text(
                text = subjectname,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Composable
private fun TaskCheckBox(
    isChecked: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    androidx.compose.material3.Checkbox(
        checked = isChecked,
        onCheckedChange = { onClick() },
        colors = androidx.compose.material3.CheckboxDefaults.colors(
            checkedColor = color,
            uncheckedColor = color
        )
    )
}