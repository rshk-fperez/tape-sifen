package com.roshka.tape.sifen.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SifenService {

    val logger : LoggerFactory


    @Scheduled
    fun checkPendingInvoices()
    {




    }

}