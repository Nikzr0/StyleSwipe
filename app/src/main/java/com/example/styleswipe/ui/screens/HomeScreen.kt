package com.example.styleswipe.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.styleswipe.ClothingViewModel
import com.example.styleswipe.data.ClothingItem
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: ClothingViewModel,
    onNavigateToAdd: () -> Unit
) {
    val items by viewModel.allItems.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ClothingItem?>(null) }

    var editName by remember { mutableStateOf("") }
    var editBrand by remember { mutableStateOf("") }
    var editSize by remember { mutableStateOf("") }
    var editPrice by remember { mutableStateOf("") }

    var editImagePaths by remember { mutableStateOf<List<String>>(emptyList()) }

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            editImagePaths = editImagePaths + tempPhotoUri.toString()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val savedUris = uris.mapNotNull { copyUriToInternalStorageHome(context, it) }
        editImagePaths = editImagePaths + savedUris.map { it.toString() }
    }

    if (showDialog && itemToEdit != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Редактирай дрехата") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Име") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editBrand, onValueChange = { editBrand = it }, label = { Text("Марка") })
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        OutlinedTextField(value = editSize, onValueChange = { editSize = it }, label = { Text("Размер") }, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(4.dp))
                        OutlinedTextField(value = editPrice, onValueChange = { editPrice = it }, label = { Text("Цена") }, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Снимки (${editImagePaths.size}):", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(100.dp)
                    ) {
                        items(editImagePaths) { path ->
                            Box(modifier = Modifier.size(100.dp)) {
                                AsyncImage(
                                    model = path,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(Color.Red, CircleShape)
                                        .padding(4.dp)
                                        .size(16.dp)
                                        .clickable {
                                            editImagePaths = editImagePaths - path
                                        }
                                )
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                                    .clickable {
                                        galleryLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {
                            val photoFile = createImageFileHome(context)
                            val authority = "com.example.styleswipe.fileprovider"
                            tempPhotoUri = FileProvider.getUriForFile(context, authority, photoFile)
                            cameraLauncher.launch(tempPhotoUri!!)
                        }) { Text("Камера") }

                        OutlinedButton(onClick = {
                            galleryLauncher.launch("image/*")
                        }) { Text("Галерия") }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedItem = itemToEdit!!.copy(
                            name = editName,
                            brand = editBrand,
                            size = editSize,
                            price = editPrice,
                            imagePaths = editImagePaths
                        )
                        viewModel.updateClothingItem(updatedItem)
                        showDialog = false
                    }
                ) {
                    Text("Запази промените")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Отказ")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (items.isEmpty()) {
            Text(
                text = "Няма добавени дрехи.\nНатисни + долу вдясно!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = "Твоят Гардероб",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                items(items) { item ->
                    ClothingCard(
                        item = item,
                        onDeleteClick = { viewModel.deleteClothingItem(item) },
                        onEditClick = {
                            itemToEdit = item
                            editName = item.name
                            editBrand = item.brand
                            editSize = item.size
                            editPrice = item.price
                            editImagePaths = item.imagePaths
                            showDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ClothingCard(
    item: ClothingItem,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    var currentImageIndex by remember { mutableIntStateOf(0) }
    val totalImages = item.imagePaths.size

    LaunchedEffect(totalImages) {
        if (currentImageIndex >= totalImages) {
            currentImageIndex = 0
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(400.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.imagePaths.getOrNull(currentImageIndex),
                contentDescription = item.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (totalImages > 1 && currentImageIndex > 0) {
                IconButton(
                    onClick = { currentImageIndex-- },
                    modifier = Modifier.align(Alignment.CenterStart).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev", tint = Color.White)
                }
            }

            if (totalImages > 1 && currentImageIndex < totalImages - 1) {
                IconButton(
                    onClick = { currentImageIndex++ },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next", tint = Color.White)
                }
            }

            if (totalImages > 0) {
                Box(
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${currentImageIndex + 1} / $totalImages", color = Color.White, fontSize = 12.sp)
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).background(Color.Black.copy(alpha = 0.7f)).padding(16.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = item.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${item.price} лв.", color = Color(0xFF4CAF50), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "${item.brand} | Размер: ${item.size}", color = Color.LightGray, fontSize = 16.sp)
                }
            }

            Row(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                IconButton(onClick = onEditClick, modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape).size(40.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDeleteClick, modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape).size(40.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

fun createImageFileHome(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

fun copyUriToInternalStorageHome(context: Context, sourceUri: Uri): Uri? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
        val newFile = createImageFileHome(context)
        val outputStream = FileOutputStream(newFile)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        val authority = "com.example.styleswipe.fileprovider"
        return FileProvider.getUriForFile(context, authority, newFile)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}