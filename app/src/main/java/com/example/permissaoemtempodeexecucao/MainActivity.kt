package com.example.permissaoemtempodeexecucao

import android.Manifest
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.example.permissaoemtempodeexecucao.ui.theme.PermissaoEmTempoDeExecucaoTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Chama a função para solicitar permissões
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

        }

        // Obtém a última localização conhecida
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Obtém a última localização conhecida. Em algumas situações raras, isso pode ser nulo.
                Log.d("location", "${location.toString()}")
            }

        fun getCurrentLocation(callback: (String) -> Unit) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val locationStr = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                            callback(locationStr)
                        } else {
                            callback("Localização indisponível")
                        }
                    }
            } else {
                callback("Permissão não concedida")
            }
        }

        setContent {
            var locationText by remember { mutableStateOf("Localização não carregada") }

            Scaffold { innerPadding ->
                Button(
                    onClick = {
                        getCurrentLocation { locationStr ->
                            locationText = locationStr
                            Toast.makeText(this, locationStr, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text("Obter Localização Atual")
                }

                Text(
                    text = locationText,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }
    }

    // Função que solicita permissões de localização
    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Permissão de localização precisa concedida
                    Toast.makeText(this, "Permissão de localização precisa concedida!", Toast.LENGTH_SHORT).show()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Apenas permissão de localização aproximada concedida
                    Toast.makeText(this, "Permissão de localização aproximada concedida!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Nenhuma permissão concedida
                    Toast.makeText(this, "Permissão de localização não concedida!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Solicitar as permissões de localização
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
