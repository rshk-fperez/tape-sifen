package com.roshka.tape.sifen.model

import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.math.BigDecimal

data class PagoContadoEntregaInicial
	(
		var tipoPago: Int,
		var montoPago: BigDecimal,
		var monedaPago: String,
		var tipoCambio: BigDecimal
	){}