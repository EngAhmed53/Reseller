package com.shouman.apps.reseller.admin.api

import com.shouman.apps.reseller.admin.data.model.DatabaseBranch
import com.shouman.apps.reseller.admin.data.model.DatabaseBranchSalesmen
import com.shouman.apps.reseller.admin.data.model.DatabaseSalesman
import com.shouman.apps.reseller.admin.data.model.SalesmanStatus


enum class ResponseCode {
    SUCCESS,
    FIREBASE_CODE_NOT_VALID,
    NEW_USER_NOT_VALID,
    NEW_USER_INFO_NOT_VALID
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

    var serverCompany: ServerCompany?
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