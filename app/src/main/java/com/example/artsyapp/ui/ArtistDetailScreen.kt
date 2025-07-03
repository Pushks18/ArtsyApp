package com.example.artsyapp.ui

import android.util.Log
import androidx.compose.ui.res.painterResource
import com.example.artsyapp.R
import com.example.artsyapp.ui.theme.ArtsyDarkNavyBlue
import com.example.artsyapp.ui.theme.ArtsyDarkItemBackground
import com.example.artsyapp.ui.theme.ArtsyDarkPrimary
import com.example.artsyapp.ui.theme.ArtsyDarkBackground
import com.example.artsyapp.ui.theme.ArtsyDarkTabBackground
import com.example.artsyapp.ui.theme.ArtsyVeryDarkBlue
import com.example.artsyapp.ui.theme.ArtsyDarkButtonBlue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.artsyapp.model.Artist
import com.example.artsyapp.model.Artwork
import com.example.artsyapp.model.Gene
import com.example.artsyapp.viewmodel.ArtistDetailViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artsyapp.repository.ArtistRepository
import com.example.artsyapp.viewmodel.HomeViewModel
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.artsyapp.ui.theme.LightThemeStatusBarColor
import com.example.artsyapp.ui.theme.DarkThemeStatusBarColor
import java.util.regex.Pattern
import kotlinx.coroutines.delay

/**
 * Replace various dash-like or mis-encoded characters with a plain hyphen-minus ('-').
 */
fun String.normalizeDashes(): String {
    // Common dash variants and mis-decoded control codes
    val dashVariants = setOf(
        '\u0096', // often a mis-decoded en-dash
        '\u0097', // often a mis-decoded em-dash
        '\u2010', // hyphen
        '\u2011', // non-breaking hyphen
        '\u2012', // figure dash
        '\u2013', // en-dash
        '\u2014', // em-dash
        '\u037F'  // Greek question mark
    )
    return buildString {
        for (ch in this@normalizeDashes) {
            if (ch in dashVariants) append('-')
            else append(ch)
        }
    }
}

/**
 * Helper function to fix encoding issues with special characters
 * Particularly for hyphens, dashes, apostrophes, and quotes
 */
