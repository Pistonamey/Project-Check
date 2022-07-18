package com.example.projectcheck.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectcheck.R
import com.example.projectcheck.firebase.FirestoreClass
import com.example.projectcheck.models.User
import com.example.projectcheck.utils.Constants
import com.example.projectcheck.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.example.projectcheck.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.example.projectcheck.utils.Constants.showImageChooser
import com.google.common.io.Files.getFileExtension
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_my_profile.iv_profile_user_image
import java.io.IOException

class MyProfileActivity : BaseActivity() {



    private var mSelectedImageFileUri: Uri?=null
    private lateinit var mUserDetails:User
    private var mProfileImageUrl:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setUpActionBar()
        FirestoreClass().loadUserData(this)

        //check permission for storage
        iv_profile_user_image.setOnClickListener {
            if(ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        btn_update.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))

                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,
                "Use Denied Permission for storage",
                Toast.LENGTH_LONG).show()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data

            try{Glide
                .with(this)
                .load(mSelectedImageFileUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(iv_profile_user_image)}
            catch(e: IOException){
                e.printStackTrace()
            }
        }
    }

    //setup the menu button
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title=resources.getString(R.string.my_profile)
        }

        //open and close the drawer
        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    //setuserdata in UI using Glide
    fun setUserDataInUI(user: User){

        mUserDetails = user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)

        et_name.setText(user.name)
        et_email.setText(user.email)
        if(user.mobile!=0L){
            et_mobile.setText(user.mobile.toString())
        }

    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()

        if(mProfileImageUrl.isNotEmpty() && mProfileImageUrl!=mUserDetails.image) {
            userHashMap["image"]
            userHashMap[Constants.IMAGE] = mProfileImageUrl
        }

        if(et_name.text.toString()!=mUserDetails.name){
            userHashMap[Constants.NAME]=et_name.text.toString()
        }

        if(et_email.text.toString()!=mUserDetails.email){
            userHashMap[Constants.EMAIL]=et_email.text.toString()
        }

        if(et_mobile.text.toString()!=mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=et_mobile.text.toString().toLong()
        }

        FirestoreClass().updateUserProfileData(this,userHashMap)
    }


    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        //structure for storing image.
        if(mSelectedImageFileUri!=null){
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference.child("USER_IMAGE"+
                    System.currentTimeMillis()+"."
                    +Constants.getFileExtension(this,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
                    Log.e("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageUrl = uri.toString()


                    updateUserProfileData()


                }

            }.addOnFailureListener{
                exception->Toast.makeText(
                this,
                exception.message,Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
        }
    }

    //function to get file extension for eg png mp3 etc


    fun profileUpdateSuccess(){
        hideProgressDialog()
        finish()
    }

}