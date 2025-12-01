package uk.ac.tees.mad.culturalvibe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.culturalvibe.NavComponents
import uk.ac.tees.mad.culturalvibe.ui.AppViewModel
import uk.ac.tees.mad.culturalvibe.ui.theme.PrimaryColor
import kotlin.math.log

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: AppViewModel
) {

    val loggedInUsr = splashViewModel.isUserLoggedIn.collectAsState().value
    LaunchedEffect(key1 = true) {
        delay(2000)
        if (loggedInUsr) {
            navController.navigate(NavComponents.HomeScreen.route) {
                popUpTo(0)
            }
        } else {
            navController.navigate(NavComponents.AuthScreen.route) {
                popUpTo(0)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = uk.ac.tees.mad.culturalvibe.R.drawable.designer),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(48.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CulturalVibe",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true, name = "CulturalVibe – Splash Screen")
@Composable
fun SplashScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = uk.ac.tees.mad.culturalvibe.R.drawable.designer),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(48.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CulturalVibe",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}