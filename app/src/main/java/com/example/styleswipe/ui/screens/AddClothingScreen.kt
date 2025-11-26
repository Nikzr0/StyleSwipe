package com.example.styleswipe.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.styleswipe.ClothingViewModel
import com.example.styleswipe.data.ClothingItem
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddClothingScreen(
    viewModel: ClothingViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var textName by remember { mutableStateOf("") }
    var textBrand by remember { mutableStateOf("") }
    var textSize by remember { mutableStateOf("") }
    var textPrice by remember { mutableStateOf("") }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            imageUris = imageUris + tempPhotoUri!!
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents() // Това позволява мулти-избор
    ) { uris: List<Uri> ->

        val savedUris = uris.mapNotNull { copyUriToInternalStorage(context, it) }
        imageUris = imageUris + savedUris
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = textName, onValueChange = { textName = it }, label = { Text("Име") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = textBrand, onValueChange = { textBrand = it }, label = { Text("Марка") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = textSize, onValueChange = { textSize = it }, label = { Text("Размер") }, modifier = Modifier.weight(1f).padding(end = 4.dp))
            OutlinedTextField(value = textPrice, onValueChange = { textPrice = it }, label = { Text("Цена") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(start = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (imageUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
            Text("${imageUris.size} снимки избрани")
        } else {
            Box(modifier = Modifier.size(200.dp).padding(8.dp), contentAlignment = Alignment.Center) {
                Text("Няма снимки")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                val photoFile = createImageFile(context)
                val authority = "com.example.styleswipe.fileprovider"
                tempPhotoUri = FileProvider.getUriForFile(context, authority, photoFile)
                cameraLauncher.launch(tempPhotoUri!!)
            }) { Text("Камера +") }

            OutlinedButton(onClick = {
                galleryLauncher.launch("image/*")
            }) { Text("Галерия +") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (textName.isNotEmpty() && imageUris.isNotEmpty()) {
                    val newItem = ClothingItem(
                        name = textName,
                        brand = textBrand,
                        size = textSize,
                        price = textPrice,
                        imagePaths = imageUris.map { it.toString() }
                    )
                    viewModel.addClothingItem(newItem)
                    onNavigateBack()
                }
            },
            enabled = textName.isNotEmpty() && imageUris.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ЗАПАЗИ (${imageUris.size} СНИМКИ)")
        }
    }
}

fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

fun copyUriToInternalStorage(context: Context, sourceUri: Uri): Uri? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
        val newFile = createImageFile(context)
        val outputStream = FileOutputStream(newFile)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        val authority = "com.example.styleswipe.fileprovider"
        return FileProvider.getUriForFile(context, authority, newFile)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}