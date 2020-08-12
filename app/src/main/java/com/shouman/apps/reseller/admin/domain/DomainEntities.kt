package com.shouman.apps.reseller.admin.domain

import androidx.room.Embedded
import androidx.room.Relation
import com.shouman.apps.reseller.admin.data.model.DatabaseBranch
import com.shouman.apps.reseller.admin.data.model.DatabaseSalesman
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

