package com.example.simpel_bank_app.ui.screens

// ui/screens/BankScreen.kt
import android.R
import android.R.attr.label
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.simpel_bank_app.data.BankKonto
import com.example.simpel_bank_app.data.Transaksjon
import com.example.simpel_bank_app.data.Transaksjonstype
import com.example.simpel_bank_appv2.ui.theme.screens.LandingViewModel
import java.time.format.DateTimeFormatter

// Dataklasser som ble definert tidligere
// data class BankKonto(...)
// data class Transaksjon(...)
// enum class Transaksjonstype

//@Suppress("NewApi")
@OptIn(ExperimentalMaterial3Api::class) // aksepterer
@Composable
fun BankScreen(
    navController: NavHostController, // bruker host controller for å navigere fordi vi bruker compose
    bankViewModel: BankViewModel,
    landingViewModel: LandingViewModel
) {
    // Hent tilstanden fra ViewModel
    var innskuddsBelopInput by remember {mutableStateOf("")}
    var uttaksBelopInput by remember {mutableStateOf("")}
    var visFeilmelding by remember {mutableStateOf<String?>(null)}

    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())



    Column(
        modifier = Modifier.fillMaxSize().padding(top = 40.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Bank App",
            style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(10.dp))
        // Kontoinformasjon seksjon
        // Kontoinformasjon seksjon
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = bankViewModel.bankKonto.kontoeierNavn,
                    onValueChange = { newValue ->
                        // --- LOGGING LAGT TIL HER ---
                        Log.d("BankScreen", "Kontoeiers navn endret fra: '${bankViewModel.bankKonto.kontoeierNavn}' til: '$newValue'")
                        bankViewModel.settKontoeierNavn(newValue)
                        landingViewModel.oppdaterKontoeierNavn(bankViewModel.bankKonto.visueltKontonummer,
                            newValue)
                        // --- SLUTT PÅ LOGGING ---
                    },
                    label = { Text("Kontoeiers navn") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Saldo: ${String.format("%.2f", bankViewModel.bankKonto.pengeSum)} kr",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

    Spacer(modifier = Modifier.height(16.dp))
    /*
        Denne koden lager en horisontal rad (Row) med to tekstfelt side om side, med litt avstand mellom dem.

        - Først er det en Spacer på 16.dp som gir **vertikal luft** over raden.
        - Row-en fyller hele bredden på skjermen (`fillMaxWidth()`), med innholdet jevnt fordelt horisontalt (`SpaceBetween`) og sentrert vertikalt (`CenterVertically`).

        Innholdet i Row-en:

        1. **Første OutlinedTextField (Innskudd)**
           - Viser teksten fra `innskuddsBelopInput`.
           - Har en ledende ikon av typen pengesymbol (`AttachMoney`).
           - Label: "Sett inn".
           - Tastaturtype: numerisk med desimal (NumberPassword).
           - Modifier `weight(1f)` gjør at tekstfeltet tar **halvparten av tilgjengelig bredde** (prosentvis, sammen med det andre feltet).

        2. **Spacer mellom tekstfeltene**
           - En horisontal avstand på 8.dp for å separere tekstfeltene.

        3. **Andre OutlinedTextField (Uttak)**
           - Viser teksten fra `uttaksBelopInput`.
           - Ledende ikon av typen euro (`Euro`).
           - Label: "Ta ut".
           - Tastaturtype: numerisk med desimal.
           - Modifier `weight(1f)` gjør at dette feltet tar **den andre halvparten av tilgjengelig bredde**.

        Resultatet visuelt:

        | [Innskudd tekstfelt]   8.dp   [Uttak tekstfelt] |

        Begge tekstfeltene er like brede og ligger på samme horisontale linje, med et lite mellomrom mellom dem.
    */

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = innskuddsBelopInput,
            onValueChange = {innskuddsBelopInput = it},
            label = { Text("Sett inn") },
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = "Innskudd") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), // Bruk NumberPassword for å få numerisk tastatur med desimaltegn
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = uttaksBelopInput,
            onValueChange = { uttaksBelopInput = it }, // Riktig oppdatering av state
            label = { Text("Ta ut") },
            leadingIcon = { Icon(Icons.Default.Euro, contentDescription = "Uttak") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.weight(1f)
        )
    }

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Knapp for settInn
        Button(onClick = {
            val belop = innskuddsBelopInput.toDoubleOrNull()
            if (belop != null) {
                bankViewModel.settInn(belop)
                innskuddsBelopInput = "" //Nullstill etter handling
                visFeilmelding = null // Nullstill eventuel gammel feilmelding
            } else {
                visFeilmelding = "Ugyldig beløp for innskudd."
            }
        }) {
            Text("Sett inn")
        }

        // Knapp for ta ut
        Button(onClick = {
            val belop = uttaksBelopInput.toDoubleOrNull()
            if (belop != null) {
                if (belop <= bankViewModel.bankKonto.pengeSum) {
                    bankViewModel.taUt(belop)
                    uttaksBelopInput = "" // Nullstill etter handling
                    visFeilmelding = null // Nullstill eventuell gammel
                } else {
                    visFeilmelding = "Ikke nok penger på konto eller ugyldig beløp."
                }
            } else {
                visFeilmelding = "Ugyldig beløp for uttak"
            }
        }) {
            Text("Ta ut")
        }
    }
        // utfører kode med variabelen
        visFeilmelding?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error, // Bruk feilfarge fra temaet
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TransaksjonsListe
        Text("Transakskjoner: ", style = MaterialTheme.typography.titleMedium)
        Divider()
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ){
            items(bankViewModel.bankKonto.transaksjoner) { transaksjon ->
                TransaksjonsItem(
                    transaksjon = transaksjon,
                    formatter = formatter)
            }
        }
    }
}

// En separat Composable for å vise en enkelt transaksjon for bedre struktur
@Composable
fun TransaksjonsItem(transaksjon: Transaksjon, formatter: SimpleDateFormat) {
    // oppreter en funksjon ssom lager et kort som fyller bredden for hver transaksjon
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
       Row (modifier = Modifier.fillMaxWidth().padding(8.dp),
           verticalAlignment = Alignment.CenterVertically,
           horizontalArrangement = Arrangement.SpaceBetween) {
           Column {
               Text(text = transaksjon.type.name, // F.eks "INNSETT" eller "UTTAK"
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (transaksjon.type) {
                        Transaksjonstype.INNSETT -> MaterialTheme.colorScheme.primary // Grønnaktig farge for innskudd
                        Transaksjonstype.UTTAK -> MaterialTheme.colorScheme.error
                    }
                   )
               Text(
                   text = formatter.format(transaksjon.tidspunkt), // her bruker vi SimpleDateFormat
                   style = MaterialTheme.typography.bodySmall,
                   color = MaterialTheme.colorScheme.onSurfaceVariant // En mykere farge for dato/tid
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

// Preview-funksjon for å se hvordan BankScreen ser ut i designvisningen
