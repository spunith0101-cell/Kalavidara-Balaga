package com.example.kalavidarabalaga.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kalavidarabalaga.ui.theme.PrimaryColor
import com.example.kalavidarabalaga.ui.theme.SecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroupeDetailScreen(
    troupeId: String?,
    viewModel: TroupeViewModel,
    onBack: () -> Unit,
    onCallClick: (String) -> Unit
) {
    val troupes by viewModel.troupes.collectAsState()
    val troupe = troupes.find { it.id == troupeId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(troupe?.name ?: "Detail", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { troupe?.let { onCallClick(it.phoneNumber) } },
                icon = { Icon(Icons.Default.Call, contentDescription = null) },
                text = { Text("Booking Inquiry") },
                containerColor = SecondaryColor,
                contentColor = Color.White
            )
        }
    ) { padding ->
        if (troupe == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Troupe not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFFFF9F0))
            ) {
                // Main Photo
                AsyncImage(
                    model = troupe.groupPhotoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = troupe.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B4513)
                    )
                    
                    Text(
                        text = troupe.artForm,
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "📍 ${troupe.district}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "About the Troupe",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = troupe.description.ifBlank { "Professional performers of ${troupe.artForm} with deep roots in traditional culture." },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Equipment List",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        if (troupe.equipment.isEmpty()) {
                            Text("• Standard instruments for ${troupe.artForm}", style = MaterialTheme.typography.bodyLarge)
                            Text("• Traditional costumes and props", style = MaterialTheme.typography.bodyLarge)
                        } else {
                            troupe.equipment.forEach { item ->
                                Text("• $item", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Portfolio Gallery",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Staggered Grid for Gallery as requested
                    val displayPhotos = if (troupe.portfolioPhotos.isEmpty()) {
                        // Just some placeholders for the demo if empty
                        listOf(troupe.groupPhotoUrl, troupe.groupPhotoUrl, troupe.groupPhotoUrl)
                    } else troupe.portfolioPhotos

                    // Note: LazyVerticalStaggeredGrid inside a verticalScroll Column needs a fixed height or to be non-lazy.
                    // For simplicity in a detail screen, we'll use a fixed height or just a Column of Rows.
                    // But to respect the requirement:
                    Box(modifier = Modifier.height(400.dp).padding(top = 8.dp)) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalItemSpacing = 8.dp,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(displayPhotos) { photo ->
                                AsyncImage(
                                    model = photo,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                }
            }
        }
    }
}
