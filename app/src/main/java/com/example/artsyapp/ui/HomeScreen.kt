@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package com.example.artsyapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.artsyapp.model.User
import com.example.artsyapp.ui.theme.ArtsyDarkNavyBlue
import com.example.artsyapp.ui.theme.ArtsyDarkItemBackground
import com.example.artsyapp.ui.theme.ArtsyDarkPrimary
import com.example.artsyapp.ui.theme.LightThemeStatusBarColor
import com.example.artsyapp.ui.theme.DarkThemeStatusBarColor
import com.example.artsyapp.util.getRelativeTime
import com.example.artsyapp.viewmodel.HomeViewModel
import com.example.artsyapp.viewmodel.LoginViewModel
import com.example.artsyapp.viewmodel.UIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    loginViewModel: LoginViewModel,
    onSearchClick: () -> Unit,
    onArtistClick: (String) -> Unit,
    onLoginClick: () -> Unit,
    showRegistrationSuccess: Boolean = false
) {
    val favorites by homeViewModel.favorites.collectAsState()
    val userState by loginViewModel.loginState.collectAsState()
    val isLoggedIn = userState is UIState.Success
    val user = if (isLoggedIn) (userState as UIState.Success<User>).data else null
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // Define colors for the top bar based on theme
    val topBarColor = if (isDarkTheme) DarkThemeStatusBarColor else Color(0xFFD1E5FF)
    val topBarTextColor = if (isDarkTheme) Color.White else Color.Black

    // For dropdown menu
    var showProfileMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // SnackbarHostState for showing messages
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        homeViewModel.loadFavorites()
    }

    // Show registration success message when navigating from registration
    LaunchedEffect(showRegistrationSuccess) {
        if (showRegistrationSuccess) {
            snackbarHostState.showSnackbar("Registered successfully")
        }
    }

    // Date display
    val currentDate = remember {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        dateFormat.format(Date())
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(if (isDarkTheme) Color(0xFF222222) else MaterialTheme.colorScheme.background)
        ) {
            // Top App Bar with increased height - uniform light blue color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(topBarColor)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Artist Search",
                    style = MaterialTheme.typography.titleLarge,
                    color = topBarTextColor,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = topBarTextColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Profile icon and dropdown
                    Box {
                        IconButton(
                            onClick = {
                                if (isLoggedIn) {
                                    showProfileMenu = true
                                } else {
                                    onLoginClick()
                                }
                            }
                        ) {
                            if (isLoggedIn && user?.profileImageURL != null) {
                                // Show user avatar if available
                                AsyncImage(
                                    model = user.profileImageURL,
                                    contentDescription = "Profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                // Show default icon
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = "Profile",
                                    tint = topBarTextColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Simple dropdown menu - styled like the screenshot
                        DropdownMenu(
                            expanded = showProfileMenu,
                            onDismissRequest = { showProfileMenu = false },
                            modifier = Modifier
                                .width(150.dp)
                                .background(if (isDarkTheme) Color(0xFF1E1E1E) else Color.White)
                        ) {
                            // Logout option
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Log out",
                                        color = if (isDarkTheme) Color.White else Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    scope.launch {
                                        loginViewModel.signOut()
                                        showProfileMenu = false
                                    }
                                },
                                modifier = Modifier.height(40.dp)
                            )

                            Divider(
                                color = Color.LightGray,
                                thickness = 1.dp
                            )

                            // Delete account option
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Delete account",
                                        color = if (isDarkTheme) Color(0xFFFF5252) else Color.Red,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    // Delete account and show confirmation message
                                    scope.launch {
                                        homeViewModel.deleteUser()
                                        loginViewModel.signOut() // Make sure to sign out from LoginViewModel too
                                        showProfileMenu = false

                                        // Show the account deleted confirmation message
                                        snackbarHostState.showSnackbar("Account deleted")
                                    }
                                },
                                modifier = Modifier.height(40.dp)
                            )

                            Divider(
                                color = if (isDarkTheme) Color(0xFF444444) else Color.LightGray,
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }

            // Date - light gray in dark mode
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkTheme) Color.LightGray else Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            // Favorites header - blue-tinted in dark mode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDarkTheme) Color(0xFF2E2E2E) else Color(0xFFF5F5F5)
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }

            // Content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!isLoggedIn) {
                        // Login button - dark blue in dark mode
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onLoginClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkTheme) ArtsyDarkPrimary else Color(0xFF2C4170)
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .width(220.dp)
                                .height(48.dp)
                        ) {
                            Text(
                                "Log in to see favorites",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else if (favorites.isEmpty()) {
                        // No favorites message - dark gray in dark mode
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(
                                    if (isDarkTheme) Color(0xFF2A2A2A) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No favorites",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkTheme) Color.LightGray else Color.Black
                            )
                        }
                    } else {
                        // Favorites list - dark mode styling
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            items(favorites) { artist ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable { onArtistClick(artist.id) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isDarkTheme) ArtsyDarkItemBackground else MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                artist.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (isDarkTheme) Color.White else Color.Black
                                            )
                                            Text(
                                                artist.nationality ?: "",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isDarkTheme) Color.LightGray else Color.Gray
                                            )
                                            if (artist.birthday != null) {
                                                Text(
                                                    artist.birthday,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isDarkTheme) Color.LightGray else Color.Gray
                                                )
                                            }
                                        }
                                        Text(
                                            text = artist.relativeTime,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isDarkTheme) Color.LightGray else Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Powered by Artsy text
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Powered by Artsy",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            color = if (isDarkTheme) Color.Gray else Color.Gray
                        ),
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.artsy.net/"))
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}