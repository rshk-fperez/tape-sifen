package com.roshka.tape.sifen.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import com.roshka.tape.sifen.model.Factura
import com.roshka.sifen.core.beans.DocumentoElectronico
import com.roshka.sifen.core.beans.response.RespuestaRecepcionDE
import com.roshka.tape.sifen.service.SifenService
import java.sql.Time
import com.roshka.sifen.core.fields.request.de.TgTimb
import com.roshka.sifen.core.types.TTiDE
import java.time.LocalDate
import com.roshka.sifen.core.fields.request.de.TdDatGralOpe
import java.time.LocalDateTime
import com.roshka.sifen.core.fields.request.de.TgEmis
import com.roshka.sifen.core.types.TiTipCont
import com.roshka.sifen.core.fields.request.de.TgOpeDE
import com.roshka.sifen.core.types.TTipEmi
import kotlin.random.Random
import com.roshka.sifen.core.fields.request.de.TgOpeCom
import com.roshka.sifen.core.types.TTipTra
import com.roshka.sifen.core.types.TTImp
import com.roshka.sifen.core.types.CMondT
import com.roshka.sifen.core.types.TdCondTiCam
import com.roshka.sifen.core.types.TiCondAnt
import com.roshka.sifen.core.types.TDepartamento
import com.roshka.sifen.core.fields.request.de.TgActEco
import kotlin.collections.mutableListOf
import com.roshka.sifen.core.fields.request.de.TgDatRec
import com.roshka.sifen.core.types.TiNatRec
import com.roshka.sifen.core.types.TiTiOpe
import com.roshka.sifen.core.types.PaisType
import com.roshka.sifen.core.types.TiTipDocRec
import com.roshka.sifen.core.fields.request.de.TgDtipDE
import com.roshka.sifen.core.fields.request.de.TgCamFE
import com.roshka.sifen.core.types.TiIndPres
import com.roshka.sifen.core.fields.request.de.TgCamCond
import com.roshka.sifen.core.types.TiCondOpe
import com.roshka.sifen.core.fields.request.de.TgCompPub
import com.roshka.sifen.core.fields.request.de.TgPaConEIni
import com.roshka.sifen.core.types.TiTiPago
import java.math.BigDecimal
import com.roshka.sifen.core.fields.request.de.TgCamItem
import com.roshka.sifen.core.types.TcUniMed
import com.roshka.sifen.core.types.TcRelMerc
import com.roshka.sifen.core.fields.request.de.TgValorItem
import com.roshka.sifen.core.fields.request.de.TgValorRestaItem
import com.roshka.sifen.core.fields.request.de.TgCamIVA
import com.roshka.sifen.core.types.TiAfecIVA
import com.roshka.sifen.core.fields.request.de.TgTotSub
import com.roshka.sifen.Sifen
import com.roshka.sifen.core.SifenConfig
import com.roshka.sifen.core.fields.request.de.TgPagTarCD
import com.roshka.sifen.core.types.TiDenTarj
import com.roshka.sifen.core.types.TiForProPa
import com.roshka.sifen.core.fields.request.de.TgPagCred
import com.roshka.sifen.core.types.TiCondCred
import com.roshka.sifen.core.fields.request.de.TgPagCheq
import com.roshka.sifen.core.fields.request.de.TgCuotas

@Service
class SifenService {

	val logger = LoggerFactory.getLogger("SifenService")

    @Scheduled
    fun checkPendingInvoices()
    {




    }

