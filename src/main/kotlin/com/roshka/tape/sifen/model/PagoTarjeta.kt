package com.roshka.tape.sifen.model

import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.math.BigDecimal

data class PagoTarjeta(
	// Campos para cuando el pago es via tarjeta de debito o credito
	var denominacionTarjeta : Short = 0,
	var formaProcesamientoPagoTarjeta: Short = 0
) {}