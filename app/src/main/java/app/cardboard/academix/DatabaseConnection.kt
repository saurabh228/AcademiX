package app.cardboard.academix

import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnection {
    private val url = "jdbc:mysql://192.168.108.177:3306/academixdb"
    private val username = "admin1"
    private val password = "admin1@AX"

    fun connect(): Connection? {
        var connection: Connection? = null

        try {
            Log.i("DatabaseConnection", "Connected to the database") // Log the successful connection
            Class.forName("com.mysql.jdbc.Driver")// Load the MySQL JDBC driver
            connection = DriverManager.getConnection(url, username, password)
        } catch (e: SQLException) {
            Log.e("DatabaseConnection", "SQL Exception: ${e.message}")
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            Log.e("DatabaseConnection", "Class Not Found Exception: ${e.message}")
            e.printStackTrace()
        }

        return connection
    }
}
