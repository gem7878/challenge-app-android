package com.example.testproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.testproject.model.Event
import com.example.testproject.ui.components.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeScreen(
    events: List<Event>,
    onVerifyEvent: (String) -> Unit,
    onEventClick: (Event) -> Unit = { _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "나의 챌린지",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "담은 이벤트가 없습니다.\n홈 화면에서 이벤트를 담아보세요!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(events) { event ->
                    EventCard(
                        event = event,
                        onEventClick = onEventClick,
                        onToggleChallenge = { /* 챌린지 화면에서는 담기 기능 비활성화 */ }
                    )
                }
            }
        }
    }
} 