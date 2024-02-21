package com.example.domain

import io.opentelemetry.api.trace.Span
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.div
import it.skrape.selects.html5.span
import io.opentelemetry.instrumentation.annotations.WithSpan

@WithSpan
fun scrapeEbayPrice(urlToScrap: String): Int {

    var price = skrape(HttpFetcher){

        request {
            this.url = urlToScrap
        }

        response {
            htmlDocument {
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

    return ridiculouslySimplePriceExtractor(price)

}