package com.example.financeapp.home

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financeapp.data.ExpenseItemEntity
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.tooling.preview.Preview
import com.example.financeapp.AppViewModel
import kotlinx.coroutines.launch

@OptIn (ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTypeSelector(
    navController: NavController,
    viewModel: AppViewModel
) {
    val haptic = LocalHapticFeedback.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val categories by viewModel.getCategories.collectAsState()
    val allItems by viewModel.expenseItems.collectAsState()

    val groupedData = remember(categories, allItems) {
        categories.map { categoryName ->
            categoryName to allItems.filter { it.category == categoryName }
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                     haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showBottomSheet = true
                },
                modifier = Modifier.padding(bottom = 84.dp, end = 16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        if (categories.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hmm...It's Lonely in Here",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Use the + button to add your first category",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Text(
                        text = "Where did you spend?",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(24.dp)
                    )
                }

                items(groupedData) { (categoryName, itemsInCategory)  ->
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            if (itemsInCategory.isNotEmpty()) {
                                items(itemsInCategory) { item ->
                                    BigSquareCard(
                                        label = item.title,
                                        icon = Icons.Rounded.Payments,
                                        onClick = {
                                            scope.launch {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                // Handle item selection here
                                            }
                                        }
                                    )
                                }
                            } else {
                                item {
                                    Text(
                                        text = "No items in $categoryName",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 40.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showBottomSheet) {
            AddCategorySheet(
                onDismiss = {
                    showBottomSheet = false
                }
            ) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategorySheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
       // windowInsets = WindowInsets.ime,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(23.dp),

        ) {
            item {
                TextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    label = { Text("Enter Category") },
                )
            }
        }
    }
}
