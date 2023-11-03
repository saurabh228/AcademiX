package app.cardboard.academix

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import app.cardboard.academix.actions.Career
import app.cardboard.academix.actions.Course
import app.cardboard.academix.actions.CourseDependency
import app.cardboard.academix.actions.Dep
import app.cardboard.academix.actions.Level
import app.cardboard.academix.actions.Professor
import app.cardboard.academix.actions.ProfessorCourse
import app.cardboard.academix.actions.Room
import app.cardboard.academix.actions.Student
import com.mysql.jdbc.Statement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection

class DatabaseTask {
    suspend fun addStu(query: String): Int? {
        var insertedId: Int? = null
        withContext(Dispatchers.IO) {
            val databaseConnection = DatabaseConnection()
            val connection = databaseConnection.connect()

            try {
                val statement = connection?.createStatement()
                val affectedRows = statement?.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)

                // Retrieve the inserted ID
                val generatedKeys = statement?.generatedKeys
                if (generatedKeys?.next() == true) {
                    insertedId = generatedKeys.getInt(1)
                }

                // Close the resources
                statement?.close()
                connection?.close()

            } catch (e: Exception) {
                Log.e("connection", "Failed: ${e.message}")
                e.printStackTrace()
            } finally {
                connection?.close()
            }
        }
        return insertedId
    }
    fun getStu(studentID: Int, connection: Connection): List<Student> {
        val students = mutableListOf<Student>()
        try {
            val query = "SELECT * FROM Student WHERE StudentID = $studentID"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

             while (resultSet.next()) {
                 val student = Student(
                     studentId = resultSet.getInt("StudentID"),
                     lastName = resultSet.getString("LastName"),
                     firstName = resultSet.getString("FirstName"),
                     dateOfBirth = resultSet.getString("DateOfBirth"),
                     email = resultSet.getString("Email"),
                     phoneNumber = resultSet.getString("PhoneNumber")
                 )
                 students.add(student)
             }
        } catch (e: Exception) {
            Log.e("connection", "Failed: ${e.message}")
            e.printStackTrace()
        }
        return students
    }


    suspend fun addLevel(connection: Connection, query: String, snackbarHostState: SnackbarHostState) {
        try {
            val statement = connection.createStatement()
            val affectedRows = statement.executeUpdate(query)

            if (affectedRows > 0) {
                snackbarHostState.showSnackbar("Level inserted successfully")
            } else {
                snackbarHostState.showSnackbar("Failed to insert level", duration = SnackbarDuration.Long)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("An error occurred: ${e.message}", duration = SnackbarDuration.Long)
        }
    }
    fun getLevel(connection: Connection): List<Level> {
        val levels = mutableListOf<Level>()
        try {
            val query = "SELECT * FROM CareerLevel"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val level = Level(
                    levelCode = resultSet.getString("CareerLevelCode"),
                    levelName = resultSet.getString("CareerLevelName")
                )
                levels.add(level)
            }
        } catch (e: Exception) {
            Log.e("connection", "Failed: ${e.message}")
            e.printStackTrace()

        }
        return levels
    }


    suspend fun addDepartment(connection: Connection, query: String, snackbarHostState: SnackbarHostState) {
        try {
            val statement = connection.createStatement()
            val affectedRows = statement.executeUpdate(query)

            if (affectedRows > 0) {
                snackbarHostState.showSnackbar("Department inserted successfully")
            } else {
                snackbarHostState.showSnackbar("Failed to insert Department", duration = SnackbarDuration.Long)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("An error occurred: ${e.message}", duration = SnackbarDuration.Long)
        }
    }
    fun getDepartments(connection: Connection): List<Dep> {
        val deps = mutableListOf<Dep>()
        try {
            val query = "SELECT * FROM Department"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val dep = Dep(
                    departmentCode = resultSet.getString("DepartmentCode"),
                    departmentName = resultSet.getString("DepartmentName")
                )
                deps.add(dep)
            }
        }
        catch (e: Exception) {
            Log.e("connection", "Failed: ${e.message}")
            e.printStackTrace()

        }
        return deps
    }


    suspend fun addCareer(connection: Connection, query: String, snackbarHostState: SnackbarHostState) {
        try {
            val statement = connection.createStatement()
            val affectedRows = statement.executeUpdate(query)

            if (affectedRows > 0) {
                snackbarHostState.showSnackbar("Career inserted successfully")
            } else {
                snackbarHostState.showSnackbar("Failed to insert Career", duration = SnackbarDuration.Long)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("An error occurred: ${e.message}", duration = SnackbarDuration.Long)
        }
    }
    fun getCareer(careerCode: String, connection: Connection): List<Career> {
        val careers = mutableListOf<Career>()
        try {
            val query = "SELECT * FROM Career WHERE CareerCode = '${careerCode.trim()}'"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val career = Career(
                    careerCode = resultSet.getString("CareerCode"),
                    careerName = resultSet.getString("CareerName"),
                    departmentCode = resultSet.getString("DepartmentCode"),
                    careerLevelCode = resultSet.getString("CareerLevelCode"),
                    durationYears = resultSet.getInt("DurationYears"),
                    requiredOptativeCourses = resultSet.getInt("RequiredOptativeCourses")
                )
                careers.add(career)
            }
        } catch (e: Exception) {
            Log.e("connection", "Failed: ${e.message}")
            e.printStackTrace()

        }
        return careers
    }


    suspend fun addCourse(connection: Connection, query: String, snackbarHostState: SnackbarHostState) {
        try {
            val statement = connection.createStatement()
            val affectedRows = statement.executeUpdate(query)

            if (affectedRows > 0) {
                snackbarHostState.showSnackbar("Course inserted successfully")
            } else {
                snackbarHostState.showSnackbar("Failed to insert Course", duration = SnackbarDuration.Long)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("An error occurred: ${e.message}", duration = SnackbarDuration.Long)
        }
    }
    fun getCourse(careerCode: String, semester:Int?, connection: Connection): List<Course> {
        val courses = mutableListOf<Course>()
        try {
            val query = if(semester==null)"SELECT * FROM Course WHERE CareerCode = '${careerCode.trim()}'" else "SELECT * FROM Course WHERE CareerCode = '${careerCode.trim()}' AND Semester = '$semester'"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val course = Course(
                    courseName = resultSet.getString("CourseName"),
                    courseCode = resultSet.getString("CourseCode"),
                    careerCode = resultSet.getString("CareerCode"),
                    semester = resultSet.getInt("Semester"),
                    isOptative = resultSet.getBoolean("Optative")
                )
                courses.add(course)
            }
        } catch (e: Exception) {
            Log.e("connection", "Failed: ${e.message}")
            e.printStackTrace()

        }
        return courses
    }


    suspend fun addCourseDependency(connection: Connection, query: String, snackbarHostState: SnackbarHostState) {
        try {
            val statement = connection.createStatement()
            val affectedRows = statement.executeUpdate(query)

            if (affectedRows > 0) {
                snackbarHostState.showSnackbar("Course Dependency inserted successfully")
            } else {
                snackbarHostState.showSnackbar("Failed to insert Course Dependency", duration = SnackbarDuration.Long)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("An error occurred: ${e.message}", duration = SnackbarDuration.Long)
        }
    }
    fun getCourseDependency(courseCode: String, connection: Connection): List<CourseDependency> {
        val courseDependencies = mutableListOf<CourseDependency>()
        try {
            val query = "SELECT * FROM CourseDependency WHERE CourseCode = '${courseCode.trim()}'"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val courseDependency = CourseDependency(
                    courseCode = resultSet.getString("CourseCode"),
                    requiredCourseCode = resultSet.getString("RequiredCourseCode")
                )
                courseDependencies.add(courseDependency)
            }
        } catch (e: Exception) {
            Log.e("connection", "Failed: ${e.message}")
            e.printStackTrace()

        }
        return courseDependencies
    }


    suspend fun addProfessor(connection: Connection, query: String, snackbarHostState: SnackbarHostState):Int? {
        var insertedId: Int? = null
        try {
            val statement = connection.createStatement()
            val affectedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)

            val generatedKeys = statement?.generatedKeys
            if (generatedKeys?.next() == true) {
                insertedId = generatedKeys.getInt(1)
            }

            if (affectedRows <= 0)  {
                snackbarHostState.showSnackbar("Failed to insert Professor", duration = SnackbarDuration.Long)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("An error occurred: ${e.message}", duration = SnackbarDuration.Long)
        }
        return insertedId
    }
    fun getProfessor(professorID: String, connection: Connection): List<Professor> {
        val professors = mutableListOf<Professor>()
        try {
            val query = "SELECT * FROM Professor WHERE ProfessorID = '$professorID'"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val professor = Professor(
                    professorID = resultSet.getInt("ProfessorID"),
                    firstName = resultSet.getString("FirstName"),
                    lastName = resultSet.getString("LastName"),
                    email = resultSet.getString("Email"),
                    phoneNumber = resultSet.getString("PhoneNumber")
                )
                professors.add(professor)
            }
        } catch (e: Exception) {
            Log.e("connection", "Failed: ${e.message}")
            e.printStackTrace()

        }
        return professors
    }


    suspend fun addProfessorCourse(connection: Connection, query: String, snackbarHostState: SnackbarHostState) {
        try {
            val statement = connection.createStatement()
            val affectedRows = statement.executeUpdate(query)

            if (affectedRows > 0) {
                snackbarHostState.showSnackbar("Professor-Course Association inserted successfully")
            } else {
                snackbarHostState.showSnackbar("Failed to insert Professor-Course Association", duration = SnackbarDuration.Long)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("An error occurred: ${e.message}", duration = SnackbarDuration.Long)
        }
    }
    fun getProfessorCourses(query: String, connection: Connection): List<ProfessorCourse> {
        val professorCourses = mutableListOf<ProfessorCourse>()
        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val professorCourse = ProfessorCourse(
                    professorID = resultSet.getInt("ProfessorID"),
                    courseCode = resultSet.getString("CourseCode"),
                    firstName = resultSet.getString("FirstName"),
                    lastName = resultSet.getString("LastName"),
                    courseName = resultSet.getString("CourseName")
                )
                professorCourses.add(professorCourse)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("connection", "Failed: ${e.message}")
        }
        return professorCourses
    }



    suspend fun addRoom(connection: Connection, query: String, snackbarHostState: SnackbarHostState) {
        try {
            val statement = connection.createStatement()
            val affectedRows = statement.executeUpdate(query)

            if (affectedRows > 0) {
                snackbarHostState.showSnackbar("Rooms added successfully")
            } else {
                snackbarHostState.showSnackbar("Failed to add Room", duration = SnackbarDuration.Long)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.showSnackbar("An error occurred: ${e.message}", duration = SnackbarDuration.Long)
        }
    }
    fun getRoom(query: String, connection: Connection): List<Room> {
        val rooms = mutableListOf<Room>()
        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val room = Room(
                    roomCode = resultSet.getString("RoomCode"),
                    roomName = resultSet.getString("RoomName"),
                    roomCapacity = resultSet.getInt("RoomCapacity"),
                    location = resultSet.getString("Location"),
                )
                rooms.add(room)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("connection", "Failed: ${e.message}")
        }
        return rooms
    }



}
