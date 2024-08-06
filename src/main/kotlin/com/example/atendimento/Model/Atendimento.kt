package com.example.atendimento.Model

import java.time.LocalDateTime

data class Atendimento(
    val id: Long,
    val cliente: Cliente,
    val horaChegada: LocalDateTime = LocalDateTime.now(),
    var horaAtendimento: LocalDateTime? = null,
    var horaFim: LocalDateTime? = null
)