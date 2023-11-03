package app.cardboard.academix.actions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


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
//                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.Actionbg)),

            ) {
                action()
            }
        }


}

