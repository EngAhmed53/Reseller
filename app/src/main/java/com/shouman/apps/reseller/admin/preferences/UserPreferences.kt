package com.shouman.apps.reseller.admin.preferences

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.shouman.apps.reseller.admin.api.ServerUser
import com.shouman.apps.reseller.admin.api.UserType

object UserPreferences {
    private const val USER_ID: String = "userId"

    private const val FIREBASE_UID: String = "firebase_uid"

    private const val EMAIL: String = "email"

    private const val USER_TYPE: String = "userType"

    private const val COMPANY_ID: String = "companyId"

    private const val USER_NAME: String = "userName"

    private const val PHONE_NUMBER: String = "phoneNumber"

    private const val COMPANY_NAME: String = "companyName"

    private const val IMAGE_URL: String = "imageURL"


    fun getUserId(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getLong(USER_ID, -1)

    fun getFirebaseUID(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
            FIREBASE_UID, null
        )

    fun getEmail(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
            EMAIL, null
        )

    fun getUserType(context: Context) =
        when (PreferenceManager.getDefaultSharedPreferences(context).getInt(USER_TYPE, 0)) {
            1 -> UserType.COMPANY
            2 -> UserType.SALESMAN
            else -> UserType.NULL
        }


    fun getCompanyId(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getLong(
            COMPANY_ID, -1
        )

    fun getUserName(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
            USER_NAME, null
        )


    fun getPhoneNumber(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
            PHONE_NUMBER, null
        )


    fun getCompanyName(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
            COMPANY_NAME, null
        )


    fun getImgURL(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getString(
            IMAGE_URL, null
        )

    fun setUserInfo(context: Context, user: ServerUser?) {
        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        user?.let { theUser ->

            //set the base user data to session
            preference.edit {
                putLong(USER_ID, theUser.id)
                putString(FIREBASE_UID, theUser.firebaseUID)
                putString(EMAIL, theUser.email)
                putInt(
                    USER_TYPE, when (theUser.type) {
                        UserType.COMPANY -> 1
                        else -> 0
                    }
                )

                // set the company data to session
                theUser.company?.let { theCompany ->
                    putLong(COMPANY_ID, theCompany.id)
                    putString(USER_NAME, theCompany.ownerName)
                    putString(PHONE_NUMBER, theCompany.ownerPhoneNumber)
                    putString(COMPANY_NAME, theCompany.companyName)
                    putString(IMAGE_URL, theCompany.imgURL)
                }
            }
        }
    }

}
