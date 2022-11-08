package com.digitalinclined.edugate.constants

import androidx.lifecycle.MutableLiveData
import com.digitalinclined.edugate.models.FetchDataClass
import com.digitalinclined.edugate.models.UserFollowingProfile
import com.digitalinclined.edugate.restapi.models.banner.Banner
import com.digitalinclined.edugate.restapi.models.notes.Note

object Constants {

    // API
    const val BASE_URL = "http://64.227.161.183/"

    // PERMISSION
    const val STORAGE_REQUEST_CODE = 101

    // Splash Screen Constant
    var IS_SPLASH_SCREEN_FIRST_SHOWED = true

    // Back toolbar btn active
    var IS_BACK_TOOLBAR_BTN_ACTIVE = false

    // Temporary memory for containing create user info till successful created account
    var TEMP_CREATE_USER_NAME: String? = null
    var TEMP_CREATE_USER_EMAIL: String? = null

    // COURSE LIST AND YEAR LIST FOR STUDENT
    val COURSE_LIST = arrayListOf<String>()

    val YEAR_LIST = arrayListOf(
        "1st Year", "2nd Year", "3rd Year", "4th Year"
    )
    val SEMESTER_LIST = arrayListOf(
        "1", "2", "3", "4", "5", "6", "7", "8"
    )

    // Indian City CONSTANT DATA
    var INDIAN_CITY_DATA = arrayListOf<String>()

    // TEMPORARY USERS DATA FIREBASE
    var SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_USERS_DATA"
    var USER_NAME = "USER_NAME"
    var USER_EMAIL = "USER_EMAIL"
    var USER_PHONE = "USER_PHONE"
    var USER_COURSE = "USER_COURSE"
    var USER_SEMESTER = "USER_SEMESTER"
    var USER_YEAR = "USER_YEAR"
    var USER_CITY = "USER_CITY"
    var USER_PROFILE_PHOTO_LINK = "USER_PROFILE_PHOTO_LINK"

    // TEMP CONSTANT
    var BASE_64_STRING = ""

    // Constant TEMP ARRAYLIST for FOLLOWING USERS ID
    var FOLLOWING_USER_ID = MutableLiveData<ArrayList<String>>()

    // Constant TEMP list for BANNER images API
    var BANNER_IMAGES_LIST = arrayListOf<Banner>()
    var FETCHED_DATA_CLASS: FetchDataClass? = null

    // Constant TEMP list for NOTES images API
    var NOTES_TEMPORARY_LIST = arrayListOf<Note>()

    // ROOM CONSTANTS
    const val PDF_TABLE_NAME = "PDF_TABLE_NAME"
    const val PDF_DATABASE_NAME = "PDF_DATABASE_NAME"

}