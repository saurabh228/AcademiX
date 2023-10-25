package app.cardboard.academix

import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DatabaseTask {
    fun executeQuery() {
        GlobalScope.launch(Dispatchers.IO) {
            Log.i("connection", "entered function")
            val databaseConnection = DatabaseConnection()
            val connection = databaseConnection.connect()
            Log.i("connection", "connected")

            try {
                val statement = connection?.createStatement()
                val affectedRows = statement?.executeUpdate("INSERT INTO Student (LastName, FirstName, DateOfBirth, Email, PhoneNumber) VALUES ('saini', 'sorb', '1995-05-15', 'john.doe@email.com', '123-456-7890')")

                // Commit the transaction
//                connection?.commit()   autocommit is on

                Log.i("connection", "Query executed, $affectedRows row(s) affected")

                // Close the resources

//                statement?.close()
//                connection?.close()

                withContext(Dispatchers.Main) {
                    // Update the UI with the results on the main thread
                }
            } catch (e: Exception) {
                Log.e("connection", "Failed: ${e.message}")

                e.printStackTrace()
            } finally {
                connection?.close()
            }
        }
    }


}


