package com.example.atendimento.builder

import com.example.atendimento.controller.request.CriarClienteRequest
import com.example.atendimento.enum.TipoAtendimento
import com.example.atendimento.model.Cliente

object ClienteBuilder {

    fun criarModel(
        id: Long = 1,
        nome: String = "Cliente Teste",
        tipoAtendimento: TipoAtendimento = TipoAtendimento.SIMPLES
    ): Cliente {
        return Cliente(id, nome, tipoAtendimento, "123456")
    }

    fun criarRequest(
        nome: String = "Cliente Teste",
        tipoAtendimento: TipoAtendimento = TipoAtendimento.SIMPLES
    ): CriarClienteRequest {
        return CriarClienteRequest(nome, tipoAtendimento)
    }
}
