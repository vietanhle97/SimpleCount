package com.example.simplecount.ui.event.eventCreation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecount.R
import com.example.simplecount.data.db.EventDatabase
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.data.db.entity.User
import com.example.simplecount.databinding.ActivityEventCreationBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function5
import kotlinx.android.synthetic.main.activity_event_creation.*
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import com.example.simplecount.ui.event.eventDetail.EventDetailActivity
import com.example.simplecount.util.Coroutines
import com.jakewharton.rxbinding2.view.clickable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class EventCreationActivity : AppCompatActivity() {

    lateinit var binding: ActivityEventCreationBinding
    private var users = HashMap<Int, User>()
    private val currency = arrayListOf("ALL", "AFN", "ARS", "AWG", "AUD", "AZN", "BSD", "BBD", "BDT", "BYR", "BZD", "BMD", "BOB", "BAM", "BWP", "BGN", "BRL", "BND", "KHR", "CAD", "KYD", "CLP", "CNY", "COP", "CRC", "HRK", "CUP", "CZK", "DKK", "DOP", "XCD", "EGP", "SVC", "EEK", "EUR", "FKP", "FJD", "GHC", "GIP", "GTQ", "GGP", "GYD", "HNL", "HKD", "HUF", "ISK", "INR", "IDR", "IRR", "IMP", "ILS", "JMD", "JPY", "JEP", "KZT", "KPW", "KRW", "KGS", "LAK", "LVL", "LBP", "LRD", "LTL", "MKD", "MYR", "MUR", "MXN", "MNT", "MZN", "NAD", "NPR", "ANG", "NZD", "NIO", "NGN", "NOK", "OMR", "PKR", "PAB", "PYG", "PEN", "PHP", "PLN", "QAR", "RON", "RUB", "SHP", "SAR", "RSD", "SCR", "SGD", "SBD", "SOS", "ZAR", "LKR", "SEK", "CHF", "SRD", "SYP", "TWD", "THB", "TTD", "TRY", "TRL", "TVD", "UAH", "GBP", "USD", "UYU", "UZS", "VEF", "VND", "YER", "ZWD")
    private var appear = false



    private val emptyRequiredField = ObservableTransformer<String, String>{observable ->
        observable.flatMap {input ->
            Observable.just(input).map { it.trim() }
                .filter { it.isNotEmpty() }
                .singleOrError()
                .onErrorResumeNext {
                    if (it is NoSuchElementException) {
                        Single.error(Exception("This field cannot be empty"))
                    } else {
                        Single.error(it)
                    }
                }
                .toObservable()
        }
    }


    private val validCurrency = ObservableTransformer<String, String> { observable ->
        observable.flatMap { input ->
            Observable.just(input).map { it.trim() }
                .filter {currency.contains(it.toUpperCase()) }
                .singleOrError()
                .onErrorResumeNext{
                    if (it is NoSuchElementException){
                        Single.error(Exception("Invalid Currency"))
                    } else{
                        Single.error(it)
                    }
                }
                .toObservable()
        }
    }

    private val redundantName = ObservableTransformer<String, String> {observable ->
        observable.flatMap { input ->
            Observable.just(input).map { it.trim() }
                .filter { !isNameExisted(it, users) }
                .singleOrError()
                .onErrorResumeNext {
                    if (it is NoSuchElementException) {
                        Single.error(Exception("Name already existed"))
                    } else {
                        Single.error(it)
                    }
                }.toObservable()
        }
    }

    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_creation)

        binding = DataBindingUtil.setContentView(this,  R.layout.activity_event_creation)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.title.requestFocus()
        val eventDao = EventDatabase.geInstance(this).eventDao
        val eventCreationViewModelFactory = EventCreationViewModelFactory(eventDao, this.application)
        val eventCreationViewModel = ViewModelProviders.of(this, eventCreationViewModelFactory).get(EventCreationViewModel::class.java)

        binding.eventCreationViewModel = eventCreationViewModel

        bindUI()
    }

    private fun bindUI() = Coroutines.main{

        validateField(binding.titleHolder, binding.title, "The title should be at least 1 character long")

        validateCurrency(binding.currency)

        validateField(binding.participantNameHolder, binding.participantName, "Please enter a valid name")

        handleSaveButtonAppearance()

        validParticipantName(binding.participantName)

        onAddButton()
    }


    private fun onAddButton(){
        binding.add.setOnClickListener {
            val name = binding.participantName.text!!
            if (name.isNotEmpty() && !isNameExisted(name.toString(), users)){
                users.put(users.size, User(users.size, name.toString(), 0.0, true, 0))
                binding.participantNameHolder.hint = "Other participant"
                name.clear()
                initRecyclerView(users)
            }
        }
    }

    private fun isNameExisted(name : String, users : HashMap<Int, User>) : Boolean{
        for ((k,v) in users){
            if (name == v.name) return true
        }

        return false
    }

    private fun validateField(holder: TextInputLayout, input: TextInputEditText, message: String){
        RxTextView.afterTextChangeEvents(input)
            .skipInitialValue()
            .map {
                holder.error = null
                it.view().text.toString() }
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(emptyRequiredField)
            .compose(retryWhenError {
                holder.error = message
            })
            .subscribe()
    }

    private fun validateCurrency(input: TextInputEditText){
        RxTextView.afterTextChangeEvents(input)
            .skipInitialValue()
            .map {
                currency_holder.error = null
                it.view().text.toString() }
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(validCurrency)
            .compose(retryWhenError {
                currency_holder.error = it.message
            })
            .subscribe()
    }

    private fun validParticipantName(input: TextInputEditText){
        RxTextView.afterTextChangeEvents(input)
            .skipInitialValue()
            .map {
                participant_name_holder.error = null
                it.view().text.toString() }
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(redundantName)
            .compose(retryWhenError {
                participant_name_holder.error = it.message
            })
            .subscribe()
    }

    private fun initRecyclerView(users : HashMap<Int, User>){
        binding.participantRecycler.setHasFixedSize(true)
        binding.participantRecycler.layoutManager = LinearLayoutManager(this)
        val adapter = NewParticipantsAdapter(users, binding.participantNameHolder, binding.participantName)
        binding.participantRecycler.adapter = adapter


    }

    private fun handleSaveButtonAppearance() {
        val title : Observable<CharSequence> = RxTextView.textChanges(binding.title)
        val participantName : Observable<CharSequence> = RxTextView.textChanges(binding.participantName)
        val valid : Observable<Unit> = Observable.combineLatest(title, participantName, BiFunction{ t1, t2  ->
            if (t1.isEmpty() && t2.isEmpty()) {
                appear = false
                invalidateOptionsMenu()
            } else {
                appear = true
                invalidateOptionsMenu()
            }
        })
        valid.subscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_of_creation_activity, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu!!.findItem(R.id.save_event).isVisible = appear
        Log.e("haha", "haha")
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_event -> {
                val observableTitle : Observable<CharSequence> = RxTextView.textChanges(binding.title)
                val observableDescription : Observable<CharSequence> = RxTextView.textChanges(binding.description)
                val observableCurrency : Observable<CharSequence> = RxTextView.textChanges(binding.currency)
                val observableParticipantName : Observable<CharSequence> = RxTextView.textChanges(binding.participantName)
                val observableUserList : Observable<HashMap<Int, User>> = Observable.just(users)

                val save : Observable<Unit> = Observable.combineLatest(observableTitle, observableDescription, observableCurrency, observableParticipantName, observableUserList, Function5{ t1, t2, t3, t4, t5 ->
                    if (t1.isNotEmpty() && t3.isNotEmpty() && (t4.isNotEmpty() || t5.isNotEmpty())){
                        val event = Event()
                        if (t4.isNotEmpty() && !isNameExisted(binding.participantName.text.toString(), users)){
                            users.put(users.size, User(users.size, binding.participantName.text.toString(), 0.0, true, 0))
                        }

                        val calendar= Calendar.getInstance()
                        val sdf = SimpleDateFormat("dd MMM yyyy")
                        val eventDate = sdf.format(calendar.time)

                        event.apply {
                            title = binding.title.text.toString()
                            if(t2.isNotEmpty()){
                                description = binding.description.text.toString()
                            } else{
                                description = "No description"
                            }
                            amount = 0.0
                            date = eventDate
                            currency = binding.currency.text.toString()
                            participants = users
                        }
                        binding.eventCreationViewModel!!.insertEvent(event)
                        binding.eventCreationViewModel!!.id.observe(this, Observer {id ->
                            val intent = Intent(this, EventDetailActivity::class.java)
                            intent.putExtra("eventId", id)
                            startActivity(intent)
                            finish()
//                            onBackPressed()
                        })


                    } else{
                        validateCurrency(binding.currency)
                        validateField(binding.participantNameHolder, binding.participantName, "Please enter a valid name")
                        validateField(binding.titleHolder, binding.title, "The title should be at least 1 character long")
                    }
                })
                save.subscribe()
                return true
            }

            else -> {return false}

        }

    }
}
