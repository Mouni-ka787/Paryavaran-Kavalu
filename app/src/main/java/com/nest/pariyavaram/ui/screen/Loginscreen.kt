package com.nest.pariyavaram.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.nest.pariyavaram.ui.components.AppLogo
import com.nest.pariyavaram.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var isSignupMode by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn, uiState.role) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE8F5E9),
                        Color.White,
                        Color(0xFFE3F2FD)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AppLogo(
                imageSize = 110.dp,
                showName = true,
                showTagline = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isSignupMode) "Create Account"
                else "Login to continue"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            viewModel.clearMessages()
                        },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            viewModel.clearMessages()
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, null)
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    showPassword = !showPassword
                                }
                            ) {
                                Icon(
                                    if (showPassword)
                                        Icons.Default.VisibilityOff
                                    else
                                        Icons.Default.Visibility,
                                    null
                                )
                            }
                        },
                        visualTransformation =
                            if (showPassword)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction =
                                if (isSignupMode) ImeAction.Next
                                else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.login(email, password)
                            }
                        )
                    )

                    if (isSignupMode) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                viewModel.clearMessages()
                            },
                            label = { Text("Confirm Password") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, null)
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        showConfirmPassword =
                                            !showConfirmPassword
                                    }
                                ) {
                                    Icon(
                                        if (showConfirmPassword)
                                            Icons.Default.VisibilityOff
                                        else
                                            Icons.Default.Visibility,
                                        null
                                    )
                                }
                            },
                            visualTransformation =
                                if (showConfirmPassword)
                                    VisualTransformation.None
                                else
                                    PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    AnimatedVisibility(
                        visible = uiState.errorMessage != null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFFFFEBEE),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                null,
                                tint = Color.Red
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = uiState.errorMessage ?: "",
                                color = Color.Red
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.successMessage != null
                    ) {
                        Text(
                            text = uiState.successMessage ?: "",
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Button(
                        onClick = {
                            if (isSignupMode) {
                                viewModel.signUp(
                                    email,
                                    password,
                                    confirmPassword
                                )
                            } else {
                                viewModel.login(
                                    email,
                                    password
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !uiState.isLoading
                    ) {

                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {

                            Icon(
                                if (isSignupMode)
                                    Icons.Default.PersonAdd
                                else
                                    Icons.Default.Login,
                                null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                if (isSignupMode)
                                    "Sign Up"
                                else
                                    "Login"
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            isSignupMode = !isSignupMode
                            viewModel.clearMessages()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isSignupMode)
                                "Already have account? Login"
                            else
                                "Don't have account? Sign Up"
                        )
                    }
                }
            }
        }
    }
}