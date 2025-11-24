package uk.ac.tees.mad.culturalvibe.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uk.ac.tees.mad.culturalvibe.ui.AppViewModel
import uk.ac.tees.mad.culturalvibe.ui.theme.OnSecondary
import uk.ac.tees.mad.culturalvibe.ui.theme.PrimaryColor
import uk.ac.tees.mad.culturalvibe.ui.theme.SecondaryColor
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import uk.ac.tees.mad.culturalvibe.NavComponents

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val description: String,
    val venue: String,
    val fee: Double,
    val imageUrl: String,
    var isBookmarked: Boolean = false
)


val dummyEvents = listOf(
    Event(
        1,
        "Cultural Night 2025",
        "12th Sept 2025",
        "An evening full of music, dance, and fun!",
        "Main Auditorium",
        10.0,
        "https://images.unsplash.com/photo-1507874457470-272b3c8d8ee2"
    ),
    Event(
        2,
        "Photography Workshop",
        "15th Sept 2025",
        "Learn portrait & event photography basics.",
        "Room 204, Arts Block",
        5.0,
        "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e"
    ),
    Event(
        3,
        "Drama Club Auditions",
        "18th Sept 2025",
        "Showcase your acting talent.",
        "Drama Hall",
        0.0,
        "https://media.istockphoto.com/id/182860581/photo/empty-theater-from-the-view-of-the-back-row.jpg?s=1024x1024&w=is&k=20&c=YZaS9PpEWtnXjdVfrJgB5LscungL78sAfbSwLXJthRg="
    )
)


val dummyGalleryImages = listOf(
    "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4",
    "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
    "https://images.unsplash.com/photo-1485561892409-8e0910be73d7",
    "https://images.unsplash.com/photo-1497032205916-ac775f0649ae",
    "https://images.unsplash.com/photo-1518972559570-0c63f7ef17a2"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: AppViewModel) {
    var events by remember { mutableStateOf(dummyEvents) }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "CulturalVibe",
                            fontSize = 22.sp,
                            color = OnSecondary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryColor
                    )
                )

                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = PrimaryColor,
                    contentColor = OnSecondary
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        text = { Text("Events") }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        text = { Text("Gallery") }
                    )
                }
            }
        },
        floatingActionButton = {
            if (pagerState.currentPage == 0) {
                FloatingActionButton(
                    onClick = { /* navController.navigate("registration") */ },
                    containerColor = SecondaryColor,
                    contentColor = OnSecondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> EventsList(events = events,{ id ->
                    events = events.map {
                        if (it.id == id) it.copy(isBookmarked = !it.isBookmarked) else it
                    }
                }, navController = navController)
                1 -> GalleryScreen()
            }
        }
    }
}

@Composable
fun EventsList(events: List<Event>, onBookmarkClick: (Int) -> Unit, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events) { event ->
            EventCard(
                event = event,
                onBookmarkClick = onBookmarkClick,
                onClick = { selectedEvent ->
                    //navController.navigate(NavComponents.EventDetails.passId(selectedEvent.id))
                }
            )

        }
    }
}

@Composable
fun EventCard(event: Event, onBookmarkClick: (Int) -> Unit, onClick: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(event) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { onBookmarkClick(event.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = if (event.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = SecondaryColor
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(event.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(event.date, fontSize = 14.sp, color = PrimaryColor)
                Text("Venue: ${event.venue}", fontSize = 14.sp)
                Text("Fee: $${event.fee}", fontSize = 14.sp, color = SecondaryColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(event.description, fontSize = 14.sp)
            }
        }
    }
}


@Composable
fun GalleryScreen() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dummyGalleryImages.size) { index ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.height(160.dp)
            ) {
                AsyncImage(
                    model = dummyGalleryImages[index],
                    contentDescription = "Gallery Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CulturalVibe – Home Screen (Events Tab)")
@Composable
fun HomeScreenPreview_Events() {
    var events by remember {
        mutableStateOf(dummyEvents)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("CulturalVibe", fontSize = 22.sp, color = OnSecondary)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
                )
                TabRow(
                    selectedTabIndex = 0,
                    containerColor = PrimaryColor,
                    contentColor = OnSecondary
                ) {
                    Tab(selected = true, onClick = {}, text = { Text("Events") })
                    Tab(selected = false, onClick = {}, text = { Text("Gallery") })
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = SecondaryColor,
                contentColor = OnSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                EventCard(
                    event = event,
                    onBookmarkClick = { id ->
                        events = events.map {
                            if (it.id == id) it.copy(isBookmarked = !it.isBookmarked) else it
                        }
                    },
                    onClick = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "CulturalVibe – Home Screen (Gallery Tab)")
@Composable
fun HomeScreenPreview_Gallery() {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("CulturalVibe", fontSize = 22.sp, color = OnSecondary)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
                )
                TabRow(
                    selectedTabIndex = 1,
                    containerColor = PrimaryColor,
                    contentColor = OnSecondary
                ) {
                    Tab(selected = false, onClick = {}, text = { Text("Events") })
                    Tab(selected = true, onClick = {}, text = { Text("Gallery") })
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dummyGalleryImages.size) { index ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    AsyncImage(
                        model = dummyGalleryImages[index],
                        contentDescription = "Gallery Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            }
        }
    }
}