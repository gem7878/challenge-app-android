package com.example.testproject

// Android 기본 라이브러리
import android.Manifest
import android.os.Bundle
// Jetpack Compose 관련 라이브러리
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// 커스텀 화면 컴포넌트들
import com.example.testproject.ui.screens.LoginScreen
import com.example.testproject.ui.screens.MainScreen
import com.example.testproject.ui.screens.SignUpScreen
// 커스텀 테마
import com.example.testproject.ui.theme.TestProjectTheme

// MainActivity는 앱의 진입점이 되는 Activity
class MainActivity : ComponentActivity() {
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // 정확한 위치 권한이 승인됨
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // 대략적인 위치 권한이 승인됨
            }
            else -> {
                // 권한이 거부됨
            }
        }
    }

    // onCreate는 Activity가 생성될 때 호출되는 생명주기 메서드
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 위치 권한 요청
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        // setContent는 Compose UI를 설정하는 함수
        setContent {
            // TestProjectTheme는 커스텀 테마를 적용하는 Composable
            TestProjectTheme {
                // Surface는 Material Design의 표면을 나타내는 컴포넌트
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 로그인 상태를 관리하는 상태 변수
                    var isLoggedIn by remember { mutableStateOf(false) }
                    // 회원가입 화면 표시 여부를 관리하는 상태 변수
                    var isSignUp by remember { mutableStateOf(false) }

                    // 로그인 상태에 따라 다른 화면을 표시
                    if (isLoggedIn) {
                        // 로그인된 경우 메인 화면 표시
                        MainScreen(
                            onLogout = { 
                                isLoggedIn = false
                                isSignUp = false
                            }
                        )
                    } else if (isSignUp) {
                        // 회원가입 화면 표시
                        SignUpScreen(
                            onSignUp = { 
                                isSignUp = false
                                isLoggedIn = true
                            },
                            onBack = { isSignUp = false }
                        )
                    } else {
                        // 로그인 화면 표시
                        LoginScreen(
                            onLogin = { 
                                try {
                                    isLoggedIn = true
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            onSignUp = { isSignUp = true }
                        )
                    }
                }
            }
        }
    }
}