package com.example.atendimento.enum

enum class TipoAtendimento(val ordemPrioridade: Long) {
    SIMPLES(3),
    MEDIO(2),
    COMPLEXO(1)
}
