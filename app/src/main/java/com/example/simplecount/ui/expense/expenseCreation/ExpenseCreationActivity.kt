package com.example.simplecount.ui.expense.expenseCreation

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecount.R
import com.example.simplecount.data.db.EventDatabase
import com.example.simplecount.data.db.ExpenseDatabase
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.data.db.entity.Expense
import com.example.simplecount.data.db.entity.User
import com.example.simplecount.databinding.ActivityExpenseCreationBinding
import com.example.simplecount.ui.event.eventCreation.EventCreationViewModel
import com.example.simplecount.ui.event.eventCreation.EventCreationViewModelFactory
import com.example.simplecount.ui.event.eventDetail.EventDetailActivity
import com.example.simplecount.ui.expense.expenseDetail.ExpenseDetailActivity
import com.example.simplecount.util.Coroutines
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.NumberFormatException
import kotlin.math.abs
import kotlin.math.round

class ExpenseCreationActivity : AppCompatActivity() {

    lateinit var binding : ActivityExpenseCreationBinding
    lateinit var expenseCreationViewModel: ExpenseCreationViewModel
    var initialAmount = 0.0
    var payerId = 0
    var id : Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expense_creation,  null)

        id = intent.extras?.getLong("eventId") as Long
        val eventDao = EventDatabase.geInstance(this).eventDao
        val expenseDao = ExpenseDatabase.geInstance(this).expenseDao
        val expenseCreationViewModelFactory = ExpenseCreationViewModelFactory(id, eventDao, expenseDao)
        expenseCreationViewModel = ViewModelProviders.of(this, expenseCreationViewModelFactory).get(
            ExpenseCreationViewModel::class.java)

        binding.expenseCreationViewModel = expenseCreationViewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.amountInput.setOnKeyListener { view, keyCode, keyEvent ->
            Log.e("code", keyCode.toString())
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyBoard()
                true
            }
            false
        }
        bindUI(expenseCreationViewModel)
    }

    private fun bindUI(expenseCreationViewModel: ExpenseCreationViewModel) = Coroutines.main{
        datePickerDialogSetup()
        payerPickerSpinnerSetup(expenseCreationViewModel)
        memberRecyclerViewSetup(expenseCreationViewModel)

    }


    private fun getCurrentDate(){
        val sdf = SimpleDateFormat("dd MMM yyyy")
        val currentDate = sdf.format(Date())
        binding.dateInput.setText(currentDate)

    }

    private fun payerPickerSpinnerSetup(expenseCreationViewModel: ExpenseCreationViewModel){
        payerSelectionDialogSetup(expenseCreationViewModel)
        payerPickerSpinnerCreation()
        binding.payerInput.setOnClickListener{
            payerPickerSpinnerCreation()
        }
    }

    private fun payerPickerSpinnerCreation(){
        hideKeyBoard()
        binding.expenseCreationViewModel!!.currentEvent.observe(this, androidx.lifecycle.Observer {event ->
            Log.e("text", binding.payerInput.text.toString())
            val users : HashMap<Int, User> = event.participants
            Log.e("Size", users.size.toString())
            val userName : HashMap<Int, String> = hashMapOf()
            for (i : Int in users.keys){
                userName[i] = users[i]!!.name
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ArrayList(userName.values))
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
            binding.payerSpinner.adapter = adapter

            binding.payerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    binding.payerInput.setText(userName[p2])
                    payerId = p2
                }
            }
        })
    }

    private fun payerSelectionDialogSetup(expenseCreationViewModel: ExpenseCreationViewModel){
        expenseCreationViewModel.currentEvent.observe(this, androidx.lifecycle.Observer {event ->
            Log.e("Text", binding.payerInput.text.toString())
            if (binding.payerInput.text.isEmpty()){
                binding.payerInput.setText(event.participants[0]!!.name)
            }
        })

    }

    private fun datePickerDialogSetup(){
        getCurrentDate()
        binding.dateInput.setOnClickListener(View.OnClickListener {
            datePickerDialogCreation()
        })
    }
    private fun datePickerDialogCreation(){
        hideKeyBoard()
        val onDateSelecListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            val calendar= Calendar.getInstance()
            calendar.set(year, month, day)
            val sdf = SimpleDateFormat("dd MMM yyyy")
            val selectedDate = sdf.format(calendar.time)
            binding.dateInput.setText(selectedDate)
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            this,
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert,
            onDateSelecListener,
            year, month, day)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.WHITE))
        dialog.show()

    }



    private fun hideKeyBoard(){
        val imm:  InputMethodManager =  this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = this.currentFocus
        if (view != null){
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun memberRecyclerViewSetup(expenseCreationViewModel: ExpenseCreationViewModel){

        expenseCreationViewModel.currentEvent.observe(this, androidx.lifecycle.Observer {event ->
            binding.participantRecycler.layoutManager = LinearLayoutManager(this)
//            binding.amountRecycler.layoutManager = LinearLayoutManager(this)

            binding.participantRecycler.setHasFixedSize(true)
//            binding.amountRecycler.setHasFixedSize(true)
            
            expenseCreationViewModel.initEvenMember(event)
            

            if(!binding.amountInput.text.isNullOrEmpty()){
                initialAmount = binding.amountInput.text.toString().toDouble()
            }

            val memberAdapter = ExpenseAdapter(
                expenseCreationViewModel.participantsList,
                applicationContext,
                currentFocus)

            binding.participantRecycler.adapter = memberAdapter
            memberAdapter.notifyDataSetChanged()
            binding.amountInput.setOnFocusChangeListener { view, b ->
                if (!b) {
                    hideKeyBoard()
                    if (binding.amountInput.text!!.isEmpty()) {
                        initialAmount = 0.0
                        memberAdapter.setTotalAmount(initialAmount)
                        memberAdapter.notifyDataSetChanged()
                    } else {
                        try {
                            initialAmount = binding.amountInput.text!!.toString().toDouble()
                            memberAdapter.setTotalAmount(initialAmount)
                            memberAdapter.notifyDataSetChanged()
                        } catch (e: NumberFormatException) {
                            binding.amountInput.error = "Invalid Input"
                        }
                    }
                }
            }
        })
    }

    private fun checkValidExpense(participantList : HashMap<Int, User>, amount : Double) : Boolean{
        var validMembers = false
        var total = 0.0
        if (binding.titleInput.text.isNullOrEmpty()){
            binding.titleInput.error = "Invalid Title"
            return false
        }

        if(binding.amountInput.text.isNullOrEmpty()){
            binding.amountInput.error = "Please enter the valid amount"
            return false
        }

        for((k,v) in participantList){
            Log.e("amount", v.payAmount.toString())
            if (v.participate) {
                validMembers = v.participate
                if (v.payAmount > amount){
                    Toast.makeText(this, "You can't impact more than the amount of the expense", Toast.LENGTH_SHORT).show()
                    return false
                }
                total += v.payAmount
            }
        }
        if (!validMembers){
            Toast.makeText(this, "Please choose at least 1 member for the expense!", Toast.LENGTH_SHORT).show()
        }

        if (round(abs(total - amount) * 100)/100 > 0.01){
            Log.e("total", total.toString())
            Log.e("amount", amount.toString())
            Log.e("diff", (total - amount).toString())
            Toast.makeText(this, "The shares don't add up to the initial sum. Please correct the shares for each members!", Toast.LENGTH_SHORT).show()
            return false
        }

        return validMembers

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN){
            val v = currentFocus
            if (v is EditText){
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())){
                    v.clearFocus()
                    hideKeyBoard()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_of_creation_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.save_event -> {
                if (checkValidExpense(expenseCreationViewModel.participantsList, initialAmount)){
                    val expense = Expense()
                    expense.apply {
                        title = binding.titleInput.text.toString()
                        eventId = binding.expenseCreationViewModel!!.currentEvent.value!!.id
                        date = binding.dateInput.text.toString()
                        val tmp = hashMapOf<Int, User>()
                        for ((k,v) in expenseCreationViewModel.participantsList){
                            if (k == payerId){
                                tmp[k] = User(v.id, v.name, initialAmount-v.payAmount, v.participate, v.portion)
                            } else {
                                if (v.payAmount != 0.0){
                                    tmp[k] = User(v.id, v.name, -v.payAmount, v.participate, v.portion)
                                } else {
                                    tmp[k] = User(v.id, v.name, 0.0, v.participate, v.portion)
                                }

                            }

                        }
                        participants = tmp
                        payer = payerId
                        amount = initialAmount
                        currency = binding.expenseCreationViewModel!!.currentEvent.value!!.currency
                    }
                    binding.expenseCreationViewModel!!.insertExpense(expense)
                    binding.expenseCreationViewModel!!.currentEvent.observe(this, androidx.lifecycle.Observer {event ->
                        val updateEvent = Event(event.id, event.title, event.description, event.date, event.currency, event.participants, event.amount+initialAmount)
                        Log.e("updateEvent", updateEvent.amount.toString())
                        binding.expenseCreationViewModel!!.updateEvent(updateEvent)
                    })

                    binding.expenseCreationViewModel!!.idExpense.observe(this, androidx.lifecycle.Observer {id ->
                        val intent = Intent(this, ExpenseDetailActivity::class.java)
                        intent.putExtra("expenseId", id)
                        startActivity(intent)
                        finish()
                    })

                }

                return true
            }

            else -> {return false}
        }
    }
}
