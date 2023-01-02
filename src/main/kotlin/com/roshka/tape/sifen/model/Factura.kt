package com.roshka.tape.sifen.model

import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.mutableListOf

data class Factura
	(
	var numero: String,
	var fecha: LocalDateTime,
	var nombreEmisor: String,
	var direccionEmisor: String,
	var rucEmisor: String,
	var dvEmisor: String,
	var distritoEmisor: Short,
	var descDistritoEmisor: String,
	var ciudadEmisor: Int,
	var descCiudadEmisor: String,
	var telefonoEmisor: String,
	var emailEmisor: String,
	var tipoContribuyente: Short,
	var timbrado: Int,
	var establecimiento: String,
	var puntoExpedicion: String,
	var tipoContribuyenteReceptor: Short,
	var rucReceptor: String,
	var dvReceptor: Short,
	var nombreReceptor: String,
	var nombreFantasiaReceptor: String,
	var distritoReceptor: Short,
	var descDistritoReceptor: String,
	var ciudadReceptor: Int,
	var fechaInicioTimbrado: LocalDate,
	var indicadorPresencia: Int,
	var descIndicadorPresencia: String,
	var fechaEmNR: LocalDate,
	var condicionOperacion: Int,
	var pagoContadoEntregaInicial: MutableList<PagoContadoEntregaInicial>? = null,
	var actividadesEconomicas: MutableList<ActividadesEconomicas>,
	var itemsOperacion: MutableList<ItemsOperacion>,
	var pagoCredito: MutableList<PagoCredito>? = null
	) {}