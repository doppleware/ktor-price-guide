package com.example.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureData() {

//     val database = Database.connect(
//        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
//        user = "root",
//        driver = "org.h2.Driver",
//        password = ""
//    )

    val env = environment.config.propertyOrNull("ktor.environment")?.getString() ?: "dev"
    val url = environment.config.propertyOrNull("ktor.database.$env.url")?.getString() ?: ""
    val user = environment.config.propertyOrNull("ktor.database.$env.user")?.getString() ?: "webprice"
    val password = environment.config.propertyOrNull("ktor.database.$env.password")?.getString() ?: "webprice"
    val driver = environment.config.propertyOrNull("ktor.database.$env.driver")?.getString() ?: "org.postgresql.Driver"

    val database = Database.connect(
        url = url,
        user = user,
        driver = driver,
        password = password

    )

    transaction(database) {
        SchemaUtils.create(Websites)
        SchemaUtils.create(PriceRecords)
    }

}
