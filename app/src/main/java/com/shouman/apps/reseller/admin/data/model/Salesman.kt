package com.shouman.apps.reseller.admin.data.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

enum class SalesmanStatus {
    ENABLED, DISABLED
}

class SalesmanStatusConverter {

    @TypeConverter
    fun fromStatusToString(salesmanStatus: SalesmanStatus): String {
        return salesmanStatus.toString()
    }

    @TypeConverter
    fun fromStringToSalesmanStatus(str: String): SalesmanStatus {
        return when (str) {
            "ENABLED" -> SalesmanStatus.ENABLED
            else -> SalesmanStatus.DISABLED
        }
    }
}

@Entity(tableName = "salesmen")
class Salesman(
    @PrimaryKey(autoGenerate = false)
    val id: Long,

    @ColumnInfo(name = "name")
    val salesmanName: String,

    @ColumnInfo(name = "phone_number")
    val salesmanPhoneNumber: String,

    @ColumnInfo(name = "img_url")
    var imgURL: String?,

    @ColumnInfo(name = "status")
    val salesmanStatus: SalesmanStatus,

    val branch_id: Long
): ListItem()