package com.example.projectcheck.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeStringList

data class Board(
    val name:String="",
    val image:String="",
    val createdBy:String="",
    val assignedTo:ArrayList<String> = ArrayList(),
    var documentID:String="",
var taskList:ArrayList<Task> = ArrayList()

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!
    ){}


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) = with(p0) {
        p0!!.writeString(name)
        p0!!.writeString(createdBy)
        p0!!.writeString(image)
        p0.writeStringList(assignedTo)
        p0.writeString(documentID)
        p0.writeTypedList(taskList)
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
