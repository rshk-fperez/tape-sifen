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
import com.roshka.sifen.core.beans.response.RespuestaConsultaDE
import org.json.JSONObject
import org.json.XML
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperFillManager
import com.sun.xml.registry.common.util.XMLUtil
import java.io.IOException
import java.io.InputStream
import java.io.StringReader
import net.sf.jasperreports.engine.util.JRXmlUtils
import net.sf.jasperreports.engine.util.JRLoader
import java.util.HashMap
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory
import net.sf.jasperreports.engine.JRParameter
import java.util.Locale
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import org.firebirdsql.gds.ng.wire.Response
import java.awt.PageAttributes.MediaType
import org.springframework.web.bind.annotation.ResponseBody
import java.io.ByteArrayInputStream
import org.apache.tomcat.util.http.fileupload.IOUtils
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimplePdfReportConfiguration
import net.sf.jasperreports.export.SimplePdfExporterConfiguration

@RestController
class TapeSifenController {
	val logger = LoggerFactory.getLogger("TapeSifenController")
	val sifenService = SifenService()
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
		return sifenService.sendInvoice(factura)
	}
	
	/*
 	 * Funcion que obtiene datos de la factura a partir del CDC.
	 */
	@GetMapping("/factura/{cdc}")
	fun getFactura(@PathVariable cdc: String) : String {
		logger.info("CDC de factura a consultar: "+cdc)
		val fc = getDatosFactura(cdc)
		return fc.getRespuestaBruta()
	}
	
	fun getDatosFactura(cdc: String) : RespuestaConsultaDE {
		return Sifen.consultaDE(cdc)
	}
	
	@PostMapping("/factura/recibir")
	fun recepcionDocumentoElectronico(@RequestBody xml: String): String {
		return sifenService.recepcionDocumentoElectronico(xml)
	}
	
	@PostMapping("/factura/recibir-cdc")
	fun recepcionDocumentoElectronicoCDC(@RequestBody cdc: String): String {
		try {
			logger.info("CDC de factura a recibir: "+cdc)
			val fc = getDatosFactura(cdc)
			val xml = fc.getRespuestaBruta()
			return sifenService.recepcionDocumentoElectronico(xml)
		} catch (e: SifenException) {
			logger.error("Error en recepcion de DE "+e.printStackTrace())
			return "Ocurrio un error al intentar recibir el DE"
		}
	}
	
	@PostMapping("/lotes-factura/recibir")
	fun recepcionLoteDocumentosElectronicos(@RequestBody xml: String): String {
		return sifenService.recepcionDocumentoElectronico(xml)
	}
	
	@GetMapping("/factura/impresion/{cdc}")
	@ResponseBody
	fun imprimirFactura(@PathVariable cdc: String) {
		//
		try {
			val fc = getDatosFactura(cdc)
			val xml = fc.getRespuestaBruta()
			val params = HashMap<String, Any>()
			val streamXML = xml.byteInputStream()
			params.put(JRXPathQueryExecuterFactory.XML_INPUT_STREAM, streamXML)
            params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd")
			val templateId = "factura-electronica3"
			val reportsDirectory = "C:\\Users\\franb\\JaspersoftWorkspace\\MyReports\\"
			val sourceFileId = "sifen-factura-electronica"
			JasperCompileManager.compileReportToFile(reportsDirectory + templateId + ".jrxml", templateId + ".jasper");
            val jasperprint = JasperFillManager.fillReportToFile(templateId + ".jasper", params);
            val exporter = JRPdfExporter()
			val simpleExporterInput = SimpleExporterInput(jasperprint)
			exporter.setExporterInput(simpleExporterInput)
			val simpleExporterOutput = SimpleOutputStreamExporterOutput(sourceFileId+".pdf")
			exporter.setExporterOutput(simpleExporterOutput)
			val reportConfig = SimplePdfReportConfiguration()
			reportConfig.setSizePageToContent(true)
            reportConfig.setForceLineBreakPolicy(false)
			val exportConfig = SimplePdfExporterConfiguration()
			exportConfig.setMetadataAuthor("roshka")
            exportConfig.setEncrypted(true)
            exportConfig.setAllowedPermissionsHint("PRINTING")
			exporter.setConfiguration(reportConfig)
			exporter.setConfiguration(exportConfig)
			return exporter.exportReport()
		} catch (e: SifenException) {
			logger.error("Error en impresion de DE "+e.printStackTrace())
			return e.printStackTrace()
		}

	}
}