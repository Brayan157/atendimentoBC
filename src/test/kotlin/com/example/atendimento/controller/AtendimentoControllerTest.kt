package com.example.atendimento.controller

import com.example.atendimento.builder.ClienteBuilder
import com.example.atendimento.enum.TipoAtendimento
import com.example.atendimento.model.Atendimento
import com.example.atendimento.model.Cliente
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus
import java.time.Duration

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class AtendimentoControllerTest(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    @Test
    fun `Deve testar o fluxo geral da aplicacao`() {
        val request = ClienteBuilder.criarRequest()
        val request2 = ClienteBuilder.criarRequest(
            nome = "Cliente Complexo", tipoAtendimento = TipoAtendimento.COMPLEXO
        )
        val request3 = ClienteBuilder.criarRequest(
            nome = "Cliente Medio",
            tipoAtendimento = TipoAtendimento.MEDIO
        )

        val response = testRestTemplate.postForEntity(
            "$BASE_URL/cliente",
            request,
            Cliente::class.java
        )

        val response2 = testRestTemplate.postForEntity(
            "$BASE_URL/cliente",
            request2,
            Cliente::class.java
        )

        val response3 = testRestTemplate.postForEntity(
            "$BASE_URL/cliente",
            request3,
            Cliente::class.java
        )

        // Verifica se o cliente foi criado corretamente
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.nome).isEqualTo(request.nome)
        assertThat(response.body!!.tipoAtendimento).isEqualTo(request.tipoAtendimento)

        // Verifica se o cliente foi criado corretamente
        assertThat(response2.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response2.body!!.nome).isEqualTo(request2.nome)
        assertThat(response2.body!!.tipoAtendimento).isEqualTo(request2.tipoAtendimento)

        // Verifica se o cliente foi criado corretamente
        assertThat(response3.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response3.body!!.nome).isEqualTo(request3.nome)
        assertThat(response3.body!!.tipoAtendimento).isEqualTo(request3.tipoAtendimento)

        val responseFila = testRestTemplate.getForEntity(
            "$BASE_URL/fila",
            Array<Cliente>::class.java
        )

        // Verifica se a fila foi criada e ordenada corretamente
        assertThat(responseFila.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseFila.body!!.size).isEqualTo(3)
        assertThat(responseFila.body!![0].nome).isEqualTo(request2.nome)
        assertThat(responseFila.body!![1].nome).isEqualTo(request3.nome)
        assertThat(responseFila.body!![2].nome).isEqualTo(request.nome)

        val inicioAtendimento = testRestTemplate.exchange<Atendimento>(
            "$BASE_URL/iniciar",
            method = PUT
        )

        // Verifica se o atendimento foi iniciado com o cliente correto
        assertThat(inicioAtendimento.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(inicioAtendimento.body!!.cliente.nome).isEqualTo(request2.nome)

        val filaAtualizada = testRestTemplate.getForEntity(
            "$BASE_URL/fila",
            Array<Cliente>::class.java
        )

        // Verifica se a fila foi atualizada e o cliente que estava em atendimento foi removido
        assertThat(filaAtualizada.body!!.size).isEqualTo(2)
        assertThat(filaAtualizada.body!![0].nome).isEqualTo(request3.nome)

        val tempoEspera = testRestTemplate.getForEntity(
            "$BASE_URL/tempo-espera",
            Duration::class.java
        )

        // Verifica se o tempo de espera foi calculado corretamente
        assertThat(tempoEspera.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(tempoEspera.body!!.toMinutes()).isEqualTo(0)

        Thread.sleep(3000)

        val encerrarAtendimento = testRestTemplate.exchange<Atendimento>(
            "$BASE_URL/${inicioAtendimento.body!!.id}/finalizar",
            method = PUT
        )

        // Verifica se o atendimento foi encerrado corretamente
        assertThat(encerrarAtendimento.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(encerrarAtendimento.body!!.cliente.nome).isEqualTo(request2.nome)

        val filaEncerrados = testRestTemplate.getForEntity(
            "$BASE_URL/fila/encerrados",
            Array<Atendimento>::class.java
        )

        // Verifica se o atendimento foi adicionado a lista de atendimentos encerrados
        assertThat(filaEncerrados.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(filaEncerrados.body!!.size).isEqualTo(1)
        assertThat(filaEncerrados.body!![0].cliente.nome).isEqualTo(request2.nome)

        val tempoEspera2 = testRestTemplate.getForEntity(
            "$BASE_URL/tempo-espera",
            Duration::class.java
        )

        // Verifica se o tempo de espera foi calculado corretamente
        assertThat(tempoEspera2.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(tempoEspera2.body!!.toSeconds()).isEqualTo(3)

        val calculaTempoEspera = testRestTemplate.getForEntity(
            "$BASE_URL/tempo-espera/${response.body!!.senha}",
            Duration::class.java
        )

        // Verifica se o tempo de espera foi calculado corretamente
        assertThat(calculaTempoEspera.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(calculaTempoEspera.body!!.toSeconds()).isEqualTo(6)

        val inicioAtendimento2 = testRestTemplate.exchange<Atendimento>(
            "$BASE_URL/iniciar",
            method = PUT
        )

        // Verifica se o atendimento foi iniciado com o cliente correto
        assertThat(inicioAtendimento2.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(inicioAtendimento2.body!!.cliente.nome).isEqualTo(request3.nome)

        val filaAtualizada2 = testRestTemplate.getForEntity(
            "$BASE_URL/fila",
            Array<Cliente>::class.java
        )

        // Verifica se a fila foi atualizada e o cliente que estava em atendimento foi removido
        assertThat(filaAtualizada2.body!!.size).isEqualTo(1)
        assertThat(filaAtualizada2.body!![0].nome).isEqualTo(request.nome)

        Thread.sleep(5000)

        val encerrarAtendimento2 = testRestTemplate.exchange<Atendimento>(
            "$BASE_URL/${inicioAtendimento2.body!!.id}/finalizar",
            method = PUT
        )

        // Verifica se o atendimento foi encerrado corretamente
        assertThat(encerrarAtendimento2.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(encerrarAtendimento2.body!!.cliente.nome).isEqualTo(request3.nome)

        val filaEncerrados2 = testRestTemplate.getForEntity(
            "$BASE_URL/fila/encerrados",
            Array<Atendimento>::class.java
        )

        // Verifica se o atendimento foi adicionado a lista de atendimentos encerrados
        assertThat(filaEncerrados2.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(filaEncerrados2.body!!.size).isEqualTo(2)
        assertThat(filaEncerrados2.body!![0].cliente.nome).isEqualTo(request2.nome)

        val tempoEspera3 = testRestTemplate.getForEntity(
            "$BASE_URL/tempo-espera",
            Duration::class.java
        )

        // Verifica se o tempo de espera foi calculado corretamente
        assertThat(tempoEspera3.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(tempoEspera3.body!!.toSeconds()).isEqualTo(4)

        val encerrarAtendimento3 = testRestTemplate.exchange<Atendimento>(
            "$BASE_URL/${inicioAtendimento2.body!!.id}/finalizar",
            method = PUT
        )

        // Verifica se o atendimento foi encerrado corretamente
        assertThat(encerrarAtendimento3.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(encerrarAtendimento3.body!!.cliente.nome).isEqualTo(request3.nome)

        val calculaTempoEspera2 = testRestTemplate.getForEntity(
            "$BASE_URL/tempo-espera/${response.body!!.senha}",
            Duration::class.java
        )

        // Verifica se o tempo de espera foi calculado corretamente
        assertThat(calculaTempoEspera2.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(calculaTempoEspera2.body!!.toSeconds()).isEqualTo(tempoEspera3.body!!.toSeconds())
    }

    companion object {
        const val BASE_URL = "/atendimentos"
    }
}
