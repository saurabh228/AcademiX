package app.cardboard.academix

import android.util.Log
import app.cardboard.academix.actions.Student
import com.mysql.jdbc.Statement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.ResultSet

class DatabaseTask {
    suspend fun addStu(query: String): Int? {
        var insertedId: Int? = null
        withContext(Dispatchers.IO) {
            var databaseConnection = DatabaseConnection()
            var connection = databaseConnection.connect()

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



    fun getStudentDetailsByStudentID(studentID: Int, connection: Connection): List<Student> {
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
            null
        }
        return students
    }



}
