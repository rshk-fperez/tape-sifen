package com.roshka.tape.sifen.model

import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.math.BigDecimal

data class PagoCredito(
	var condicionCredito: Short,
	var plazoCredito: String,
	var cantidadCuotas: Short,
	var montoEntregaInicial: BigDecimal?
) {}