private fun fixEncoding(text: String): String {
    if (text.isBlank()) return text

    // First apply our standard text replacements
    var fixedText = text
        // Fix common encoding issues
        .replace("â€", "—")  // em dash
        .replace("â€", "–")  // en dash
        .replace("â€-", "-")  // regular hyphen
        .replace("\\u2013", "–")  // en dash unicode
        .replace("\\u2014", "—")  // em dash unicode
        .replace("\\u0096", "–")  // another en dash variation
        .replace("\\x96", "–")    // another en dash variation
        .replace("â€™", "'")  // apostrophe
        .replace("â€œ", """)  // left double quote
        .replace("â€", """)   // right double quote
        .replace("â€˜", "'")  // left single quote
        .replace("â€™", "'")  // right single quote
        .replace("â€¦", "…")  // ellipsis
        .replace("â€¢", "•")  // bullet
        .replace("â€¬", "€")  // euro
        .replace("â€š", "‚")  // single low quote
        .replace("Â", "")     // non-breaking space

        // Fix problematic Windows-1252 characters
        .replace("\u0080", "€")  // Euro sign
        .replace("\u0082", "‚")  // Single low quote
        .replace("\u0083", "ƒ")  // Florin/function
        .replace("\u0084", "„")  // Double low quote
        .replace("\u0085", "…")  // Ellipsis
        .replace("\u0086", "†")  // Dagger
        .replace("\u0087", "‡")  // Double dagger
        .replace("\u0088", "ˆ")  // Circumflex accent
        .replace("\u0089", "‰")  // Per mille sign
        .replace("\u008A", "Š")  // Capital S with caron
        .replace("\u008B", "‹")  // Left single guillemet
        .replace("\u008C", "Œ")  // Capital OE ligature
        .replace("\u008E", "Ž")  // Capital Z with caron
        .replace("\u0091", "'")  // Left single quote
        .replace("\u0092", "'")  // Right single quote
        .replace("\u0093", """)  // Left double quote
        .replace("\u0094", """)  // Right double quote
        .replace("\u0095", "•")  // Bullet
        .replace("\u0096", "–")  // En dash
        .replace("\u0097", "—")  // Em dash
        .replace("\u0098", "˜")  // Small tilde
        .replace("\u0099", "™")  // Trademark sign
        .replace("\u009A", "š")  // Small s with caron
        .replace("\u009B", "›")  // Right single guillemet
        .replace("\u009C", "œ")  // Small oe ligature
        .replace("\u009E", "ž")  // Small z with caron
        .replace("\u009F", "Ÿ")  // Capital Y with diaeresis

        // Fix for other common special characters
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")

        // Fix square brackets in URLs
        .replace("\\[", "[")
        .replace("\\]", "]")

    // Use regex to handle any other potential encoding issues
    fixedText = fixedText.replace(Regex("\\\\u([0-9a-fA-F]{4})")) { result ->
        result.groupValues[1].toInt(16).toChar().toString()
    }
    
    // Apply dash normalization
    return fixedText.normalizeDashes()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistId: String,
    viewModel: ArtistDetailViewModel = viewModel(),
    homeViewModel: HomeViewModel,
    onBack: () -> Unit,
    onNavigateToArtist: (String) -> Unit,
    isFavoriteInitially: Boolean,
    onToggleFavorite: (Artist) -> Unit
) {
    val artist by viewModel.artist.collectAsState()
    val artworks by viewModel.artworks.collectAsState()
    val similarArtists by viewModel.similar.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoggedIn = homeViewModel.isLoggedIn()
    val isDarkTheme = isSystemInDarkTheme()

    var isFavorite by remember { mutableStateOf(isFavoriteInitially) }

    // Load artist when screen opens
    LaunchedEffect(artistId) {
        Log.d("ArtistDetailScreen", "Loading artist with ID: $artistId")
        viewModel.loadArtist(artistId)
    }

    // Keep local favorite state in sync with fetched artist
    LaunchedEffect(artist) {
        isFavorite = artist?.id?.let { homeViewModel.isArtistFavorited(it) } ?: false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        artist?.name.orEmpty()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Only show the star icon if user is logged in
                    if (isLoggedIn && artist != null) {
                        IconButton(onClick = {
                            scope.launch {
                                artist?.let { nonNullArtist ->
                                    onToggleFavorite(nonNullArtist)
                                    isFavorite = !isFavorite
                                    snackbarHostState.showSnackbar(
                                        if (isFavorite) "Added to Favorites" else "Removed from Favorites"
                                    )
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (isFavorite) "Unfavorite" else "Favorite"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkThemeStatusBarColor else LightThemeStatusBarColor,
                    titleContentColor = if (isDarkTheme) Color.White else Color.Black,
                    navigationIconContentColor = if (isDarkTheme) Color.White else Color.Black,
                    actionIconContentColor = if (isDarkTheme) Color.White else Color.Black
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = if (isDarkTheme) ArtsyDarkBackground else MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(if (isDarkTheme) ArtsyDarkBackground else MaterialTheme.colorScheme.background)
        ) {
            if (artist == null) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center),
                    color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                )
            } else {
                Column(Modifier.fillMaxSize()) {
                    val tabs = if (isLoggedIn) {
                        listOf("Details", "Artworks", "Similar")
                    } else {
                        listOf("Details", "Artworks")
                    }
                    var selectedTab by remember { mutableStateOf(0) }

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = if (isDarkTheme) ArtsyDarkTabBackground else MaterialTheme.colorScheme.surface,
                        contentColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 2.dp,
                                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        color = if (isDarkTheme) ArtsyDarkButtonBlue else Color.Black
                                    )
                                },
                                icon = {
                                    Icon(
                                        when (index) {
                                            0 -> Icons.Outlined.Info
                                            1 -> Icons.Outlined.AccountBox
                                            else -> Icons.Outlined.PersonSearch
                                        },
                                        contentDescription = null,
                                        tint = if (isDarkTheme) ArtsyDarkButtonBlue else Color.Black
                                    )
                                }
                            )
                        }
                    }

                    when (tabs[selectedTab]) {
                        "Details" -> DetailsTab(artist!!, isDarkTheme)
                        "Artworks" -> ArtworksTab(artworks, isDarkTheme)
                        "Similar" -> SimilarTab(
                            artistId = artist!!.id,
                            similarArtists = similarArtists,
                            onArtistClick = onNavigateToArtist,
                            favoriteIds = homeViewModel.favorites.collectAsState().value.map { it.id }.toSet(),
                            onToggleFavorite = onToggleFavorite,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsTab(artist: Artist, isDarkTheme: Boolean) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Artist name - large, bold, and centered
        Text(
            text = artist.name,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color.Black
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 4.dp)
        )

        // For artist years, preserve original format but ensure proper en dash
        // This approach handles special cases like Claude Monet's "French, 18 – 0 – 19 – 6"
        val nationality = artist.nationality ?: "Unknown"
        val birth = artist.birthday ?: ""
        val death = artist.deathday ?: ""
        
        // Special case for missing dashes in date ranges (e.g., between years and days)
        // Format: "Claude Monet (14 November 1840  5 December 1926)"
        val datePattern = Pattern.compile("(\\d{4})\\s{2,}(\\d{1,2})")
        
        // Fix the date text if it contains a year followed by multiple spaces and then a day
        var fixedDescription = artist.description ?: ""
        if (fixedDescription.isNotEmpty()) {
            val matcher = datePattern.matcher(fixedDescription)
            if (matcher.find()) {
                fixedDescription = matcher.replaceAll("${matcher.group(1)}-${matcher.group(2)}")
            }
            fixedDescription = fixedDescription.normalizeDashes()
        }
        
        // Keep the original text intact in case of special formatting
        val yearsText = if (!birth.isBlank() && !death.isBlank()) {
            "$nationality, $birth - $death" 
        } else if (!birth.isBlank()) {
            "$nationality, $birth"
        } else {
            nationality
        }
        
        Text(
            text = yearsText,
            style = MaterialTheme.typography.titleMedium.copy(
                color = if (isDarkTheme) Color.White else Color.Black
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Artist biography - regular paragraph text with proper spacing
        if (fixedDescription.isNotBlank()) {
            Text(
                text = fixedDescription,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkTheme) Color.White else Color.Black
                ),
                modifier = Modifier.padding(bottom = 32.dp)  // Increased bottom padding for better scroll experience
            )
        } else {
            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)  // Increased bottom padding for better scroll experience
            )
        }
    }
}

