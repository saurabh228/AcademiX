package app.cardboard.academix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import app.cardboard.academix.ui.theme.AcademiXTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import java.sql.Connection



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AcademiXTheme {
                HomeScreen()
            }
        }
        val databaseTask = DatabaseTask()
        databaseTask.executeQuery()
    }
}



@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD2B48C)), // Light brown color
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.app_name), color = Color.Black) // Replace with your app's name
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AcademiXTheme {
        HomeScreen()
    }
}
