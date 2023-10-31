package app.cardboard.academix.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cardboard.academix.actions.GetStudentDetails
import app.cardboard.academix.actions.StudentInfoInput

data class ButtonInfo(val label: String, val action: @Composable () -> Unit)


@Composable
fun ButtonRowScreen(onButtonClick: (action: @Composable () -> Unit) -> Unit) {

    val buttons = listOf(
        ButtonInfo("View Student") { GetStudentDetails() },
        ButtonInfo("Add Student") { StudentInfoInput() },
        ButtonInfo("View Batch") {  },
        // Add more buttons and actions as needed
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 15.dp),
        content = {
            items(items = buttons.chunked(2)) { rowButtons ->
                ButtonRow(rowButtons, onButtonClick)
            }
        }
    )
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
                onClick =
                {
                    onButtonClick(buttonInfo.action)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(text = buttonInfo.label)
            }
        }
    }
}

