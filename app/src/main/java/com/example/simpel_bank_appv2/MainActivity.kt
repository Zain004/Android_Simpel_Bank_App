package com.example.simpel_bank_appv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simpel_bank_app.ui.screens.BankScreen
import com.example.simpel_bank_app.ui.screens.BankViewModel
import com.example.simpel_bank_appv2.ui.theme.Simpel_Bank_APPV2Theme
import com.example.simpel_bank_appv2.ui.theme.screens.LandingScreen
import com.example.simpel_bank_appv2.ui.theme.screens.LandingViewModel
import android.util.Log
import com.example.simpel_bank_appv2.data.BankKontoEntity
import com.example.simpel_bank_appv2.ui.theme.screens.SplashScreen

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
    NavHost(navController = navController, startDestination = "splash") {
        // Steg 1: Splash screen som vises kun ved oppstart
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // Steg 2: Landing Screen
        composable("landing") {
            LandingScreen(navController = navController, landingViewModel = landingViewModel)
        }

        // Steg 3: Hvis vedkommende ønsker å administrere en av sine kontoer
        composable("bankScreen/{visueltKontonummer}") { backStackEntry ->
            val visueltKontonummerString = backStackEntry.arguments?.getString("visueltKontonummer")
            val visueltKontonummer = visueltKontonummerString?.toLongOrNull()

            val logMessage = "Converted visueltKontonummer: $visueltKontonummer"
            // Bruk Log.d() med en tag og meldingen
            Log.d("MainActivity", logMessage)

            if (visueltKontonummer != null) {
                val currentAccount = landingViewModel.getKontoByVisueltKontonummer(visueltKontonummer)
                if (currentAccount != null) {
                    Log.d("MainActivity", "Initial BankKonto for bankScreen: ${currentAccount.toString()}")
                    BankScreenwithAccount(initialKonto = currentAccount, navController = navController, landingViewModel = landingViewModel)
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
    initialKonto: BankKontoEntity,
    navController: NavHostController,
    landingViewModel: LandingViewModel) {
    val bankViewModel: BankViewModel = viewModel (
        factory = BankViewModelFactory(initialKonto)
    )
    BankScreen(navController = navController, bankViewModel = bankViewModel, landingViewModel = landingViewModel)
}

class BankViewModelFactory(
    private val initialKonto: BankKontoEntity
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BankViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BankViewModel(initialKonto) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



