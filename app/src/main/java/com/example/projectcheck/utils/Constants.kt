package com.example.projectcheck.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.projectcheck.activities.MyProfileActivity


object Constants {
    const val USERS:String = "Users"

    const val IMAGE:String="image"
    const val NAME:String="name"
    const val MOBILE:String="mobile"
    const val EMAIL:String="email"
    const val BOARDS:String = "boards"
    const val ASSIGNED_TO:String="assignedTo"
    const val DOCUMENT_ID:String="documentId"
    const val TASK:String = "taskList"
    const val BOARD_DETAIL:String="board_detail"
    const val ID:String = "id"
    const val TASK_LIST_ITEM_POSITION:String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION:String = "card_list_item_position"
    const val BOARD_MEMBERS_LIST:String = "board_members_list"
    const val SELECT:String ="Select"
    const val UN_SELECT:String = "UnSelect"
    const val PROJECTCHECK_PREFERENCES = "ProjectCheckPrefs"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAA-_vvGNI:APA91bF9xfSzbacs2j9RKkmEg7aYqY4pmRr89vYoy8pOfr0Ds2yHyVlhkhDiryrPndNYbXHUYyCdzZakvrlxxDLjsyQv5Ybtom5dFr7VWaMzDOL6YcSF-09GAOoxHU7SAisyZ222PW3w"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"



    fun showImageChooser(activity: Activity){
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity:Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }


    const val READ_STORAGE_PERMISSION_CODE=1
    const val PICK_IMAGE_REQUEST_CODE=2

}