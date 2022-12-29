package com.roshka.tape.sifen.model

import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.mutableListOf
import java.math.BigDecimal
import org.firebirdsql.extern.decimal.Decimal

data class ItemsOperacion
	(
		var codigoInterno: String,
		var partidaArancelaria: Int,
		var nCM: Int,
		var codigoDNCPG: String,
		var codigoDNCPE: String,
		var descripcionProductoServicio: String,
	    var unidadMedida: Short,
	    var cantidadProductoServicio: BigDecimal,
	    var codigoPaisOrigen: String,
	    var infoInteres: String,
	    var precioUnitario: BigDecimal,
	    var descuentoItem: BigDecimal,
	    var afectaIVA: Short,
	    var proporcionIVA: BigDecimal,
	    var tasaIVA: BigDecimal
	){}