package com.example.logs

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.logs.ui.theme.LogsTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class DetailsActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var databaseHelper: DatabaseHelper

    private val capturedImageUriState: MutableState<Uri?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        databaseHelper = DatabaseHelper(this)

        requestCameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show()
            }
        }

        requestLocationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Log.d("DetailsActivity", "Imagem capturada com URI: ${capturedImageUriState.value}")
            } else {
                Toast.makeText(this, "Falha ao capturar imagem", Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            LogsTheme {
                DetailsScreen(
                    getLocation = ::getLocation,
                    checkCameraPermission = ::checkCameraPermission,
                    capturedImageUri = capturedImageUriState.value,
                    onImageCaptured = { uri -> capturedImageUriState.value = uri },
                    onConfirm = { nome, email, comentario ->
                        saveDataToDatabase(nome, email, comentario, capturedImageUriState.value)
                    }
                )
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val photoFile: File = createImageFile()
        val uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            photoFile
        )
        capturedImageUriState.value = uri
        cameraLauncher.launch(uri)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(java.util.Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Toast.makeText(this, "Localização: Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("DetailsActivity", "Error fetching location: ${e.message}")
            Toast.makeText(this, "Erro ao obter a localização", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToDatabase(nome: String, email: String, comentario: String, imageUri: Uri?) {
        Log.d("DetailsActivity", "Nome: $nome")
        Log.d("DetailsActivity", "Email: $email")
        Log.d("DetailsActivity", "Comentário: $comentario")
        Log.d("DetailsActivity", "Caminho da Imagem: ${imageUri.toString()}")

        val result = databaseHelper.insertData(nome, email, comentario, imageUri.toString())
        if (result) {
            Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Erro ao salvar dados", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun DetailsScreen(
    getLocation: () -> Unit,
    checkCameraPermission: () -> Unit,
    capturedImageUri: Uri?,
    onImageCaptured: (Uri?) -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            label = { Text("Nome") },
            value = nome,
            onValueChange = { nome = it },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            label = { Text("Email") },
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            label = { Text("Comentário") },
            value = comentario,
            onValueChange = { comentario = it },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { getLocation() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Obter Localização")
        }

        Button(
            onClick = { checkCameraPermission() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Abrir Câmera")
        }

        Button(
            onClick = { onConfirm(nome, email, comentario) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Confirmar")
        }

        if (capturedImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(capturedImageUri),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(top = 16.dp)
            )
        }
    }
}