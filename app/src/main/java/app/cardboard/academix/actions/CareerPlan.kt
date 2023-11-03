package app.cardboard.academix.actions

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Switch
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction.Companion.Done
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cardboard.academix.DatabaseConnection
import app.cardboard.academix.DatabaseTask
import app.cardboard.academix.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun SetCareerLevel() {
    var levelCode by remember { mutableStateOf("") }
    var levelName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val kbController = LocalSoftwareKeyboardController.current
    val isSubmitEnabled by derivedStateOf {
        levelCode.isNotBlank() && levelName.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Career Level",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = levelCode,
            onValueChange = { levelCode = it },
            label = { Text("Level Code") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = levelName,
            onValueChange = { levelName = it },
            label = { Text("Level Name") }
        )

    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = {
            kbController?.hide()
            GlobalScope.launch(Dispatchers.IO) {
                val databaseConnection = DatabaseConnection()
                val connection = databaseConnection.connect()
                val databaseTask = DatabaseTask()
                val query = "INSERT INTO CareerLevel (CareerLevelCode, CareerLevelName) VALUES ('${levelCode.trim()}', '${levelName.trim()}')"
                levelCode =""
                levelName=""
                if (connection != null) {
                    databaseTask.addLevel(connection, query,  snackbarHostState)
                    connection.close()
                } else {
                    snackbarHostState.showSnackbar(
                        message = "Error Connecting To the Server",
                        withDismissAction = false,
                        duration = SnackbarDuration.Short
                    )
                }

                withContext(Dispatchers.Main) {
                    // Update the UI with the result
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = isSubmitEnabled
    ) {
        Text("Submit")
    }
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxWidth()
    )
}
}
data class Level(
    val levelCode: String,
    val levelName: String
)
@OptIn( DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GetCareerLevels() {
    var levels: List<Level> by remember { mutableStateOf(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val resultVisible = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }
    GlobalScope.launch(Dispatchers.IO) {
        val databaseConnection = DatabaseConnection()
        val connection = databaseConnection.connect()
        val databaseTask = DatabaseTask()
        if (connection != null) {
            Log.d("Career","Connection Established")
            levels = databaseTask.getLevel(connection)
            resultVisible.targetState = true
            connection.close()
        } else {
            snackbarHostState.showSnackbar(
                message = "Error Connecting To the Server",
                withDismissAction = false,
                duration = SnackbarDuration.Long
            )
        }
    }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "View Career Levels",
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
            )
            AnimatedVisibility(
                visibleState = resultVisible
            )
            {

            // Display student details if available
            if (levels.isNotEmpty()) {
                Column {
                    levels.forEach { level ->
                        Text(
                            "Level Code: ${level.levelCode}",
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = colorResource(id = R.color.black)
                            )
                        )
                        Text(
                            "Level Name: ${level.levelName}",
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = colorResource(id = R.color.black)
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            else {
                Text(
                    text = "Career Levels does not exist in records. Please add Career Levels",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = colorResource(id = R.color.black)
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        }


        }
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun SetDepartment() {
    var departmentCode by remember { mutableStateOf("") }
    var departmentName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val kbController = LocalSoftwareKeyboardController.current
    val isSubmitEnabled by derivedStateOf {
        departmentCode.isNotBlank() && departmentName.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Department",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = departmentCode,
            onValueChange = { departmentCode = it },
            label = { Text("Department Code") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = departmentName,
            onValueChange = { departmentName = it },
            label = { Text("Department Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                kbController?.hide()
                GlobalScope.launch(Dispatchers.IO) {
                    val databaseConnection = DatabaseConnection()
                    val connection = databaseConnection.connect()
                    val databaseTask = DatabaseTask()
                    val query = "INSERT INTO Department (DepartmentCode, DepartmentName) VALUES ('${departmentCode.trim()}', '${departmentName.trim()}')"
                    departmentCode =""
                    departmentName=""
                    if (connection != null) {
                        databaseTask.addDepartment(connection, query,  snackbarHostState)
                        connection.close()
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "Error Connecting To the Server",
                            withDismissAction = false,
                            duration = SnackbarDuration.Long
                        )
                    }

                    withContext(Dispatchers.Main) {
                        // Update the UI with the result
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isSubmitEnabled
        ) {
            Text("Submit")
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
data class Dep(
    val departmentCode: String,
    val departmentName: String
)
@OptIn( DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GetDepartments() {
    var dep: List<Dep> by remember { mutableStateOf(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val resultVisible = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }

    GlobalScope.launch(Dispatchers.IO) {
        val databaseConnection = DatabaseConnection()
        val connection = databaseConnection.connect()
        val databaseTask = DatabaseTask()
        if (connection != null) {
            Log.d("Dep","Connection Established")
            dep = databaseTask.getDepartments(connection)
            resultVisible.targetState = true
            connection.close()
        } else {
            snackbarHostState.showSnackbar(
                message = "Error Connecting To the Server",
                withDismissAction = false,
                duration = SnackbarDuration.Long
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "View Department",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
        )

        AnimatedVisibility(
            visibleState = resultVisible
        )
        {
        if (dep.isNotEmpty()) {
            Column {
                dep.forEach { depp ->
                    Text(
                        "Department Code: ${depp.departmentCode}",
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.black)
                        )
                    )
                    Text(
                        "Department Name: ${depp.departmentName}",
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.black)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            Text(
                text = "Departments does not exist in records. Please add Departments",
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 15.sp,
                    color = colorResource(id = R.color.black)
                ),
                fontWeight = FontWeight.Bold
            )
        }

    }
    }

}



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun SetCareer() {

    var careerCode by remember { mutableStateOf("") }
    var careerName by remember { mutableStateOf("") }
    var departmentCode by remember { mutableStateOf("") }
    var careerLevelCode by remember { mutableStateOf("") }
    val duration: MutableState<Int?> = remember { mutableStateOf(null) }
    var requiredOptatives by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val kbController = LocalSoftwareKeyboardController.current
    val isSubmitEnabled by derivedStateOf {
        careerCode.isNotBlank() && careerName.isNotBlank() && careerLevelCode.isNotBlank() && departmentCode.isNotBlank() &&
                duration.value!=null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Career",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = careerCode,
            onValueChange = { careerCode = it },
            label = { Text("Career Code") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = careerName,
            onValueChange = { careerName = it },
            label = { Text("Career Name") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = departmentCode,
            onValueChange = { departmentCode = it },
            label = { Text("Department Code") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = careerLevelCode,
            onValueChange = { careerLevelCode = it },
            label = { Text("Career Level Code") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = if(duration.value == null) "" else duration.value.toString(),
            onValueChange = { duration.value = it.toIntOrNull() },
            label = { Text("Duration Years") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done)
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = requiredOptatives.toString(),
            onValueChange = { requiredOptatives = if(it == "") 0 else it.toInt() },
            label = { Text("Required Optative No.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                kbController?.hide()
                GlobalScope.launch(Dispatchers.IO) {
                    val databaseConnection = DatabaseConnection()
                    val connection = databaseConnection.connect()
                    val databaseTask = DatabaseTask()
                    val query = "INSERT INTO Career (CareerCode, CareerName, DepartmentCode, CareerLevelCode, DurationYears, RequiredOptativeCourses) VALUES ('${careerCode.trim()}','${careerName.trim()}','${departmentCode.trim()}', '${careerLevelCode.trim()}','${duration.value}', '$requiredOptatives')"
                    careerCode=""
                    careerName=""
                    departmentCode =""
                    careerLevelCode=""
                    duration.value = null
                    requiredOptatives = 0

                    if (connection != null) {
                        databaseTask.addCareer(connection, query,  snackbarHostState)
                        connection.close()
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "Error Connecting To the Server",
                            withDismissAction = false,
                            duration = SnackbarDuration.Long
                        )
                    }

                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isSubmitEnabled
        ) {
            Text("Submit")
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
data class Career(
    val careerCode: String,
    val careerName: String,
    val departmentCode: String,
    val careerLevelCode: String,
    val durationYears: Int,
    val requiredOptativeCourses: Int
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun GetCareer() {
    var careerCodeText by remember { mutableStateOf("") }
    var careers: List<Career> by remember { mutableStateOf(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val inputVisible = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val resultVisible = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }
    val kbController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visibleState = inputVisible
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Get Career Details",
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = careerCodeText,
                    onValueChange = { careerCodeText = it },
                    label = { Text("Enter Career Code") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        kbController?.hide()
                        val careerCode = careerCodeText
                        careerCodeText = ""
                        GlobalScope.launch(Dispatchers.IO) {
                            val databaseConnection = DatabaseConnection()
                            val connection = databaseConnection.connect()
                            val databaseTask = DatabaseTask()
                            if (connection != null) {
                                careers = databaseTask.getCareer(careerCode, connection)
                                connection.close()
                                resultVisible.targetState = true
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = "Error Connecting To the Server",
                                    withDismissAction = false,
                                    duration = SnackbarDuration.Long
                                )
                            }
                            withContext(Dispatchers.Main) {
                                // Update the UI with the result
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Fetch Career Details")
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        AnimatedVisibility(
            visibleState = resultVisible
        ) {
            inputVisible.targetState = false
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (careers.isNotEmpty()) {
                    Column {
                        careers.forEach { career ->
                            Text("Career Code: ${career.careerCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Career Name: ${career.careerName}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Department Code: ${career.departmentCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Career Level Code: ${career.careerLevelCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Duration (Years): ${career.durationYears}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Required Optative Courses: ${career.requiredOptativeCourses}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    Text(
                        text = "Career Code does not exist in records",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.black)
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun AddCourse() {
    var courseName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var careerCode by remember { mutableStateOf("") }
    val semester: MutableState<Int?> = remember { mutableStateOf(null) }
    var isOptative by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val kbController = LocalSoftwareKeyboardController.current
    val isSubmitEnabled by derivedStateOf {
        courseName.isNotBlank() && courseCode.isNotBlank() && careerCode.isNotBlank() && semester.value!=null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Course",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = courseName,
            onValueChange = { courseName = it },
            label = { Text("Course Name") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = courseCode,
            onValueChange = { courseCode = it },
            label = { Text("Course Code") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = careerCode,
            onValueChange = { careerCode = it },
            label = { Text("Career Code") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = if(semester.value == null) "" else semester.value.toString(),
            onValueChange = { semester.value = it.toIntOrNull() ?: 1 },
            label = { Text("Semester") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Switch(
                checked = isOptative,
                onCheckedChange = { isOptative = it },
            )
            Text("Is Optative Course")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                kbController?.hide()
                GlobalScope.launch(Dispatchers.IO) {
                    val databaseConnection = DatabaseConnection()
                    val connection = databaseConnection.connect()
                    val databaseTask = DatabaseTask()
                    val query = "INSERT INTO Course (CourseName, CourseCode, CareerCode, Semester, Optative) " +
                            "VALUES ('${courseName.trim()}', '${courseCode.trim()}', '${careerCode.trim()}', ${semester.value}, $isOptative)"
                    courseName = ""
                    courseCode = ""
                    careerCode = ""
                    semester.value = null
                    isOptative = false

                    if (connection != null) {
                        databaseTask.addCourse(connection, query, snackbarHostState)
                        connection.close()
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "Error Connecting To the Server",
                            withDismissAction = false,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isSubmitEnabled
        ) {
            Text("Submit")
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
data class Course(
    val courseCode: String,
    val courseName: String,
    val careerCode: String,
    val semester: Int,
    val isOptative: Boolean
)
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun GetCourse() {
    var courseCodeText by remember { mutableStateOf("") }
    val semester: MutableState<Int?> = remember { mutableStateOf(null) }
    val sem: MutableState<Int?> = remember { mutableStateOf(null) }
    var courses: List<Course> by remember { mutableStateOf(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val inputVisible = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val resultVisible = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }
    val kbController = LocalSoftwareKeyboardController.current
    val isSubmitEnabled by derivedStateOf {
        courseCodeText.isNotBlank()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visibleState = inputVisible
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "View Courses in Careers",
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = courseCodeText,
                    onValueChange = { courseCodeText = it },
                    label = { Text("Enter Career Code") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = if(semester.value == null) "" else semester.value.toString(),
                    onValueChange = {
                        semester.value = it.toIntOrNull() ?: 1
                        sem.value = it.toIntOrNull() ?: 1},
                    label = { Text("Semester") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        kbController?.hide()
                        val courseCode = courseCodeText
                        courseCodeText = ""
                        GlobalScope.launch(Dispatchers.IO) {
                            val databaseConnection = DatabaseConnection()
                            val connection = databaseConnection.connect()
                            val databaseTask = DatabaseTask()
                            if (connection != null) {
                                courses = databaseTask.getCourse(courseCode, semester.value, connection)
                                connection.close()
                                resultVisible.targetState = true
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = "Error Connecting To the Server",
                                    withDismissAction = false,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isSubmitEnabled
                ) {
                    Text("Fetch Courses")
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        AnimatedVisibility(
            visibleState = resultVisible
        ) {
            inputVisible.targetState = false
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (courses.isNotEmpty()) {
                    Column {
                        Text(
                            text = if(sem.value==null) "Courses in ${courses[0].careerCode}" else "Courses in ${courses[0].careerCode}\n and Semester ${courses[0].semester}",
                            modifier = Modifier
                                .padding(25.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if(sem.value == null)
                        courses.forEach { course ->
                            Text("Course Code: ${course.courseCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Course Name: ${course.courseName}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Career Code: ${course.careerCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Semester: ${course.semester}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Is Optative: ${if(course.isOptative) "Yes" else "No"}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        else
                            courses.forEach { course ->
                                Text("Course Code: ${course.courseCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                                Text("Course Name: ${course.courseName}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                                Text("Career Code: ${course.careerCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                                Text("Is Optative: ${if(course.isOptative) "Yes" else "No"}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                    }
                } else {
                    Text(
                        text = "No Courses exist in records for entered Career",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.black)
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun AddCourseDependency() {
    var courseCode by remember { mutableStateOf("") }
    var requiredCourseCode by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val kbController = LocalSoftwareKeyboardController.current
    val isSubmitEnabled by derivedStateOf {
        courseCode.isNotBlank() && requiredCourseCode.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Course Dependency",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = courseCode,
            onValueChange = { courseCode = it },
            label = { Text("Course Code") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = requiredCourseCode,
            onValueChange = { requiredCourseCode = it },
            label = { Text("Required Course Code") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                kbController?.hide()
                GlobalScope.launch(Dispatchers.IO) {
                    val databaseConnection = DatabaseConnection()
                    val connection = databaseConnection.connect()
                    val databaseTask = DatabaseTask()
                    val query = "INSERT INTO CourseDependency (CourseCode, RequiredCourseCode) " +
                            "VALUES ('${courseCode.trim()}', '${requiredCourseCode.trim()}')"
                    courseCode = ""
                    requiredCourseCode = ""

                    if (connection != null) {
                        databaseTask.addCourseDependency(connection, query, snackbarHostState)
                        connection.close()
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "Error Connecting To the Server",
                            withDismissAction = false,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isSubmitEnabled
        ) {
            Text("Submit")
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
data class CourseDependency(
    val courseCode: String,
    val requiredCourseCode: String
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun GetCourseDependency() {
    var courseCodeText by remember { mutableStateOf("") }
    var cours by remember { mutableStateOf("") }
    var courseDependencies: List<CourseDependency> by remember { mutableStateOf(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val inputVisible = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val resultVisible = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }
    val kbController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visibleState = inputVisible
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Check Course Dependency",
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = courseCodeText,
                    onValueChange = {
                        courseCodeText = it
                        cours = it},
                    label = { Text("Enter Course Code") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        kbController?.hide()
                        val courseCode = courseCodeText
                        courseCodeText = ""
                        GlobalScope.launch(Dispatchers.IO) {
                            val databaseConnection = DatabaseConnection()
                            val connection = databaseConnection.connect()
                            val databaseTask = DatabaseTask()
                            if (connection != null) {
                                courseDependencies = databaseTask.getCourseDependency(courseCode, connection)
                                connection.close()
                                resultVisible.targetState = true
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = "Error Connecting To the Server",
                                    withDismissAction = false,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Fetch Course Dependency")
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        AnimatedVisibility(
            visibleState = resultVisible
        ) {
            inputVisible.targetState = false
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Course Dependency",
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (courseDependencies.isNotEmpty()) {
                    Column {
                        courseDependencies.forEach { courseDependency ->
                            Text("Course Code: ${courseDependency.courseCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Required Course Code: ${courseDependency.requiredCourseCode}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    Text(
                        text = "Course Dependency does not exist for $cours",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.black)
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}




@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun AddProfessor() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val kbController = LocalSoftwareKeyboardController.current
    val insertedProfessorId = remember { mutableStateOf<Int?>(null) }
    val isSubmitEnabled by derivedStateOf {
        firstName.isNotBlank() && lastName.isNotBlank() && isValidEmail(email) && isValidPhone(phoneNumber)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Professor",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                kbController?.hide()
                GlobalScope.launch(Dispatchers.IO) {
                    val databaseConnection = DatabaseConnection()
                    val connection = databaseConnection.connect()
                    val databaseTask = DatabaseTask()
                    val query = "INSERT INTO Professor (FirstName, LastName, Email, PhoneNumber) " +
                            "VALUES ('${firstName.trim()}', '${lastName.trim()}', '${email.trim()}', '${phoneNumber.trim()}')"
                    val professorName = "${firstName.trim()} ${lastName.trim()}"
                    firstName = ""
                    lastName = ""
                    email = ""
                    phoneNumber = ""

                    if (connection != null) {
                        insertedProfessorId.value = databaseTask.addProfessor(connection, query, snackbarHostState)
                        connection.close()
                    }
                    else {
                        snackbarHostState.showSnackbar(
                            message = "Error Connecting To the Server",
                            withDismissAction = false,
                            duration = SnackbarDuration.Short
                        )
                    }
                    withContext(Dispatchers.Main) {
                        if (insertedProfessorId.value != null) {
                            val message = "Professor '$professorName' inserted with ID '${insertedProfessorId.value}'"

                            snackbarHostState.showSnackbar(message = message, withDismissAction = false, duration = SnackbarDuration.Long)

                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isSubmitEnabled
        ) {
            Text("Submit")
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
data class Professor(
    val professorID: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun GetProfessor() {
    var professorIDText by remember { mutableStateOf("") }
    var professors: List<Professor> by remember { mutableStateOf(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val inputVisible = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val resultVisible = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }
    val kbController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visibleState = inputVisible
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Get Professor Details",
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = professorIDText,
                    onValueChange = { professorIDText = it },
                    label = { Text("Enter Professor ID") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        kbController?.hide()
                        val professorID = professorIDText
                        professorIDText = ""
                        GlobalScope.launch(Dispatchers.IO) {
                            val databaseConnection = DatabaseConnection()
                            val connection = databaseConnection.connect()
                            val databaseTask = DatabaseTask()
                            if (connection != null) {
                                professors = databaseTask.getProfessor(professorID, connection)
                                connection.close()
                                resultVisible.targetState = true
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = "Error Connecting To the Server",
                                    withDismissAction = false,
                                    duration = SnackbarDuration.Short
                                )
                            }
                            withContext(Dispatchers.Main) {
                                // Update the UI with the result
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Fetch Professor Details")
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        AnimatedVisibility(
            visibleState = resultVisible
        ) {
            inputVisible.targetState = false
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (professors.isNotEmpty()) {
                    Column {
                        Text(
                            text = "Professor Details",
                            modifier = Modifier
                                .padding(25.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        professors.forEach { professor ->
                            Text("Professor ID: ${professor.professorID}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("First Name: ${professor.firstName}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Last Name: ${professor.lastName}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Email: ${professor.email}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Phone Number: ${professor.phoneNumber}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    Text(
                        text = "Professor ID does not exist in records",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.black)
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Preview
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun AddProfessorCourse() {
    var professorIDText by remember { mutableStateOf("") }
    var courseCodeText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val kbController = LocalSoftwareKeyboardController.current
    val coursesToAdd = remember { mutableStateListOf<String>() }
    val isSubmitEnabled by derivedStateOf {
        coursesToAdd.isNotEmpty() && professorIDText.isNotBlank()
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Professor-Course Association",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = professorIDText,
            onValueChange = { professorIDText = it },
            label = { Text("Enter Professor ID") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Allow the user to add multiple course codes
        TextField(
            value = courseCodeText,
            onValueChange = { courseCodeText = it },
            label = { Text("Enter Course Code") },
            keyboardOptions = KeyboardOptions(imeAction = Done)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {
                        courseCodeText = coursesToAdd[coursesToAdd.size -1]
                        coursesToAdd.removeAt(coursesToAdd.size - 1)
                },
                modifier = Modifier.weight(1f),
                enabled = coursesToAdd.isNotEmpty()
            ) {
                Text("Remove")
            }
            Spacer(modifier = Modifier.width(90.dp))

            Button(
                onClick = {
                    coursesToAdd.add(courseCodeText.trim())
                    courseCodeText = ""
                },
                modifier = Modifier.weight(1f),
                enabled = courseCodeText.isNotBlank() && professorIDText.isNotBlank()
                ) {
                Text("Add")
            }
        }

        // Display the list of courses to be added
        AnimatedVisibility(
            visible = coursesToAdd.isNotEmpty(),
            enter = slideInVertically(animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)),
            exit = slideOutVertically(animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing))
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Courses to Add:")
                coursesToAdd.reversed().forEach { course ->
                    Text(course)
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                kbController?.hide()
                val professorID = professorIDText
                professorIDText = ""
                GlobalScope.launch(Dispatchers.IO) {
                    val databaseConnection = DatabaseConnection()
                    val connection = databaseConnection.connect()
                    val databaseTask = DatabaseTask()
                    var query = "INSERT INTO ProfessorCourse (ProfessorID, CourseCode) " + "VALUES "
                    if (connection != null) {
                        // Insert each course into the database
                        for (course in coursesToAdd) {
                            query += "('${professorID.trim()}', '${course.trim()}'),"
                        }
                        query = query.substring(0,query.length -1)
                        databaseTask.addProfessorCourse(connection, query, snackbarHostState)

                        // Clear the list of courses
                        coursesToAdd.clear()

                        connection.close()
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "Error Connecting To the Server",
                            withDismissAction = false,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isSubmitEnabled
        ) {
            Text("Submit")
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
data class ProfessorCourse(
    val professorID: Int,
    val courseCode: String,
    val firstName: String,
    val lastName: String,
    val courseName: String
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun GetProfessorCourse() {
    var professorIDText by remember { mutableStateOf("") }
    var courseCodeText by remember { mutableStateOf("") }
    var professorCourses: List<ProfessorCourse> by remember { mutableStateOf(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val inputVisible = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val resultVisible = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }
    val kbController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visibleState = inputVisible
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Get Professor-Course Association",
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = professorIDText,
                    onValueChange = { professorIDText = it },
                    label = { Text("Enter Professor ID") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = Done)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        kbController?.hide()
                        val professorID = professorIDText
                        professorIDText = ""
                        courseCodeText = ""

                        GlobalScope.launch(Dispatchers.IO) {
                            val databaseConnection = DatabaseConnection()
                            val connection = databaseConnection.connect()
                            val databaseTask = DatabaseTask()
                            if (connection != null) {
                                val query = "SELECT pc.ProfessorID, pc.CourseCode, p.FirstName, p.LastName, c.CourseName\n" +
                                        "    FROM ProfessorCourse pc\n" +
                                        "    INNER JOIN Course c ON c.CourseCode = pc.CourseCode\n"+
                                        "    INNER JOIN Professor p ON pc.ProfessorID = p.ProfessorID " +
                                "WHERE p.ProfessorID = '${professorID.trim()}'"
                                professorCourses = (databaseTask.getProfessorCourses(query, connection))
                                connection.close()
                                resultVisible.targetState = true
                            } else {
                                snackbarHostState.showSnackbar(
                                message = "Error Connecting To the Server",
                                withDismissAction = false,
                                duration = SnackbarDuration.Long
                                )
                            }
                            withContext(Dispatchers.Main) {
                            // Update the UI with the result
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fetch Professor-Course Association")
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        AnimatedVisibility(
        visibleState = resultVisible
        ) {
            inputVisible.targetState = false
            Column {
                if (professorCourses.isNotEmpty()) {
                    Text(
                        text = "Courses Assigned to ${professorCourses[0].firstName} ${professorCourses[0].lastName}",
                        modifier = Modifier
                            .padding(25.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontSize = 22.sp, color = colorResource(id = R.color.black)),
                        fontWeight = FontWeight.Bold
                    )
                    professorCourses.forEach { professorCourse ->
//                        Text("Professor ID: ${professorCourse.professorID}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                        Text("${professorCourse.courseCode} : ${professorCourse.courseName}", style = TextStyle(fontSize = 18.sp, color = colorResource(id = R.color.black)))
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                else {
                    Text(
                        text = "Professor-Course Association not found in records",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.black)
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}