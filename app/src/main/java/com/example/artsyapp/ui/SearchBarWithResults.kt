// app/src/main/java/com/example/artsyapp/ui/SearchBarWithResults.kt
package com.example.artsyapp.ui

import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.ChevronRight
import com.example.artsyapp.ui.theme.ArtsyDarkNavyBlue
import com.example.artsyapp.ui.theme.ArtsyDarkItemBackground
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.artsyapp.R
import com.example.artsyapp.model.Artist
import com.example.artsyapp.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.artsyapp.ui.theme.ArtsyHeaderBackground
import com.example.artsyapp.ui.theme.ArtsyLightBlue
import com.example.artsyapp.ui.theme.LightThemeStatusBarColor
import com.example.artsyapp.ui.theme.DarkThemeStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWithResults(
    viewModel: SearchViewModel,
    onArtistClick: (String) -> Unit,
    onCloseClick: () -> Unit,
    isLoggedIn: Boolean,
    favoriteIds: Set<String>,
    onToggleFavorite: (Artist) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()
    
    // Create a focus requester for the search field
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Define colors for the search UI based on theme
    val searchBarColor = if (isDarkTheme) DarkThemeStatusBarColor else Color(0xFFD1E5FF)
    val searchTextColor = if (isDarkTheme) Color.White else Color.Black
    val searchIconColor = if (isDarkTheme) Color.White else Color.DarkGray
    val searchPlaceholderColor = if (isDarkTheme) Color.LightGray else Color.DarkGray

    // Track whether a search has been performed
    var hasSearched by remember { mutableStateOf(false) }
    
    // Request focus when the screen is first displayed
    LaunchedEffect(Unit) {
        // Small delay to ensure the UI is ready
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top App Bar with search field - theme-appropriate color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(searchBarColor)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Custom search bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search icon on the left
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = searchIconColor,
                        modifier = Modifier.padding(start = 8.dp, end = 12.dp)
                    )

                    // Text field with transparent background
                    BasicTextField(
                        value = query,
                        onValueChange = {
                            viewModel.onQueryChange(it)

                            // Reset hasSearched when query is cleared
                            if (it.isEmpty()) {
                                hasSearched = false
                            }
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = searchTextColor
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                if (query.isNotEmpty()) {
                                    hasSearched = true
                                }
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                            .focusRequester(focusRequester), // Apply the focus requester
                        // Use theme-appropriate cursor color
                        cursorBrush = SolidColor(searchTextColor),
                        decorationBox = { innerTextField ->
                            Box {
                                if (query.isEmpty()) {
                                    Text(
                                        "Search artists...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = searchPlaceholderColor
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    
                    // Close button on the right
                    IconButton(
                        onClick = {
                            viewModel.clearSearch()
                            onCloseClick()
                            focusManager.clearFocus()
                            hasSearched = false
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Clear search",
                            tint = searchIconColor
                        )
                    }
                }
            }

            // Results area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (query.isNotEmpty() && results.isEmpty() && hasSearched) {
                    // No results found message - only shows after search is performed
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                            .background(
                                if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFF5F5F5),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Results Found",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDarkTheme) Color.White else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (results.isNotEmpty()) {
                    // Results list - only shown when we have results
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(results) { artist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { onArtistClick(artist.id) },
                                shape = RoundedCornerShape(12.dp),
                                // Blue-tinted card in dark mode
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkTheme) ArtsyDarkItemBackground else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                                    // Image covers the full card
                                    if (artist.imageUrl.isNullOrBlank()) {
                                        Image(
                                            painter = painterResource(R.drawable.artsy_logo),
                                            contentDescription = "Placeholder",
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        AsyncImage(
                                            model = artist.imageUrl,
                                            placeholder = painterResource(R.drawable.artsy_logo),
                                            error = painterResource(R.drawable.artsy_logo),
                                            contentDescription = artist.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        )
                                    }

                                    // Favorite button at the top right
                                    if (isLoggedIn) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                                .size(32.dp)
                                                .background(
                                                    color = if (isDarkTheme) 
                                                        Color(0xFF3A5995).copy(alpha = 0.9f) 
                                                    else 
                                                        Color(0xFFAEBBC5).copy(alpha = 0.9f),
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    onToggleFavorite(artist)
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            if (favoriteIds.contains(artist.id))
                                                                "Removed from Favorites"
                                                            else
                                                                "Added to Favorites"
                                                        )
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (favoriteIds.contains(artist.id))
                                                    Icons.Outlined.Star
                                                else
                                                    Icons.Outlined.StarBorder,
                                                contentDescription = "Favorite",
                                                tint = if (favoriteIds.contains(artist.id))
                                                    if (isDarkTheme) Color.White else Color.Black
                                                else
                                                    if (isDarkTheme) Color.White else Color(0xFF3F51B5),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    // Artist name with transparent background overlaid at the bottom
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .background(
                                                color = (if (isDarkTheme) DarkThemeStatusBarColor else LightThemeStatusBarColor).copy(alpha = if (isDarkTheme) 0.85f else 0.7f),
                                                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                                            )
                                            .padding(vertical = 12.dp, horizontal = 16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = artist.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = if (isDarkTheme) Color.White else Color.Black
                                            )

                                            Icon(
                                                imageVector = Icons.Outlined.ChevronRight,
                                                contentDescription = "View details",
                                                tint = if (isDarkTheme) Color.White else Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // When neither condition is met (no search performed yet or empty query),
                // nothing is displayed in the results area
            }
        }
    }
}