package app.cardboard.academix

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.cardboard.academix.ui.theme.AcademiXTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import app.cardboard.academix.actions.ActionView
import app.cardboard.academix.components.ButtonRowScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AcademiXTheme {
                MyApp {
                    TopAppBar()
                }
            }
        }

    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    Surface( modifier = Modifier.fillMaxSize()
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar()
{
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "AcademiX",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    )
    {
            innerPadding ->
        MainScreen(innerPadding)
    }
}


@Composable
fun MainScreen(
    innerPadding: PaddingValues
){
    var showDialog by remember { mutableStateOf(false) }
    var isDialogActive by remember { mutableStateOf(false) }
    var selectedButtonAction by remember { mutableStateOf<( @Composable () -> Unit)?>(null) }


    Surface(
        modifier = Modifier.padding(innerPadding),
        color = colorResource(id = R.color.homebg)
    ) {
        ButtonRowScreen { action ->
            showDialog = true
            selectedButtonAction = action
        }

        if (showDialog) {
            ActionView(
                onDismiss = {
                    showDialog = false
                    selectedButtonAction = null
                    isDialogActive = false
                },
                action = {
                    selectedButtonAction?.invoke()
//                    showDialog = false
//                    selectedButtonAction = null
                    isDialogActive = true
                }
            )
        }
    }
}







@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    AcademiXTheme {
        MyApp {
            TopAppBar()
        }
    }
}
