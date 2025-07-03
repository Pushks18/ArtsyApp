package com.example.artsyapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.artsyapp.viewmodel.LoginViewModel
import com.example.artsyapp.viewmodel.UIState
import kotlinx.coroutines.launch
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import com.example.artsyapp.viewmodel.HomeViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import com.example.artsyapp.ui.theme.ArtsyDarkPrimary
import com.example.artsyapp.ui.theme.LightThemeStatusBarColor
import com.example.artsyapp.ui.theme.DarkThemeStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var emailFormatValid by remember { mutableStateOf(true) }
    var showLoginError by remember { mutableStateOf(false) }

    val state by viewModel.loginState.collectAsState()
    val emailRegex = Regex("^[A-Za-z](.*)([@])(.+)(\\.)(.+)")
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(state) {
        when (state) {
            is UIState.Success -> {
                homeViewModel.loadFavorites()
                onLoginSuccess()
            }
            is UIState.Error -> {
                showLoginError = true
            }
            else -> { /* do nothing */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") },
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
                value = email,
                onValueChange = {
                    email = it
                    emailTouched = true
                },
                label = { Text("Email") },
                isError = emailTouched && (!emailFormatValid || email.isBlank()),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) emailTouched = true
                    }
            )
            if (emailTouched && email.isBlank()) {
                Text("Email cannot be empty", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start))
            } else if (emailTouched && !emailFormatValid) {
                Text("Invalid email format", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start))
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordTouched = true
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordTouched && password.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) passwordTouched = true
                    }
            )
            if (passwordTouched && password.isBlank()) {
                Text("Password cannot be empty", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start))
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    emailTouched = true
                    passwordTouched = true
                    emailFormatValid = emailRegex.matches(email)
                    showLoginError = false
                    if (email.isNotBlank() && password.isNotBlank() && emailFormatValid) {
                        viewModel.signIn(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is UIState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme) ArtsyDarkPrimary else Color(0xFF2C4170),
                    contentColor = Color.White
                )
            ) {
                if (state is UIState.Loading)
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                else
                    Text(
                        "Login",
                        color = Color.White
                    )
            }

            if (showLoginError) {
                Text(
                    "Username or Password is incorrect",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Don't have an account yet? ",
                    color = if (isDarkTheme) Color.White else Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = onNavigateToRegister,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        "Register",
                        color = Color(0xFF3A8BFF),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}