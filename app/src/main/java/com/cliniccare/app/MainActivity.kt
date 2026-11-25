
package com.cliniccare.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cliniccare.app.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ClinicCareScreen()
            }
        }
    }
}

@Composable
fun ClinicCareScreen() {

    val scope = rememberCoroutineScope()

    var status by remember {
        mutableStateOf("Backend not checked")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "ClinicCare",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Clinic Management Application"
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                scope.launch {

                    loading = true

                    status = try {

                        val response = withContext(
                            Dispatchers.IO
                        ) {
                            RetrofitClient.api.checkHealth()
                        }

                        "${response.application}: " +
                                response.message

                    } catch (e: Exception) {

                        "Connection failed: ${e.message}"

                    } finally {

                        loading = false
                    }
                }
            },
            enabled = !loading
        ) {

            Text(
                if (loading) "Connecting..."
                else "Test Backend Connection"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = status)
    }
}
