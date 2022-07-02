package com.roshka.tape.sifen.controller

import com.roshka.tape.sifen.model.PingResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/status")
class StatusController {

    @GetMapping("/ping")
    fun ping(): PingResponse {
        return PingResponse("ok")
    }

}