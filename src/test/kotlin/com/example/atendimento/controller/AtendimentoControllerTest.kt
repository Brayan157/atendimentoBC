package com.example.atendimento.controller

import com.example.atendimento.builder.ClienteBuilder
import com.example.atendimento.model.Cliente
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class AtendimentoControllerTest(
    @Autowired private val testRestTemplate: TestRestTemplate
) {

    @Test
    fun `deve criar um cliente`() {
        val request = ClienteBuilder.criarRequest()

        val response = testRestTemplate.postForEntity(
            "$BASE_URL/cliente",
            request,
            Cliente::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.nome).isEqualTo(request.nome)
        assertThat(response.body!!.tipoAtendimento).isEqualTo(request.tipoAtendimento)
    }

    companion object {
        const val BASE_URL = "/atendimentos"
    }
}