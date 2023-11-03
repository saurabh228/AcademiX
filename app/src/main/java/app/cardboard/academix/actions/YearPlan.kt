package app.cardboard.academix.actions

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cardboard.academix.DatabaseConnection
import app.cardboard.academix.DatabaseTask
import app.cardboard.academix.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.material.Switch
import androidx.compose.material3.Divider
import androidx.compose.material3.RangeSlider
import kotlinx.coroutines.DelicateCoroutinesApi

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    DelicateCoroutinesApi::class
)
@Composable
fun AddRoom() {
    var roomCodeText by remember { mutableStateOf("") }
    var roomNameText by remember { mutableStateOf("") }
    var roomCapacityText by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val kbController = LocalSoftwareKeyboardController.current
    val roomsToAdd = remember { mutableStateListOf<Room>() }

    val isAddEnabled by derivedStateOf {
        roomCodeText.isNotBlank() && roomNameText.isNotBlank() && roomCapacityText.isNotBlank() && locationText.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Rooms",
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 30.sp, color = colorResource(id = R.color.black)),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = roomCodeText,
            onValueChange = { roomCodeText = it },
            label = { Text("Room Code") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = roomNameText,
            onValueChange = { roomNameText = it },
            label = { Text("Room Name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = roomCapacityText,
            onValueChange = { roomCapacityText = it },
            label = { Text("Room Capacity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = locationText,
            onValueChange = { locationText = it },
            label = { Text("Location") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    roomCodeText = roomsToAdd[roomsToAdd.size-1].roomCode
                    roomNameText = roomsToAdd[roomsToAdd.size-1].roomName
                    roomCapacityText = roomsToAdd[roomsToAdd.size-1].roomCapacity.toString()
                    roomsToAdd.removeAt(roomsToAdd.size - 1)
                },
                modifier = Modifier.weight(1f),
                enabled = roomsToAdd.isNotEmpty()
            ) {
                Text("Remove")
            }
            Spacer(modifier = Modifier.width(90.dp))
            Button(
                onClick = {
                    kbController?.hide()
                    val room = Room(
                        roomCode = roomCodeText.trim(),
                        roomName = roomNameText.trim(),
                        roomCapacity = roomCapacityText.toInt(),
                        location = locationText.trim()
                    )
                    roomsToAdd.add(room)

                    // Clear the text fields
                    roomCodeText = ""
                    roomNameText = ""
                    roomCapacityText = ""
                },
                modifier = Modifier.weight(1f),
                enabled = isAddEnabled
            ) {
                Text("Add")
            }
        }

        AnimatedVisibility(
            visible = roomsToAdd.isNotEmpty(),
            enter = slideInVertically(animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)),
            exit = slideOutVertically(animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Rooms to Add:")
                roomsToAdd.reversed().forEach { room ->
                    Text(room.roomCode)
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))
        var notClicked = true
        Button(
        onClick = {
            notClicked = false
            kbController?.hide()
            if (roomsToAdd.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val databaseConnection = DatabaseConnection()
                    val connection = databaseConnection.connect()
                    val databaseTask = DatabaseTask()

                    if (connection != null) {
                        var query = "INSERT INTO Room (RoomCode, RoomName, RoomCapacity, Location) " + "VALUES"
                        for (room in roomsToAdd) {
                            query += "('${room.roomCode}', '${room.roomName}', ${room.roomCapacity}, '${room.location}'),"
                        }
                        query = query.substring(0,query.length-1)
                        databaseTask.addRoom(connection, query, snackbarHostState)
                        roomsToAdd.clear()
                        connection.close()
                    } else {
                        snackbarHostState.showSnackbar(
                            message = "Error Connecting To the Server",
                            withDismissAction = false,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }
            notClicked = true
                  },
        modifier = Modifier.fillMaxWidth(),
        enabled = roomsToAdd.isNotEmpty() && notClicked
        ) {
            Text("Submit")
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
data class Room(
    val roomCode: String,
    val roomName: String,
    val roomCapacity: Int,
    val location: String
)
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
@Preview
@Composable
fun GetRoom(){
    var roomCode by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var rooms: List<Room> by remember { mutableStateOf(emptyList()) }
    var sliderPosition by remember { mutableStateOf(0f..200f) }
    var capacityRange by remember { mutableStateOf(0..200) }
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
    val byCode = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }
    val byLocation = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }
    val notByLocation = remember {
        MutableTransitionState(false).apply {
            targetState = !(byLocation.targetState)
        }
    }
    val kbController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isSubmitEnabled by derivedStateOf {
        (!byCode.targetState || roomCode.isNotBlank()) && (!byLocation.targetState || location.isNotBlank())
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        AnimatedVisibility(visibleState = inputVisible) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Get Rooms",
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 25.sp, color = colorResource(id = R.color.black)),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray, thickness = 2.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Search by Room Code")
                    Switch(
                        checked = byCode.targetState,
                        onCheckedChange = { byCode.targetState = it
                                          if(!it) roomCode=""},
                    )
                }
                 Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Search by Location")
                        Switch(
                            checked = byLocation.targetState,
                            onCheckedChange = { byLocation.targetState = it
                                              if(!it) location=""},
                        )
                    }

                Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray, thickness = 2.dp)
                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(visibleState = byCode) {
                    TextField(
                        value = roomCode,
                        onValueChange = { roomCode = it },
                        label = { Text(text = "Room Code") }
                    )
                }
                AnimatedVisibility(visibleState = byLocation) {
                    TextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(text = "Location") }
                    )
                }
                AnimatedVisibility(visibleState = notByLocation) {
                    Text(text = "Rooms from all Locations", modifier = Modifier.padding(8.dp))
                }
                var notClicked = true
                Button(
                    onClick = {
                        kbController?.hide()
                        notClicked = false

                        GlobalScope.launch(Dispatchers.IO) {
                            val databaseConnection = DatabaseConnection()
                            val connection = databaseConnection.connect()
                            val databaseTask = DatabaseTask()
                            if (connection != null) {
                                var query = "SELECT RoomCode, RoomName, RoomCapacity, Location\n" +
                                        "    FROM Room\n"
                                if(roomCode.isNotBlank()) query +="WHERE RoomCode = '${roomCode.trim()}'"
                                if(roomCode.isNotBlank() && location.isNotBlank()) query+= "AND"
                                if(location.isNotBlank()) query+= "WHERE Location = '${location.trim()}'"

                                rooms = (databaseTask.getRoom(query, connection))
                                connection.close()
                                resultVisible.targetState = true
                                notClicked = true
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = "Error Connecting To the Server",
                                    withDismissAction = false,
                                    duration = SnackbarDuration.Long
                                )
                                notClicked = true
                            }
                            
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isSubmitEnabled && notClicked
                ) {
                    Text("Submit")
                }
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }

        AnimatedVisibility(visibleState = resultVisible) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                inputVisible.targetState = false

                if (rooms.isNotEmpty()) {
                    Text(
                        text = if (!byLocation.targetState) "Rooms" else "Rooms in ${location.trim()}",
                        modifier = Modifier
                            .padding(25.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = colorResource(id = R.color.black)
                        ),
                        fontWeight = FontWeight.Bold
                    )
//                    Spacer(modifier = Modifier.height(16.dp))
                    RangeSlider(
                        value = sliderPosition,
                        steps = 9,
                        onValueChange = { range -> sliderPosition = range },
                        valueRange = 0f..200f,
                        onValueChangeFinished = {
                            // launch some business logic update with the state you hold
                            // viewModel.updateSelectedSliderValue(sliderPosition)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${sliderPosition.start.toInt()}",
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = {
                                capacityRange = sliderPosition.start.toInt()..sliderPosition.endInclusive.toInt()
                            },
                            modifier = Modifier.weight(2f),
                            enabled = (capacityRange.first != sliderPosition.start.toInt() || capacityRange.last != sliderPosition.endInclusive.toInt())
                        ) {
                            Text(
                                text = "Set Capacity",
                                textAlign = TextAlign.Center
                            )
                        }

                        Text(
                            text = " ${sliderPosition.endInclusive.toInt()}",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray, thickness = 2.dp)
                    LazyColumn()
                    {
                        item {
                            rooms.forEach { room ->
                                AnimatedVisibility(visible = room.roomCapacity >= capacityRange.first && room.roomCapacity <= capacityRange.last)
                                {
                                    Column {
                                        Text(
                                            "Room Name: ${room.roomName}",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                color = colorResource(id = R.color.black)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            "Room code: ${room.roomCode}",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                color = colorResource(id = R.color.black)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            "Capacity: ${room.roomCapacity}",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                color = colorResource(id = R.color.black)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        if (!byLocation.targetState) {
                                            Text(
                                                "Location: ${room.location}",
                                                style = TextStyle(
                                                    fontSize = 18.sp,
                                                    color = colorResource(id = R.color.black)
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp)
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }
                                        Divider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = Color.Gray,
                                            thickness = 2.dp
                                        )
                                    }
                                }
                            }
                        }

                    }


                }
                

                else {
                    Text(
                        text = if (!byLocation.targetState) "No Rooms Found" else "No Rooms Found in ${location.trim()}",
                        modifier = Modifier
                            .padding(25.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = colorResource(id = R.color.black)
                        ),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}


