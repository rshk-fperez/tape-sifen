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

@RestController
class TapeSifenController {
	
	
	/*
 	 * Inicializamos la configuracion de sifen a partir de lo
 	 * que se defina en sifen.properties
	*/
	
	init {
		try {
			val sc = SifenConfig.cargarConfiguracion("config/sifen.properties")
			Sifen.setSifenConfig(sc)
		} catch (e: SifenException) {
			e.printStackTrace()
		}
	}
	
	/*
 	 * Funcion que devuelve datos a partir del ruc (sin digito verificador)
	 */
	@GetMapping("/ruc/{ruc}")
	fun consultaRuc(@PathVariable ruc: String) : String {
		val cr = Sifen.consultaRUC(ruc)
		return cr.getRespuestaBruta()
	}
}