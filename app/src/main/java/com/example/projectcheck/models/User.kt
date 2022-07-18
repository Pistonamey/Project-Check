package com.example.projectcheck.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.*

data class User(
    val id: String="",
    val name: String = "",
    val email:String="",
    val image:String="",//Store images in storage functionality of Firebase
    val mobile:Long = 0,
    val fcmToken:String ="",
    var selected: Boolean = false
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,

    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {

        writeString(p0!!,p1,id,true)
        writeString(p0,p1,name,true)
        writeString(p0,p1,email,true)
        writeString(p0,p1,image,true)
        writeLong(p0,p1,mobile)
        writeString(p0,p1,fcmToken,true)


    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}