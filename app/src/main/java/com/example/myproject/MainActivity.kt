package com.example.myproject

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myproject.ui.theme.Course
import com.example.myproject.ui.theme.MyProjectTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            MyProjectTheme {
                var shouldSaveOnboarding by remember { mutableStateOf(false) }
                val onboardingCompleted by dataStoreManager.onboardingCompleted.collectAsState(initial = false)

                if (shouldSaveOnboarding) {
                    LaunchedEffect(Unit) {
                        dataStoreManager.saveOnboardingCompleted(true)
                        shouldSaveOnboarding = false
                    }
                }

                if (onboardingCompleted) {
                    CourseListScreen(dataStoreManager)
                } else {
                    OnboardingScreen(
                        onContinueClicked = {
                            shouldSaveOnboarding = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Course Explorer!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onContinueClicked,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun CourseListScreen(dataStoreManager: DataStoreManager) {
    val courses = getSampleCourses()
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 64.dp)
    ) {
        items(courses) { course ->
            ExpandableCourseCard(course = course)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            dataStoreManager.saveOnboardingCompleted(false)
                        }
                    }
                ) {
                    Text("Reset Onboarding")
                }
            }
        }
    }
}

@Composable
fun ExpandableCourseCard(course: Course) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Code: ${course.code}")
            Text(text = "Credits: ${course.creditHours}")

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Description: ${course.description}")
                Text(text = "Prerequisites: ${course.prerequisites}")
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { expanded = !expanded }
            ) {
                Text(if (expanded) "Show Less" else "Show More")
            }
        }
    }
}

fun getSampleCourses(): List<Course> = listOf(
    Course(
        title = "Introduction to Computer Science",
        code = "CS101",
        creditHours = 3,
        description = "Learn the basics of programming and computer science.",
        prerequisites = "None"
    ),
    Course(
        title = "Data Structures and Algorithms",
        code = "CS201",
        creditHours = 4,
        description = "Explore data structures, recursion, and algorithms.",
        prerequisites = "CS101"
    ),
    Course(
        title = "Operating Systems",
        code = "CS301",
        creditHours = 4,
        description = "Understand how OS works: processes, threads, memory.",
        prerequisites = "CS201"
    )
)


// ================= PREVIEWS =================

@Preview(
    showBackground = true,
    name = "Onboarding - Light"
)
@Preview(
    showBackground = true,
    name = "Onboarding - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun OnboardingPreview() {
    MyProjectTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}

@Preview(
    showBackground = true,
    name = "Course Card - Light"
)
@Preview(
    showBackground = true,
    name = "Course Card - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ExpandableCourseCardPreview() {
    val sampleCourse = Course(
        title = "Sample Course",
        code = "SC101",
        creditHours = 3,
        description = "This course is a sample to show how the UI looks.",
        prerequisites = "None"
    )

    MyProjectTheme {
        ExpandableCourseCard(course = sampleCourse)
    }
}
