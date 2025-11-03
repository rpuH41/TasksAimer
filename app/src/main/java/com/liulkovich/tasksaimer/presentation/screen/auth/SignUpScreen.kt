package com.liulkovich.tasksaimer.presentation.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.liulkovich.tasksaimer.R

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier
){
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
                    fontWeight = FontWeight.Bold,
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Start
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                value = "",
                onValueChange = { },
                label = {
                    Text("John")
                },
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                value = "",
                onValueChange = { },
                label = {
                    Text("Doe")
                },
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 15.dp)
                .fillMaxWidth(),
            text = "Email",
            style = TextStyle(
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Start
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(bottom = 10.dp, start = 15.dp, end = 15.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            value = "",
            onValueChange = { },
            label = {
                Text("Enter your email")
            },

            )
        Text(
            modifier = Modifier
                .padding(start = 15.dp)
                .fillMaxWidth(),
            text = "Password",
            style = TextStyle(
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Start
        )
        PasswordTextFieldSignUp()
        Text(
            modifier = Modifier
                .padding(top = 10.dp, start = 15.dp)
                .fillMaxWidth(),
            text = "Password confirmation",
            style = TextStyle(
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Start
        )
        PasswordTextFieldSignUp()
        Button( modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp,start = 15.dp, end = 15.dp, top = 30.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            onClick = {  }
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
            TextButton(onClick = { }) {
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
fun PasswordTextFieldSignUp() {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Enter your password") },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp),

        // Скрытие пароля
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
        }
    )
}