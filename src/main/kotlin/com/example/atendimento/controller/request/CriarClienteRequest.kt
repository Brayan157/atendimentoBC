package com.example.atendimento.controller.request

import com.example.atendimento.enum.TipoAtendimento

data class CriarClienteRequest(
    val nome: String,
    val tipoAtendimento: TipoAtendimento
)