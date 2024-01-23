package com.example

import com.example.dtos.ScrapeRequest
import com.example.plugins.PriceRecords
import com.example.plugins.Websites
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.vendors.PostgreSQLDialect
import org.junit.Before
import org.junit.Rule
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test


class WebPriceScapingTests {

    @JvmField
    @Rule
    var rabbit = RabbitMQContainer(DockerImageName.parse("rabbitmq:3.7.25-management-alpine"));

    @JvmField
    @Rule
    var postgres = PostgreSQLContainer(DockerImageName.parse("postgres:16.1"));

    var rabbitUri =""
    var postgresUser=""
    var postgresPwd=""
    var postgresUrl=""

    @Before
    fun setUp() {
        val address: String = rabbit.getHost()
        val port: Int = rabbit.getFirstMappedPort()
        rabbitUri = "amqp://guest:guest@$address:$port"

        postgresUrl="jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/test"

        //postgresUrl="jdbc:tc:postgresql:10.16:///test"
        postgresPwd=postgres.getPassword()
        postgresUser=postgres.getUsername()

//        Database.registerJdbcDriver("jdbc:tc:postgresql","org.testcontainers.jdbc.ContainerDatabaseDriver",
//            PostgreSQLDialect.dialectName)

    }
    @Test
    fun shoudlScrapeProductPriceFromUrl() = testApplication {

        environment {

            config = MapApplicationConfig( "rabbit.uri" to rabbitUri,
                "ktor.environment" to "test", "ktor.database.test.url" to postgresUrl ,
                "ktor.database.test.user" to postgresUser, "ktor.database.test.password" to postgresPwd,
                "ktor.database.test.driver" to "org.testcontainers.jdbc.ContainerDatabaseDriver")

        }
        application {
            module()
        }

        var url = "https://www.ebay.com/itm/145309888550?hash=item21d524f026";

        //RedoConnection()

        client.post("/scrape") {

            contentType(ContentType.Application.Json)
            setBody("{ \"url\" : \"$url\", \"name\": \"Ebay\"}")

        }.apply {
            assert(this.status== HttpStatusCode.OK)
        }

    }

    private fun RedoConnection() {

        Database.connect(
            url = postgresUrl,
            user = postgresUser,
            driver = "org.testcontainers.jdbc.ContainerDatabaseDriver",
            password = postgresPwd

        )
        transaction {
            SchemaUtils.create(Websites)
            SchemaUtils.create(PriceRecords)
        }

    }
}