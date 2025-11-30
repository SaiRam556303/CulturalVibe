package uk.ac.tees.mad.culturalvibe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.culturalvibe.ui.AppViewModel
import uk.ac.tees.mad.culturalvibe.ui.screens.AuthScreen
import uk.ac.tees.mad.culturalvibe.ui.screens.EventDetailsScreen
import uk.ac.tees.mad.culturalvibe.ui.screens.HomeScreen
import uk.ac.tees.mad.culturalvibe.ui.screens.RegistrationScreen
import uk.ac.tees.mad.culturalvibe.ui.screens.SplashScreen
import uk.ac.tees.mad.culturalvibe.ui.theme.CulturalVibeTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CulturalVibeTheme {
                App()
            }
        }
    }
}

sealed class NavComponents(val route : String){
    object SplashScreen : NavComponents("splash")
    object AuthScreen : NavComponents("auth")
    object HomeScreen : NavComponents("home")
    object EventDetails : NavComponents("eventDetails/{eventId}") {
        fun passId(eventId: Int) = "eventDetails/$eventId"
    }
    object RegistrationScreen : NavComponents("registration")
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel : AppViewModel = viewModel()

    NavHost(navController, startDestination = NavComponents.SplashScreen.route) {
        composable(NavComponents.SplashScreen.route) {
            SplashScreen(navController, viewModel)
        }
        composable(NavComponents.AuthScreen.route) {
            AuthScreen(navController, viewModel)
        }
        composable(NavComponents.HomeScreen.route) {
            HomeScreen(navController, viewModel)
        }
        composable(
            route = NavComponents.EventDetails.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId")
            val event = viewModel.events.collectAsState().value.find { it.id == eventId }

            if (event != null) {
                EventDetailsScreen(navController, event, viewModel)
            } else {
                Text("Event not found")
            }
        }
        composable(NavComponents.RegistrationScreen.route) {
            RegistrationScreen(navController, viewModel)
        }
    }
}