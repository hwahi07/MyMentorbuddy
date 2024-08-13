package com.example.mymentorbuddy

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.database.database
import org.mindrot.jbcrypt.BCrypt

// Data model for User
data class User(
    val password: String = "",  // Store hashed password
    val phoneNumber: String = "",
    val email: String = ""
)

private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

private fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

// Function to write user data to Firebase Realtime Database
fun writeUserData(username: String, password: String, phoneNumber: String, email: String): Task<Void> {
    val user = User(password = hashPassword(password), phoneNumber = phoneNumber, email = email)
    val userRef = database.child("users").child(username)
    return userRef.setValue(user)
}

// Function to check if the user exists in Firebase Realtime Database
fun checkUserExists(username: String, callback: (Boolean, DataSnapshot?) -> Unit) {
    val userRef = database.child("users").child(username)
    userRef.get().addOnSuccessListener { snapshot ->
        callback(snapshot.exists(), snapshot)
    }.addOnFailureListener {
        callback(false, null)
    }
}

// Function to check if the provided password matches the stored hashed password
fun checkPasswordMatch(snapshot: DataSnapshot, password: String): Boolean {
    val storedHashedPassword = snapshot.child("password").getValue(String::class.java) ?: ""
    return BCrypt.checkpw(password, storedHashedPassword)
}

// Function to update user data in Firebase Realtime Database
fun updateUserData(userId: String, updatedValues: Map<String, Any>): Task<Void> {
    val userRef = database.child("users").child(userId)
    return userRef.updateChildren(updatedValues)
}

// Function to delete user data from Firebase Realtime Database
fun deleteUserData(userId: String): Task<Void> {
    val userRef = database.child("users").child(userId)
    return userRef.removeValue()
}

// Function to validate the password
fun isValidPassword(password: String): Boolean {
    val specialCharPattern = "[^a-zA-Z0-9]"
    val capitalLetterPattern = "[A-Z]"
    val numberPattern = "[0-9]"
    return password.length >= 8 &&
            password.contains(Regex(specialCharPattern)) &&
            password.contains(Regex(capitalLetterPattern)) &&
            password.contains(Regex(numberPattern)) &&
            !password.contains(" ")
}

