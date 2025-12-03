package com.liulkovich.tasksaimer.presentation.screen.createboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CreateBoardScreen(
    onBackClick: () -> Unit,
    viewModel: CreateBoardViewModel = hiltViewModel(),
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    if (state is CreateBoardState.Finished) {
        LaunchedEffect(Unit) { onSaveClick() }
    }

    if (state is CreateBoardState.Creation) {
        val creation = state as CreateBoardState.Creation

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 100.dp),
            //verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            //Spacer(Modifier.height(8.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Board Title *",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            BoardTitleTextField(
                boardTitle = creation.title,
                onBoardTitleChange = { viewModel.processCommand(CreateBoardCommand.InputTitle(it)) }
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Description (Optional)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            BoardDescriptionTextField(
                boardDescription = creation.description,
                onBoardDescriptionChange = { viewModel.processCommand(CreateBoardCommand.InputDescription(it)) }
            )
            Spacer(Modifier.height(56.dp))
            Button(
                onClick = { viewModel.processCommand(CreateBoardCommand.SaveBoard) },
                enabled = creation.isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Board", fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))
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
