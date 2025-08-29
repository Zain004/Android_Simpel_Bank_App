package com.example.simpel_bank_app.ui.screens

import android.util.Log
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.simpel_bank_appv2.data.Transaksjonstype
import com.example.simpel_bank_appv2.data.TransaksjonEntity
import com.example.simpel_bank_appv2.ui.theme.screens.LandingViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankScreen(
    navController: NavHostController,
    bankViewModel: BankViewModel,
    landingViewModel: LandingViewModel
) {
    // Hent konto som Flow fra databasen
    val currentKonto by bankViewModel.currentKonto.collectAsState()

    val transaksjoner by bankViewModel.transaksjoner.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Last transaksjoner når kontoen endres
    /*
    LaunchedEffect(currentKonto?.id) {
        currentKonto?.let {
            transaksjoner = bankViewModel.transaksjoner
        }
    }

     */

    var innskuddsBelopInput by remember { mutableStateOf("") }
    var uttaksBelopInput by remember { mutableStateOf("") }
    var visFeilmelding by remember { mutableStateOf<String?>(null) }

    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 40.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Bank App", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(10.dp))

        // Kontoinformasjon
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = currentKonto?.kontoeierNavn ?: "",
                    onValueChange = { newValue ->
                        Log.d("BankScreen", "Kontoeiers navn endret til: '$newValue'")
                        bankViewModel.settKontoeierNavn(newValue)
                        currentKonto?.id?.let {
                            landingViewModel.oppdaterKontoeierNavn(it, newValue)
                        }
                    },
                    label = { Text("Kontoeiers navn") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Saldo: ${String.format("%.2f", currentKonto?.pengeSum ?: 0.0)} kr",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Felt for innskudd/uttak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = innskuddsBelopInput,
                onValueChange = { innskuddsBelopInput = it },
                label = { Text("Sett inn") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = "Innskudd") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = uttaksBelopInput,
                onValueChange = { uttaksBelopInput = it },
                label = { Text("Ta ut") },
                leadingIcon = { Icon(Icons.Default.Euro, contentDescription = "Uttak") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.weight(1f)
            )
        }

        // Knapper for innskudd/uttak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val belop = innskuddsBelopInput.toDoubleOrNull()
                if (belop != null) {
                    bankViewModel.settInn(belop)
                    //coroutineScope.launch { transaksjoner = bankViewModel.visTransaksjoner() }
                    innskuddsBelopInput = ""
                    visFeilmelding = null
                } else {
                    visFeilmelding = "Ugyldig beløp for innskudd."
                }
            }) {
                Text("Sett inn")
            }

            Button(onClick = {
                val belop = uttaksBelopInput.toDoubleOrNull()
                if (belop != null) {
                    coroutineScope.launch {
                        val ok = bankViewModel.taUt(belop)
                        if (ok) {
                            //transaksjoner = bankViewModel.visTransaksjoner()
                            uttaksBelopInput = ""
                            visFeilmelding = null
                        } else {
                            visFeilmelding = "Ikke nok penger på konto eller ugyldig beløp."
                        }
                    }
                } else {
                    visFeilmelding = "Ugyldig beløp for uttak."
                }
            }) {
                Text("Ta ut")
            }
        }

        // Feilmelding
        visFeilmelding?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transaksjonsliste hentet fra databasen
        Text("Transaksjoner: ", style = MaterialTheme.typography.titleMedium)
        Divider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(transaksjoner) { transaksjon ->
                TransaksjonsItem(transaksjon = transaksjon, formatter = formatter)
            }
        }
    }
}

@Composable
fun TransaksjonsItem(transaksjon: TransaksjonEntity, formatter: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = transaksjon.type.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (transaksjon.type) {
                        Transaksjonstype.INNSETT -> MaterialTheme.colorScheme.primary
                        Transaksjonstype.UTTAK -> MaterialTheme.colorScheme.error
                    }
                )
                Text(
                    text = formatter.format(transaksjon.tidspunkt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${String.format("%.2f", transaksjon.belop)} kr",
                style = MaterialTheme.typography.titleMedium,
                color = when (transaksjon.type) {
                    Transaksjonstype.INNSETT -> MaterialTheme.colorScheme.primary
                    Transaksjonstype.UTTAK -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}