@Composable
fun SignupScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    GradientBox(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.15f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 50.dp, bottomEnd = 50.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(100.dp))

                    var username by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var confirmPassword by remember { mutableStateOf("") }
                    var phoneNumber by remember { mutableStateOf("") }
                    var email by remember { mutableStateOf("") }
                    var passwordError by remember { mutableStateOf("") }
                    var confirmPasswordError by remember { mutableStateOf("") }
                    var visibility by remember { mutableStateOf(false) }
                    var status by remember { mutableStateOf("") }
                    var rememberMe by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_account_circle_24),
                                contentDescription = "User icon",
                                tint = Color.Black
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Phone
                        ),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    val icon = if (visibility)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = if (isValidPassword(password)) "" else "Password must be at least 8 characters, contain a special character, a capital letter, a number, and no spaces"
                        },
                        label = { Text("Password") },
                        trailingIcon = {
                            IconButton(onClick = {
                                visibility = !visibility
                            }) {
                                Icon(
                                    painter = icon,
                                    contentDescription = "visibility icon",
                                    tint = Color.Black
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = if (visibility) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.lock),
                                contentDescription = "Password icon",
                                tint = Color.Black
                            )
                        },
                        isError = passwordError.isNotEmpty(),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        trailingIcon = {
                            IconButton(onClick = {
                                visibility = !visibility
                            }) {
                                Icon(
                                    painter = icon,
                                    contentDescription = "visibility icon",
                                    tint = Color.Black
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        visualTransformation = if (visibility) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.lock),
                                contentDescription = "Confirm Password icon",
                                tint = Color.Black
                            )
                        },
                        isError = confirmPasswordError.isNotEmpty(),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                    if (confirmPasswordError.isNotEmpty()) {
                        Text(
                            text = confirmPasswordError,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkmarkColor = Color.White,
                                checkedColor = Color.Black,
                                uncheckedColor = Color.Black
                            )
                        )
                        Text(
                            text = "Remember Me",
                            color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            // Check for existing username
                            checkUserExists(username) { exists, _ ->
                                if (exists) {
                                    status = "Username already exists. Please choose another one."
                                } else {
                                    // Validate password and confirm password on button click
                                    passwordError = if (isValidPassword(password)) "" else "Password must be at least 8 characters, contain a special character, a capital letter, a number, and no spaces"
                                    confirmPasswordError = if (password == confirmPassword) "" else "Passwords do not match"

                                    if (passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                                        // Write user data to Firebase
                                        writeUserData(username, password, phoneNumber, email)
                                            .addOnSuccessListener {
                                                status = "User registered successfully"

                                                if (rememberMe) {
                                                    with(sharedPreferences.edit()) {
                                                        putString("username", username)
                                                        apply()
                                                    }
                                                }

                                                navController.navigate("new_screen")
                                            }
                                            .addOnFailureListener {
                                                status = "User registration failed: ${it.message}"
                                            }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .width(200.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Signup",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (status.contains("success", ignoreCase = true)) Color.Green else Color.Red,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    var isFocused by remember {
        mutableStateOf(false)
    }
    // Animate the height of the Box
    val boxHeightFraction by animateFloatAsState(targetValue = if (isFocused) 0.15f else 0.35f)

    // Check if the user is already remembered
    val rememberedUser = sharedPreferences.getString("username", null)
    if (rememberedUser != null) {
        navController.navigate("new_screen")
        return
    }

    GradientBox(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(boxHeightFraction),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 50.dp, bottomEnd = 50.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(100.dp))

                    var username by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var visibility by remember { mutableStateOf(false) }
                    var status by remember { mutableStateOf("") }
                    var rememberMe by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.onFocusChanged { isFocused = it.hasFocus },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_account_circle_24),
                                contentDescription = "User icon",
                                tint = Color.Black
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    val icon = if (visibility)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.onFocusChanged { isFocused = it.hasFocus },
                        trailingIcon = {
                            IconButton(onClick = {
                                visibility = !visibility
                            }) {
                                Icon(
                                    painter = icon,
                                    contentDescription = "visibility icon",
                                    tint = Color.Black
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        visualTransformation = if (visibility) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.lock),
                                contentDescription = "Password icon",
                                tint = Color.Black
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkmarkColor = Color.White,
                                checkedColor = Color.Black,
                                uncheckedColor = Color.Black
                            )
                        )
                        Text(
                            text = "Remember Me",
                            color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            checkUserExists(username) { exists, snapshot ->
                                if (exists && snapshot != null) {
                                    if (checkPasswordMatch(snapshot, password)) {
                                        status = "Login successful"

                                        if (rememberMe) {
                                            with(sharedPreferences.edit()) {
                                                putString("username", username)
                                                apply()
                                            }
                                        }
                                    } else {
                                        status = "Incorrect password"
                                    }
                                } else {
                                    status = "User does not exist"
                                }
                            }
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .width(275.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Login",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (status.contains("success", ignoreCase = true)) Color.Green else Color.Red,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Annotated text with clickable "Create one"
                    val annotatedText = buildAnnotatedString {
                        append("Don't have an account? ")
                        withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                            append("Create one")
                        }
                    }

                    ClickableText(
                        text = annotatedText,
                        onClick = { navController.navigate("signup") },
                        style = LocalTextStyle.current.copy(color = Color.Black)
                    )
                }
            }
        }
    }
}

