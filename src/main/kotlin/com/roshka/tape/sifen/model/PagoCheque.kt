package com.roshka.tape.sifen.model

import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.math.BigDecimal

data class PagoCheque(
	var numeroCheque: String,
	var bancoEmisorCheque: String
) {}