package com.roshka.tape.sifen.controller

import com.roshka.sifen.core.SifenConfig
import com.roshka.sifen.core.exceptions.SifenException
import com.roshka.sifen.Sifen
import org.springframework.stereotype.Controller
import javax.annotation.PostConstruct
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import com.roshka.tape.sifen.model.Factura
import com.roshka.tape.sifen.service.SifenService
import org.slf4j.LoggerFactory

@RestController
class TapeSifenController {
	val logger = LoggerFactory.getLogger("TapeSifenController")
	
	/*
 	 * Inicializamos la configuracion de sifen a partir de lo
 	 * que se defina en sifen.properties
	*/
	
	init {
		try {
			logger.info("Se intentara cargar la config de SIFEN desde config/sifen.properties")
			val sc = SifenConfig.cargarConfiguracion("config/sifen.properties")
			Sifen.setSifenConfig(sc)
			logger.info("Config de SIFEN cargada correctamente")
		} catch (e: SifenException) {
			logger.error("Error al cargar la configuracion de SIFEN "+e.printStackTrace())			
		}
	}
	
	/*
 	 * Funcion que devuelve datos a partir del ruc (sin digito verificador)
	 */
	@GetMapping("/ruc/{ruc}")
	fun consultaRuc(@PathVariable ruc: String) : String {
		logger.info("RUC consultado: "+ruc)
		val cr = Sifen.consultaRUC(ruc)
		return cr.getRespuestaBruta()
	}
	
	/*
 	 * Funcion que envia a SIFEN un documento electronico,
	 * de acuerdo al parametro recibido (JSON con datos de la factura).
	 */
	@PostMapping("/factura")
	fun enviarFactura(@RequestBody factura: Factura) : String {
		logger.info("Se invocará a SifenService para generar el DE")
		val ss = SifenService()
		return ss.sendInvoice(factura)
	}
	
}