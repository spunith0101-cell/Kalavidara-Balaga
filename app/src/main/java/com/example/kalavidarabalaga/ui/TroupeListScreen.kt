package com.example.kalavidarabalaga.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kalavidarabalaga.data.Troupe
import com.example.kalavidarabalaga.ui.theme.PrimaryColor
import com.example.kalavidarabalaga.ui.theme.SecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroupeListScreen(
    viewModel: TroupeViewModel,
    onTroupeClick: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    val troupes by viewModel.troupes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    
    val districts = listOf("All Districts", "Shimoga", "Mandya", "Haveri", "Udupi", "Tumkur")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalavidara Balaga", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onRegisterClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Register Team", fontWeight = FontWeight.Bold) },
                containerColor = SecondaryColor,
                contentColor = Color.White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF9F0)) 
        ) {
            // Search and Filter Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryColor,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp)),
                        placeholder = { Text("Search Name, Art, or District...", color = Color(0xFF616161)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryColor) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = PrimaryColor,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("District: ", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        ScrollableTabRow(
                            selectedTabIndex = districts.indexOf(selectedDistrict).coerceAtLeast(0),
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            edgePadding = 0.dp,
                            divider = {}
                        ) {
                            districts.forEach { district ->
                                Tab(
                                    selected = selectedDistrict == district,
                                    onClick = { viewModel.onDistrictChanged(district) },
                                    text = { 
                                        Text(
                                            text = district, 
                                            fontWeight = if(selectedDistrict == district) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        ) 
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading && troupes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else {
                if (troupes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No teams found for \"$searchQuery\"", 
                            color = Color(0xFF5D4037), 
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(troupes, key = { it.id }) { troupe ->
                            TroupeCard(troupe = troupe, onClick = { onTroupeClick(troupe.id) })
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TroupeCard(troupe: Troupe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = troupe.groupPhotoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = troupe.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3E2723) // Very Dark Brown
                    )
                )
                Text(
                    text = troupe.artForm,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = "📍 ${troupe.district}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF212121), // Near Black
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
