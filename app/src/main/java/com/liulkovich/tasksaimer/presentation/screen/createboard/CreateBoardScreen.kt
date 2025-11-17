package com.liulkovich.tasksaimer.presentation.screen.createboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CreateBoardScreen(
    viewModel: CreateBoardViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is CreateBoardState.Finished -> {
            LaunchedEffect(Unit) { onFinished() }
        }
        is CreateBoardState.Creation -> {
            CreateBoardForm(
                creation = state as CreateBoardState.Creation,
                onCommand = viewModel::processCommand
            )
        }
    }
}

@Composable
fun CreateBoardForm(
    creation: CreateBoardState.Creation,
    onCommand: (CreateBoardCommand) -> Unit
) {
    Scaffold { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onCommand(CreateBoardCommand.Back) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "New Board",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Divider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Board Title *",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                BoardTitleTextField(
                    boardTitle = creation.title,
                    onBoardTitleChange = { title ->
                        onCommand(CreateBoardCommand.InputTitle(title))
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Description (Optional)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                BoardDescriptionTextField(
                    boardDescription = creation.description,
                    onBoardDescriptionChange = { description ->
                        onCommand(CreateBoardCommand.InputDescription(description))
                    }
                )

                Spacer(modifier = Modifier.height(48.dp))

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                Spacer(modifier = Modifier.height(48.dp))

                // Button
                Button(
                    onClick = { onCommand(CreateBoardCommand.SaveBoard) },
                    enabled = creation.isSaveEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Create Board",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun BoardTitleTextField(
    boardTitle: String,
    onBoardTitleChange: (String) -> Unit
) {
    OutlinedTextField(
        value = boardTitle,
        onValueChange = onBoardTitleChange,
        label = { Text("e.g., Marketing Campaign") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun BoardDescriptionTextField(
    boardDescription: String,
    onBoardDescriptionChange: (String) -> Unit
) {

    OutlinedTextField(
        value = boardDescription,
        onValueChange = onBoardDescriptionChange,
        label = { Text("Add details about the purpose of this board...") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp, max = 500.dp)
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 13
    )
}

@Composable
fun NavigationCreateTasksAimerBar(modifier: Modifier = Modifier) {

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        "Boards",
        "Notifications",
        "Profile"
    )
    val selectedIcons = listOf(
        Icons.Filled.Dashboard,
        Icons.Filled.Notifications,
        Icons.Filled.Person
    )
    val unselectedIcons =
        listOf(
            Icons.Outlined.Dashboard,
            Icons.Outlined.Notifications,
            Icons.Outlined.Person
        )
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
        ,
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item,
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
            )
        }
    }
}
