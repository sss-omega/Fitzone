package kz.evn.wssession2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun DetailScreen(
    item: CardItemRes,
    viewModel: NearbyViewModel,
    onBackClick: () -> Unit,
    onOpenReviews: () -> Unit,
    onOpenAuth: () -> Unit
) {
    val isRestaurant = item.objecttype.startsWith("Restaurant", ignoreCase = true)
    val currentUser by viewModel.currentUser.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    LaunchedEffect(item.id) {
        viewModel.loadReviews(item.objecttype, item.id)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                if (!item.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.resname,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isRestaurant) "🍴" else "🏛️",
                            fontSize = 60.sp
                        )
                    }
                }

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isRestaurant) Color(0xFF1976D2) else Color(0xFF388E3C),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isRestaurant) "Restaurant" else "Attraction",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = item.resname,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ ${item.reviewres}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57C00)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = "📍 ${item.distancemap} km away",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                Text(
                    text = "Information",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = "🕒", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Working Hours", fontSize = 12.sp, color = Color.Gray)
                        Text(text = item.workingHours, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                if (isRestaurant || item.parking > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_parking),
                            contentDescription = "Parking",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Remaining Parking Spaces", fontSize = 12.sp, color = Color.Gray)
                            Text(text = "${item.parking} slots available", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                if (item.priceRange.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dollar),
                            contentDescription = "Price",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = if (isRestaurant) "Average Cost" else "Ticket Price", fontSize = 12.sp, color = Color.Gray)
                            Text(text = item.priceRange, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                if (item.address.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(text = "🏠", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Address", fontSize = 12.sp, color = Color.Gray)
                            Text(text = item.address, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Description", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.description, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reviews (${reviews.size})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (currentUser != null) {
                        TextButton(onClick = onOpenReviews) {
                            Text("View More")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (currentUser == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔒 Reviews are hidden for guests",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onOpenAuth,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Login to View & Write Reviews")
                            }
                        }
                    }
                } else {
                    if (reviews.isEmpty()) {
                        Text(
                            text = "No reviews yet. Be the first to review!",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        reviews.take(3).forEach { review ->
                            ReviewItemCard(review = review)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenReviews,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("All Reviews & Write Review", fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewsScreen(
    item: CardItemRes,
    viewModel: NearbyViewModel,
    onBackClick: () -> Unit
) {
    val reviews by viewModel.reviews.collectAsState()
    var selectedStarFilter by remember { mutableStateOf("All") }
    var isNewestFirst by remember { mutableStateOf(true) }

    var userRating by remember { mutableStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    var reviewError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val filteredReviews = remember(reviews, selectedStarFilter, isNewestFirst) {
        val list = if (selectedStarFilter == "All") {
            reviews
        } else {
            val starInt = selectedStarFilter.removeSuffix("★").trim().toIntOrNull() ?: 5
            reviews.filter { it.rating?.toIntOrNull() == starInt }
        }
        if (isNewestFirst) {
            list.sortedByDescending { it.reviewTime }
        } else {
            list.sortedBy { it.reviewTime }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Reviews",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.resname,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("All", "5★", "4★", "3★").forEach { star ->
                        FilterChip(
                            selected = selectedStarFilter == star,
                            onClick = { selectedStarFilter = star },
                            label = { Text(star, fontSize = 12.sp) }
                        )
                    }
                }

                TextButton(onClick = { isNewestFirst = !isNewestFirst }) {
                    Text(if (isNewestFirst) "Newest ▼" else "Oldest ▲", fontSize = 13.sp)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (filteredReviews.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No reviews found for this filter.", color = Color.Gray)
                        }
                    }
                } else {
                    items(filteredReviews) { review ->
                        ReviewItemCard(review = review)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Write a Review", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rating: ", fontSize = 13.sp)
                        (1..5).forEach { star ->
                            Text(
                                text = if (star <= userRating) "⭐" else "☆",
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .clickable { userRating = star }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = {
                            if (it.length <= 15) {
                                reviewText = it
                                reviewError = null
                            }
                        },
                        placeholder = { Text("5 to 15 characters...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = reviewText.isNotEmpty() && reviewText.length < 5
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remaining: ${15 - reviewText.length} (${reviewText.length}/15, min 5)",
                            fontSize = 11.sp,
                            color = if (reviewText.length in 5..15) Color(0xFF2E7D32) else Color.Gray
                        )

                        Button(
                            onClick = {
                                if (reviewText.length in 5..15) {
                                    isSubmitting = true
                                    viewModel.addReview(
                                        businessType = item.objecttype,
                                        businessId = item.id,
                                        rating = userRating.toString(),
                                        content = reviewText,
                                        onSuccess = {
                                            reviewText = ""
                                            isSubmitting = false
                                        },
                                        onError = {
                                            reviewError = it
                                            isSubmitting = false
                                        }
                                    )
                                }
                            },
                            enabled = !isSubmitting && reviewText.length in 5..15,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Post")
                        }
                    }

                    if (reviewError != null) {
                        Text(
                            text = reviewError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(review: ReviewDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1976D2)),
                        contentAlignment = Alignment.Center
                    ) {
                        val initial = review.userName?.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                        Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = review.userName ?: "Anonymous",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = review.reviewTime ?: "",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                val stars = review.rating?.toIntOrNull() ?: 5
                Text("⭐".repeat(stars.coerceIn(1, 5)), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.reviewContent ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (review.likeCount > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("❤️ ${review.likeCount}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
