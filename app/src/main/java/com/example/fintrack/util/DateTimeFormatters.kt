package com.example.fintrack.util

import java.time.format.DateTimeFormatter
import java.util.Locale

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("tr"))
val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("tr"))