package com.example.simpel_bank_appv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpel_bank_app.data.BankKonto
import com.example.simpel_bank_app.ui.screens.BankScreen
import com.example.simpel_bank_app.ui.screens.BankViewModel
import com.example.simpel_bank_appv2.ui.theme.Simpel_Bank_APPV2Theme
import com.example.simpel_bank_appv2.ui.theme.screens.LandingScreen
import com.example.simpel_bank_appv2.ui.theme.screens.LandingViewModel
import android.util.Log
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
                    // Vi trenger en instans av LandingViewModel for å dele den med AppNavigation
                    val landingViewModel: LandingViewModel = viewModel()
                    AppNavigation(navController = rememberNavController(), landingViewModel = landingViewModel)
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
    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") {
            LandingScreen(navController = navController, landingViewModel = landingViewModel)
        }
        // Definer ruten for bank-skjermen INNE i NavHost
        composable("bankScreen/{visueltKontonummer}") { backStackEntry ->
            val visueltKontonummerString = backStackEntry.arguments?.getString("visueltKontonummer")
            val visueltKontonummer = visueltKontonummerString?.toLongOrNull()

            val logMessage = "Converted visueltKontonummer: $visueltKontonummer"
            // Bruk Log.d() med en tag og meldingen
            Log.d("MainActivity", logMessage)

            if (visueltKontonummer != null) {
                val currentAccount = landingViewModel.getKontoByVisueltKontonummer(visueltKontonummer)
                if (currentAccount != null) {
                    BankScreenwithAccount(konto = currentAccount, navController = navController)
                } else {
                    Text("Konto med kontonummer $visueltKontonummer ikke funnet.")
                }
            } else {
                Text("Ugyldig kontonummer i URL.")
            }
        }
    }
}
@Composable
fun BankScreenwithAccount(
    konto: BankKonto,
    navController: NavHostController) {
    BankScreen(konto = konto, navController = navController)
    }
/*
// ViewModel Factory for å initialisere BankViewModel med en gitt konto
class BankViewModelFactory(private val initialKonto: BankKonto) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BankViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BankViewModel(initialKonto) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

*/