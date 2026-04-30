package com.despegue.tv.ui.screens.collection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.despegue.tv.data.local.ValidationResult
import com.despegue.tv.domain.model.Collection
import com.despegue.tv.ui.components.LoadingIndicator
import com.despegue.tv.ui.theme.DespegueColors
import com.despegue.tv.R
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DespegueButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.colors(
            containerColor = DespegueColors.BackgroundCard,
            contentColor = DespegueColors.TextPrimary,
            focusedContainerColor = DespegueColors.FocusBackground,
            focusedContentColor = DespegueColors.Primary
        ),
        border = ButtonDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, DespegueColors.FocusRing),
                shape = RoundedCornerShape(12.dp)
            )
        ),
        shape = ButtonDefaults.shape(RoundedCornerShape(12.dp)),
        content = { content() }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CollectionManagementScreen(
    viewModel: CollectionManagementViewModel = hiltViewModel(),
    onNavigateToEditor: (String?) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var exportMessage by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(exportMessage) {
        if (exportMessage != null) {
            kotlinx.coroutines.delay(3000)
            exportMessage = null
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingIndicator()
        }
        return
    }

    if (uiState.showImportDialog) {
        ImportContent(
            importText = uiState.importText,
            importError = uiState.importError,
            onTextChange = { viewModel.updateImportText(it) },
            onImport = { viewModel.importCollections() },
            onPaste = {},
            onBack = { viewModel.hideImportDialog() },
            importMode = uiState.importMode,
            onModeChange = { viewModel.setImportMode(it) },
            importUrl = uiState.importUrl,
            onUrlChange = { viewModel.updateImportUrl(it) },
            onFetchUrl = { viewModel.fetchUrl() },
            validationResult = uiState.validationResult,
            isLoadingImport = uiState.isLoadingImport,
            onValidate = { viewModel.validateCurrentText() },
            onPickFile = {
                scope.launch {
                    viewModel.loadFromFile(context)
                }
            },
            onConfirmImport = { viewModel.confirmImport() }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 48.dp, end = 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.collections_header),
                style = MaterialTheme.typography.headlineMedium,
                color = DespegueColors.TextPrimary
            )
            val newButtonFocusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                repeat(3) { withFrameNanos { } }
                try { newButtonFocusRequester.requestFocus() } catch (_: Exception) {}
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (uiState.collections.isNotEmpty()) {
                    DespegueButton(onClick = {
                        scope.launch {
                            try {
                                val json = viewModel.getExportJson()
                                withContext(Dispatchers.IO) {
                                    val resolver = context.contentResolver
                                    // Delete existing file first
                                    val existingUri = android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI
                                    resolver.delete(
                                        existingUri,
                                        "${android.provider.MediaStore.Downloads.DISPLAY_NAME} = ?",
                                        arrayOf("despegue-collections.json")
                                    )
                                    // Write new file
                                    val values = android.content.ContentValues().apply {
                                        put(android.provider.MediaStore.Downloads.DISPLAY_NAME, "despegue-collections.json")
                                        put(android.provider.MediaStore.Downloads.MIME_TYPE, "application/json")
                                    }
                                    val uri = resolver.insert(existingUri, values)
                                    uri?.let { resolver.openOutputStream(it)?.use { os -> os.write(json.toByteArray()) } }
                                }
                                exportMessage = "saved"
                            } catch (_: Exception) {
                                exportMessage = "failed"
                            }
                        }
                    }) {
                        Text(exportMessage?.let {
                            when (it) {
                                "saved" -> stringResource(R.string.collections_saved_downloads)
                                "failed" -> stringResource(R.string.collections_export_failed)
                                else -> it
                            }
                        } ?: stringResource(R.string.collections_export_file))
                    }
                }
                DespegueButton(onClick = { viewModel.showImportDialog() }) {
                    Text(stringResource(R.string.collections_import))
                }
                DespegueButton(
                    onClick = { onNavigateToEditor(null) },
                    modifier = Modifier.focusRequester(newButtonFocusRequester)
                ) {
                    Text(stringResource(R.string.collections_new))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.collections.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.collections_empty),
                    color = DespegueColors.TextSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = uiState.collections,
                    key = { _, item -> item.id }
                ) { index, collection ->
                    CollectionListItem(
                        collection = collection,
                        isFirst = index == 0,
                        isLast = index == uiState.collections.size - 1,
                        onEdit = { onNavigateToEditor(collection.id) },
                        onDelete = { viewModel.deleteCollection(collection.id) },
                        onMoveUp = { viewModel.moveUp(index) },
                        onMoveDown = { viewModel.moveDown(index) }
                    )
                }
            }
        }
    }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ImportContent(
    importText: String,
    importError: String?,
    onTextChange: (String) -> Unit,
    onImport: () -> Unit,
    onPaste: () -> Unit,
    onBack: () -> Unit,
    importMode: ImportMode,
    onModeChange: (ImportMode) -> Unit,
    importUrl: String,
    onUrlChange: (String) -> Unit,
    onFetchUrl: () -> Unit,
    validationResult: ValidationResult?,
    isLoadingImport: Boolean,
    onValidate: () -> Unit,
    onPickFile: () -> Unit,
    onConfirmImport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 48.dp, end = 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.collections_import_header),
                style = MaterialTheme.typography.headlineMedium,
                color = DespegueColors.TextPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DespegueButton(onClick = onBack) { Text(stringResource(R.string.collections_cancel)) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.collections_import_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DespegueColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mode tabs
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImportMode.entries.filter { it != ImportMode.PASTE }.forEach { mode ->
                        val label = when (mode) {
                            ImportMode.PASTE -> stringResource(R.string.collections_mode_paste)
                            ImportMode.FILE -> stringResource(R.string.collections_mode_file)
                            ImportMode.URL -> stringResource(R.string.collections_mode_url)
                        }
                        if (mode == importMode) {
                            Button(
                                onClick = { onModeChange(mode) },
                                colors = ButtonDefaults.colors(
                                    containerColor = DespegueColors.Primary,
                                    contentColor = DespegueColors.Background,
                                    focusedContainerColor = DespegueColors.Primary,
                                    focusedContentColor = DespegueColors.Background
                                ),
                                border = ButtonDefaults.border(
                                    focusedBorder = Border(
                                        border = BorderStroke(2.dp, DespegueColors.FocusRing),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                ),
                                shape = ButtonDefaults.shape(RoundedCornerShape(12.dp))
                            ) {
                                Text(label)
                            }
                        } else {
                            DespegueButton(onClick = { onModeChange(mode) }) {
                                Text(label)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                when (importMode) {
                    ImportMode.PASTE -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DespegueButton(onClick = onPaste) { Text(stringResource(R.string.collections_paste_clipboard)) }
                            if (importText.isNotBlank()) {
                                DespegueButton(onClick = onValidate) { Text(stringResource(R.string.collections_validate)) }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            colors = SurfaceDefaults.colors(containerColor = DespegueColors.BackgroundElevated),
                            border = Border(
                                border = BorderStroke(1.dp, if (importError != null) DespegueColors.Error else DespegueColors.Border),
                                shape = RoundedCornerShape(12.dp)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                BasicTextField(
                                    value = importText,
                                    onValueChange = onTextChange,
                                    modifier = Modifier.fillMaxSize(),
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        color = DespegueColors.TextPrimary
                                    ),
                                    cursorBrush = SolidColor(DespegueColors.Primary),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    decorationBox = { innerTextField ->
                                        if (importText.isEmpty()) {
                                            Text(
                                                text = stringResource(R.string.collections_paste_hint),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = DespegueColors.TextTertiary
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }
                    }

                    ImportMode.FILE -> {
                        Text(
                            text = stringResource(R.string.collections_file_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = DespegueColors.TextTertiary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DespegueButton(onClick = onPickFile) { Text(stringResource(R.string.collections_load_file)) }

                        if (importText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.collections_file_loaded, importText.length),
                                style = MaterialTheme.typography.bodyMedium,
                                color = DespegueColors.TextSecondary
                            )
                        }
                    }

                    ImportMode.URL -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            colors = SurfaceDefaults.colors(containerColor = DespegueColors.BackgroundElevated),
                            border = Border(
                                border = BorderStroke(1.dp, DespegueColors.Border),
                                shape = RoundedCornerShape(12.dp)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
                                BasicTextField(
                                    value = importUrl,
                                    onValueChange = onUrlChange,
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = DespegueColors.TextPrimary
                                    ),
                                    cursorBrush = SolidColor(DespegueColors.Primary),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    decorationBox = { innerTextField ->
                                        if (importUrl.isEmpty()) {
                                            Text(
                                                text = "https://example.com/collections.json",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = DespegueColors.TextTertiary
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        DespegueButton(onClick = onFetchUrl) {
                            Text(if (isLoadingImport) stringResource(R.string.collections_fetching) else stringResource(R.string.collections_fetch_url))
                        }

                        if (isLoadingImport) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LoadingIndicator()
                        }
                    }
                }
            }

            // Validation result preview
            if (validationResult != null && validationResult.valid) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        colors = SurfaceDefaults.colors(containerColor = DespegueColors.BackgroundCard),
                        border = Border(
                            border = BorderStroke(1.dp, DespegueColors.Primary),
                            shape = RoundedCornerShape(12.dp)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.collections_valid_json),
                                style = MaterialTheme.typography.titleMedium,
                                color = DespegueColors.Primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.collections_valid_summary, validationResult.collectionCount, validationResult.folderCount),
                                style = MaterialTheme.typography.bodyMedium,
                                color = DespegueColors.TextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            DespegueButton(onClick = onConfirmImport) {
                                Text(stringResource(R.string.collections_confirm_import))
                            }
                        }
                    }
                }
            }

            if (importError != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = importError,
                        style = MaterialTheme.typography.bodySmall,
                        color = DespegueColors.Error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CollectionListItem(
    collection: Collection,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        colors = SurfaceDefaults.colors(containerColor = DespegueColors.BackgroundCard),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = DespegueColors.TextPrimary
                )
                Text(
                    text = stringResource(R.string.collections_folder_count, collection.folders.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = DespegueColors.TextTertiary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = onMoveUp,
                    enabled = !isFirst,
                    colors = ButtonDefaults.colors(
                        containerColor = DespegueColors.BackgroundCard,
                        contentColor = DespegueColors.TextSecondary,
                        focusedContainerColor = DespegueColors.FocusBackground,
                        focusedContentColor = DespegueColors.Primary
                    ),
                    border = ButtonDefaults.border(
                        focusedBorder = Border(
                            border = BorderStroke(2.dp, DespegueColors.FocusRing),
                            shape = RoundedCornerShape(12.dp)
                        )
                    ),
                    shape = ButtonDefaults.shape(RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, stringResource(R.string.cd_move_up))
                }

                Button(
                    onClick = onMoveDown,
                    enabled = !isLast,
                    colors = ButtonDefaults.colors(
                        containerColor = DespegueColors.BackgroundCard,
                        contentColor = DespegueColors.TextSecondary,
                        focusedContainerColor = DespegueColors.FocusBackground,
                        focusedContentColor = DespegueColors.Primary
                    ),
                    border = ButtonDefaults.border(
                        focusedBorder = Border(
                            border = BorderStroke(2.dp, DespegueColors.FocusRing),
                            shape = RoundedCornerShape(12.dp)
                        )
                    ),
                    shape = ButtonDefaults.shape(RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, stringResource(R.string.cd_move_down))
                }

                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.colors(
                        containerColor = DespegueColors.BackgroundCard,
                        contentColor = DespegueColors.TextSecondary,
                        focusedContainerColor = DespegueColors.FocusBackground,
                        focusedContentColor = DespegueColors.Primary
                    ),
                    border = ButtonDefaults.border(
                        focusedBorder = Border(
                            border = BorderStroke(2.dp, DespegueColors.FocusRing),
                            shape = RoundedCornerShape(12.dp)
                        )
                    ),
                    shape = ButtonDefaults.shape(RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Edit, stringResource(R.string.cd_edit))
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.colors(
                        containerColor = DespegueColors.BackgroundCard,
                        contentColor = DespegueColors.TextSecondary,
                        focusedContainerColor = DespegueColors.FocusBackground,
                        focusedContentColor = DespegueColors.Error
                    ),
                    border = ButtonDefaults.border(
                        focusedBorder = Border(
                            border = BorderStroke(2.dp, DespegueColors.FocusRing),
                            shape = RoundedCornerShape(12.dp)
                        )
                    ),
                    shape = ButtonDefaults.shape(RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Delete, stringResource(R.string.cd_delete))
                }
            }
        }
    }
}
