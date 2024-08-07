package com.example.atendimento.service

import com.example.atendimento.controller.request.CriarClienteRequest
import com.example.atendimento.enum.TipoAtendimento
import com.example.atendimento.model.Atendimento
import com.example.atendimento.model.Cliente
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Duration.ZERO
import java.time.Duration.between
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

@Service
class AtendimentoService {
    private val clientes = mutableListOf<Cliente>()
    private val listaAtendimentos = mutableListOf<Atendimento>()
    private val atendimentoIdCounter = AtomicLong(1)
    private val clienteIdCounter = AtomicLong(1)

    fun criarCliente(criarClienteRequest: CriarClienteRequest): Cliente {
        val senha = gerarSenhaUnica(criarClienteRequest.tipoAtendimento)
        val cliente = Cliente(
            id = clienteIdCounter.getAndIncrement(),
            nome = criarClienteRequest.nome,
            tipoAtendimento = criarClienteRequest.tipoAtendimento,
            senha = senha
        )

        clientes.add(cliente)

        clientes.sortBy { it.tipoAtendimento.ordemPrioridade }

        return cliente
    }

    fun getFilaDeAtendimentosRestantes(): List<Cliente> {
        return clientes
    }

    fun getFilaDeAtendimentosConcluidos(): List<Atendimento> {
        return listaAtendimentos.filter { it.horaFim != null }
    }

    fun iniciarAtendimento(): Atendimento {
        val atendimento = Atendimento(
            id = atendimentoIdCounter.getAndIncrement(),
            cliente = clientes.first(),
            horaInicio = LocalDateTime.now()
        )

        listaAtendimentos.add(atendimento)
        clientes.remove(atendimento.cliente)

        return atendimento
    }

    fun finalizarAtendimento(atendimentoId: Long): Atendimento {
        val atendimento = listaAtendimentos.find { it.id == atendimentoId }
            ?: throw IllegalArgumentException("Atendimento não encontrado")

        if (atendimento.horaInicio == null) {
            throw IllegalStateException("Atendimento ainda não iniciado")
        }

        atendimento.horaFim = LocalDateTime.now()

        return atendimento
    }

    fun calcularTempoMedioDeAtendimento(): Duration {
        val duracoes = listaAtendimentos
            .filter { it.horaFim != null }
            .map { between(it.horaInicio, it.horaFim) }

        return if (duracoes.isEmpty()) {
            ZERO
        } else {
            val soma = duracoes.reduce { acc, duration -> acc + duration }

            soma.dividedBy(duracoes.size.toLong())
        }
    }

    fun calcularTempoEspera(senhaCliente: String): Duration {
        val cliente = clientes.find { it.senha == senhaCliente }
            ?: throw IllegalArgumentException("Cliente não encontrado")

        val posicao = clientes.indexOf(cliente) + 1

        return calcularTempoMedioDeAtendimento().multipliedBy(posicao.toLong())
    }

    private fun gerarSenha(tipoAtendimento: TipoAtendimento): String {
        val sufixo = when (tipoAtendimento) {
            TipoAtendimento.COMPLEXO -> "PRF"
            TipoAtendimento.MEDIO -> "MED"
            TipoAtendimento.SIMPLES -> "SIM"
        }

        val numeros = (0..999).shuffled().first().toString().padStart(3, '0')
        return sufixo + numeros
    }

    private fun gerarSenhaUnica(tipoAtendimento: TipoAtendimento): String {
        var senha: String
        do {
            senha = gerarSenha(tipoAtendimento)
        } while (clientes.any { it.senha == senha })

        return senha
    }
}
