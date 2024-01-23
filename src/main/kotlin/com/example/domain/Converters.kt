package com.example.domain

import io.opentelemetry.instrumentation.annotations.WithSpan

@WithSpan
fun ridiculouslySimplePriceExtractor(price: String): Int {

    var result = price.filter { it.isDigit() }
    return result.toInt();
}

@WithSpan
fun ridiculouslySimplePriceFormatter(price: Int): String {

    var result = price.toString();
    if (price > 5){
        result += " (expensive!)"
    }

    if (price <= 5){
        result += " (go for it)"
    }

    return result;
}