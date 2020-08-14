package com.shouman.apps.reseller.admin.utils

import java.util.regex.Pattern


class Validator {
    companion object {
        fun isEmailValid(email: String?): Boolean {

            val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$"

            val pat: Pattern = Pattern.compile(emailRegex)
            return if (email == null) false else pat.matcher(email.trim()).matches()
        }

        fun isPasswordValid(password: String?): Boolean {
            return if (password == null || password.isEmpty()) false
            else password.length >= 6 //6 chars length
        }

        fun isCompanyNameValid(companyName: String?): Boolean {
            return !(companyName == null || companyName.trim()
                .isEmpty() || companyName.trim().length < 3)
        }

        fun isOwnerNameValid(ownerName: String?): Boolean {
            ownerName?.let {
                if (it.length < 5) return false
                return it.trim().all { char ->
                    char.isLetter() || char == ' '
                }
            }
            return false
        }

        fun isPhoneValid(phoneNumber: String?): Boolean {
            return !(phoneNumber == null || phoneNumber.trim()
                .isEmpty() || phoneNumber.trim().length < 3)

        }

        fun isBranchNameValid(branchName: String?): Boolean {
            return !(branchName == null || branchName.trim().isEmpty())

        }

        fun isSelectedBranchValid(position: Long): Boolean {
            return (position != -1L)

        }
    }
}