package com.example.atendimento.model

import com.example.atendimento.enum.TipoAtendimento
import java.time.LocalDateTime

data class Cliente(
    val id: Long,
    val nome: String,
    val tipoAtendimento: TipoAtendimento,
    val senha: String,
    val horaChegada: LocalDateTime = LocalDateTime.now()
)
