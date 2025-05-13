package com.example.testproject.model

data class User(
    val id: String,
    val name: String,
    val points: Int = 0,
    val verifiedEvents: List<String> = emptyList()  // 인증 완료한 이벤트 ID 목록
) 