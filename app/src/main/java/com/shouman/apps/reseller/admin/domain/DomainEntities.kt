package com.shouman.apps.reseller.admin.domain

import com.shouman.apps.reseller.admin.data.model.SalesmanStatus

data class DomainBranch(

    val branchName: String,

    val country: String,

    val city: String
)


data class DomainSalesman(

    val salesmanName: String,

    val salesmanPhoneNumber: String,

    val imgURL: String?,

    val salesmanStatus: SalesmanStatus
)


class DomainBranchSalesmen(
    val databaseBranch: DomainBranch,

    val salesmenList: List<DomainSalesman>
)

data class DomainCustomer(

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

    val visitsSet: MutableSet<DomainVisit> = HashSet()
)

data class DomainVisit(

    val id: Long,

    val createTime: Long,

    val createdBy: String,

    val invoiceNum: Long?,

    val invoiceBalance: Int,

    val invoiceCash: Int
)

