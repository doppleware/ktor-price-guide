package com.example.plugins

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.application.*
import io.ktor.server.config.*
import pl.jutupe.ktor_rabbitmq.RabbitMQ

fun Application.configureRabbitMQ() {
    install(RabbitMQ) {

        val urlFromConfig = environment.config.tryGetString("rabbit.uri") ?: "amqp://guest:guest@localhost:5672"
        uri = urlFromConfig
        connectionName = "Connection name"

        //serialize and deserialize functions are required
        serialize { jacksonObjectMapper().writeValueAsBytes(it) }
        deserialize { bytes, type -> jacksonObjectMapper().readValue(bytes, type.javaObjectType) }

        //example initialization logic
        initialize {
            exchangeDeclare(/* exchange = */ "exchange", /* type = */ "direct", /* durable = */ true)
            queueDeclare(
                /* queue = */ "queue",
                /* durable = */true,
                /* exclusive = */false,
                /* autoDelete = */false,
                /* arguments = */emptyMap()
            )
            queueBind(/* queue = */ "queue", /* exchange = */ "exchange", /* routingKey = */ "routingKey")
        }
    }
}
