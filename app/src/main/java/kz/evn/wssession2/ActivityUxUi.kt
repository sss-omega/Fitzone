package kz.evn.wssession2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kz.evn.wssession2.ui.theme.WsSession2Theme

class ActivityUxUi : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WsSession2Theme {
                NearbyServicesScreen()
            }
        }
    }
}

data class CardItemRes(
    val resname: String,
    val distancemap: Float,
    val reviewres: Float,
    val objecttype: String,
    val imageUrl: String?
)

@Composable
fun CartItemFun(item: CardItemRes) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                if (!item.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.resname,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = if (item.objecttype.startsWith("Restaurant")) "🍴" else "🏛️",
                        fontSize = 30.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isRestaurant = item.objecttype.startsWith("Restaurant")
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isRestaurant) Color(0xFF1976D2) else Color(0xFF388E3C),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isRestaurant) "Restaurant" else "Attraction",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = item.resname,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ ${item.reviewres}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF57C00)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "📍 ${item.distancemap} km",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NearbyServicesScreen(viewModel: NearbyViewModel = viewModel()) {
    val restaurantList by viewModel.restaurants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val filteredList = remember(restaurantList, searchQuery, selectedFilter) {
        restaurantList.filter { item ->
            val matchesCategory = when (selectedFilter) {
                "Restaurant" -> item.objecttype.startsWith("Restaurant")
                "Attraction" -> item.objecttype.startsWith("Attraction")
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() || item.resname.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Text("🗺️") },
                    label = { Text("Map Navigation") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("📍") },
                    label = { Text("Nearby Services") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Text("👤") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter: ",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Box {
                    OutlinedButton(
                        onClick = { dropdownExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "$selectedFilter  ▼", fontSize = 14.sp)
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = {
                                selectedFilter = "All"
                                dropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Restaurant") },
                            onClick = {
                                selectedFilter = "Restaurant"
                                dropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Attraction") },
                            onClick = {
                                selectedFilter = "Attraction"
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            if (errorMessage != null && restaurantList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage ?: "Connection error",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredList) { restaurant ->
                    CartItemFun(item = restaurant)
                }
            }
        }
    }
}