@Composable
fun Input_task_names(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    val focusManager = LocalFocusManager.current

    // Dialog content
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add New Subject",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            var subjectName by remember { mutableStateOf("") }
            var countryName by remember { mutableStateOf("") }
            var topicName by remember { mutableStateOf("") }
            var className by remember { mutableStateOf("") }

            // Subject Name input
            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Subject Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Subject icon",
                        tint = Color.Black
                    )
                },
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Country Name dropdown
            var expanded by remember { mutableStateOf(false) }
            val countries = listOf(
                "Afghanistan",
                "Albania",
                "Algeria",
                "Andorra",
                "Angola",
                "Antigua and Barbuda",
                "Argentina",
                "Armenia",
                "Australia",
                "Austria",
                "Azerbaijan",
                "Bahamas",
                "Bahrain",
                "Bangladesh",
                "Barbados",
                "Belarus",
                "Belgium",
                "Belize",
                "Benin",
                "Bhutan",
                "Bolivia",
                "Bosnia and Herzegovina",
                "Botswana",
                "Brazil",
                "Brunei",
                "Bulgaria",
                "Burkina Faso",
                "Burundi",
                "Cabo Verde",
                "Cambodia",
                "Cameroon",
                "Canada",
                "Central African Republic",
                "Chad",
                "Chile",
                "China",
                "Colombia",
                "Comoros",
                "Congo",
                "Costa Rica",
                "Croatia",
                "Cuba",
                "Cyprus",
                "Czech Republic",
                "Denmark",
                "Djibouti",
                "Dominica",
                "Dominican Republic",
                "East Timor",
                "Ecuador",
                "Egypt",
                "El Salvador",
                "Equatorial Guinea",
                "Eritrea",
                "Estonia",
                "Eswatini",
                "Ethiopia",
                "Fiji",
                "Finland",
                "France",
                "Gabon",
                "Gambia",
                "Georgia",
                "Germany",
                "Ghana",
                "Greece",
                "Grenada",
                "Guatemala",
                "Guinea",
                "Guinea-Bissau",
                "Guyana",
                "Haiti",
                "Honduras",
                "Hungary",
                "Iceland",
                "India",
                "Indonesia",
                "Iran",
                "Iraq",
                "Ireland",
                "Israel",
                "Italy",
                "Ivory Coast",
                "Jamaica",
                "Japan",
                "Jordan",
                "Kazakhstan",
                "Kenya",
                "Kiribati",
                "Kosovo",
                "Kuwait",
                "Kyrgyzstan",
                "Laos",
                "Latvia",
                "Lebanon",
                "Lesotho",
                "Liberia",
                "Libya",
                "Liechtenstein",
                "Lithuania",
                "Luxembourg",
                "Madagascar",
                "Malawi",
                "Malaysia",
                "Maldives",
                "Mali",
                "Malta",
                "Marshall Islands",
                "Mauritania",
                "Mauritius",
                "Mexico",
                "Micronesia",
                "Moldova",
                "Monaco",
                "Mongolia",
                "Montenegro",
                "Morocco",
                "Mozambique",
                "Myanmar",
                "Namibia",
                "Nauru",
                "Nepal",
                "Netherlands",
                "New Zealand",
                "Nicaragua",
                "Niger",
                "Nigeria",
                "North Korea",
                "North Macedonia",
                "Norway",
                "Oman",
                "Pakistan",
                "Palau",
                "Panama",
                "Papua New Guinea",
                "Paraguay",
                "Peru",
                "Philippines",
                "Poland",
                "Portugal",
                "Qatar",
                "Romania",
                "Russia",
                "Rwanda",
                "Saint Kitts and Nevis",
                "Saint Lucia",
                "Saint Vincent and the Grenadines",
                "Samoa",
                "San Marino",
                "Sao Tome and Principe",
                "Saudi Arabia",
                "Senegal",
                "Serbia",
                "Seychelles",
                "Sierra Leone",
                "Singapore",
                "Slovakia",
                "Slovenia",
                "Solomon Islands",
                "Somalia",
                "South Africa",
                "South Korea",
                "South Sudan",
                "Spain",
                "Sri Lanka",
                "Sudan",
                "Suriname",
                "Sweden",
                "Switzerland",
                "Syria",
                "Taiwan",
                "Tajikistan",
                "Tanzania",
                "Thailand",
                "Togo",
                "Tonga",
                "Trinidad and Tobago",
                "Tunisia",
                "Turkey",
                "Turkmenistan",
                "Tuvalu",
                "Uganda",
                "Ukraine",
                "United Arab Emirates",
                "United Kingdom",
                "United States",
                "Uruguay",
                "Uzbekistan",
                "Vanuatu",
                "Vatican City",
                "Venezuela",
                "Vietnam",
                "Yemen",
                "Zambia",
                "Zimbabwe"
            )

            OutlinedTextField(
                value = countryName,
                onValueChange = { countryName = it },
                label = { Text("Country Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Country icon",
                        tint = Color.Black
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                            contentDescription = "Dropdown icon",
                            tint = Color.Black
                        )
                    }
                },
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = { Text(country) },
                        onClick = {
                            countryName = country
                            expanded = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Topic Name input
            OutlinedTextField(
                value = topicName,
                onValueChange = { topicName = it },
                label = { Text("Topic Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Topic icon",
                        tint = Color.Black
                    )
                },
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Class input
            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                label = { Text("Class") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_class),
                        contentDescription = "Class icon",
                        tint = Color.Black
                    )
                },
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Submit",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
