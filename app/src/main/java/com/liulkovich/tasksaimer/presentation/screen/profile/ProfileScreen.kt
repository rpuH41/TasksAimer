package com.liulkovich.tasksaimer.presentation.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liulkovich.tasksaimer.domain.entiity.User

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onOpenBoardClick: (boardId: String, boardTitle: String) -> Unit,
  // navController: NavHostController,
    modifier: Modifier = Modifier
){

    val state by viewModel.state.collectAsState()
    var showAddContactDialog by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()

            ) {

                Icon(Icons.Default.Person, "Avatar")
                Text(
                    text = state.currentUser?.firstName ?: "No name",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = state.currentUser?.email ?: "No email",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        item {
            MyContactsSection(
                contacts = state.contacts.map { it.firstName ?: "No name" },
                onAddClick = { showAddContactDialog = true },
                onContactClick = { idx -> /* open contact */ },
                onMenuClick = { idx -> /* show menu */ },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text("My Boards", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                state.boards.forEach { board ->
                    BoardCard(
                        title = board.title ?: "Untitled Board",
                        subtitle = "${board.tasksCount ?: 0} active tasks",
                        onClick = { onOpenBoardClick(board.id ?: "", board.title ?: "") }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.processCommand(ProfileCommand.Logout)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Button Logout",
                    tint = LocalContentColor.current
                )
                Spacer(
                    modifier.padding(horizontal = 5.dp)
                )
               Text(
                   text = "Logout",
                   fontWeight = FontWeight.Bold,
                   style = MaterialTheme.typography.titleMedium,
                   fontSize = 17.sp,
                   color = LocalContentColor.current
               )
            }
        }

    }
    if (showAddContactDialog) {
        SelectUserPopup(
            users = state.allUsers.filter { it.id != state.currentUserId }, // не показываем себя
            onAddClick = { user ->
                viewModel.processCommand(
                    ProfileCommand.AddContact(
                        userId = state.currentUserId!!,
                        contact = user
                    )
                )
                showAddContactDialog = false
            },
            onDismiss = { showAddContactDialog = false }
        )
    }


}

@Composable
fun BoardCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}


@Composable
fun MyContactsSection(
    contacts: List<String>,
    onAddClick: () -> Unit,
    onContactClick: (index: Int) -> Unit,
    onMenuClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val cardColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "My Contacts",
                    style = MaterialTheme.typography.titleMedium,
                   // color = contentColor,
                    fontSize = 18.sp
                )

                IconButton(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add contact",
                        //tint = contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                contacts.forEachIndexed { index, name ->
                    MyContactCard(
                        fullName = name,
                        onClick = { onContactClick(index) },
                        onMenuClick = { onMenuClick(index) },
                        contentColor = contentColor
                    )
                }
            }
        }
    }
}

@Composable
fun MyContactCard(
    fullName: String,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            tint = contentColor,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = fullName,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "menu",
                tint = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun SelectUserPopup(
    users: List<User>,
    onAddClick: (User) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            val filtered = users.filter { user ->
                user.firstName?.contains(search, ignoreCase = true) == true ||
                        user.email?.contains(search, ignoreCase = true) == true
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                filtered.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.firstName ?: "No name")
                            Text(
                                user.email ?: "No email",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }

                        IconButton(onClick = { onAddClick(user) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
            }
        }
    }
}