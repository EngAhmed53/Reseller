package com.shouman.apps.reseller.admin.data.model


enum class UserType {
    NULL, COMPANY, SALESMAN
}


data class User(
    val id: Long,

    val firebaseUID: String,

    val email: String,

    var type: UserType = UserType.NULL,

    var company: Company?
) {

}


data class Company(
    val id: Long,

    var ownerName: String,

    var ownerPhoneNumber: String,

    var companyName: String,

    var imgURL: String?
)
