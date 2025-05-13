package com.example.testproject.model

// 팝업/전시회 이벤트를 나타내는 데이터 클래스
data class Event(
    val id: String,                // 이벤트 고유 ID
    val title: String,             // 이벤트 제목
    val description: String,       // 이벤트 설명
    val startDate: String,         // 시작일
    val endDate: String,           // 종료일
    val location: String,          // 위치
    val imageUrl: String,          // 이미지 URL
    val category: EventCategory,   // 이벤트 카테고리 (팝업/전시회)
    val points: Int = 0,           // 방문 시 획득할 포인트
    val isInChallenge: Boolean = false,
    val isVerified: Boolean = false
)

// 이벤트 카테고리를 나타내는 enum 클래스
enum class EventCategory {
    POPUP,      // 팝업스토어
    EXHIBITION  // 전시회
} 