package uk.ac.tees.mad.culturalvibe.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
                        viewModel.unregisterFromEvent(id = event.id, context = context)
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
                    viewModel.registerToEvent(event.id, context)
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



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Event Details – Not Registered")
@Composable
fun EventDetailsScreenPreview_NotRegistered() {
    val sampleEvent = Event(
        title = "African Cultural Festival 2025",
        date = null,
        venue = "Middlesbrough Town Hall",
        fee = 25.00,
        description = "Join us for a vibrant celebration of African music, dance, food, and art. Experience traditional performances, workshops, and authentic cuisine from across the continent.",
        imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800",
        id = 2,
    )

    var isRegistered by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sampleEvent.title, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        bottomBar = {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
            ) {
                Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AsyncImage(
                model = sampleEvent.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(sampleEvent.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Venue: ${sampleEvent.venue}", fontSize = 16.sp)
                Text("Fee: $${sampleEvent.fee}", fontSize = 16.sp, color = SecondaryColor)
                Spacer(Modifier.height(12.dp))
                Text(sampleEvent.description, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Register for ${sampleEvent.title}") },
            text = {
                Text("Please confirm your registration. Fee: $${sampleEvent.fee}")
            },
            confirmButton = {
                TextButton(onClick = {
                    isRegistered = true
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Event Details – Already Registered")
@Composable
fun EventDetailsScreenPreview_Registered() {
    val sampleEvent = Event(
        title = "Bollywood Night Extravaganza",
        date = null,
        venue = "Teesside University SU",
        fee = 15.00,
        description = "Get ready for a night full of Bollywood music, dance performances, delicious Indian street food, and colorful decorations!",
        imageUrl = "https://images.unsplash.com/photo-1543007630-9710e4db7a4f?w=800",
        id = 2,
    )

    var isRegistered by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sampleEvent.title, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        bottomBar = {
            Button(
                onClick = { isRegistered = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Unregister", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            AsyncImage(
                model = sampleEvent.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(sampleEvent.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Venue: ${sampleEvent.venue}", fontSize = 16.sp)
                Text("Fee: $${sampleEvent.fee}", fontSize = 16.sp, color = SecondaryColor)
                Spacer(Modifier.height(12.dp))
                Text(sampleEvent.description, fontSize = 14.sp, lineHeight = 20.sp)
            }
        }
    }
}