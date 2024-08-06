package com.example.atendimento.Enum

enum class TipoAtendimento(val tempoEstimadoMinutos: Long) {
    SIMPLES(10),
    COMPLEXO(30),
    MEDIO(20)
}