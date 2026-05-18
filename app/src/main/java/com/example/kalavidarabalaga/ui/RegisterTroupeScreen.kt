package com.example.kalavidarabalaga.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kalavidarabalaga.data.Troupe
import com.example.kalavidarabalaga.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTroupeScreen(
    viewModel: TroupeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var artForm by remember { mutableStateOf("") }
    var leadContact by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var equipment by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Your Team", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF9F0))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Troupe Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037) // Dark Brown
            )

            // Photo Upload UI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, PrimaryColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(48.dp))
                        Text("Upload Group Photo", color = PrimaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Input Fields with better contrast
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = PrimaryColor,
                unfocusedLabelColor = Color(0xFF5D4037),
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color(0xFFBCAAA4),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Team/Troupe Name", fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true
            )

            OutlinedTextField(
                value = artForm,
                onValueChange = { artForm = it },
                label = { Text("Art Form (e.g., Dollu Kunitha)", fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true
            )

            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District", fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true
            )

            OutlinedTextField(
                value = leadContact,
                onValueChange = { leadContact = it },
                label = { Text("Lead Contact Name", fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number", fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description/History", fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                minLines = 3
            )

            OutlinedTextField(
                value = equipment,
                onValueChange = { equipment = it },
                label = { Text("Equipment (comma separated)", fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
            }

            Button(
                onClick = {
                    if (name.isBlank() || artForm.isBlank() || phoneNumber.isBlank() || district.isBlank()) {
                        errorMessage = "Please fill in all required fields (Name, Art, District, Phone)"
                        return@Button
                    }
                    
                    val troupe = Troupe(
                        name = name,
                        artForm = artForm,
                        leadContact = leadContact,
                        phoneNumber = phoneNumber,
                        district = district,
                        description = description,
                        groupPhotoUrl = selectedImageUri?.toString() ?: "https://karnatakatourism.org/wp-content/uploads/2020/06/Dollu-Kunitha-1.jpg",
                        equipment = equipment.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    )

                    viewModel.registerTroupe(
                        troupe = troupe,
                        onSuccess = onSuccess,
                        onError = { errorMessage = it }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Register Team", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
