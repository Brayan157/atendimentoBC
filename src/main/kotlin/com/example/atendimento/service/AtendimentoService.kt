package com.example.atendimento.service

import com.example.atendimento.controller.request.CriarClienteRequest
import com.example.atendimento.model.Atendimento
import com.example.atendimento.model.Cliente
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.atomic.AtomicLong

@Service
class AtendimentoService {
    private val clientes = mutableListOf<Cliente>()
    private val filaAtendimentos = mutableListOf<Atendimento>()
    private val atendimentoIdCounter = AtomicLong(1)
    private val clienteIdCounter = AtomicLong(1)

    fun criarCliente(criarClienteRequest: CriarClienteRequest): Cliente {
        val senha = gerarSenhaUnica()
        val cliente = Cliente(
            id = clienteIdCounter.getAndIncrement(),
            nome = criarClienteRequest.nome,
            tipoAtendimento = criarClienteRequest.tipoAtendimento,
            senha = senha
        )
        clientes.add(cliente)
        return cliente
    }

    fun getFilaDeAtendimentosRestantes(): List<Atendimento> {
        return filaAtendimentos.filter {
            it.horaFim == null
        }
    }

    fun getFilaDeAtendimentosConcluidos(): List<Atendimento> {
        return filaAtendimentos.filter {
            it.horaFim != null
        }
    }

    fun iniciarAtendimento(clienteId: Long): Atendimento {
        val atendimento = Atendimento(
            id = atendimentoIdCounter.getAndIncrement(),
            cliente = clientes.first { it.id == clienteId },
            horaInicio = LocalDateTime.now()
        )

        filaAtendimentos.add(atendimento)

        return atendimento
    }

    fun finalizarAtendimento(atendimentoId: Long): Atendimento {
        val atendimento = filaAtendimentos.find { it.id == atendimentoId }
            ?: throw IllegalArgumentException("Atendimento não encontrado")

        if (atendimento.horaInicio == null) {
            throw IllegalStateException("Atendimento ainda não iniciado")
        }

        atendimento.horaFim = LocalDateTime.now()

        return atendimento
    }

    fun calcularTempoMedioDeAtendimento(): Duration {
        val atendimentosConcluidos = filaAtendimentos.filter { it.horaFim != null }

        if (atendimentosConcluidos.isEmpty()) {
            throw IllegalStateException("Nenhum atendimento concluído")
        }

        val duracoes = atendimentosConcluidos.map {
            Duration.between(it.horaInicio, it.horaFim)
        }

        return duracoes.reduce { acc, duration -> acc.plus(duration) }.dividedBy(duracoes.size.toLong())
    }

    fun calcularTempoEspera(senhaCliente: String): Duration {
        val cliente = clientes.find { it.senha == senhaCliente }
            ?: throw IllegalArgumentException("Cliente não encontrado")

        val atendimentosRestantes = getFilaDeAtendimentosRestantes()

        val posicao = atendimentosRestantes.indexOfFirst { it.cliente == cliente }

        if (posicao == -1) {
            throw IllegalArgumentException("Cliente não está na fila de atendimento")
        }

        val duracoes = atendimentosRestantes.subList(0, posicao).map {
            Duration.between(it.horaInicio, LocalDateTime.now())
        }

        return duracoes.reduceOrNull { acc, duration -> acc.plus(duration) } ?: Duration.ZERO
    }

    private fun gerarSenha(): String {
        val letras = ('A'..'Z').toList().shuffled().take(2).joinToString("")
        val numeros = (0..999).shuffled().first().toString().padStart(3, '0')
        return letras + numeros
    }

    private fun gerarSenhaUnica(): String {
        var senha: String
        do {
            senha = gerarSenha()
        } while (clientes.any { it.senha == senha && !it.atendimentoConcluido() })
        return senha
    }
}