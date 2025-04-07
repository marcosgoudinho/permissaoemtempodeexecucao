package com.example.permissaoemtempodeexecucao

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.permissaoemtempodeexecucao.ui.theme.PermissaoEmTempoDeExecucaoTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>

    private var onPermissionGranted: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Registra o launcher da permissão
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

                if (fineLocationGranted || coarseLocationGranted) {
                    Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show()
                    onPermissionGranted?.invoke()
                } else {
                    Toast.makeText(this, "Permissão negada!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setContent {
            PermissaoEmTempoDeExecucaoTheme {
                var locationText by remember { mutableStateOf("Localização não carregada") }

                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        Button(onClick = {
                            // Verifica se a permissão já foi concedida
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED ||
                                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                getCurrentLocation {
                                    locationText = it
                                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                                }
                            } else {
                                // Solicita permissão
                                onPermissionGranted = {
                                    getCurrentLocation {
                                        locationText = it
                                        Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                                    }
                                }

                                locationPermissionRequest.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }) {
                            Text("Obter Localização Atual")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = locationText)
                    }
                }
            }
        }
    }

    private fun getCurrentLocation(callback: (String) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    callback("Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                } else {
                    callback("Localização indisponível")
                }
            }
    }
}
