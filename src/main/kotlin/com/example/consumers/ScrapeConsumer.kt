package com.example.consumers

import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.div
import it.skrape.selects.html5.span

//fun Application.configureScapeConsumer( scrapeLogic: ScrapeLogic) {
//    rabbitConsumer {
//        consume<ScrapingTarget>("queue") { body ->
////            async {
////                val price = scrapeLogic.scrapePrice(body.url)
////
////            }
//                dbQuery{
//                    Website.new {
//                        name = "Ebay"
//                        url="https://www.ebay.com/itm/145309888550?hash=item21d524f026"
//                    }
//                }
//              val price = scrapeLogic.scrapePrice(body.url)
//
//        }
//    }
//}

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



