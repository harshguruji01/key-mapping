package com.harshguruji.keynova.ui.screens.profiles

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harshguruji.keynova.KeyNovaApp
import com.harshguruji.keynova.data.model.Profile
import com.harshguruji.keynova.ui.components.GlassCard
import com.harshguruji.keynova.ui.components.NeonButton
import com.harshguruji.keynova.ui.theme.BgDark
import com.harshguruji.keynova.ui.theme.BorderDark
import com.harshguruji.keynova.ui.theme.CardDark
import com.harshguruji.keynova.ui.theme.NeonBlue
import com.harshguruji.keynova.ui.theme.NeonCyan
import com.harshguruji.keynova.ui.theme.NeonGreen
import com.harshguruji.keynova.ui.theme.NeonPurple
import com.harshguruji.keynova.ui.theme.NeonRed
import com.harshguruji.keynova.ui.theme.SurfaceDark
import com.harshguruji.keynova.ui.theme.TextMuted
import com.harshguruji.keynova.ui.theme.TextPrimary
import com.harshguruji.keynova.ui.theme.TextSecondary
import com.harshguruji.keynova.utils.ProfileExporter
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ProfilesScreen() {
    val app = KeyNovaApp.instance
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profiles by app.profileRepository.getAllProfilesFlow().collectAsState(initial = emptyList())
    val activeProfile by app.mappingEngine.activeProfile.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    val filteredProfiles = profiles.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROFILES",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${profiles.size} TOTAL CONFIGURATIONS",
                    color = NeonPurple,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row {
                IconButton(onClick = { showImportDialog = true }) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Import", tint = NeonCyan)
                }
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Profile", tint = NeonGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search profiles by name or game...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredProfiles) { profile ->
                val isActive = profile.id == activeProfile?.id

                GlassCard(
                    borderColor = if (isActive) NeonCyan else BorderDark,
                    cornerRadius = 14.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = profile.name,
                                color = if (isActive) NeonCyan else TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isActive) {
                                Text(
                                    text = "ACTIVE",
                                    color = NeonGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = profile.description,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${profile.mappings.size} mappings • ${profile.controls.size} controls",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Row {
                                // Activate
                                if (!isActive) {
                                    IconButton(onClick = {
                                        scope.launch {
                                            app.mappingEngine.loadProfile(profile)
                                            app.settingsRepository.setActiveProfileId(profile.id)
                                        }
                                    }) {
                                        Icon(Icons.Default.Check, contentDescription = "Activate", tint = NeonGreen)
                                    }
                                }

                                // Duplicate
                                IconButton(onClick = {
                                    scope.launch {
                                        app.profileRepository.duplicateProfile(profile.id, "${profile.name} (Copy)")
                                    }
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = NeonBlue)
                                }

                                // Export
                                IconButton(onClick = {
                                    val json = ProfileExporter.exportToJson(profile)
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, json)
                                        type = "application/json"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Export KeyNova Profile")
                                    context.startActivity(shareIntent)
                                }) {
                                    Icon(Icons.Default.FileUpload, contentDescription = "Export", tint = NeonPurple)
                                }

                                // Delete (if not default)
                                if (!profile.isDefault) {
                                    IconButton(onClick = {
                                        scope.launch {
                                            app.profileRepository.deleteProfile(profile.id)
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Profile Dialog
    if (showCreateDialog) {
        var profileName by remember { mutableStateOf("") }
        var profileDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = profileName,
                        onValueChange = { profileName = it },
                        label = { Text("Profile Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = profileDesc,
                        onValueChange = { profileDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (profileName.isNotBlank()) {
                            val newProf = Profile(
                                id = "profile_" + UUID.randomUUID().toString().take(8),
                                name = profileName,
                                description = profileDesc
                            )
                            scope.launch {
                                app.profileRepository.saveProfile(newProf)
                                app.mappingEngine.loadProfile(newProf)
                            }
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = BgDark)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardDark
        )
    }

    // Import Profile Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Profile JSON", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Paste profile JSON data below:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        placeholder = { Text("{\"profileName\": ...}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ProfileExporter.importFromJson(importJsonText).onSuccess { imported ->
                            scope.launch {
                                app.profileRepository.saveProfile(imported)
                                app.mappingEngine.loadProfile(imported)
                            }
                            showImportDialog = false
                            importJsonText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = BgDark)
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardDark
        )
    }
}
