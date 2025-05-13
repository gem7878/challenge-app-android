package com.example.testproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUp: () -> Unit,  // 회원가입 버튼 클릭 시 호출될 콜백
    onBack: () -> Unit     // 뒤로가기 버튼 클릭 시 호출될 콜백
) {
    // 입력 필드의 상태를 관리하는 변수들
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 제목
        Text(
            text = "회원가입",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 이메일 입력 필드
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 이름 입력 필드
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 비밀번호 입력 필드
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        // 회원가입 버튼
        Button(
            onClick = onSignUp,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("회원가입")
        }

        // 로그인 화면으로 돌아가기 버튼
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("이미 계정이 있으신가요? 로그인")
        }
    }
} 