@Composable
private fun ArtworksTab(artworks: List<Artwork>, isDarkTheme: Boolean) {
    var showCategoryDialog by remember { mutableStateOf(false) }
    var selectedArtwork by remember { mutableStateOf<Artwork?>(null) }

    // State variable for tracking loading state, with an initial value of true to show spinner first
    var isLoading by remember { mutableStateOf(true) }
    
    // Track if this is the first load
    var isFirstLoad by remember { mutableStateOf(true) }

    LaunchedEffect(artworks) {
        if (isFirstLoad) {
            // Small delay to simulate loading and ensure spinner shows
            delay(500)
            isFirstLoad = false
        }
        // Set loading to false when artworks arrive
        isLoading = false
        Log.d("ArtworksTab", "Received ${artworks.size} artworks")
        if (artworks.isNotEmpty()) {
            Log.d("ArtworksTab", "First artwork: ${artworks[0].title}, ID: ${artworks[0].id}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background)
    ) {
        // Show loading indicator while loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                )
            }
        }
        // Show "No Artworks" message when loaded but empty
        else if (artworks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE3F2FD))
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Artworks",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }
        }
        // Show actual artwork grid when loaded and not empty
        else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(artworks.size) { index ->
                    val art = artworks[index]

                    // Fix encoding in artwork title and date
                    val fixedTitle = fixEncoding(art.title ?: "Untitled")
                    val fixedDate = fixEncoding(art.date ?: "Unknown year")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedArtwork = art
                                showCategoryDialog = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Image covers the full card without any padding or borders
                            AsyncImage(
                                model = art.imageUrl,
                                contentDescription = fixedTitle,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            
                            // Caption area at the bottom
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        color = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF5F5F5),
                                    )
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = fixedTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isDarkTheme) Color.White else Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
                                )
                                
                                if (fixedDate.isNotBlank()) {
                                    Text(
                                        text = fixedDate,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDarkTheme) Color.LightGray else Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                    )
                                }
                                
                                Button(
                                    onClick = {
                                        selectedArtwork = art
                                        showCategoryDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDarkTheme) ArtsyDarkButtonBlue else Color(0xFF2C4170)
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .height(48.dp)
                                        .width(180.dp)
                                ) {
                                    Text(
                                        "View categories",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCategoryDialog && selectedArtwork != null) {
        CategoryDialog(
            artworkId = selectedArtwork!!.id,
            onDismiss = { showCategoryDialog = false },
            isDarkTheme = isDarkTheme
        )
    }
}

