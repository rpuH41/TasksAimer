package com.liulkovich.tasksaimer.presentation.screen.auth

import android.R.attr.password
import android.R.attr.text
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.liulkovich.tasksaimer.R

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
    onSignInClick: () -> Unit
){

    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val newEmail by viewModel.newEmail.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isButtonEnabled by viewModel.isButtonEnabled.collectAsState()

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_circle),
                contentDescription = "app logo",
                modifier = Modifier
                    .size(200.dp, 150.dp)
                    .padding(bottom = 30.dp)
            )
            Text(
                modifier = Modifier
                    .padding(bottom = 15.dp),
                text = "Create Account",
                style = TextStyle(
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                modifier = Modifier
                    .padding(bottom = 45.dp),
                text = "Let's get you started.",
                style = TextStyle(
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .weight(1f),
                    text = "First Name",
                    style = TextStyle(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Start
                )
                Text(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .weight(1f),
                    text = "Last Name",
                    style = TextStyle(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Start
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FirstNameTextField(
                    firstName = firstName,
                    onFirstNameChange = viewModel::onFirstNameChanged,
                    modifier = Modifier
                        .weight(1f)
                )
                LastNameTextField(
                    lastName = lastName,
                    onLastNameChange = viewModel::onLastNameChanged,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .fillMaxWidth(),
                text = "Email",
                style = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Start
            )
            NewEmailTextField(
                newEmail = newEmail,
                onNewEmailChange = viewModel::onEmailChanged
            )
            Text(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .fillMaxWidth(),
                text = "Password",
                style = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Start
            )
            NewPasswordTextField(
                newPassword = newPassword,
                onPasswordChange = viewModel::onPasswordChanged
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp, start = 15.dp)
                    .fillMaxWidth(),
                text = "Password confirmation",
                style = TextStyle(
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Start
            )
            ConfirmPasswordTextField(
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChanged
            )
            Button( modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 15.dp, end = 15.dp,),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = { viewModel.signUp() },
                enabled = isButtonEnabled
            ) {
                Text(
                    modifier = Modifier,
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            Row(
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = TextStyle(
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
                TextButton(onClick = { onSignInClick() }) {
                    Text(
                        text = "Sign In",
                        style = TextStyle(
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
        }
}

@Composable
fun FirstNameTextField(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    modifier: Modifier
){
    TextField(

        modifier = modifier
            .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface
        ),
        value = firstName,
        onValueChange = onFirstNameChange,
        placeholder = {
            Text(
                text = "John",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
    )
}

@Composable
fun LastNameTextField(
    lastName: String,
    onLastNameChange: (String) -> Unit,
    modifier: Modifier
)
{
    TextField(
        modifier = modifier
            .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface
        ),
        value = lastName,
        onValueChange = onLastNameChange,
        placeholder = {
            Text(
                text = "Doe",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        },
    )
}

@Composable
fun NewEmailTextField(
    newEmail: String,
    onNewEmailChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier
            .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface
        ),
        value = newEmail,
        onValueChange = onNewEmailChange,
        placeholder = {
            Text(
                text = "Enter your email",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
    )
}

@Composable
fun NewPasswordTextField(
    newPassword: String,
    onPasswordChange: (String) -> Unit
) {
    var showPassword by remember { mutableStateOf(false) }

    TextField(
        modifier = Modifier
            .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface
        ),
        value = newPassword,
        onValueChange = onPasswordChange,
        placeholder = {
            Text(
                text = "Enter your password",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ) },

        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

        trailingIcon = {
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Show/hide password",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },

    )
}
@Composable
fun ConfirmPasswordTextField(
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit
) {

    var showPassword by remember { mutableStateOf(false) }

    TextField(
        modifier = Modifier
            .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface
        ),
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        placeholder = {
            Text(
                text = "Enter your password",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
                },

        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        // Тип клавиатуры
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        // Иконка глаза справа
        trailingIcon = {
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Show/hide password",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },

    )
}