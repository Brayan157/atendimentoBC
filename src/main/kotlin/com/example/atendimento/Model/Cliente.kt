package com.example.atendimento.Model

import com.example.atendimento.Enum.TipoAtendimento

data class Cliente(
    val nome: String,
    val tipoAtendimento: TipoAtendimento,
    val senha: String
) {
    fun atendimentoConcluido(): Boolean {
        return false
    }
}