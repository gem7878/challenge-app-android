package com.example.testproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.testproject.model.Event
import com.example.testproject.model.EventCategory
import com.example.testproject.model.User
import com.example.testproject.ui.components.EventCard
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var events by remember { mutableStateOf(initialEvents) }
    
    val currentUser = remember {
        User(
            id = "user1",
            name = "홍길동",
            points = 450,
            verifiedEvents = listOf("1", "3", "5")
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("팝업/전시회") },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "로그아웃")
                        }
                    }
                )
                // 검색창
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("이벤트 검색") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "검색") },
                    singleLine = true
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "홈") },
                    label = { Text("홈") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Map, contentDescription = "지도") },
                    label = { Text("지도") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "챌린지") },
                    label = { Text("챌린지") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "프로필") },
                    label = { Text("프로필") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeContent(
                    events = events,
                    searchQuery = searchQuery,
                    onToggleChallenge = { eventId ->
                        events = events.map { event ->
                            if (event.id == eventId) {
                                event.copy(isInChallenge = !event.isInChallenge)
                            } else {
                                event
                            }
                        }
                    }
                )
                1 -> MapScreen(events = events)
                2 -> ChallengeScreen(
                    events = events.filter { it.isInChallenge },
                    onVerifyEvent = { eventId ->
                        events = events.map { event ->
                            if (event.id == eventId) {
                                event.copy(isVerified = true)
                            } else {
                                event
                            }
                        }
                    },
                    onEventClick = { event -> /* TODO: 이벤트 상세 화면으로 이동 */ }
                )
                3 -> ProfileContent(user = currentUser)
            }
        }
    }
}

@Composable
fun HomeContent(
    events: List<Event>,
    searchQuery: String,
    onToggleChallenge: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val filteredEvents = events.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.location.contains(searchQuery, ignoreCase = true)
        }
        
        items(filteredEvents) { event ->
            EventCard(
                event = event,
                onEventClick = { /* TODO: 이벤트 상세 화면으로 이동 */ },
                onToggleChallenge = { onToggleChallenge(event.id) }
            )
        }
    }
}

@Composable
fun MapScreen(events: List<Event>) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // 위치 권한 상태
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 위치 권한 요청
    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.entries.any { it.value }
    }

    // 서울 중심 좌표
    val seoul = LatLng(37.5665, 126.9780)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(seoul, 12f)
    }

    // 이벤트 위치를 LatLng으로 변환하는 함수
    fun getLocationFromAddress(address: String): LatLng {
        return when {
            address.contains("강남구") -> LatLng(37.5172, 127.0473)
            address.contains("종로구") -> LatLng(37.5724, 126.9760)
            address.contains("홍대입구") -> LatLng(37.5575, 126.9258)
            address.contains("이태원") -> LatLng(37.5344, 126.9941)
            address.contains("광화문") -> LatLng(37.5757, 126.9768)
            else -> seoul
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = hasLocationPermission
            )
        ) {
            // 이벤트 마커 표시
            events.forEach { event ->
                val location = getLocationFromAddress(event.location)
                Marker(
                    state = MarkerState(position = location),
                    title = event.title,
                    snippet = "${event.location}\n${event.startDate} ~ ${event.endDate}"
                )
            }
        }

        // 현재 위치 버튼
        FloatingActionButton(
            onClick = {
                if (hasLocationPermission) {
                    scope.launch {
                        try {
                            val location = fusedLocationClient.lastLocation.await()
                            location?.let {
                                currentLocation = LatLng(it.latitude, it.longitude)
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        currentLocation!!,
                                        15f
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            // 위치 권한이 없는 경우 처리
                        }
                    }
                } else {
                    // 권한 요청
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "현재 위치"
            )
        }
    }
}

@Composable
fun NotificationContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "알림 화면",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ProfileContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "보유 포인트",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${user.points}P",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "인증 완료",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "${user.verifiedEvents.size}개",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 초기 이벤트 데이터
private val initialEvents = listOf(
    Event(
        id = "1",
        title = "봄맞이 팝업스토어",
        description = "봄 시즌을 맞이한 특별한 팝업스토어",
        startDate = "2024-03-01",
        endDate = "2024-03-31",
        location = "서울 강남구",
        imageUrl = "@drawable/oliveyoung",
        category = EventCategory.POPUP,
        points = 100,
        isInChallenge = false,
        isVerified = false
    ),
    Event(
        id = "2",
        title = "현대 미술 전시회",
        description = "세계 유명 작가들의 작품을 만나보세요",
        startDate = "2024-03-15",
        endDate = "2024-04-15",
        location = "서울 종로구",
        imageUrl = "@drawable/oliveyoung",
        category = EventCategory.EXHIBITION,
        points = 150,
        isInChallenge = false,
        isVerified = false
    ),
    Event(
        id = "3",
        title = "디자이너 브랜드 팝업",
        description = "신진 디자이너들의 특별한 컬렉션",
        startDate = "2024-03-20",
        endDate = "2024-04-10",
        location = "서울 홍대입구",
        imageUrl = "@drawable/oliveyoung",
        category = EventCategory.POPUP,
        points = 120,
        isInChallenge = false,
        isVerified = false
    ),
    Event(
        id = "4",
        title = "사진 작가 전시회",
        description = "자연의 아름다움을 담은 사진 전시",
        startDate = "2024-04-01",
        endDate = "2024-04-30",
        location = "서울 이태원",
        imageUrl = "@drawable/oliveyoung",
        category = EventCategory.EXHIBITION,
        points = 200,
        isInChallenge = false,
        isVerified = false
    ),
    Event(
        id = "5",
        title = "한식 푸드 팝업",
        description = "전통 한식의 현대적 재해석",
        startDate = "2024-04-05",
        endDate = "2024-04-20",
        location = "서울 광화문",
        imageUrl = "@drawable/oliveyoung",
        category = EventCategory.POPUP,
        points = 80,
        isInChallenge = false,
        isVerified = false
    )
) 