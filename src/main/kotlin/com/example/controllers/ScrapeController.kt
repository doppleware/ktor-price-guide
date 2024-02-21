package com.example.controllers

import com.example.consumers.ScrapeLogic
import com.example.domain.createOrGetWebsite
import com.example.domain.ridiculouslySimplePriceFormatter
import com.example.domain.savePriceRecord
import com.example.domain.scrapeEbayPrice
import com.example.dtos.PriceRecordDto
import com.example.dtos.ScrapeRequest
import com.example.helpers.dbQuery
import com.example.plugins.PriceRecord
import com.example.plugins.PriceRecords
import com.example.plugins.Website
import com.example.plugins.Websites
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SortOrder.DESC
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.transactions.transaction
import pl.jutupe.ktor_rabbitmq.publish

//import io.ktor.client.*
//import io.ktor.client.request.*

fun Application.configurePriceController(scrapeLogic: ScrapeLogic) {


    routing {
        // Create city
        get("/latest") {

            val currentMoment= Clock.System.now()
            val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)

            var response = listOf<PriceRecordDto>();

            transaction{

                val records = PriceRecord.find { PriceRecords.timeStamp less datetimeInUtc}
                    .orderBy(PriceRecords.timeStamp to DESC).limit(100)
                response=records.map { PriceRecordDto(ridiculouslySimplePriceFormatter(it.price),it.website.url) }
            }
            call.respond(response)
        }

        get("/oldest") {

            val currentMoment= Clock.System.now()
            val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)

            var response = listOf<PriceRecordDto>();

            transaction{

                val records = PriceRecord.find { PriceRecords.timeStamp less datetimeInUtc}
                    .orderBy(PriceRecords.timeStamp to SortOrder.ASC).limit(100)
                response=records.map { PriceRecordDto(ridiculouslySimplePriceFormatter(it.price),it.website.url) }
            }
            call.respond(response)
        }


        get("/prices") {

            var response = listOf<PriceRecordDto>();
            val url = call.request.queryParameters["url"].toString()

            transaction{

                val website = Website.find{Websites.url eq url}
                response = website.first().price_records.map { PriceRecordDto(ridiculouslySimplePriceFormatter(it.price),it.website.url)}

            }

            call.respond( response)

        }

        get("/website_max_price") {

            var response = ""
            val name = call.request.queryParameters["name"].toString()

            transaction{

                val website = Website.find{Websites.name eq name}.first()
                val max_price = website.price_records.maxBy { it.price  }.price
                response = " { \"max_price\" : \"${ridiculouslySimplePriceFormatter(max_price)}\" }"

            }

            call.respond( response)

        }

        get("/website_max_price") {

            var response = ""
            val name = call.request.queryParameters["name"].toString()

            transaction{

                val website = Website.find{Websites.name eq name}.first()
                val max_price = website.price_records.maxBy { it.price  }.price
                response = " { \"max_price\" : \"${ridiculouslySimplePriceFormatter(max_price)}\" }"

            }

            call.respond( response)

        }

        get("/website_prices") {

            var response = listOf<PriceRecordDto>()
            val name = call.request.queryParameters["name"].toString()

            transaction{

                val website = Website.find{Websites.name eq name}.first()
                response = website.price_records.map { PriceRecordDto(ridiculouslySimplePriceFormatter(it.price),it.website.url)}

            }

            call.respond( response)

        }

        post("/scrape"){
//            var scrapeRequest =call.receive<ScrapeRequest>();
//
//            val urlWebsite = createOrGetWebsite(scrapeRequest)
//
//            var price = scrapeEbayPrice(scrapeRequest.url)
//            savePriceRecord(urlWebsite, price)
//
//
//
            var scrapeRequest =call.receive<ScrapeRequest>();
            call.publish("exchange", "routingKey",null, scrapeRequest)

            call.respond(HttpStatusCode.OK)


        }


    }
}

