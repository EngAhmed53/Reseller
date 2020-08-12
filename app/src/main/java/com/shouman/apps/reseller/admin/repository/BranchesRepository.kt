package com.shouman.apps.reseller.admin.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.shouman.apps.reseller.admin.api.NetworkCall
import com.shouman.apps.reseller.admin.api.toDatabaseModels
import com.shouman.apps.reseller.admin.data.database.ResellerDatabase
import com.shouman.apps.reseller.admin.data.model.toDomainModels
import com.shouman.apps.reseller.admin.domain.DomainBranchSalesmen
import com.shouman.apps.reseller.admin.preferences.UserPreferences


class BranchesRepository(private val database: ResellerDatabase) {

    /**
     * get the branches list from database and transform it to domain models
     */
    val branchesSalesmenList: LiveData<List<DomainBranchSalesmen>> =
        Transformations.map(database.resellerDAO.getBranchesAndSalesmen()) {
            it.toDomainModels()
        }

    suspend fun refreshBranchesSalesmenList(context: Application): Boolean {
        val newData = NetworkCall
            .branchesServices
            .getAllBranchesAsync(
                UserPreferences
                    .getCompanyId(context)
            )
            .await()
            .toDatabaseModels()

        newData.forEach {
            database.resellerDAO.insertNewBranch(it.databaseBranch)
            database.resellerDAO.insertSalesmenList(*it.salesmenList.toTypedArray())
        }

        return !newData.isNullOrEmpty()
    }

    //suspend fun addNewBranchToCompany(bra)
}