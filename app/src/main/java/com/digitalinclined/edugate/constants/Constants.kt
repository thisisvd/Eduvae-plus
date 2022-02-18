package com.digitalinclined.edugate.constants

object Constants {

    // Splash Screen Constant
    var IS_SPLASH_SCREEN_FIRST_SHOWED = true

    // Back toolbar btn active
    var IS_BACK_TOOLBAR_BTN_ACTIVE = false

    // Temporary memory for containing create user info till successful created account
    var TEMP_CREATE_USER_NAME: String? = null
    var TEMP_CREATE_USER_EMAIL: String? = null

    // Indian City CONSTANT DATA
    var INDIAN_CITY_DATA = arrayListOf<String>()

    // TEMPORARY USERS DATA FIREBASE
    var SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_USERS_DATA"
    var USER_NAME = "USER_NAME"
    var USER_EMAIL = "USER_EMAIL"
    var USER_PHONE = "USER_PHONE"
    var USER_COURSE = "USER_COURSE"
    var USER_YEAR = "USER_YEAR"
    var USER_CITY = "USER_CITY"
    var USER_PROFILE_PHOTO_LINK = "USER_PROFILE_PHOTO_LINK"

}