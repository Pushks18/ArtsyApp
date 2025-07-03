package com.example.artsyapp

import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.artsyapp.model.Artist
import com.example.artsyapp.network.RetrofitInstance
import com.example.artsyapp.network.TokenManager
import com.example.artsyapp.repository.ArtistRepository
import com.example.artsyapp.repository.UserRepository
import com.example.artsyapp.ui.ArtistDetailScreen
import com.example.artsyapp.ui.HomeScreen
import com.example.artsyapp.ui.LoginScreen
import com.example.artsyapp.ui.RegisterScreen
import com.example.artsyapp.ui.SearchBarWithResults
import com.example.artsyapp.ui.theme.ArtsyAppTheme
import com.example.artsyapp.viewmodel.HomeViewModel
import com.example.artsyapp.viewmodel.LoginViewModel
import com.example.artsyapp.viewmodel.RegisterViewModel
import com.example.artsyapp.viewmodel.SearchViewModel
import com.example.artsyapp.viewmodel.UIState

class MainActivity : ComponentActivity() {
    private val searchViewModel by lazy { SearchViewModel(ArtistRepository()) }
    private val homeViewModel by lazy { HomeViewModel(ArtistRepository()) }
    private val loginViewModel by lazy { LoginViewModel(UserRepository()) }
    private val registerViewModel by lazy { RegisterViewModel(UserRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitInstance.init(applicationContext)
        TokenManager.init(applicationContext)
        setContent {
            loginViewModel.validateSession()
            ArtsyAppTheme {  // Use our custom theme here
                AppNavHost(
                    searchViewModel = searchViewModel,
                    homeViewModel = homeViewModel,
                    loginViewModel = loginViewModel,
                    registerViewModel = registerViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    searchViewModel: SearchViewModel,
    homeViewModel: HomeViewModel,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel
) {
    val navController = rememberNavController()
    val loginState by loginViewModel.loginState.collectAsState()
    val isLoggedIn = loginState is UIState.Success

    NavHost(navController = navController, startDestination = "home") {
        // FIXED: Removed duplicate "home" route and consolidated with parameterized route
        composable(
            route = "home?showRegistrationSuccess={showRegistrationSuccess}",
            arguments = listOf(
                navArgument("showRegistrationSuccess") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            // Extract the parameter
            val showRegistrationSuccess = backStackEntry.arguments?.getBoolean("showRegistrationSuccess") ?: false

            LaunchedEffect(Unit) {
                homeViewModel.loadFavorites()
            }

            HomeScreen(
                homeViewModel = homeViewModel,
                loginViewModel = loginViewModel,
                onSearchClick = { navController.navigate("search") },
                onArtistClick = { artistId -> navController.navigate("detail/$artistId") },
                onLoginClick = { navController.navigate("login") },
                showRegistrationSuccess = showRegistrationSuccess
            )
        }

        composable("search") {
            SearchBarWithResults(
                viewModel = searchViewModel,
                onArtistClick = { artistId -> navController.navigate("detail/$artistId") },
                onCloseClick = { navController.popBackStack() },
                isLoggedIn = isLoggedIn,
                favoriteIds = homeViewModel.favorites.collectAsState().value.map { it.id }.toSet(),
                onToggleFavorite = { artist -> homeViewModel.toggleFavorite(artist) }
            )
        }

        composable(
            "detail/{artistId}",
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""

            ArtistDetailScreen(
                artistId = artistId,
                homeViewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToArtist = { newArtistId ->
                    navController.navigate("detail/$newArtistId")
                },
                isFavoriteInitially = homeViewModel.isArtistFavorited(artistId),
                onToggleFavorite = { artist ->
                    homeViewModel.toggleFavorite(artist)
                }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                homeViewModel = homeViewModel,
                onLoginSuccess = { navController.navigate("home") },
                onBack = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = registerViewModel,
                loginViewModel = loginViewModel,
                navController = navController,
                onBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToHome = { navController.navigate("home") }
            )
        }
    }
}