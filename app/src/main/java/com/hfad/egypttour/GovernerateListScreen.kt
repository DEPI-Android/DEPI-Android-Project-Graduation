package com.hfad.egypttour

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfad.egypttour.data.model.Governorate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovernorateListScreen(
    onGovernorateClick: (String) -> Unit // Callback to handle navigation
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Egypt Tour") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Loop through the ENUM values you defined!
            items(Governorate.entries) { governorate ->
                GovernorateCard(
                    governorate = governorate,
                    onClick = { onGovernorateClick(governorate.id) }
                )
            }
        }
    }
}

@Composable
fun GovernorateCard(
    governorate: Governorate,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = governorate.displayName,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}