package uk.ac.tees.mad.culturalvibe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.culturalvibe.ui.AppViewModel
import uk.ac.tees.mad.culturalvibe.ui.screens.AuthScreen
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
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel : AppViewModel = viewModel()

    NavHost(navController, startDestination = NavComponents.AuthScreen.route) {
        composable(NavComponents.SplashScreen.route) {
            SplashScreen(navController, viewModel)
        }
        composable(NavComponents.AuthScreen.route) {
            AuthScreen(navController, viewModel)
        }
        composable(NavComponents.HomeScreen.route) {
            //HomeScreen()
        }
    }
}