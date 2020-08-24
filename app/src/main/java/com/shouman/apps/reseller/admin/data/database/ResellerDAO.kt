package com.shouman.apps.reseller.admin.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shouman.apps.reseller.admin.data.model.DatabaseBranch
import com.shouman.apps.reseller.admin.data.model.DatabaseBranchSalesmen
import com.shouman.apps.reseller.admin.data.model.DatabaseCustomer
import com.shouman.apps.reseller.admin.data.model.DatabaseSalesman

@Dao
interface ResellerDAO {

    /**
     *get all branches an its branch salesmen list.
     */
    @Transaction
    @Query("SELECT * FROM branches")
    fun getBranchesAndSalesmen(): LiveData<List<DatabaseBranchSalesmen>>

    @Query("SELECT * FROM branches")
    fun getBranches(): LiveData<List<DatabaseBranch>>

    /**
     *add new branch to database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewBranch(databaseBranch: DatabaseBranch)

    /**
     *add a list of branches to database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBranchesList(vararg databaseBranches: DatabaseBranch)

    /**
     * delete a branch from database,
     * this only works if no salesmen linked with this branch.
     */
    @Delete
    fun deleteBranch(databaseBranch: DatabaseBranch)

    /**
     * update branch information.
     */
    @Update
    fun updateBranch(databaseBranch: DatabaseBranch)

    /**
     *add new salesman to database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewSalesmen(salesman: DatabaseSalesman)

    /**
     *add a list of salesmen to database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSalesmenList(vararg salesmenList: DatabaseSalesman)

    /**
     * delete a salesman from database,
     * this only works if no customers linked with this salesman.
     */
    @Delete
    fun deleteSalesman(salesman: DatabaseSalesman)

    /**
     * update salesman information.
     */
    @Update
    fun updateSalesman(salesman: DatabaseSalesman)


    /**
     * get all the customers list, this list is used when we add a visit
     * for existing customer to use its name, business name, latitude, longitude
     */
    @Query("SELECT * FROM customers")
    fun getAllCustomerList(): LiveData<List<DatabaseCustomer>>

    /**
     * add the returned customer object returned from the server to database
     */
    @Insert
    fun addCustomerToCustomersList(customer:DatabaseCustomer)

    /**
     *update all the customers list in the database,
     * this update to ensure tha all the saved customers
     * data [name, businessName, latitude, longitude]
     * is up to date
     */

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAllCustomerList(vararg customerList: DatabaseCustomer)
}