package com.example.projectcheck.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.projectcheck.R
import com.example.projectcheck.databinding.ActivitySignUpBinding
import com.example.projectcheck.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : BaseActivity() {


    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }


        binding.toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }
        btn_sign_up.setOnClickListener { registerUser() }

    }

    //Trim the user input and pass to the Firebase to register the user
    fun registerUser(){
        val name:String=et_name.text.toString().trim{it<=' '}
        val email:String=et_email.text.toString().trim{it<=' '}
        val password:String=et_password.text.toString().trim{it<=' '}

        if(validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener({
                    task->
                    if(task.isSuccessful){
                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email
                        val user = com.example.projectcheck.models.User(firebaseUser.uid,name,email)
                        FirestoreClass().registerUser(this,user)
                    }else{
                        Toast.makeText(this,
                        task.exception!!.message,Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }

    //check if user input is empty
    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please Enter a Name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please Enter an Email Adress")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please Enter a Password")
                false
            }else->{
                return true
            }
        }
    }

    //firebase registeration successfull
    fun userRegisteredSuccess() {
        Toast.makeText(this,
            "You Have Successfully Registered with us.",
            Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

}