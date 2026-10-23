package kz.evn.wssession2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

data class TwoScreenData(
    val img: String,
    val maintext: String,
    val distancemap: Float,
    val reviewres: Float,
    val workingHours: String,
    val objecttype: String,
    val parking: Int,
    val status: Boolean
)

@Composable
fun ScreenOverview(
    item: TwoScreenData,
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        if (item.img.isNotBlank()) {
            AsyncImage(
                model = item.img,
                contentDescription = "Фото заведения",
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
                    text = if (item.objecttype.startsWith("Restaurant")) "🍴" else "🏛️",
                    fontSize = 50.sp
                )
            }
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Назад",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }
}


@Composable
fun DetailScreen(
    item: CardItemRes,
    onBackClick: () -> Unit
) {


    val isRestaurant = item.objecttype.startsWith("Restaurant")

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {




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

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.resname,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ ${item.reviewres}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF57C00)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = "📍 ${item.distancemap} km away",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))


                Text(
                    text = "Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Text(text = "🕒", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Working Hours", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = if (item.workingHours.isNotBlank()) item.workingHours else "10:00 - 22:00",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }


                if (item.parking > 0 || isRestaurant) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_parking),
                            contentDescription = "Parking",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Remaining Parking Spaces", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = "${item.parking} slots available",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }


                if (item.priceRange.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dollar),
                            contentDescription = "Price",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isRestaurant) "Average Cost" else "Ticket Price",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = item.priceRange,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }


                if (item.address.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Text(text = "🏠", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Address", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = item.address,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }


                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = item.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))


                Button(
                    onClick = { /* Вход / Отзывы */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Login to write a review", fontSize = 16.sp)
                }
            }
        }
    }
}
