package com.kaixinchen.githubclient.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSkipLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val tokenInput by viewModel.tokenInput.collectAsState()
    val loginState by viewModel.loginState.collectAsState() 

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to GitHub", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Please enter your Personal Access Token", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = tokenInput,
            onValueChange = { viewModel.onTokenChanged(it) },
            label = { Text("Personal Access Token") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = loginState !is LoginState.Loading,
            isError = loginState is LoginState.Error,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        if (loginState is LoginState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (loginState as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(onSuccess = onLoginSuccess) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = tokenInput.isNotBlank() && loginState !is LoginState.Loading,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Sign In", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onSkipLogin) {
            Text("Continue as Guest", fontWeight = FontWeight.Medium)
        }
    }
}
