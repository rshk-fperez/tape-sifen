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

@Service
class SifenService {

    protected lateinit var logger : LoggerFactory


    @Scheduled
    fun checkPendingInvoices()
    {




    }

	fun sendInvoice(factura : Factura) : String  {
		val documentoElectronico = DocumentoElectronico()
		val timbrado = TgTimb()
		val emisor = TgEmis()
		val opeDe = TgOpeDE()
		val opeCom = TgOpeCom()
		val dataGeneralOperaciones = TdDatGralOpe()
		val codigoSeguridad = Random.nextInt(0,999999999)
		val dataRec = TgDatRec()
		val tipDe = TgDtipDE()
		val camFe = TgCamFE()
		val totSub = TgTotSub()
		val camCond = TgCamCond()
		// Actividades economicas del emisor
		var actEconomica = TgActEco()
		var actEconomicaList = mutableListOf(actEconomica)
		factura.actividadesEconomicas.forEach(){
			var actEconomica = TgActEco()
			actEconomica.setcActEco(it.actividadEconomica)
			actEconomica.setdDesActEco(it.descActividadEconomica)
			actEconomicaList.add(actEconomica)
		}
		actEconomicaList.removeAt(0)
		// Tipo emisor. Posibles valores: Normal y Contingencia
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
		emisor.setiTipCont(TiTipCont.PERSONA_JURIDICA)
		emisor.setgActEcoList(actEconomicaList)
		// Para getgDatGralOpe().getdFeEmiDE()
		dataGeneralOperaciones.setdFeEmiDE(factura.fecha)
		dataGeneralOperaciones.setgEmis(emisor)
		// dataRec
		dataRec.setiNatRec(TiNatRec.CONTRIBUYENTE)
		dataRec.setiTiOpe(TiTiOpe.B2B)
		dataRec.setcPaisRec(PaisType.PYF)
		dataRec.setiTiContRec(TiTipCont.PERSONA_JURIDICA)
		dataRec.setdRucRec(factura.rucReceptor)
		dataRec.setdDVRec(factura.dvReceptor)
		dataRec.setdNomRec(factura.nombreReceptor)
		dataRec.setdNomFanRec(factura.nombreFantasiaReceptor)
		dataRec.setcDisRec(factura.distritoReceptor)
		dataRec.setdDesDisRec(factura.descDistritoReceptor)
		dataRec.setcCiuRec(factura.ciudadReceptor)
		
		dataGeneralOperaciones.setgDatRec(dataRec)
		// hay que setear los valores a opeCom
		opeCom.setiTipTra(TTipTra.DONACION)
		opeCom.setiTImp(TTImp.IVA)
		opeCom.setcMoneOpe(CMondT.PYG)
		opeCom.setdCondTiCam(TdCondTiCam.GLOBAL)
		opeCom.setiCondAnt(TiCondAnt.ANTICIPO_GLOBAL)
		dataGeneralOperaciones.setgOpeCom(opeCom)
		
		timbrado.setiTiDE(TTiDE.FACTURA_ELECTRONICA)
		timbrado.setdNumTim(factura.timbrado)
		// Para this.getgTimb().getdEst()
		timbrado.setdEst(factura.establecimiento)
		// Para this.getgTimb().getdPunExp()
		timbrado.setdPunExp(factura.puntoExpedicion)
		// Para this.getgTimb().getdNumDoc()
		timbrado.setdNumDoc(factura.numero)
		timbrado.setdSerieNum("AA")
		timbrado.setdFeIniT(factura.fechaInicioTimbrado)
				
		camFe.setiIndPres(TiIndPres.getByVal(factura.indicadorPresencia.toShort()))
		camFe.setdDesIndPres(camFe.getiIndPres().getDescripcion())
		
		camFe.setdFecEmNR(factura.fechaEmNR)
		
 		tipDe.setgCamFE(camFe)
		//  TgCamCond gCamCond
		
		var pagConEIni = TgPaConEIni()
		var pagConEIniList = mutableListOf(pagConEIni)
		factura.pagoContadoEntregaInicial.forEach(){
			var pagConEIni = TgPaConEIni()
			pagConEIni.setiTiPago(TiTiPago.getByVal(it.tipoPago.toShort()))
			pagConEIni.setdMonTiPag(it.montoPago)
			pagConEIni.setcMoneTiPag(CMondT.getByName(it.monedaPago))
			pagConEIni.setdTiCamTiPag(it.tipoCambio)
			pagConEIniList.add(pagConEIni)
		}
		pagConEIniList.removeAt(0)
		camCond.setgPaConEIniList(pagConEIniList)
		camCond.setiCondOpe(TiCondOpe.getByVal(factura.condicionOperacion.toShort()))

		tipDe.setgCamCond(camCond)
		// Detalles de la factura
		var camItem = TgCamItem()
		var camItemList = mutableListOf(camItem)
		factura.itemsOperacion.forEach(){
			var camItem = TgCamItem()
			var valorItem = TgValorItem()
			var valorRestaItem = TgValorRestaItem()
			var camIva = TgCamIVA()
			camItem.setdCodInt(it.codigoInterno)
			camItem.setdParAranc(it.partidaArancelaria.toShort())
			camItem.setdNCM(it.nCM)
			camItem.setdDncpG(it.codigoDNCPG)
			camItem.setdDncpE(it.codigoDNCPE)
			camItem.setdDesProSer(it.descripcionProductoServicio)
			camItem.setcUniMed(TcUniMed.getByVal(it.unidadMedida))
			camItem.setdCantProSer(it.cantidadProductoServicio)
			camItem.setcPaisOrig(PaisType.getByName(it.codigoPaisOrigen))
			camItem.setdInfItem(it.infoInteres)
			valorRestaItem.setdDescItem(it.descuentoItem)
			valorItem.setgValorRestaItem(valorRestaItem)
			valorItem.setdPUniProSer(it.precioUnitario)
			camIva.setiAfecIVA(TiAfecIVA.getByVal(it.afectaIVA))
			camIva.setdPropIVA(it.proporcionIVA)
			camIva.setdTasaIVA(it.tasaIVA)
			camItem.setgCamIVA(camIva)
			camItem.setgValorItem(valorItem)
			camItemList.add(camItem)
		}
		camItemList.removeAt(0)
		tipDe.setgCamItemList(camItemList)
		documentoElectronico.setgTimb(timbrado)
		documentoElectronico.setgDatGralOpe(dataGeneralOperaciones)
		documentoElectronico.setdSisFact(1)
		documentoElectronico.setdFecFirma(LocalDateTime.now())
		documentoElectronico.setgOpeDE(opeDe)
		documentoElectronico.setgDtipDE(tipDe)
		documentoElectronico.setgTotSub(totSub)
		
		val ef = Sifen.recepcionDE(documentoElectronico)
		return ef.getRespuestaBruta()
	}
}