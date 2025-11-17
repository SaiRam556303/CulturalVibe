package uk.ac.tees.mad.culturalvibe.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.tees.mad.culturalvibe.ui.AppViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel : AppViewModel) {
    Text("Home Screen", fontSize = 42.sp)
}