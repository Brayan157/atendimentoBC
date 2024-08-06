package com.example.atendimento.Controller


import com.example.atendimento.Enum.TipoAtendimento
import com.example.atendimento.Model.Atendimento
import com.example.atendimento.Model.Cliente
import com.example.atendimento.Service.AtendimentoService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/atendimentos")
class AtendimentoController(private val atendimentoService: AtendimentoService) {

    @PostMapping("/clientes")
    fun criarCliente(@RequestParam nome: String, @RequestParam tipo: TipoAtendimento): Cliente {
        return atendimentoService.criarCliente(nome, tipo)
    }

    @PostMapping("/fila")
    fun criarFilaAtendimento(@RequestParam senhaCliente: String): Atendimento {
        return atendimentoService.criarFilaAtendimento(senhaCliente)
    }

    @PostMapping("/{id}/iniciar")
    fun iniciarAtendimento(@PathVariable id: Long): Atendimento {
        return atendimentoService.iniciarAtendimento(id)
    }

    @PostMapping("/{id}/finalizar")
    fun finalizarAtendimento(@PathVariable id: Long): Atendimento {
        return atendimentoService.finalizarAtendimento(id)
    }

    @GetMapping("/tempo-espera")
    fun calcularTempoEspera(@RequestParam senhaCliente: String): Long {
        val duration = atendimentoService.calcularTempoEspera(senhaCliente)
        return duration.toMinutes()
    }
}