package uk.ac.tees.mad.culturalvibe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uk.ac.tees.mad.culturalvibe.data.models.Event
import uk.ac.tees.mad.culturalvibe.ui.AppViewModel
import uk.ac.tees.mad.culturalvibe.ui.theme.PrimaryColor
import uk.ac.tees.mad.culturalvibe.ui.theme.SecondaryColor
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    navController: NavController,
    event: Event,
    viewModel: AppViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event.title, color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (!viewModel.isRegistered(event)) showDialog = true
                    else {
                        viewModel.unregisterFromEvent(id = event.id, context = context, onSuccess = {
                            navController.popBackStack()
                        })
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!viewModel.isRegistered(event)) SecondaryColor else MaterialTheme.colorScheme.error
                )
            ) {
                Text(if (!viewModel.isRegistered(event)) "Register" else "Unregister")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(event.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(SimpleDateFormat("dd/MM/yyyy").format(event.date?.toDate()), fontSize = 16.sp, color = PrimaryColor)
                Text("Venue: ${event.venue}", fontSize = 16.sp)
                Text("Fee: $${event.fee}", fontSize = 16.sp, color = SecondaryColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(event.description, fontSize = 14.sp)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.registerToEvent(event.id, context, onSuccess = {
                        navController.popBackStack()
                    })
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Register for ${event.title}") },
            text = {
                Column {
                    Text("Please confirm your registration. Fee: $${event.fee}")
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "CulturalVibe – Event Details")
@Composable
fun EventDetailsScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6D4C41))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
            Spacer(Modifier.width(16.dp))
            Text(
                "Bollywood Dance Festival 2025",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Event Image
        Box( modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color(0xFFFFB74D)),
        contentAlignment = Alignment.Center
        ) {
        Text(
            "EVENT IMAGE",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }

        Spacer(Modifier.height(24.dp))

        // Event Info
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                "Bollywood Dance Festival 2025",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(8.dp))

            Text("Date: 15/10/2025", fontSize = 16.sp, color = Color(0xFF6D4C41))
            Text("Venue: Albert Park, Middlesbrough", fontSize = 16.sp, color = Color.Black)
            Text("Fee: $25.00", fontSize = 16.sp, color = Color(0xFFFFB74D))

            Spacer(Modifier.height(16.dp))

            Text(
                "Join us for an electrifying celebration of Bollywood music and dance! Experience vibrant performances, traditional food stalls, and cultural showcases from across India.",
                fontSize = 15.sp,
                color = Color(0xFF424242)
            )
        }

        Spacer(Modifier.weight(1f))

        // Bottom Register Button
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
        ) {
            Text("Register", color = Color.White, fontSize = 18.sp)
        }
    }
}