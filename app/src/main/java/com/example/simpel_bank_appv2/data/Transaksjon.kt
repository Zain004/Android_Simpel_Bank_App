package com.example.simpel_bank_app.data

import java.util.Date

data class Transaksjon(
    val type: Transaksjonstype,
    val belop: Double,
    val tidspunkt: Date
)