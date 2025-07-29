package com.example.apphabitosteste

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import java.util.SortedMap
import java.util.TreeMap

import com.example.apphabitosteste.data.HabitDataStoreManager
import com.example.apphabitosteste.ui.theme.AppHabitosTesteTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            HabitApp(context)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HabitApp(context: android.content.Context) {
    val navController = rememberNavController()
    val dataStoreManager = remember { HabitDataStoreManager(context) }

    val calendarTabs = remember { mutableStateListOf<String>() }
    val calendarData = remember { mutableStateMapOf<String, MutableMap<LocalDate, Boolean>>() }

    var isDarkThemeEnabled by remember { mutableStateOf(false) }

    val appCoroutineScope = rememberCoroutineScope()

    var areDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        areDataLoaded = false

        calendarTabs.clear()
        calendarData.clear()

        val loadedTabs = dataStoreManager.loadCalendarTabs()
        calendarTabs.addAll(loadedTabs)

        loadedTabs.forEach { tabName ->
            val loadedData = dataStoreManager.loadCalendarData(tabName)
            calendarData[tabName] = loadedData
        }

        isDarkThemeEnabled = dataStoreManager.loadDarkThemePreference()

        areDataLoaded = true
    }

    AppHabitosTesteTheme(darkTheme = isDarkThemeEnabled) {
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            bottomBar = { BottomNavigationBar(navController) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") {
                    if (areDataLoaded) {
                        CalendarScreen(calendarTabs, calendarData, dataStoreManager, snackbarHostState)
                    } else {

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                composable("usage") { UsageScreen() }
                composable("reports") { ReportsScreen(calendarData) }
                composable("settings") {
                    SettingsScreen(
                        isDarkThemeEnabled = isDarkThemeEnabled,
                        onToggleDarkTheme = { newState ->
                            appCoroutineScope.launch {
                                isDarkThemeEnabled = newState
                                dataStoreManager.saveDarkThemePreference(newState)
                            }
                        },
                        dataStoreManager = dataStoreManager,
                        navController = navController,
                        onDataReset = {
                            appCoroutineScope.launch {

                                areDataLoaded = false
                                val loadedTabs = dataStoreManager.loadCalendarTabs()
                                calendarTabs.clear()
                                calendarTabs.addAll(loadedTabs)
                                calendarData.clear()
                                loadedTabs.forEach { tabName ->
                                    val loadedData = dataStoreManager.loadCalendarData(tabName)
                                    calendarData[tabName] = loadedData
                                }
                                isDarkThemeEnabled = dataStoreManager.loadDarkThemePreference()
                                areDataLoaded = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.secondary) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == "home",
            onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                unselectedIconColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Info, contentDescription = "Uso") },
            label = { Text("Uso") },
            selected = navController.currentDestination?.route == "usage",
            onClick = { navController.navigate("usage") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                unselectedIconColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Relatórios") },
            label = { Text("Relatórios") },
            selected = navController.currentDestination?.route == "reports",
            onClick = { navController.navigate("reports") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                unselectedIconColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Configurações") },
            label = { Text("Configurações") },
            selected = navController.currentDestination?.route == "settings",
            onClick = { navController.navigate("settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                unselectedIconColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
fun UsageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Como Usar o App",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Criando Novos Calendários:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            "1. Na tela inicial, insira o nome do novo calendário no campo 'Nome do novo calendário'.\n" +
                    "2. Clique em 'Adicionar Calendário' para criar uma nova aba com o nome inserido.\n",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Marcações no Calendário:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            "1. Navegue entre os meses e anos usando as setas no topo do calendário.\n" +
                    "2. Clique nos dias para marcar ou desmarcar hábitos. Dias marcados ficam com fundo azul claro.\n",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Relatórios:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            "A aba de relatórios mostra o nome de cada calendário criado e quantas marcações foram feitas em cada um.\n",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportsScreen(calendarData: Map<String, Map<LocalDate, Boolean>>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Relatórios",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (calendarData.isEmpty()) {
            Text(
                "Nenhum calendário criado.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        } else {
            val groupedReports = mutableMapOf<String, SortedMap<YearMonth, Int>>()

            calendarData.forEach { (calendarName, markedDates) ->
                val monthlyCounts = TreeMap<YearMonth, Int>(compareByDescending { it })
                markedDates.forEach { (date, isMarked) ->
                    if (isMarked) {
                        val yearMonth = YearMonth.of(date.year, date.month)
                        monthlyCounts[yearMonth] = (monthlyCounts[yearMonth] ?: 0) + 1
                    }
                }
                if (monthlyCounts.isNotEmpty()) {
                    groupedReports[calendarName] = monthlyCounts
                }
            }

            if (groupedReports.isEmpty()) {
                Text(
                    "Nenhuma marcação de hábito registrada em qualquer calendário.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                groupedReports.forEach { (calendarName, monthlyCounts) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                calendarName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (monthlyCounts.isEmpty()) {
                                Text(
                                    "Nenhuma marcação para este calendário.",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                monthlyCounts.forEach { (yearMonth, count) ->
                                    val totalDaysInMonth = yearMonth.lengthOfMonth()
                                    val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                                    val percentage = if (totalDaysInMonth > 0) {
                                        (count.toFloat() / totalDaysInMonth.toFloat()) * 100
                                    } else {
                                        0f
                                    }
                                    val progress = percentage / 100f

                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(
                                            "$monthName ${yearMonth.year}: $count / $totalDaysInMonth dias (${"%.1f".format(percentage)}%)",
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = progress,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
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
fun SettingsScreen(
    isDarkThemeEnabled: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    dataStoreManager: HabitDataStoreManager,
    navController: NavHostController,
    onDataReset: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Configurações",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Sobre o Aplicativo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Versão: 1.0.0",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Desenvolvido por: Matheus Gonçalves Lima",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Modo Noturno",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = isDarkThemeEnabled,
                        onCheckedChange = { newState ->
                            coroutineScope.launch {
                                onToggleDarkTheme(newState)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            uncheckedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Feedback e Suporte",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val emailSubject = "Feedback sobre o App de Hábitos"
                val developerEmail = "seu.email@example.com"

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(developerEmail))
                            putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                        }
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Enviar Feedback / Entrar em Contato", color = MaterialTheme.colorScheme.onTertiary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reiniciar Todos os Dados", color = MaterialTheme.colorScheme.onError)
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reiniciar Dados?") },
                text = { Text("Isso apagará todos os seus calendários e marcações, além das configurações do aplicativo. Esta ação é irreversível.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                dataStoreManager.clearAllData()
                                showResetDialog = false
                                onDataReset()
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    calendarTabs: MutableList<String>,
    calendarData: MutableMap<String, MutableMap<LocalDate, Boolean>>,
    dataStoreManager: HabitDataStoreManager,
    snackbarHostState: SnackbarHostState
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var newCalendarName by remember { mutableStateOf("") }
    var showAddCalendarFields by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(calendarTabs) {
        if (calendarTabs.isEmpty()) {
            selectedTab = 0
            showAddCalendarFields = true
        } else {
            selectedTab = selectedTab.coerceIn(0, calendarTabs.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Calendários",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    showAddCalendarFields = !showAddCalendarFields
                    if (!showAddCalendarFields) {
                        newCalendarName = ""
                    }
                },
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(if (showAddCalendarFields) "Fechar Criação" else "Criar Calendário", color = MaterialTheme.colorScheme.onPrimary)
            }

            Button(
                onClick = {
                    if (calendarTabs.isNotEmpty()) {
                        coroutineScope.launch {
                            val removedCalendarName = calendarTabs[selectedTab]


                            calendarData.remove(removedCalendarName)
                            calendarTabs.removeAt(selectedTab)

                            dataStoreManager.saveCalendarTabs(calendarTabs)
                            dataStoreManager.removeCalendarData(removedCalendarName)

                            snackbarHostState.showSnackbar("Calendário '$removedCalendarName' excluído!")
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Nenhum calendário para excluir.")
                        }
                    }
                },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = calendarTabs.isNotEmpty()
            ) {
                Text("Excluir Calendário", color = MaterialTheme.colorScheme.onError)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showAddCalendarFields) {
            OutlinedTextField(
                value = newCalendarName,
                onValueChange = { newCalendarName = it },
                label = { Text("Nome do novo calendário") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (newCalendarName.isNotBlank()) {
                        val newName = newCalendarName.trim()
                        if (!calendarTabs.contains(newName)) {
                            calendarTabs.add(newName)
                            calendarData[newName] = mutableMapOf()
                            coroutineScope.launch {
                                dataStoreManager.saveCalendarTabs(calendarTabs)
                                dataStoreManager.saveCalendarData(newName, mutableMapOf())
                            }
                            newCalendarName = ""
                            showAddCalendarFields = false

                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Calendário com este nome já existe!",
                                    withDismissAction = true
                                )
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("O nome do calendário não pode ser vazio.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Adicionar Calendário", color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (!showAddCalendarFields) {
            if (calendarTabs.isNotEmpty()) {
                LazyRow(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    itemsIndexed(calendarTabs) { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(tab) },
                            modifier = Modifier.border(
                                width = 1.dp,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            ),
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (selectedTab >= 0 && selectedTab < calendarTabs.size) {
                            CalendarView(
                                calendarName = calendarTabs[selectedTab],
                                calendarData = calendarData,
                                dataStoreManager = dataStoreManager
                            )
                        } else {
                            Text(
                                "Nenhum calendário selecionado. Por favor, crie um novo.",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp)
                            )
                        }
                    }
                }
            } else {
                Text(
                    "Crie um novo calendário para começar!",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                )
            }
        } else {
            Text(
                "Insira o nome do calendário para criar um novo.",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    calendarName: String,
    calendarData: MutableMap<String, MutableMap<LocalDate, Boolean>>,
    dataStoreManager: HabitDataStoreManager
) {
    var currentYear by remember { mutableIntStateOf(YearMonth.now().year) }
    var currentMonth by remember { mutableIntStateOf(YearMonth.now().monthValue) }
    val currentYearMonth = YearMonth.of(currentYear, currentMonth)
    val daysInMonth = currentYearMonth.lengthOfMonth()
    val firstDayOfMonth = currentYearMonth.atDay(1).dayOfWeek.value % 7

    val coroutineScope = rememberCoroutineScope()

    val markedDates = remember(calendarName) {
        mutableStateMapOf<LocalDate, Boolean>().apply {
            calendarData[calendarName]?.let { putAll(it) } ?: run {
                calendarData[calendarName] = mutableMapOf()
            }
        }
    }

    LaunchedEffect(calendarName) {
        markedDates.clear()
        calendarData[calendarName]?.let { markedDates.putAll(it) }
    }


    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = {
                if (currentMonth == 1) {
                    currentYear--
                    currentMonth = 12
                } else {
                    currentMonth--
                }
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Mês anterior", tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                text = "${currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentYearMonth.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = {
                if (currentMonth == 12) {
                    currentYear++
                    currentMonth = 1
                } else {
                    currentMonth++
                }
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Próximo mês", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            val daysOfWeek = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
            daysOfWeek.forEach { dayOfWeek ->
                Text(text = dayOfWeek, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

        for (week in 0..5) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                for (day in 1..7) {
                    val date = week * 7 + day - firstDayOfMonth
                    if (date in 1..daysInMonth) {
                        val localDate = currentYearMonth.atDay(date)
                        val isChecked = markedDates[localDate] == true
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    val newIsChecked = !isChecked
                                    markedDates[localDate] = newIsChecked
                                    calendarData[calendarName] = markedDates.toMap().toMutableMap()

                                    coroutineScope.launch {
                                        dataStoreManager.saveCalendarData(calendarName, markedDates.toMap())
                                    }
                                }
                                .background(
                                    color = if (isChecked) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                    shape = MaterialTheme.shapes.small
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.toString(),
                                color = if (isChecked) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Box(modifier = Modifier.size(40.dp)) {}
                    }
                }
            }
        }
    }
}