package com.shouman.apps.reseller.admin.repository

import com.shouman.apps.reseller.admin.data.database.ResellerDatabase
import com.shouman.apps.reseller.admin.data.model.DatabaseCustomer

class CustomersRepo(private val database: ResellerDatabase) {


    val customersList = database.resellerDAO.getAllCustomerList()


    suspend fun updateAllCustomerList(vararg customersList: DatabaseCustomer) {

        database.resellerDAO.updateAllCustomerList(*customersList)

    }

    suspend fun addNewCustomer(customer:DatabaseCustomer) {

        database.resellerDAO.addCustomerToCustomersList(customer)

    }

}