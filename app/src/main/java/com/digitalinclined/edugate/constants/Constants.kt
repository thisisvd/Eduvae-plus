package com.digitalinclined.edugate.constants

import androidx.lifecycle.MutableLiveData
import com.digitalinclined.edugate.models.ColorComboRecyclerClass
import com.digitalinclined.edugate.models.quizzesmodel.QuizSubmissionDataClass

object Constants {

    // APIs
    const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    const val BASE_YOUTUBE_API_KEY = "AIzaSyCdn27lKADWOvbq8-nclKDnKphucdUKmls"

    // admin
    var ADMIN_USER_NAME = "Admin"

    // Splash Screen Constant
    var IS_SPLASH_SCREEN_FIRST_SHOWED = true

    // Back toolbar btn active
    var IS_BACK_TOOLBAR_BTN_ACTIVE = false

    // Temporary memory for containing create user info till successful created account
    var TEMP_CREATE_USER_NAME: String? = null
    var TEMP_CREATE_USER_EMAIL: String? = null

    // COURSE LIST AND YEAR LIST FOR STUDENT
    val COURSE_LIST = arrayListOf("BTECH","BCA","MBA","BCOM")

    val YEAR_LIST = arrayListOf(
        "1st Year", "2nd Year", "3rd Year", "4th Year"
    )
    val SEMESTER_LIST = arrayListOf(
        "1", "2", "3", "4", "5", "6", "7", "8"
    )

    // Indian City CONSTANT DATA
    var INDIAN_CITY_DATA = arrayListOf<String>()

    // TEMPORARY USERS DATA FIREBASE (Shared preferences)
    var SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_USERS_DATA"
    var USER_NAME = "USER_NAME"
    var USER_EMAIL = "USER_EMAIL"
    var USER_PHONE = "USER_PHONE"
    var USER_COURSE = "USER_COURSE"
    var USER_SEMESTER = "USER_SEMESTER"
    var USER_YEAR = "USER_YEAR"
    var USER_CITY = "USER_CITY"
    var USER_PROFILE_PHOTO_LINK = "USER_PROFILE_PHOTO_LINK"

    // Constant TEMP ARRAYLIST for FOLLOWING USERS ID
    var FOLLOWING_USER_ID = MutableLiveData<ArrayList<String>>()
    var USER_CURRENT_COURSE = ""

    // constant - join classroom list
    var JOINED_CLASSROOM_LIST = ArrayList<String>()

    // ROOM CONSTANTS
    const val PDF_TABLE_NAME = "PDF_TABLE_NAME"
    const val PDF_DATABASE_NAME = "PDF_DATABASE_NAME"

    // submit marks of quiz
    val quizSubmissionObserver = MutableLiveData<QuizSubmissionDataClass>()

    // Constant color of maps
    val mapOfColors = mapOf(
        0 to ColorComboRecyclerClass("#FEF8E2","#FFDC5C"),
        1 to ColorComboRecyclerClass("#E7FAE9","#27FF3D"),
        2 to ColorComboRecyclerClass("#EEF9FF","#49BEFD"),
        3 to ColorComboRecyclerClass("#FDEBF9","#E529BC")
    )
}