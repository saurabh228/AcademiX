package app.cardboard.academix.actions


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.cardboard.academix.DatabaseConnection
import app.cardboard.academix.DatabaseTask
import app.cardboard.academix.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun ActionView(
    onDismiss: () -> Unit,
    action: @Composable () -> Unit){

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnClickOutside = true,
                dismissOnBackPress = true,
                usePlatformDefaultWidth = false
            )
        ) {

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.Actionbg)),

            ) {
                action()
            }
        }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInfoInput() {
    var lastName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val insertedStudentId = remember { mutableStateOf<Int?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val isSubmitEnabled by remember {
        derivedStateOf {
            lastName.isNotBlank() &&
                    firstName.isNotBlank() &&
                    dob.isNotBlank() &&
                    isValidEmail(email) &&
                    isValidPhone(phone)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Add Student Details",
             modifier = Modifier
                 .padding(25.dp)
                 .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("Date of Birth (DOB)") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") }
        )
        Spacer(modifier = Modifier.height(18.dp))


        Button(
            onClick = {

                    val query = "INSERT INTO Student (LastName, FirstName, DateOfBirth, Email, PhoneNumber) VALUES ('$lastName', '$firstName', '$dob', '$email', '$phone')"
                val studentName = "$firstName $lastName"
                lastName = ""
                firstName = ""
                dob = ""
                email = ""
                phone = ""
                val databaseTask = DatabaseTask()
                GlobalScope.launch(Dispatchers.IO) {
                        val newStudentId = databaseTask.addStu(query)

                    // Update the UI with the newly inserted StudentID
                        withContext(Dispatchers.Main) {
                            insertedStudentId.value = newStudentId
                            if (newStudentId != null) {
                                val message = "Student '$studentName' inserted with ID '$newStudentId'"
                                val actionLabel = "OK"

                                snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel, withDismissAction = false, duration = SnackbarDuration.Long)

                            }
                    }
                }


            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isSubmitEnabled
        ) {
            Text("Submit")
        }

    }
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxWidth()
    )
}
fun isValidEmail(email: String): Boolean {
    return email.contains("@")
}
fun isValidPhone(phone: String): Boolean {
    return phone.length == 10
}



data class Student(
    val studentId: Int,
    val lastName: String,
    val firstName: String,
    val dateOfBirth: String,
    val email: String,
    val phoneNumber: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetStudentDetails() {
    var studentIdText by remember { mutableStateOf("") }
    var students: List<Student> by remember { mutableStateOf(emptyList()) }
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        AnimatedVisibility(
            visibleState = inputVisible
        )
        {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Get Student Details",
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = studentIdText,
                    onValueChange = { studentIdText = it },
                    label = { Text("Enter StudentID") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        // Parse the input text as an integer
                        val studentID = studentIdText.toIntOrNull()
                        studentIdText = ""
                        if (studentID != null) {
                            // Call the function to fetch student details
                            GlobalScope.launch(Dispatchers.IO) {
                                val databaseConnection = DatabaseConnection()
                                val connection = databaseConnection.connect()
                                val databaseTask = DatabaseTask()
                                if (connection != null) {
                                    students =
                                        databaseTask.getStudentDetailsByStudentID(studentID, connection)
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
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Fetch Student Details")
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }

        AnimatedVisibility(
            visibleState = resultVisible
        )
        {
            inputVisible.targetState = false
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display student details if available
                if (students.isNotEmpty()) {
                    Column {
                        students.forEach { student ->
                            Text("Student ID: ${student.studentId}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Last Name: ${student.lastName}",style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("First Name: ${student.firstName}",style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Date of Birth: ${student.dateOfBirth}",style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Email: ${student.email}",style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Text("Phone Number: ${student.phoneNumber}", style = TextStyle(fontSize = 15.sp, color = colorResource(id = R.color.black)))
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                else {

                    Text(
                        text = "StudentID does not exist in records",
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
