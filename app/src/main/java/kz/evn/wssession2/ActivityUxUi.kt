package kz.evn.wssession2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: NearbyViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(1) }
    var selectedItem by remember { mutableStateOf<CardItemRes?>(null) }
    var showReviews by remember { mutableStateOf(false) }
    var showMyReviews by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    if (showAuthDialog) {
        AuthDialog(
            viewModel = viewModel,
            onDismiss = { showAuthDialog = false }
        )
    }

    if (showMyReviews) {
        BackHandler { showMyReviews = false }
        MyReviewsScreen(
            viewModel = viewModel,
            onBackClick = { showMyReviews = false }
        )
        return
    }

    if (showReviews && selectedItem != null) {
        BackHandler { showReviews = false }
        ReviewsScreen(
            item = selectedItem!!,
            viewModel = viewModel,
            onBackClick = { showReviews = false }
        )
        return
    }

    if (selectedItem != null) {
        BackHandler { selectedItem = null }
        DetailScreen(
            item = selectedItem!!,
            viewModel = viewModel,
            onBackClick = { selectedItem = null },
            onOpenReviews = { showReviews = true },
            onOpenAuth = { showAuthDialog = true }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("🗺️") },
                    label = { Text("Map") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("📍") },
                    label = { Text("Nearby") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text("👤") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> MapNavigationScreen()
                1 -> NearbyServicesContent(
                    viewModel = viewModel,
                    onItemClick = { item -> selectedItem = item }
                )
                2 -> ProfileScreen(
                    viewModel = viewModel,
                    onOpenAuth = { showAuthDialog = true },
                    onOpenMyReviews = { showMyReviews = true }
                )
            }
        }
    }
}

