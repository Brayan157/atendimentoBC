package com.example.atendimento.model

import java.time.LocalDateTime

data class Atendimento(
    val id: Long,
    val cliente: Cliente,
    var horaInicio: LocalDateTime? = null,
    var horaFim: LocalDateTime? = null
)
