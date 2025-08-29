package com.example.simpel_bank_appv2.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.simpel_bank_appv2.data.BankKontoEntity // Antar du har denne dataklassen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    navController: NavController,
    landingViewModel: LandingViewModel = viewModel()
    ) {
    val kontoer by landingViewModel.kontoer.collectAsState(initial = emptyList())
    var nyKontoeierNavn by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().
        padding(top = 40.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mine Kontoer", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Seksjon for å legge til ny konto
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Legg til ny konto", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nyKontoeierNavn,
                    onValueChange = { nyKontoeierNavn = it },
                    label = { Text("Kontoeiers navn") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        landingViewModel.leggTilKonto(nyKontoeierNavn)
                        nyKontoeierNavn = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Legg til")
                    Text("Legg til Konto")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liste over eksisterende kontoer
        Text("Kontoer", style = MaterialTheme.typography.titleMedium)

        Divider()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(kontoer) { konto ->
                KontoItem(konto = konto) {
                    // Når en konto klikkes, naviger til BankScreen for den kontoen
                    navController.navigate("bankScreen/${konto.id}")
                }
            }
        }
    }
}

@Composable
fun KontoItem(konto: BankKontoEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Gjør hele kortet klikkbart
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Kontonr: ${konto.id}",
                style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                text = "${String.format("%.2f", konto.pengeSum)} kr",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

