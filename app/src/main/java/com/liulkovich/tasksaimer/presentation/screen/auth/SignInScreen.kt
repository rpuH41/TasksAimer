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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liulkovich.tasksaimer.R

@Composable
fun SigInScreen(
    modifier: Modifier = Modifier,

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
            text = "Welcome Back",
            style = TextStyle(
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            modifier = Modifier
                .padding(bottom = 45.dp),
            text = "Sign in to continue to your tasks",
            style = TextStyle(
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        )
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
        OutlinedTextField(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            value = "",
            onValueChange = { },
            label = {
                Text("Enter your password")
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "Password is invisible"
                )
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { }) {
                Text("Forgot password?")
            }

        }
        Button( modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp,start = 15.dp, end = 15.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            onClick = {  }
        ) {
            Text(
                modifier = Modifier,
                text = "Sign In",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )


            )
        }
        Row(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                style = TextStyle(
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
            TextButton(onClick = { }) {
                Text(
                    text = "Sign Up",
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