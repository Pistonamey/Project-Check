package com.example.projectcheck.firebase

import android.app.Activity
import com.example.projectcheck.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import android.util.Log
import android.widget.Toast
import com.example.projectcheck.activities.*
import com.example.projectcheck.models.Board
import com.example.projectcheck.models.User
import com.google.firebase.firestore.ktx.toObject

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    //register user in Firestore database
    fun registerUser(activity: SignUpActivity, userInfo: com.example.projectcheck.models.User){
        mFireStore.collection(Constants.USERS)
                //merge the user INFO
            .document(getCurrentUserId()).
            set(userInfo, SetOptions.merge())
            .addOnSuccessListener { activity.userRegisteredSuccess() }



    }

    fun createBoard(activity: CreateBoardActivity,board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("Bad error","Board created successfully")

                Toast.makeText(activity,
                    "Board Created Successfully",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccesfully()
                Log.i("Inside","Inside")

            }.addOnFailureListener{
                exception->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    exception
                )
        }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()
            .addOnSuccessListener{
                document->
                Log.e("bad",document.documents.toString())
                val boardList:ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentID = i.id
                    boardList.add(board)
                }

                activity.populateBoardsListToUI(boardList)
            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error While Creating the Board",e)
            }
    }

    fun updateUserProfileData(activity:Activity,userHashMap:HashMap<String,Any>)
    {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile Data updated")
                Toast.makeText(activity,"Profile updated.",Toast.LENGTH_SHORT).show()
                when(activity){
                    is MainActivity ->{
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity->{
                        activity.profileUpdateSuccess()
                    }
                }

            }.addOnFailureListener {
                e->
                when(activity){
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName,"Error board.",e)
                Toast.makeText(activity,"Error when uploading the image",Toast.LENGTH_SHORT).show()
            }
    }

    fun getBoardDetails(activity: TaskListActivity,documentId:String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener{
                    document->
                Log.e("bad",document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentID = document.id

            //TODO get board details
                activity.boardDetails(board)

            }.addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error While Creating the Board",e)
            }
    }

    fun addUpdateTaskList(activity: Activity,board: Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK]=board.taskList

        mFireStore.collection(Constants.BOARDS).document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"TaskList updated successfully")
                if(activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                }
                else if(activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }
            }.addOnFailureListener {

                exception->
                if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                else if(activity is CardDetailsActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating the board,",exception)
            }
    }

    fun loadUserData(activity: Activity,readBoardsList:Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(com.example.projectcheck.models.User::class.java)

                when(activity){
                    is SignInActivity->{
                            activity.signInSuccess(loggedInUser!!)
                    }
                    is MainActivity->{
                        activity.updateNavigationUserDetails(loggedInUser!!,readBoardsList)
                    }
                    is MyProfileActivity->{
                        activity.setUserDataInUI(loggedInUser!!)
                    }
                }

            }.addOnFailureListener{
                e->when(activity){
                is SignInActivity->{
                    activity.hideProgressDialog()
                }
                is MainActivity->{
                    activity.hideProgressDialog()
                }
            }

            }
    }

    //get current User ID
    fun getCurrentUserId():String{
        var currentUser =  FirebaseAuth.getInstance().currentUser
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid
        }

        return currentUserID
    }


    fun getAssignedMembersListDetails(
        activity: Activity,
        assignedTo : ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener {
                document->
                Log.e(activity.javaClass.simpleName,document.documents.toString())

                val usersList:ArrayList<User> = ArrayList()
                for(i in document)
                {
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                if(activity is MembersActivity)
                    activity.setupMembersList(usersList)
                else if(activity is TaskListActivity)
                    activity.boardMembersDetailsList(usersList)



            }.addOnFailureListener { e ->

                if(activity is MembersActivity)
                    activity.hideProgressDialog()
                else if(activity is TaskListActivity)
                    activity.hideProgressDialog()


                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    fun getMemberDetails(activity:MembersActivity,email:String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document->
                if(document.documents.size>0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting user details",e)
            }
    }

    fun assignMemberToBoard(activity:MembersActivity,board: Board,user: User){
        val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.membersAssignSuccess(user)
            }
            .addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting user details",e)
            }


    }
}


