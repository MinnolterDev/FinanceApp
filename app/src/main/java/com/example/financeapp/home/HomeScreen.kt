package com.example.financeapp.home

import android.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financeapp.AppViewModel
import com.example.financeapp.data.ExpenseEntity
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    homeScreenViewModel: HomeScreenViewModel,
    appViewModel: AppViewModel,
    navController: NavController
) {
    val uiState by homeScreenViewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val total by appViewModel.totalExpense.collectAsState()
    val recentExpenses by appViewModel.recentExpenses.collectAsState()

    // Formatting total with commas
    val formattedTotal = remember(total) {
        NumberFormat.getNumberInstance(Locale("en", "IN")).format(total ?: 0)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Add Expense") },
                icon = { Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Expense") },
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    navController.navigate("keyboardScreen")
                },
                modifier = Modifier.padding(bottom = 84.dp, end = 16.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                val currentDate = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                GlobalText(
                    text = currentDate,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(5.dp)) }

            item {
                // Responsive font size for HomeScreen total
                val fontSize = when {
                    formattedTotal.length > 10 -> 42.sp
                    formattedTotal.length > 7 -> 56.sp
                    else -> 70.sp
                }

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = fontSize * 0.7f)) { append("₹") }
                        append(formattedTotal)
                    },
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = fontSize
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    item {
                        ActionIconButton(textOne = "Cash", textTwo = "Hello", icon = Icons.Rounded.Payments, onClick = {})
                    }
                    item {
                        ActionIconButton(textOne = "UPI Balance", textTwo = "Hello", icon = Icons.Rounded.CreditCard, onClick = {})
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                WideExpenseHistoryCard(
                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    recentExpenses = recentExpenses
                )
            }

            repeat(6) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    WideSquareCard(label = "Add Points", icon = Icons.Rounded.Payment, onClick = {})
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KeyboardScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    appViewModel: AppViewModel
) {
    val haptic = LocalHapticFeedback.current
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isNavigating by remember { mutableStateOf(false) }

    // Dynamic Font Size logic for keyboard input
    val displayFontSize = remember(input) {
        when {
            input.length > 9 -> 44.sp
            input.length > 7 -> 54.sp
            input.length > 5 -> 64.sp
            else -> 80.sp
        }
    }
    // Animate displayFontSize
    val animatedFontSize by animateFloatAsState(
        targetValue = displayFontSize.value,
        animationSpec = tween(durationMillis = 200)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Amount Display
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = displayFontSize * 0.6f)) { append("₹ ") }
                        append(if (input.isEmpty()) "0" else input)
                    },
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = animatedFontSize.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Visible
                )

            }

            Spacer(modifier = Modifier.weight(1f))

            // THE SQUARE GRID KEYBOARD

            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val keyboardRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("DYNAMIC_BACKSPACE_BACK", "0", "DONE")
                )

                keyboardRows.forEach { rowKeys ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowKeys.forEach { key ->
                            when (key) {
                                "DYNAMIC_BACKSPACE_BACK" -> {
                                    if (input == "") {
                                        SquareKeyboardButton(
                                            modifier = Modifier.weight(1f),
                                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            onClick = {
                                                if (!isNavigating) {
                                                    isNavigating = true
                                                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                                    navController.popBackStack()
                                                }
                                            }
                                        )
                                    } else {
                                        SquareKeyboardButton(
                                            modifier = Modifier.weight(1f),
                                            icon = Icons.AutoMirrored.Filled.Backspace,
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                                input = input.dropLast(1)
                                            },
                                            onLongClick = {
                                                scope.launch {
                                                    while (input.isNotEmpty()) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        input = input.dropLast(1)
                                                        delay(40)
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                "DONE" -> {
                                    SquareKeyboardButton(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        onClick = {
                                            if (!isNavigating && input.isNotEmpty()) {
                                                isNavigating = true
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                navController.navigate("expenseTypeSelector")
                                            }
                                        }
                                    )
                                }
                                else -> {
                                    SquareKeyboardButton(
                                        modifier = Modifier.weight(1f),
                                        symbol = key,
                                        onClick = {
                                            if (input.length < 12) {
                                                if (key == "0" && input == "") return@SquareKeyboardButton
                                                input += key
                                                haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SquareKeyboardButton(
    modifier: Modifier = Modifier,
    symbol: String? = null,
    icon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null
) {
    Surface(
        // Use combinedClickable for long press support
        modifier = modifier
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (symbol != null) {
                Text(text = symbol, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            } else if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
fun GlobalText(text: String?, style: TextStyle, modifier: Modifier = Modifier) {
    Text(text = text ?: "", style = style, textAlign = TextAlign.Center, modifier = modifier)
}

@Composable
fun ActionIconButton(
    modifier: Modifier = Modifier,
    textOne: String,
    textTwo: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        modifier = modifier.padding(10.dp).width(170.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = contentColor)
            }
            Column {
                Text(text = textOne, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = contentColor, maxLines = 15)
                Text(text = textTwo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Light, color = contentColor, maxLines = 1)
            }
        }
    }
}

@Composable
fun BigSquareCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.size(160.dp).aspectRatio(1f),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(28.dp))
            }
            Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2)
        }
    }
}

@Composable
fun WideSquareCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(150.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(28.dp))
            }
            Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2)
        }
    }
}

@Composable
fun WideExpenseHistoryCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    recentExpenses: List<ExpenseEntity>
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(150.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
    ) {
        Row(modifier = modifier.fillMaxSize().padding(8.dp)) {
            ElevatedCard(
                modifier = Modifier.width(90.dp).fillMaxHeight(),
                shape = RoundedCornerShape(topEnd = 8.dp, topStart = 24.dp, bottomEnd = 8.dp, bottomStart = 24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Column(modifier = modifier.fillMaxSize().padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(imageVector = Icons.Rounded.History, contentDescription = "Expense History", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(45.dp))
                }
            }
            Spacer(modifier = modifier.width(10.dp))
            ElevatedCard(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RoundedCornerShape(topEnd = 24.dp, topStart = 8.dp, bottomEnd = 24.dp, bottomStart = 8.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                LazyRow() {
                   if (recentExpenses.isEmpty()) {
                       item {
                           Box(modifier = Modifier.padding(4.dp).fillParentMaxSize(), contentAlignment = Alignment.Center) {
                               Text(text = "No Expenses yet!", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Center))
                           }
                       }
                   } else {
                       items(recentExpenses) { expense ->
                           Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                               Text(text = expense.title, style = MaterialTheme.typography.titleMedium)
                               Spacer(modifier = modifier.width(4.dp))
                               Text(text = "₹${expense.amount}", style = MaterialTheme.typography.titleMedium)
                           }
                       }
                   }
                }
            }
        }
    }
}
