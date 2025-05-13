package com.example.testproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.testproject.R
import com.example.testproject.model.Event
import com.example.testproject.model.EventCategory
import androidx.compose.foundation.Image

// 이벤트 카드 컴포넌트
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    event: Event,
    onEventClick: (Event) -> Unit,
    onToggleChallenge: (String) -> Unit
) {
    var isVerified by remember { mutableStateOf(false) }

    // Material Design 3의 Card 컴포넌트 사용
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEventClick(event) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // 이벤트 이미지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.oliveyoung),
                        contentDescription = event.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // 이벤트 정보
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 카테고리 태그
                Surface(
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = when (event.category) {
                        EventCategory.POPUP -> MaterialTheme.colorScheme.primary
                        EventCategory.EXHIBITION -> MaterialTheme.colorScheme.secondary
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = when (event.category) {
                            EventCategory.POPUP -> "팝업스토어"
                            EventCategory.EXHIBITION -> "전시회"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // 이벤트 제목
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onToggleChallenge(event.id) }) {
                        Icon(
                            imageVector = if (event.isInChallenge) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (event.isInChallenge) "챌린지에서 제거" else "챌린지에 추가",
                            tint = if (event.isInChallenge) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // 이벤트 설명
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 이벤트 기간과 위치
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${event.startDate} ~ ${event.endDate}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // 포인트 정보
                Text(
                    text = "방문 시 ${event.points}P 획득",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // 인증 버튼
                Button(
                    onClick = { isVerified = !isVerified },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVerified) 
                            MaterialTheme.colorScheme.secondary 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (isVerified) "인증 완료" else "방문 인증하기",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                if (event.isVerified) {
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "인증 완료",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "인증 완료",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
} 