	fun sendInvoice(factura : Factura) : String  {
		logger.info("Recibimos factura electronica")
		val documentoElectronico = DocumentoElectronico()
		val timbrado = TgTimb()
		val emisor = TgEmis()
		val opeDe = TgOpeDE()
		val opeCom = TgOpeCom()
		val dataGeneralOperaciones = TdDatGralOpe()
		logger.info("Se genera codigo de seguridad")
		val codigoSeguridad = Random.nextInt(0,999999999)
		val dataRec = TgDatRec()
		val tipDe = TgDtipDE()
		val camFe = TgCamFE()
		val totSub = TgTotSub()
		val camCond = TgCamCond()
		// Actividades economicas del emisor
		logger.info("Se obtiene el listado de actividades economicas")
		var actEconomicaList = mutableListOf<TgActEco>()
		factura.actividadesEconomicas.forEach(){
			var actEconomica = TgActEco()
			actEconomica.setcActEco(it.actividadEconomica)
			actEconomica.setdDesActEco(it.descActividadEconomica)
			actEconomicaList.add(actEconomica)
		}
		emisor.setgActEcoList(actEconomicaList)
		// Tipo emisor. Posibles valores: 1 Normal, 2 Contingencia
		opeDe.setiTipEmi(TTipEmi.NORMAL)
		// Codigo de seguridad random, debio ser numerico pero el metodo setdCodSeg recibe string
		opeDe.setdCodSeg(codigoSeguridad.toString().padStart(9, '0'))
		if (Sifen.getSifenConfig().getAmbiente().toString() == "DEV") {
			emisor.setdNomEmi("DE generado en ambiente de prueba - sin valor comercial ni fiscal")
			emisor.setdDirEmi("DE generado en ambiente de prueba - sin valor comercial ni fiscal")
		} else {
			emisor.setdNomEmi(factura.nombreEmisor)
			emisor.setdDirEmi(factura.direccionEmisor)
		}
		emisor.setdRucEm(factura.rucEmisor)
		emisor.setdDVEmi(factura.dvEmisor)
		// Por ahora definimos para que sea en Asuncion,
		//TODO: Ver de conseguir la ciudad de la empresa que emite la factura
		emisor.setcDepEmi(TDepartamento.CAPITAL)
		emisor.setcDisEmi(factura.distritoEmisor)
		emisor.setdDesDisEmi(factura.descDistritoEmisor)
		emisor.setcCiuEmi(factura.ciudadEmisor)
		emisor.setdDesCiuEmi(factura.descCiudadEmisor)
		emisor.setdTelEmi(factura.telefonoEmisor)
		emisor.setdEmailE(factura.emailEmisor)
		
		// Para this.getgDatGralOpe().getgEmis()
		// Naturaleza del contribuyente. 1 persona fisica, 2 persona juridica
		emisor.setiTipCont(TiTipCont.getByVal(factura.tipoContribuyente))
		// Para getgDatGralOpe().getdFeEmiDE()
		dataGeneralOperaciones.setdFeEmiDE(factura.fecha)
		dataGeneralOperaciones.setgEmis(emisor)
		// dataRec
		// Naturaleza del contribuyente. 1 contribuyente, 2 no contribuyente
		dataRec.setiNatRec(TiNatRec.CONTRIBUYENTE)
		dataRec.setiTiOpe(TiTiOpe.B2B)
		dataRec.setcPaisRec(PaisType.PRY)
		// Tipo de contribuyente. 1 persona fisica, 2 persona juridica
		dataRec.setiTiContRec(TiTipCont.getByVal(factura.tipoContribuyenteReceptor))
		dataRec.setdRucRec(factura.rucReceptor)
		dataRec.setdDVRec(factura.dvReceptor)
		dataRec.setdNomRec(factura.nombreReceptor)
		dataRec.setdNomFanRec(factura.nombreFantasiaReceptor)
		dataRec.setcDisRec(factura.distritoReceptor)
		dataRec.setdDesDisRec(factura.descDistritoReceptor)
		dataRec.setcCiuRec(factura.ciudadReceptor)
		
		dataGeneralOperaciones.setgDatRec(dataRec)
		// hay que setear los valores a opeCom
		opeCom.setiTipTra(TTipTra.PRESTACION_SERVICIOS)
		opeCom.setiTImp(TTImp.IVA)
		opeCom.setcMoneOpe(CMondT.PYG)
		dataGeneralOperaciones.setgOpeCom(opeCom)
		
		timbrado.setiTiDE(TTiDE.FACTURA_ELECTRONICA)
		timbrado.setdNumTim(factura.timbrado)
		// Para this.getgTimb().getdEst()
		timbrado.setdEst(factura.establecimiento)
		// Para this.getgTimb().getdPunExp()
		timbrado.setdPunExp(factura.puntoExpedicion)
		// Para this.getgTimb().getdNumDoc()
		timbrado.setdNumDoc(factura.numero)
		timbrado.setdFeIniT(factura.fechaInicioTimbrado)
				
		camFe.setiIndPres(TiIndPres.getByVal(factura.indicadorPresencia.toShort()))
		camFe.setdDesIndPres(camFe.getiIndPres().getDescripcion())
		
		if (factura.fechaEmNR != null) {
			camFe.setdFecEmNR(factura.fechaEmNR)
		}
		
 		tipDe.setgCamFE(camFe)
		//  TgCamCond gCamCond
		val existMontoEntregaInicial = factura.pagoCredito?.find { it.montoEntregaInicial != null};
		camCond.setiCondOpe(TiCondOpe.getByVal(factura.condicionOperacion.toShort()))
		logger.info("Se evalua si es necesario armar el objeto pagConEIni")
		if (camCond.getiCondOpe() == TiCondOpe.CONTADO ||
		(factura.pagoCredito != null
		&& existMontoEntregaInicial != null )){
			var pagConEIniList = mutableListOf<TgPaConEIni>()
			factura.pagoContadoEntregaInicial?.forEach(){
				var pagConEIni = TgPaConEIni()
				pagConEIni.setiTiPago(TiTiPago.getByVal(it.tipoPago.toShort()))
				pagConEIni.setdMonTiPag(it.montoPago)
				pagConEIni.setcMoneTiPag(CMondT.getByName(it.monedaPago))
				// Solo debe asignarse cuando el tipo de moneda es distinto a PYG
				if (pagConEIni.getcMoneTiPag() != CMondT.getByName("PYG")){
					pagConEIni.setdTiCamTiPag(it.tipoCambio)
				}
				pagConEIniList.add(pagConEIni)
				if (pagConEIni.getiTiPago() == TiTiPago.TARJETA_DE_CREDITO ||
					pagConEIni.getiTiPago() == TiTiPago.TARJETA_DE_DEBITO) {
					var pagTarCD = TgPagTarCD()
					it.pagoTarjeta?.forEach(){
						pagTarCD.setiDenTarj(TiDenTarj.getByVal(it.denominacionTarjeta))
						pagTarCD.setiForProPa(TiForProPa.getByVal(it.formaProcesamientoPagoTarjeta))							
					}
					pagConEIni.setgPagTarCD(pagTarCD)
				} else if (pagConEIni.getiTiPago() == TiTiPago.CHEQUE){
					var pagCheque = TgPagCheq()
					it.pagoCheque?.forEach(){
						pagCheque.setdNumCheq(it.numeroCheque)
						pagCheque.setdBcoEmi(it.bancoEmisorCheque)
					}
					pagConEIni.setgPagCheq(pagCheque)
				}
			}
			camCond.setgPaConEIniList(pagConEIniList)
		} 

		if (camCond.getiCondOpe() == TiCondOpe.CREDITO) {
			var pagCred = TgPagCred()
			factura.pagoCredito?.forEach(){
				pagCred.setiCondCred(TiCondCred.getByVal(it.condicionCredito))
				if (pagCred.getiCondCred() == TiCondCred.PLAZO){
					pagCred.setdPlazoCre(it.plazoCredito)
				} else if (pagCred.getiCondCred() == TiCondCred.CUOTA){
					pagCred.setdCuotas(it.cantidadCuotas)
					var cuotaList = mutableListOf<TgCuotas>()
					it.cuotas?.forEach() {
						var cuota = TgCuotas()
						cuota.setcMoneCuo(CMondT.getByName(it.monedaCuota))
						cuota.setdMonCuota(it.montoCuota)
						cuota.setdVencCuo(it.vencimientoCuota)
						cuotaList.add(cuota)
					}
					pagCred.setgCuotasList(cuotaList)
				}
				if (it.montoEntregaInicial != null) {
					pagCred.setdMonEnt(it.montoEntregaInicial)
				}
			}
			camCond.setgPagCred(pagCred)
		}
		tipDe.setgCamCond(camCond)
		// Detalles de la factura
		logger.info("Se obtienen los detalles de la factura para cargar el objeto TgCamItem")
		var camItemList = mutableListOf<TgCamItem>()
		factura.itemsOperacion.forEach(){
			var camItem = TgCamItem()
			var valorItem = TgValorItem()
			var valorRestaItem = TgValorRestaItem()
			var camIva = TgCamIVA()
			camItem.setdCodInt(it.codigoInterno)
			camItem.setdDesProSer(it.descripcionProductoServicio)
			camItem.setcUniMed(TcUniMed.getByVal(it.unidadMedida))
			camItem.setdCantProSer(it.cantidadProductoServicio)
			camItem.setdInfItem(it.infoInteres)
			if (it.descuentoItem != null) {
				valorRestaItem.setdDescItem(it.descuentoItem)
			}
			valorItem.setgValorRestaItem(valorRestaItem)
			valorItem.setdPUniProSer(it.precioUnitario)
			camIva.setiAfecIVA(TiAfecIVA.getByVal(it.afectaIVA))
			camIva.setdPropIVA(it.proporcionIVA)
			camIva.setdTasaIVA(it.tasaIVA)
			camItem.setgCamIVA(camIva)
			camItem.setgValorItem(valorItem)
			camItemList.add(camItem)
		}
		tipDe.setgCamItemList(camItemList)
		documentoElectronico.setgTimb(timbrado)
		documentoElectronico.setgDatGralOpe(dataGeneralOperaciones)
		documentoElectronico.setdSisFact(1)
		documentoElectronico.setdFecFirma(LocalDateTime.now())
		documentoElectronico.setgOpeDE(opeDe)
		documentoElectronico.setgDtipDE(tipDe)
		documentoElectronico.setgTotSub(totSub)
		logger.info("Se invocara al metodo Sifen.recepcionLoteDE")
		val loteDe = mutableListOf<DocumentoElectronico>()
		loteDe.add(documentoElectronico)
		val ef = Sifen.recepcionLoteDE(loteDe)
		// El codigo resultado 0301 es devuelto por Sifen cuando no se recibe exitosamente el lote.
		if (ef.getdCodRes() == "0301"){
			logger.info("El lote no pudo ser procesado para envio. Motivo: "+ef.getdMsgRes())			
		}
		else {
			logger.info("Lote procesado exitosamente. Nro de lote obtenido: "+ ef.getdProtConsLote())			
		}
		return ef.getRespuestaBruta()
	}
	
	fun recepcionDocumentoElectronico (xml: String) : String {
		val de = DocumentoElectronico(xml)
		val rde = Sifen.recepcionDE(de)
		return rde.getRespuestaBruta()
	}
	
	fun recepcionLoteDocumentoElectronico (xml: String) : String {
		return xml
	}
	
}