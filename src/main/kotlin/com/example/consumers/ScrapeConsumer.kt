package com.example.consumers

import com.example.domain.createOrGetWebsite
import com.example.domain.savePriceRecord
import com.example.domain.scrapeEbayPrice
import com.example.dtos.ScrapeRequest
import com.example.dtos.ScrapingTarget
import com.example.plugins.Website
import io.ktor.server.application.*
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.div
import it.skrape.selects.html5.span
import pl.jutupe.ktor_rabbitmq.consume
import pl.jutupe.ktor_rabbitmq.rabbitConsumer

fun Application.configureScapeConsumer(scrapeLogic: ScrapeLogic) {
    rabbitConsumer {
        consume<ScrapeRequest>("queue") { scrapeRequest ->

            val website = createOrGetWebsite(scrapeRequest)
            val price = scrapeEbayPrice(scrapeRequest.url)
            savePriceRecord(website, price)

        }
    }
}

interface ScrapeLogic{
      fun scrapePrice(scrapeUrl: String): String

}
class ScrapeLogicImpl : ScrapeLogic{
//    override suspend fun scrapePrice(scrapeUrl: String): String =
//        withSpan("scrape website for price")
//    { span ->
//
//        val price = skrape(HttpFetcher) {
//            request {
//                this.url = scrapeUrl
//            }
//            response {
//                htmlDocument {
//                    // all official html and html5 elements are supported by the DSL
//                    div {
//                        withClass = "x-price-primary"
//                        findFirst {
//                            span {
//                                withClass = "ux-textspans"
//                                findFirst {
//                                    text
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        span.setAttribute("price",price)
//        price
//    }

    @WithSpan
    override  fun scrapePrice(scrapeUrl: String): String {

        val price = skrape(HttpFetcher) {
            request {
                this.url = scrapeUrl
            }
            response {
                htmlDocument {
                    // all official html and html5 elements are supported by the DSL
                    div {
                        withClass = "x-price-primary"
                        findFirst {
                            span {
                                withClass = "ux-textspans"
                                findFirst {
                                    text
                                }
                            }
                        }
                    }
                }
            }
        }
        Span.current().setAttribute("price",price)
        return price

    }

}



