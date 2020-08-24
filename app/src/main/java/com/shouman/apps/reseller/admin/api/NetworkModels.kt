package com.shouman.apps.reseller.admin.api

import com.shouman.apps.reseller.admin.data.model.DatabaseBranch
import com.shouman.apps.reseller.admin.data.model.DatabaseBranchSalesmen
import com.shouman.apps.reseller.admin.data.model.DatabaseSalesman
import com.shouman.apps.reseller.admin.data.model.SalesmanStatus


enum class ResponseCode {
    SUCCESS,
    FIREBASE_CODE_NOT_VALID,
    COMPANY_ID_NOT_VALID,
    BRANCH_ID_NOT_VALID,
    NEW_USER_NOT_VALID,
    NEW_USER_INFO_NOT_VALID,
    CUSTOMER_ID_NOT_VALID
}

data class ServerResponse<T>(val responseCode: ResponseCode, val body: T?)


enum class UserType {
    NULL, COMPANY, SALESMAN
}

data class ServerUser(
    val id: Long,

    val firebaseUID: String,

    val email: String,

    var type: UserType = UserType.NULL,

    var company: ServerCompany?
)

data class ServerCompany(
    val id: Long,

    var ownerName: String,

    var ownerPhoneNumber: String,

    var companyName: String,

    var imgURL: String?
)

data class ServerBranch(
    val id: Long,

    val branchName: String,

    val country: String,

    val city: String,

    val salesmenSet: MutableSet<ServerSalesman>
)

data class ServerSalesman(
    val id: Long,

    val salesmanName: String,

    val salesmanPhoneNumber: String,

    val imgURL: String?,

    val salesmanStatus: SalesmanStatus
)

data class PageableCustomer(
    val id: Long,

    val belongToSalesman: String,

    val customerName: String,

    val companyName: String
)

fun Set<ServerBranch>.toDatabaseModels(): List<DatabaseBranchSalesmen> {
    return map { serverBranch ->

        DatabaseBranchSalesmen(
            DatabaseBranch(
                serverBranch.id,
                serverBranch.branchName,
                serverBranch.country,
                serverBranch.city
            ), serverBranch.salesmenSet.map { serverSalesman ->
                DatabaseSalesman(
                    serverBranch.id,
                    serverSalesman.salesmanName,
                    serverSalesman.salesmanPhoneNumber,
                    serverSalesman.imgURL,
                    serverSalesman.salesmanStatus,
                    serverBranch.id
                )
            }
        )
    }
}

fun ServerBranch.toDatabaseBranch(): DatabaseBranch {
    return DatabaseBranch(
        id,
        branchName,
        country,
        city
    )
}


data class ServerCustomer(

    val id: Long,

    val createTime: Long,

    val createdBy: String,

    val customerName: String,

    val superVisor: String,

    val businessName: String,

    val phoneNum: String,

    val email: String?,

    val latitude: Double,

    val longitude: Double,

    val visit: ServerVisit
)

data class ServerVisit(

    val id: Long,

    val createTime: Long,

    val createdBy: String,

    val invoiceNum: Long?,

    val price: Int,

    val payment: Int
)

data class CustomerProfile(
    val id: Long,

    val createTime: Long,

    val createdBy: String,

    val branchName: String,

    val salesmanName: String?,

    val customerName: String,

    val superVisor: String,

    val businessName: String,

    val phoneNum: String,

    val email: String?,

    val latitude: Double,

    val longitude: Double,

    val totalVisits: Int,

    val invoicesTotal:Long,

    val payment: Long,

    val debt: Long
)

enum class ActivityType {
    NEW_VISIT, NEW_CUSTOMER
}

data class PageableActivity(
    val id: Long,

    val type: ActivityType,

    val createdTime: Long,

    val createdBy: String?,

    val createdById: Long?,

    val createdByImgUrl: String?,

    val visitId: Long,

    val customerId: Long,

    val customerName: String
)

data class CompanyDateSummary(
    val totalNewCustomer: Int,

    val totalVisits: Int,

    val totalIncome: Long,

    val totalSales: Long
)



