@Composable
fun CategoryDialog(artworkId: String, onDismiss: () -> Unit, isDarkTheme: Boolean) {
    val scope = rememberCoroutineScope()
    var genes by remember { mutableStateOf<List<Gene>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedIndex by remember { mutableStateOf(0) }

    // Dark blue color for text container
    val textBgColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF5F5F5)
    // Gray color for spacing/gaps
    val spacingColor = Color.Gray.copy(alpha = 0.2f)

    LaunchedEffect(artworkId) {
        scope.launch {
            val repo = ArtistRepository()
            genes = repo.getGenes(artworkId)
            loading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        },
        text = {
            if (loading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 5.dp,
                        color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                    )
                }
            } else if (genes.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No categories found.",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                val currentGene = genes[selectedIndex]
                val prevIndex = if (selectedIndex > 0) selectedIndex - 1 else genes.lastIndex
                val nextIndex = if (selectedIndex < genes.lastIndex) selectedIndex + 1 else 0

                // Fix encoding in gene text
                val fixedGeneName = fixEncoding(currentGene.name)
                var fixedGeneDescription = fixEncoding(currentGene.description ?: "No description available")
                
                // Fix specific issues with square brackets in URLs like "[Alberto Giacometti](/artist/alberto-giacometti)"
                fixedGeneDescription = fixedGeneDescription.replace(Regex("\\[(.*?)\\]\\(/(.*?)\\)")) { result ->
                    val text = result.groupValues[1]
                    val url = result.groupValues[2]
                    "$text"  // Just show the text without the URL formatting
                }

                // Main content with absolute positioned navigation buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Content column
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Gray background for spacing and borders
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(spacingColor) // Background for gaps
                        ) {
                            // Content column
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Image section with spacing
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left image slice with spacing
                                    if (genes.size > 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(12.dp)
                                                .fillMaxHeight()
                                                .padding(end = 2.dp) // Gap on right
                                        ) {
                                            AsyncImage(
                                                model = genes[prevIndex].thumbnailUrl,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(
                                                        topStart = 12.dp,
                                                        bottomStart = 0.dp // No rounding at bottom
                                                    ))
                                            )
                                        }
                                    }

                                    // Main image with spacing
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .padding(horizontal = 2.dp) // Gaps on both sides
                                    ) {
                                        AsyncImage(
                                            model = currentGene.thumbnailUrl,
                                            contentDescription = fixedGeneName,
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.Center)
                                        )
                                    }

                                    // Right image slice with spacing
                                    if (genes.size > 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(12.dp)
                                                .fillMaxHeight()
                                                .padding(start = 2.dp) // Gap on left
                                        ) {
                                            AsyncImage(
                                                model = genes[nextIndex].thumbnailUrl,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(
                                                        topEnd = 12.dp,
                                                        bottomEnd = 0.dp // No rounding at bottom
                                                    ))
                                            )
                                        }
                                    }
                                }

                                // Dark blue background for text section (entire width)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(textBgColor) // Full width dark blue background
                                ) {
                                    // Text section with spacing
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Left spacer - only 2dp for the gap
                                        if (genes.size > 1) {
                                            Spacer(
                                                modifier = Modifier
                                                    .width(2.dp)
                                            )
                                        }

                                        // Text content
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            // Category title
                                            Text(
                                                text = fixedGeneName,
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 22.sp,
                                                    color = if (isDarkTheme) Color.White else Color.Black
                                                ),
                                                modifier = Modifier.padding(bottom = 12.dp)
                                            )

                                            // Description with scrolling
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp) // Reduced height
                                            ) {
                                                Text(
                                                    text = fixedGeneDescription,
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontSize = 16.sp,
                                                        lineHeight = 24.sp,
                                                        color = if (isDarkTheme) Color.White else Color.Black
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .verticalScroll(rememberScrollState())
                                                )
                                            }
                                        }

                                        // Right spacer - only 2dp for the gap
                                        if (genes.size > 1) {
                                            Spacer(
                                                modifier = Modifier
                                                    .width(2.dp)
                                            )
                                        }
                                    }
                                }

                                // Bottom corners
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(textBgColor) // Use same dark blue background
                                ) {
                                    // Left corner - just for rounded corner
                                    if (genes.size > 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(12.dp)
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(
                                                    bottomStart = 12.dp
                                                ))
                                                .background(textBgColor) // Dark blue
                                        )
                                    }

                                    // Middle spacer
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .background(textBgColor) // Dark blue
                                    )

                                    // Right corner - just for rounded corner
                                    if (genes.size > 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(12.dp)
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(
                                                    bottomEnd = 12.dp
                                                ))
                                                .background(textBgColor) // Dark blue
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Left navigation button
                    Box(
                        modifier = Modifier
                            .offset(x = (-30).dp, y = 220.dp)
                            .size(48.dp)
                            .clickable {
                                selectedIndex = if (selectedIndex > 0)
                                    selectedIndex - 1
                                else
                                    genes.lastIndex
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "Previous",
                            tint = if (isDarkTheme) Color.White else Color.DarkGray,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Right navigation button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 30.dp, y = 220.dp)
                            .size(48.dp)
                            .clickable {
                                selectedIndex = if (selectedIndex < genes.lastIndex)
                                    selectedIndex + 1
                                else
                                    0
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight,
                            contentDescription = "Next",
                            tint = if (isDarkTheme) Color.White else Color.DarkGray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme) ArtsyDarkButtonBlue else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .width(120.dp)
                    .height(48.dp)
            ) {
                Text(
                    "Close",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
            }
        },
        dismissButton = null
    )
}

