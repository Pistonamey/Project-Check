package com.example.projectcheck.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.projectcheck.R
import com.example.projectcheck.dialogs.LabelColorListDialog
import com.example.projectcheck.dialogs.MembersListDialog
import com.example.projectcheck.firebase.FirestoreClass
import com.example.projectcheck.models.Board
import com.example.projectcheck.models.Card
import com.example.projectcheck.models.Task
import com.example.projectcheck.models.User
import com.example.projectcheck.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition=-1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList: ArrayList<User>
    private var mSelectedDueDateMilliSeconds:Long=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()
        setUpActionBar()

        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if(et_name_card_details.text.toString().isNotEmpty())
                updateCardDetails()
            else{
                Toast.makeText(this@CardDetailsActivity,"Enter a card name",Toast.LENGTH_SHORT).show()
            }
        }

        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }

        //tv_select_members.setOnClickListener { membersListDialog() }

        mSelectedDueDateMilliSeconds = mBoardDetails.taskList[mTaskListPosition]
            .cards[mCardPosition].dueDate

        if(mSelectedDueDateMilliSeconds>0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            tv_select_due_date.text=selectedDate
        }

        tv_select_due_date.setOnClickListener {
            showDataPicker()
        }

    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_card_details_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        //open and close the drawer
        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun colorList():ArrayList<String>{
        val colorsList:ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor(){
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card->{
                deleteCard()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getIntentData(){

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails= intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition=intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }

    }

    private fun membersListDialog(){
        var cardAssignedMembersList=mBoardDetails
            .taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        if(cardAssignedMembersList.size>0){
            for(i in mMembersDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailList[i].id==j){
                        mMembersDetailList[i].selected= true
                    }
                }
            }
        }else{
            for(i in mMembersDetailList.indices){
                mMembersDetailList[i].selected=false
            }
        }

        val listDialog = object:MembersListDialog(this,mMembersDetailList
        ,resources.getString(R.string.str_select_member)

        ){
            override fun onItemSelected(user: User, action: String) {
                //TODO("Not yet implemented")
            }

        }
        listDialog.show()
    }

    private fun updateCardDetails(){
        val card = Card(et_name_card_details.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,mSelectedDueDateMilliSeconds)
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)



    }

    private fun deleteCard(){
        val cardslist:ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards

        cardslist.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardslist
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    private fun labelColorsListDialog(){
        val colorList:ArrayList<String> = colorList()
        val listDialog = object: LabelColorListDialog(
            this,colorList,resources.getString(R.string.str_select_label_color),mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()

    }

    private fun showDataPicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this,
        DatePickerDialog.OnDateSetListener{
            view,year,monthOfYear,dayOfMonth->
            val sDayOfMonth = if (dayOfMonth<10) "0$dayOfMonth" else "$dayOfMonth"
            val sMonthOfYear = if((monthOfYear+1)<10) "0${monthOfYear+1}" else "${monthOfYear+1}"

            val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
            tv_select_due_date.text=selectedDate

            val sdf = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            val theDate = sdf.parse(selectedDate)
            mSelectedDueDateMilliSeconds = theDate!!.time

        },year,month,day
        )
        dpd.show()
    }
}