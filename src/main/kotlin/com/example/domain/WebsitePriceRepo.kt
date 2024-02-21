package com.example.domain

import com.example.dtos.ScrapeRequest
import com.example.plugins.PriceRecord
import com.example.plugins.Website
import com.example.plugins.Websites
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


    fun savePriceRecord(urlWebsite: Website, urlPrice: Int) {

        val currentMoment= Clock.System.now()
        val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
        transaction() {
            val website = PriceRecord.new {
                timeStamp = datetimeInUtc
                price =  urlPrice
                website = urlWebsite

            }

        }
    }

    fun createOrGetWebsite(scrapeRequest: ScrapeRequest): Website {

        return transaction() {
            var website = Website.find { Websites.url eq scrapeRequest.url }.firstOrNull()
            if (website==null) {
                website = Website.new {
                    name = scrapeRequest.name
                    url = scrapeRequest.url
                }
            }
            website
        }
    }

