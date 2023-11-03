package app.cardboard.academix.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.cardboard.academix.actions.AddCourse
import app.cardboard.academix.actions.AddCourseDependency
import app.cardboard.academix.actions.AddProfessor
import app.cardboard.academix.actions.AddProfessorCourse
import app.cardboard.academix.actions.AddRoom
import app.cardboard.academix.actions.GetCareer
import app.cardboard.academix.actions.GetCareerLevels
import app.cardboard.academix.actions.GetCourse
import app.cardboard.academix.actions.GetCourseDependency
import app.cardboard.academix.actions.GetDepartments
import app.cardboard.academix.actions.GetProfessor
import app.cardboard.academix.actions.GetProfessorCourse
import app.cardboard.academix.actions.GetRoom
import app.cardboard.academix.actions.GetStudent
import app.cardboard.academix.actions.SetCareer
import app.cardboard.academix.actions.SetCareerLevel
import app.cardboard.academix.actions.SetDepartment
import app.cardboard.academix.actions.SetStudent


data class ButtonInfo(val label: String, val action: @Composable () -> Unit)
sealed class Screen(val route: String, val lable: String) {
    object CareerPlan : Screen("CareerPlan", "Career Plan")
    object StuEnroll : Screen("StuEnroll", "Stu & Enroll")
    object YearPlan : Screen("YearPlan", "Year Plan")

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonRowScreen(onButtonClick: (action: @Composable () -> Unit) -> Unit) {

    val careerPlan = listOf(
        ButtonInfo("View Career Levels") { GetCareerLevels() },
        ButtonInfo("Add Career Level") { SetCareerLevel() },

        ButtonInfo("View Departments") { GetDepartments() },
        ButtonInfo("Add Department") { SetDepartment() },

        ButtonInfo("View Careers") { GetCareer() },
        ButtonInfo("Add Career") { SetCareer() },

        ButtonInfo("View Courses in Careers") { GetCourse() },
        ButtonInfo("Add Courses") { AddCourse() },

        ButtonInfo("Check Course Dependency") { GetCourseDependency() },
        ButtonInfo("Add Course Dependency") { AddCourseDependency() },

        ButtonInfo("View Professor") { GetProfessor() },
        ButtonInfo("Add Professor") { AddProfessor() },

        ButtonInfo("View Assigned Courses") { GetProfessorCourse() },
        ButtonInfo("Assign Course to Professor") { AddProfessorCourse() }
    )
    val studentEnrollment = listOf<ButtonInfo>(
        ButtonInfo("View Student") { GetStudent() },
        ButtonInfo("Add Student") { SetStudent() },

        ButtonInfo("View Students Career") {  },
        ButtonInfo("Enroll Student in Career") {  },

        ButtonInfo("View Enrolled Courses") {  },
        ButtonInfo("Enroll into Course") {  },

        ButtonInfo("Check Career Status") {  },
        ButtonInfo("Update Career Status") {  },

        ButtonInfo("View Evaluation Types") {  },
        ButtonInfo("Add Evaluation Type") {  },

        ButtonInfo("View Evaluation Result") {  },
        ButtonInfo("Add Evaluation Result") {  },

    )
    val yearPlan = listOf<ButtonInfo>(
        ButtonInfo("View Room Details") { GetRoom() },
        ButtonInfo("Add Room") {  AddRoom() },

        ButtonInfo("View Course Occurrence") {  },
        ButtonInfo("Add Course Occurrence") {  },

        ButtonInfo("View Assistant Professor") {  },
        ButtonInfo("Add Assistant Professor") {  },

        ButtonInfo("View Schedule") {  },
        ButtonInfo("Add Schedule") {  },
    )

    val items = listOf(
        Screen.CareerPlan,
        Screen.StuEnroll,
        Screen.YearPlan
    )

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
                        label = { Text(screen.lable) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost( navController, startDestination = Screen.YearPlan.route, Modifier.padding(innerPadding))
        {
            composable(Screen.CareerPlan.route) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 15.dp),
                    content = {
                        items(items = careerPlan.chunked(2)) { rowButtons ->
                            ButtonRow(rowButtons, onButtonClick)
                        }
                    }
                )
            }
            composable(Screen.StuEnroll.route) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 15.dp),
                    content = {
                        items(items = studentEnrollment.chunked(2)) { rowButtons ->
                            ButtonRow(rowButtons, onButtonClick)
                        }
                    }
                )
            }
            composable(Screen.YearPlan.route) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 15.dp),
                    content = {
                        items(items = yearPlan.chunked(2)) { rowButtons ->
                            ButtonRow(rowButtons, onButtonClick)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun ButtonRow(buttons: List<ButtonInfo>, onButtonClick: (action: @Composable () -> Unit) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (buttonInfo in buttons) {
            Button(
                onClick ={
                    onButtonClick(buttonInfo.action)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),

            ) {
                Text(text = buttonInfo.label, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }
}




