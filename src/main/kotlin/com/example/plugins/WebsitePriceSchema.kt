package com.example.plugins

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Websites : IntIdTable() {
        val name = varchar("name", 50)
        val url = varchar("url", 100)
}
class Website(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Website>(Websites)
    var name by Websites.name
    var url by Websites.url
    val price_records by PriceRecord referrersOn PriceRecords.website
}
object PriceRecords : IntIdTable() {
    val timeStamp = datetime("timestamp")
    val price = integer("price")
    val website = reference("website", Websites)
}

class PriceRecord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PriceRecord>(PriceRecords)
    var timeStamp by PriceRecords.timeStamp
    var price by PriceRecords.price
    var website by Website referencedOn PriceRecords.website

}

