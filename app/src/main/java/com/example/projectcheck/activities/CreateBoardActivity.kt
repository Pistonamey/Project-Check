package com.example.projectcheck.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectcheck.R
import com.example.projectcheck.firebase.FirestoreClass
import com.example.projectcheck.models.Board
import com.example.projectcheck.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri?=null

    private lateinit var mUserName:String

    private var mBoardImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)


            setUpActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName= intent.getStringExtra(Constants.NAME)!!
        }

            iv_board_image.setOnClickListener {
                if(
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
            }

        btn_create.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

    }

    private fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        val board = Board(
            et_board_name.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this,board)

    }

    //to Firebase
    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageFileUri!=null){
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference.child("BOARD_IMAGE"+
                        System.currentTimeMillis()+"."
                        +Constants.getFileExtension(this,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot->
                Log.e("Board Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mBoardImageURL = uri.toString()


                    createBoard()


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

    fun boardCreatedSuccesfully(){
        Log.i("Done Second time","Done done done")
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }


    private fun setUpActionBar(){
        setSupportActionBar(toolbar_create_board_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title=resources.getString(R.string.board_name)
        }

        //open and close the drawer
        toolbar_create_board_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
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
        if(resultCode== Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data

            try{
                Glide
                .with(this)
                .load(Uri.parse(mSelectedImageFileUri.toString()))
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(iv_board_image)}
            catch(e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun boardCreatedSuccessfully() {

        hideProgressDialog()
        setResult(Activity.RESULT_OK)


        finish()
    }
}