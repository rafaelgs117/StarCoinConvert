package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgDark
import com.example.ui.theme.GoldStar
import com.example.ui.theme.MutedText
import com.example.ui.theme.StarCoinTheme
import com.example.ui.theme.WhiteText
import java.util.Locale

enum class Screen {
    Home, Converter
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarCoinTheme {
                var currentScreen by remember { mutableStateOf(Screen.Home) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgDark),
                    contentWindowInsets = WindowInsets.systemBars
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(BgDark, BgCard)
                                )
                            )
                    ) {
                        Crossfade(
                            targetState = currentScreen,
                            label = "ScreenTransition"
                        ) { screen ->
                            when (screen) {
                                Screen.Home -> HomeScreen(
                                    onNavigateToConverter = {
                                        currentScreen = Screen.Converter
                                    },
                                    viewModel = viewModel
                                )
                                Screen.Converter -> ConverterScreen(
                                    onNavigateBack = {
                                        currentScreen = Screen.Home
                                    },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onNavigateToConverter: () -> Unit,
    viewModel: MainViewModel
) {
    val scrollState = rememberScrollState()
    var showKeyEditor by remember { mutableStateOf(false) }
    val currentKey by viewModel.apiKey.collectAsStateWithLifecycle()
    var tempKeyText by remember { mutableStateOf(currentKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper logo and title header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(BgCard)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star Logo",
                    tint = GoldStar,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Star Coin Convert",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = GoldStar,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Conversão de Moedas Inteligente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = WhiteText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Consulte taxas de câmbio reais atualizadas automaticamente a cada hora, com conversor dinâmico de 15 moedas globais.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Functional presentation card
        Card(
            colors = CardDefaults.cardColors(containerColor = BgCard),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Suporte a 15 Principais Moedas",
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldStar,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Conversões robustas abrangendo USD, BRL, EUR, GBP, ARS, JPY, CAD, CHF, AUD, CLP, MXN, UYU, CNY, INR e BTC.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WhiteText,
                    lineHeight = 18.sp
                )
            }
        }

        // Main CTA Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onNavigateToConverter,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldStar,
                    contentColor = BgDark
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("ir_conversor_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Ir para Conversor",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expandable segment for API key management (Fail safety)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { showKeyEditor = !showKeyEditor }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Configurar Chave",
                        tint = MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showKeyEditor) "Ocultar Chave de API" else "Mostrar Configurações de API",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (showKeyEditor) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BgCard.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Chave da ExchangeRate-API:",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldStar,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = tempKeyText,
                                    onValueChange = { tempKeyText = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = GoldStar,
                                        unfocusedBorderColor = MutedText
                                    ),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.updateApiKey(tempKeyText)
                                        showKeyEditor = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldStar,
                                        contentColor = BgDark
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text("Salvar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Usando chave padrão robusta da documentação.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConverterScreen(
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val currencies = listOf(
        "USD", "BRL", "EUR", "GBP", "ARS",
        "JPY", "CAD", "CHF", "AUD", "CLP",
        "MXN", "UYU", "CNY", "INR", "BTC"
    )

    val currencyLabels = listOf(
        "USD — Dólar Americano",
        "BRL — Real Brasileiro",
        "EUR — Euro",
        "GBP — Libra Esterlina",
        "ARS — Peso Argentino",
        "JPY — Iene Japonês",
        "CAD — Dólar Canadense",
        "CHF — Franco Suíço",
        "AUD — Dólar Australiano",
        "CLP — Peso Chileno",
        "MXN — Peso Mexicano",
        "UYU — Peso Uruguaio",
        "CNY — Yuan Chinês",
        "INR — Rúpia Indiana",
        "BTC — Bitcoin"
    )

    var fromIndex by remember { mutableStateOf(0) } // Default USD
    var toIndex by remember { mutableStateOf(1) } // Default BRL

    var inputAmount by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    var resultText by remember { mutableStateOf("—") }
    var conversionRateText by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val rates by viewModel.rates.collectAsStateWithLifecycle()
    val lastUpdate by viewModel.lastUpdate.collectAsStateWithLifecycle()
    val apiError by viewModel.error.collectAsStateWithLifecycle()

    // Trigger initial load when screen launches
    LaunchedEffect(fromIndex) {
        viewModel.fetchRates(currencies[fromIndex])
        resultText = "—"
        conversionRateText = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = GoldStar
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Star Coin Convert",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GoldStar
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Amount Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BgCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Valor para Converter",
                        style = MaterialTheme.typography.labelMedium,
                        color = GoldStar,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = {
                            inputAmount = it
                            inputError = null
                        },
                        placeholder = { Text("Digite o valor", color = MutedText) },
                        isError = inputError != null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldStar,
                            unfocusedBorderColor = MutedText,
                            errorBorderColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("et_amount"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (inputError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = inputError ?: "",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector Container Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BgCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Origin Select Dropdowns
                    CurrencyDropdown(
                        label = "Moeda de Origem:",
                        currentIndex = fromIndex,
                        currencyLabels = currencyLabels,
                        onIndexSelected = { pos ->
                            fromIndex = pos
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("spinner_from")
                    )

                    // Swapper Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                val temp = fromIndex
                                fromIndex = toIndex
                                toIndex = temp
                                resultText = "—"
                                conversionRateText = ""
                            },
                            modifier = Modifier
                                .testTag("btn_swap")
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GoldStar)
                        ) {
                            Text(
                                text = "⇆",
                                color = BgDark,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Destination Select Dropdown
                    CurrencyDropdown(
                        label = "Moeda de Destino:",
                        currentIndex = toIndex,
                        currencyLabels = currencyLabels,
                        onIndexSelected = { pos ->
                            toIndex = pos
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("spinner_to")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loader
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = GoldStar,
                        modifier = Modifier.testTag("progress_bar")
                    )
                }
            }

            // Calculation Action Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()

                    val cleanValText = inputAmount.replace(",", ".")
                    val amount = cleanValText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        inputError = "Digite um valor válido"
                        return@Button
                    }

                    val from = currencies[fromIndex]
                    val to = currencies[toIndex]
                    val result = viewModel.convert(amount, to)
                    val rate = viewModel.getRate(to)

                    if (rate == 0.0) {
                        apiError?.let {
                            inputError = "Taxas indisponíveis: $it"
                        } ?: run {
                            inputError = "Aguarde o carregamento das taxas de câmbio"
                        }
                        return@Button
                    }

                    resultText = String.format(Locale.US, "%.2f %s", result, to)
                    conversionRateText = String.format(Locale.US, "1 %s = %.4f %s", from, rate, to)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldStar,
                    contentColor = BgDark
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_convert"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Converter",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display Results Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BgCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Resultado da Conversão",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldStar,
                        modifier = Modifier.testTag("tv_result")
                    )
                    if (conversionRateText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = conversionRateText,
                            style = MaterialTheme.typography.bodySmall,
                            color = WhiteText,
                            modifier = Modifier.testTag("tv_rate")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = BgDark, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Status indicators (Last update or API Errors)
                    if (apiError != null) {
                        Text(
                            text = "Erro: $apiError",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (lastUpdate.isNotEmpty()) {
                        Text(
                            text = "Atualizado: $lastUpdate",
                            color = MutedText,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tv_status")
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    label: String,
    currentIndex: Int,
    currencyLabels: List<String>,
    onIndexSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(
                containerColor = BgCard,
                contentColor = WhiteText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldStar,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currencyLabels[currentIndex],
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "▾",
                        color = GoldStar,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(BgCard)
                .fillMaxWidth(0.8f)
        ) {
            currencyLabels.forEachIndexed { index, labelText ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = labelText,
                            color = if (index == currentIndex) GoldStar else Color.White,
                            fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onIndexSelected(index)
                        expanded = false
                    },
                    modifier = Modifier.background(if (index == currentIndex) BgDark else Color.Transparent)
                )
            }
        }
    }
}