@Composable
fun NearbyServicesContent(
    viewModel: NearbyViewModel,
    onItemClick: (CardItemRes) -> Unit
) {
    val restaurantList by viewModel.restaurants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val filteredList = remember(restaurantList, searchQuery, selectedFilter) {
        restaurantList.filter { item ->
            val matchesCategory = when (selectedFilter) {
                "Restaurant" -> item.objecttype.startsWith("Restaurant", ignoreCase = true)
                "Attraction" -> item.objecttype.startsWith("Attraction", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() || item.resname.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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
                CartItemFun(
                    item = restaurant,
                    onClick = { onItemClick(restaurant) }
                )
            }
        }
    }
}

@Composable
fun CartItemFun(
    item: CardItemRes,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
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
                        text = if (item.objecttype.startsWith("Restaurant", ignoreCase = true)) "🍴" else "🏛️",
                        fontSize = 30.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isRestaurant = item.objecttype.startsWith("Restaurant", ignoreCase = true)
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
fun ProfileScreen(
    viewModel: NearbyViewModel,
    onOpenAuth: () -> Unit,
    onOpenMyReviews: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val myReviews by viewModel.myReviews.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (currentUser == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Not Logged In",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Sign in to manage your reviews and access all features.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onOpenAuth,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Login / Register", fontSize = 15.sp)
                    }
                }
            }
        } else {
            val user = currentUser!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val avatarUrl = RetrofitClient.resolveImageUrl(user.avatar)
                    if (!avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = user.nickName.ifBlank { user.username }.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                            Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.nickName.ifBlank { user.username },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "@${user.username}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    if (user.email.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.email,
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                onClick = onOpenMyReviews,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📝", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "My Reviews (${myReviews.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text("❯", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Log Out", fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun MyReviewsScreen(
    viewModel: NearbyViewModel,
    onBackClick: () -> Unit
) {
    val myReviews by viewModel.myReviews.collectAsState()
    var isNewestFirst by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadMyReviews()
    }

    val sortedReviews = remember(myReviews, isNewestFirst) {
        if (isNewestFirst) {
            myReviews.sortedByDescending { it.reviewTime }
        } else {
            myReviews.sortedBy { it.reviewTime }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Reviews",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(onClick = { isNewestFirst = !isNewestFirst }) {
                    Text(if (isNewestFirst) "Newest ▼" else "Oldest ▲", fontSize = 13.sp)
                }
            }
        }
    ) { innerPadding ->
        if (sortedReviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You haven't written any reviews yet.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(sortedReviews) { review ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val typeBadge = if (review.businessType?.equals("restaurant", ignoreCase = true) == true) "Restaurant" else "Attraction"
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (typeBadge == "Restaurant") Color(0xFF1976D2) else Color(0xFF388E3C),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(typeBadge, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    val stars = review.rating?.toIntOrNull() ?: 5
                                    Text("⭐".repeat(stars.coerceIn(1, 5)), fontSize = 12.sp)
                                }

                                TextButton(
                                    onClick = {
                                        review.reviewId?.let { id ->
                                            viewModel.deleteReview(id, onSuccess = {}, onError = {})
                                        }
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Delete", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = review.reviewContent ?: "",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = review.reviewTime ?: "",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapNavigationScreen() {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 2.8f)
                        offset += pan
                    }
                }
        ) {
            val cx = size.width / 2f + offset.x
            val cy = size.height / 2f + offset.y

            val pHome = Offset(cx - 140f * scale, cy - 200f * scale)
            val pGas = Offset(cx + 120f * scale, cy - 80f * scale)
            val pRest = Offset(cx - 100f * scale, cy + 90f * scale)
            val pComp = Offset(cx + 130f * scale, cy + 220f * scale)

            drawLine(
                color = Color(0xFFD32F2F),
                start = pHome,
                end = pGas,
                strokeWidth = 10f * scale,
                cap = StrokeCap.Round
            )

            drawLine(
                color = Color(0xFFFFA000),
                start = pGas,
                end = pRest,
                strokeWidth = 10f * scale,
                cap = StrokeCap.Round
            )

            drawLine(
                color = Color(0xFF388E3C),
                start = pRest,
                end = pComp,
                strokeWidth = 10f * scale,
                cap = StrokeCap.Round
            )

            listOf(
                Triple(pHome, Color(0xFF1976D2), 18f * scale),
                Triple(pGas, Color(0xFFD32F2F), 18f * scale),
                Triple(pRest, Color(0xFFFFA000), 18f * scale),
                Triple(pComp, Color(0xFF388E3C), 18f * scale)
            ).forEach { (pt, color, radius) ->
                drawCircle(color = Color.White, center = pt, radius = radius + 6f * scale)
                drawCircle(color = color, center = pt, radius = radius)
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Route & Traffic Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFD32F2F), CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home ➔ Gas Station: Congested (Red)", fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFFFA000), CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gas Station ➔ Restaurant: Slow (Yellow)", fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF388E3C), CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restaurant ➔ Company: Normal (Green)", fontSize = 11.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { scale = (scale + 0.3f).coerceAtMost(2.8f) },
                modifier = Modifier.size(46.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            FloatingActionButton(
                onClick = { scale = (scale - 0.3f).coerceAtLeast(0.6f) },
                modifier = Modifier.size(46.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("—", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AuthDialog(
    viewModel: NearbyViewModel,
    onDismiss: () -> Unit
) {
    var isRegister by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("user1") }
    var password by remember { mutableStateOf("123456") }
    var nickname by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { isRegister = false; errorMessage = null }) {
                    Text(
                        "Sign In",
                        fontWeight = if (!isRegister) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isRegister) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                TextButton(onClick = { isRegister = true; errorMessage = null }) {
                    Text(
                        "Sign Up",
                        fontWeight = if (isRegister) FontWeight.Bold else FontWeight.Normal,
                        color = if (isRegister) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMessage = null },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isRegister) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Nickname (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    if (isRegister) {
                        viewModel.register(
                            username = username,
                            password = password,
                            nickName = nickname,
                            onSuccess = {
                                isLoading = false
                                onDismiss()
                            },
                            onError = { err ->
                                isLoading = false
                                errorMessage = err
                            }
                        )
                    } else {
                        viewModel.login(
                            username = username,
                            password = password,
                            onSuccess = {
                                isLoading = false
                                onDismiss()
                            },
                            onError = { err ->
                                isLoading = false
                                errorMessage = err
                            }
                        )
                    }
                },
                enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
            ) {
                Text(if (isRegister) "Register" else "Login")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
