package com.example.simpel_bank_appv2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpel_bank_app.ui.screens.BankScreen
import com.example.simpel_bank_app.ui.screens.BankViewModel
import com.example.simpel_bank_appv2.data.BankKontoEntity
import com.example.simpel_bank_appv2.data.KontoRepository
import com.example.simpel_bank_appv2.ui.theme.Simpel_Bank_APPV2Theme
import com.example.simpel_bank_appv2.ui.theme.screens.LandingScreen
import com.example.simpel_bank_appv2.ui.theme.screens.LandingViewModel
import com.example.simpel_bank_appv2.ui.theme.screens.LandingViewModelFactory
import com.example.simpel_bank_appv2.ui.theme.screens.SplashScreen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Simpel_Bank_APPV2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val landingViewModel: LandingViewModel = viewModel(
                        factory = LandingViewModelFactory(application)
                    )
                    AppNavigation(
                        navController = rememberNavController(),
                        landingViewModel = landingViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    landingViewModel: LandingViewModel
) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController = navController)
        }

        composable("landing") {
            LandingScreen(navController = navController, landingViewModel = landingViewModel)
        }

        composable("bankScreen/{visueltKontonummer}") { backStackEntry ->
            val visueltKontonummerString = backStackEntry.arguments?.getString("visueltKontonummer")
            val visueltKontonummer = visueltKontonummerString?.toLongOrNull()

            if (visueltKontonummer == null) {
                Text("Ugyldig kontonummer i URL.")
                return@composable
            }

            var konto by remember { mutableStateOf<BankKontoEntity?>(null) }
            var lastet by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            // Laster kontoen fra databasen når skjermen åpnes
            LaunchedEffect(visueltKontonummer) {
                scope.launch {
                    val hentetKonto = landingViewModel.getKontoByVisueltKontonummer(visueltKontonummer).first()
                    konto = hentetKonto
                    lastet = true
                    Log.d("MainActivity", "Konto hentet fra database: ${konto?.toString()}")
                }
            }

            if (!lastet) {
                Text("Laster konto...")
            } else if (konto != null) {
                BankScreenwithAccount(
                    initialKonto = konto!!,
                    navController = navController,
                    landingViewModel = landingViewModel
                )
            } else {
                Text("Konto med kontonummer $visueltKontonummer ikke funnet.")
            }
        }
    }
}

@Composable
fun BankScreenwithAccount(
    initialKonto: BankKontoEntity,
    navController: NavHostController,
    landingViewModel: LandingViewModel
) {
    // Samler Flow fra ViewModel som State for Compose
    val konto by landingViewModel
        .getKontoByVisueltKontonummer(initialKonto.visueltKontonummer)
        .collectAsState(initial = null)

    if (konto != null) {
        BankScreen(
            navController = navController,
            landingViewModel = landingViewModel,
            bankViewModel = BankViewModelFactory(konto!!.visueltKontonummer,
                    landingViewModel.getRepository()).create(BankViewModel::class.java)

        )
    } else {
        Text("Laster konto…")
    }
}




class BankViewModel(
    visueltKontonummer: Long,
    repository: KontoRepository
) : ViewModel() {

    // Konto som StateFlow – oppdateres automatisk når databasen endres
    val konto: StateFlow<BankKontoEntity?> = repository
        .getKontoByVisueltKontonummer(visueltKontonummer)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}

class BankViewModelFactory(
    private val visueltKontonummer: Long,
    private val repository: KontoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BankViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BankViewModel(visueltKontonummer, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/*
val kontoer by landingViewModel.kontoer.collectAsState()
LazyColumn {
    items(kontoer) { konto ->
        Text("Navn: ${konto.kontoeierNavn}, Saldo: ${konto.pengeSum}")
    }
}


 */