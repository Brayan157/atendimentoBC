package com.example.atendimento.Service

import com.example.atendimento.Model.Atendimento
import com.example.atendimento.Model.Cliente
import com.example.atendimento.Enum.TipoAtendimento
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.atomic.AtomicLong

@Service
class AtendimentoService {
    private val clientes = mutableListOf<Cliente>()
    private val filaAtendimentos: Queue<Atendimento> = LinkedList()
    private val atendimentoIdCounter = AtomicLong(1)

    fun criarCliente(nome: String, tipoAtendimento: TipoAtendimento): Cliente {
        val senha = gerarSenhaUnica()
        val cliente = Cliente(nome = nome, tipoAtendimento = tipoAtendimento, senha = senha)
        clientes.add(cliente)
        return cliente
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

    fun criarFilaAtendimento(senhaCliente: String): Atendimento {
        val cliente = clientes.find { it.senha == senhaCliente } ?: throw IllegalArgumentException("Cliente não encontrado")
        val atendimento = Atendimento(id = atendimentoIdCounter.getAndIncrement(), cliente = cliente)
        filaAtendimentos.add(atendimento)
        return atendimento
    }

    fun iniciarAtendimento(atendimentoId: Long): Atendimento {
        val atendimento = filaAtendimentos.find { it.id == atendimentoId } ?: throw IllegalArgumentException("Atendimento não encontrado")
        val tempoEstimado = atendimento.cliente.tipoAtendimento.tempoEstimadoMinutos
        atendimento.horaAtendimento = LocalDateTime.now().plusMinutes(tempoEstimado)
        return atendimento
    }

    fun finalizarAtendimento(atendimentoId: Long): Atendimento {
        val atendimento = filaAtendimentos.find { it.id == atendimentoId } ?: throw IllegalArgumentException("Atendimento não encontrado")
        if (atendimento.horaAtendimento == null) {
            throw IllegalStateException("Atendimento ainda não iniciado")
        }
        atendimento.horaFim = LocalDateTime.now()
        filaAtendimentos.remove(atendimento) // Remove o atendimento da fila
        return atendimento
    }

    fun calcularTempoEspera(senhaCliente: String): Duration {
        val cliente = clientes.find { it.senha == senhaCliente } ?: throw IllegalArgumentException("Cliente não encontrado")
        val atendimento = filaAtendimentos.find { it.cliente == cliente && it.horaFim == null } ?: throw IllegalArgumentException("Atendimento não encontrado")
        val indice = filaAtendimentos.indexOf(atendimento)
        val atendimentosAnteriores = filaAtendimentos.take(indice)
        val tempoEspera = atendimentosAnteriores.filter { it.horaAtendimento == null }
            .map { it.cliente.tipoAtendimento.tempoEstimadoMinutos }
            .sum()
        return Duration.ofMinutes(tempoEspera)
    }
}