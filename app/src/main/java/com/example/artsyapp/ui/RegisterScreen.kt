package com.example.artsyapp.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.artsyapp.viewmodel.LoginViewModel
import com.example.artsyapp.viewmodel.RegisterViewModel
import com.example.artsyapp.viewmodel.UIState
import kotlinx.coroutines.delay
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import com.example.artsyapp.ui.theme.ArtsyDarkPrimary
import com.example.artsyapp.ui.theme.LightThemeStatusBarColor
import com.example.artsyapp.ui.theme.DarkThemeStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    loginViewModel: LoginViewModel,
    navController: NavHostController,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var fullNameFocused by remember { mutableStateOf(false) }
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    var fullNameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    var emailFormatValid by remember { mutableStateOf(true) }
    var emailAlreadyExists by remember { mutableStateOf(false) }

    val emailRegex = Regex("^[A-Za-z](.*)([@])(.+)(\\.)(.+)")
    val registerState by viewModel.registerState.collectAsState()
    val loginState by loginViewModel.loginState.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()

    // Function to validate email format
    fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() && emailRegex.matches(email)
    }

    // Monitor registration state changes
    LaunchedEffect(registerState) {
        Log.d("RegisterScreen", "Register state changed to: $registerState")
        when (registerState) {
            is UIState.Success -> {
                Log.d("RegisterScreen", "Registration successful, validating session")

                // First validate the session
                loginViewModel.validateSession()

                // Longer delay to ensure session is validated properly
                delay(500)

                // Then explicitly fetch and refresh the user profile data
                loginViewModel.refreshUserProfile()

                Log.d("RegisterScreen", "Navigating to home screen")

                // Clear backstack and navigate to home with the registration success flag
                navController.popBackStack()
                navController.navigate("home?showRegistrationSuccess=true")
            }
            is UIState.Error -> {
                val error = (registerState as UIState.Error).message
                Log.e("RegisterScreen", "Registration error: $error")
                if (error.contains("exists", ignoreCase = true)) {
                    emailAlreadyExists = true
                }
            }
            else -> {}
        }
    }

    // Also monitor login state as a backup
    LaunchedEffect(loginState) {
        Log.d("RegisterScreen", "Login state changed to: $loginState")
        if (loginState is UIState.Success) {
            Log.d("RegisterScreen", "Login state is Success, navigating to home")
            // User is logged in, navigate to home with the registration success flag
            navController.popBackStack()
            navController.navigate("home?showRegistrationSuccess=true")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkThemeStatusBarColor else LightThemeStatusBarColor,
                    titleContentColor = if (isDarkTheme) Color.White else Color.Black,
                    navigationIconContentColor = if (isDarkTheme) Color.White else Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    fullNameTouched = true
                },
                label = { Text("Enter full name") },
                isError = (fullNameFocused || fullNameTouched) && fullName.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        fullNameFocused = focusState.isFocused
                        if (focusState.isFocused) {
                            fullNameTouched = true
                        }
                    }
            )
            if ((fullNameFocused || fullNameTouched) && fullName.isBlank()) {
                Text(
                    "Full name cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailTouched = true
                    emailFormatValid = validateEmail(it)
                    emailAlreadyExists = false
                },
                label = { Text("Enter email") },
                isError = (emailFocused || emailTouched) &&
                        (email.isBlank() || !emailFormatValid || emailAlreadyExists),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        emailFocused = focusState.isFocused
                        if (focusState.isFocused) {
                            emailTouched = true
                            emailFormatValid = validateEmail(email)
                        }
                    }
            )
            if ((emailFocused || emailTouched) && email.isBlank()) {
                Text(
                    "Email cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp),
                    textAlign = TextAlign.Start
                )
            } else if ((emailFocused || emailTouched) && !emailFormatValid && email.isNotEmpty()) {
                Text(
                    "Invalid email format",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp),
                    textAlign = TextAlign.Start
                )
            } else if (emailAlreadyExists) {
                Text(
                    "Email already exists",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordTouched = true
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = (passwordFocused || passwordTouched) && password.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        passwordFocused = focusState.isFocused
                        if (focusState.isFocused) {
                            passwordTouched = true
                        }
                    }
            )
            if ((passwordFocused || passwordTouched) && password.isBlank()) {
                Text(
                    "Password cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    fullNameTouched = true
                    emailTouched = true
                    passwordTouched = true
                    emailFormatValid = validateEmail(email.trim())

                    if (
                        fullName.trim().isNotEmpty() &&
                        email.trim().isNotEmpty() &&
                        password.trim().isNotEmpty() &&
                        emailFormatValid
                    ) {
                        Log.d("RegisterScreen", "Attempting to register: ${email.trim()}")
                        viewModel.signUp(
                            fullName.trim(),
                            email.trim(),
                            password.trim(),
                            null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = registerState !is UIState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme) ArtsyDarkPrimary else Color(0xFF2C4170),
                    contentColor = Color.White
                )
            ) {
                if (registerState is UIState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Register",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Already have an account? ",
                    color = if (isDarkTheme) Color.White else Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = onNavigateToLogin,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        "Login",
                        color = Color(0xFF3A8BFF),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}