package com.shouman.apps.reseller.admin.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.android.play.core.install.model.InstallErrorCode
import com.shouman.apps.reseller.admin.data.model.Branch
import com.shouman.apps.reseller.admin.data.model.BranchSalesmen
import com.shouman.apps.reseller.admin.data.model.Salesman

@Dao
interface ResellerDAO {

    /**
     *get all branches an its branch salesmen list.
     */
    @Transaction
    @Query("SELECT * FROM branches")
    fun getBranchesAndSalesmen():LiveData<List<BranchSalesmen>>

    /**
     *add new branch to database.
     */
    @Insert
    fun insertNewBranch(branch:Branch)

    /**
     *add a list of branches to database.
     */
    @Insert
    fun insertBranchesList(branchesList:List<Branch>)

    /**
     * delete a branch from database,
     * this only works if no salesmen linked with this branch.
     */
    @Delete
    fun deleteBranch(branch: Branch)

    /**
     * update branch information.
     */
    @Update
    fun updateBranch(branch: Branch)

    /**
     *add new salesman to database.
     */
    @Insert
    fun insertNewSalesmen(salesman: Salesman)

    /**
     *add a list of salesmen to database.
     */
    @Insert
    fun insertSalesmenList(salesmenList:List<Salesman>)

    /**
     * delete a salesman from database,
     * this only works if no customers linked with this salesman.
     */
    @Delete
    fun deleteSalesman(salesman: Salesman)

    /**
     * update salesman information.
     */
    @Update
    fun updateSalesman(salesman: Salesman)










}