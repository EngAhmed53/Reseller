package com.shouman.apps.reseller.admin.data.model

import androidx.room.*
import com.shouman.apps.reseller.admin.domain.DomainBranch
import com.shouman.apps.reseller.admin.domain.DomainBranchSalesmen
import com.shouman.apps.reseller.admin.domain.DomainSalesman


@Entity(tableName = "branches")
data class DatabaseBranch(
    @PrimaryKey(autoGenerate = false)
    val id: Long,

    @ColumnInfo(name = "branch_name")
    val branchName: String,

    val country: String,

    val city: String
)


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
data class DatabaseSalesman(
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
)


class DatabaseBranchSalesmen(
    @Embedded
    val databaseBranch: DatabaseBranch,
    @Relation(parentColumn = "id", entityColumn = "branch_id")
    val salesmenList: List<DatabaseSalesman>
)

data class MiniDatabaseBranch(val id:Long, val name:String)


fun List<DatabaseBranchSalesmen>.toDomainModels(): List<DomainBranchSalesmen> {
    return map { branchSalesmen ->
        DomainBranchSalesmen(
            DomainBranch(
                branchSalesmen.databaseBranch.branchName,
                branchSalesmen.databaseBranch.city,
                branchSalesmen.databaseBranch.country
            ),
            branchSalesmen.salesmenList.map { databaseSalesman ->
                DomainSalesman(
                    databaseSalesman.salesmanName,
                    databaseSalesman.salesmanPhoneNumber,
                    databaseSalesman.imgURL,
                    databaseSalesman.salesmanStatus
                )
            }
        )
    }
}







