package com.example.atendimento.controller


import com.example.atendimento.controller.request.CriarClienteRequest
import com.example.atendimento.model.Atendimento
import com.example.atendimento.model.Cliente
import com.example.atendimento.service.AtendimentoService
import org.springframework.web.bind.annotation.*
import java.time.Duration

@RestController
@RequestMapping("/atendimentos")
class AtendimentoController(private val atendimentoService: AtendimentoService) {

    @PostMapping("/cliente")
    fun criarCliente(@RequestBody criarClienteRequest: CriarClienteRequest): Cliente {
        return atendimentoService.criarCliente(criarClienteRequest)
    }

    @GetMapping("/fila")
    fun getFilaClientes(): List<Cliente> {
        return atendimentoService.getFilaDeAtendimentosRestantes()
    }

    @GetMapping("/fila/encerrados")
    fun getListaAtendimentosEncerrados(): List<Atendimento> {
        return atendimentoService.getFilaDeAtendimentosConcluidos()
    }

    @GetMapping("/tempo-espera")
    fun calcularTempoMedioEspera(): Duration {
        return atendimentoService.calcularTempoMedioDeAtendimento()
    }

    @GetMapping("/tempo-espera/{senha}")
    fun calcularTempoEspera(@PathVariable senha: String): Duration {
        return atendimentoService.calcularTempoEspera(senha)
    }

    @PutMapping("/iniciar")
    fun iniciarAtendimento(): Atendimento {
        return atendimentoService.iniciarAtendimento()
    }

    @PutMapping("/{id}/finalizar")
    fun finalizarAtendimento(@PathVariable id: Long): Atendimento {
        return atendimentoService.finalizarAtendimento(id)
    }
}
