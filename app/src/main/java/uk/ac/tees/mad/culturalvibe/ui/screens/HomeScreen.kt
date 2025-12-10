package uk.ac.tees.mad.culturalvibe.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.BrowseGallery
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import uk.ac.tees.mad.culturalvibe.NavComponents
import uk.ac.tees.mad.culturalvibe.R
import uk.ac.tees.mad.culturalvibe.data.models.Event
import uk.ac.tees.mad.culturalvibe.ui.AppViewModel
import uk.ac.tees.mad.culturalvibe.ui.components.EventCard
import uk.ac.tees.mad.culturalvibe.ui.theme.OnSecondary
import uk.ac.tees.mad.culturalvibe.ui.theme.PrimaryColor
import uk.ac.tees.mad.culturalvibe.ui.theme.SecondaryColor

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
    val events = viewModel.events.collectAsState().value
    val bookmarkedEvents = viewModel.bookmarkedEvents.collectAsState().value
    val pagerState = rememberPagerState(pageCount = { 3 }) // now 3 pages
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(NavComponents.ProfileScreen.route)
                        }) {
                            Icon(
                                Icons.Rounded.Person,
                                contentDescription = "Profile",
                                tint = OnSecondary
                            )
                        }
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
                    Tab(
                        selected = pagerState.currentPage == 2,
                        onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
                        text = { Text("Bookmarks") }
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
                0 -> EventsList(
                    events = events,
                    context = context,
                    viewModel = viewModel,
                    navController = navController
                )
                1 -> GalleryScreen(viewModel)
                2 -> BookmarkScreen(
                    bookmarks = bookmarkedEvents,
                    context = context,
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun EventsList(
    events: List<Event>,
    context: Context,
    viewModel: AppViewModel,
    navController: NavController
) {
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
                onBookmarkClick = { viewModel.addBookmark(context, it) },
                onClick = { selectedEvent ->
                    navController.navigate(NavComponents.EventDetails.passId(selectedEvent.id))
                }
            )
        }
    }
}

@Composable
fun BookmarkScreen(
    bookmarks: List<Event>,
    context: Context,
    viewModel: AppViewModel,
    navController: NavController
) {
    if (bookmarks.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("No bookmarks yet", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bookmarks) { event ->
                EventCard(
                    event = event,
                    showDeleteIcons = true,
                    onBookmarkClick = { viewModel.removeBookmark(context, it) },
                    onClick = { selectedEvent ->
                        navController.navigate(NavComponents.EventDetails.passId(selectedEvent.id))
                    }
                )
            }
        }
    }
}


@Composable
fun GalleryScreen(viewModel: AppViewModel) {
    val images = viewModel.images.collectAsState().value
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadImageToSupabase(it, context)
        }
    }
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.uploadImageToSupabase(it, context)
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                takePhotoLauncher.launch()
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchImages()
    }

    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(onClick = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                }

                FloatingActionButton(onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                }

            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images.size) { index ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.height(160.dp)
                ) {
                    AsyncImage(
                        model = images[index],
                        contentDescription = "Gallery Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