@Composable
private fun SimilarTab(
    artistId: String,
    similarArtists: List<Artist>,
    onArtistClick: (String) -> Unit,
    favoriteIds: Set<String>,
    onToggleFavorite: (Artist) -> Unit,
    isDarkTheme: Boolean
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (similarArtists.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                )
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(similarArtists) { artist ->
                    // Fix encoding in artist name
                    val fixedArtistName = fixEncoding(artist.name)

                    SimilarArtistItem(
                        artist = artist.copy(name = fixedArtistName),
                        onClick = { onArtistClick(artist.id) },
                        isFavorite = favoriteIds.contains(artist.id),
                        onToggleFavorite = {
                            onToggleFavorite(artist)
                            scope.launch {
                                val message = if (favoriteIds.contains(artist.id)) {
                                    "Removed from Favorites"
                                } else {
                                    "Added to Favorites"
                                }
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun SimilarArtistItem(
    artist: Artist,
    onClick: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Artist Image Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                // Artist Image
                if (!artist.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = artist.imageUrl,
                        contentDescription = artist.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isDarkTheme) Color(0xFF2A2A2A) else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Favorite Button with light blue circular background
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(
                            color = if (isDarkTheme) Color(0xFF1A2747).copy(alpha = 0.9f) else Color(0xFFE3F2FD).copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                        .clickable(onClick = onToggleFavorite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite)
                            if (isDarkTheme) Color.White else Color.Black
                        else
                            if (isDarkTheme) Color.White else Color(0xFF3F51B5),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Artist Name Container - Transparent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                        contentDescription = "View artist",
                        tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}