package com.example.artsyapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// A tiny UI‐only model so your Preview + UI never break
data class ArtistUi(
    val name: String,
    val imageUrl: String? = null
)

@Composable
fun FavoritesList(
    favorites: List<ArtistUi>,
    onArtistClick: (ArtistUi) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(favorites) { artist ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(vertical = 4.dp)
                    .clickable { onArtistClick(artist) },
                shape     = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // load from URL, or show gray box + person icon
                    val url = artist.imageUrl.orEmpty()
                    if (url.isNotBlank()) {
                        AsyncImage(
                            model               = url,
                            contentDescription  = artist.name,
                            contentScale        = ContentScale.Crop,
                            modifier            = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray)
                        ) {
                            Icon(
                                imageVector        = Icons.Outlined.Person,
                                contentDescription = null,
                                modifier           = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center),
                                tint               = Color.DarkGray
                            )
                        }
                    }

                    // a semi-transparent overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )

                    // bottom row: name + arrow
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text  = artist.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector        = Icons.Outlined.ArrowForward,
                            contentDescription = "View",
                            tint               = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesListPreview() {
    Surface {
        FavoritesList(
            favorites = listOf(
                ArtistUi(name = "Pablo Picasso", imageUrl = null),
                ArtistUi(name = "Claude Monet",   imageUrl = null)
            ),
            onArtistClick = {}
        )
    }
}
