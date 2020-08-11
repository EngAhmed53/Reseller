package com.shouman.apps.reseller.admin.data.model

import androidx.room.*


@Entity(tableName = "branches")
class Branch(
    @PrimaryKey(autoGenerate = false)
    val id: Long,

    @ColumnInfo(name = "branch_name")
    val branchName: String,

    val country: String,

    val city: String
) : ListItem()


class BranchSalesmen(
    @Embedded
    val branch: Branch,
    @Relation(parentColumn = "id", entityColumn = "branch_id")
    val salesmenList: List<Salesman>
)