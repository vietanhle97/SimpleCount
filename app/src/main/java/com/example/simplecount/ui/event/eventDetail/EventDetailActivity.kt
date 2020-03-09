package com.example.simplecount.ui.event.eventDetail

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecount.R
import com.example.simplecount.data.db.EventDatabase
import com.example.simplecount.data.db.ExpenseDatabase
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.data.db.entity.User
import com.example.simplecount.databinding.ActivityEventDetailBinding
import com.example.simplecount.ui.balance.BalanceFragment
import com.example.simplecount.ui.expense.expenseDisplay.ExpenseFragment
import com.example.simplecount.ui.expense.expenseCreation.ExpenseCreationActivity
import com.example.simplecount.ui.expense.expenseDisplay.ExpenseAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class EventDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityEventDetailBinding
    private val iconList = arrayListOf(R.drawable.ic_expense, R.drawable.ic_balance)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail, null)

        val id = intent.extras?.getLong("eventId") as Long
//        Log.e("eventId", id.toString())
        val eventDao = EventDatabase.geInstance(this).eventDao
        val expenseDao = ExpenseDatabase.geInstance(this).expenseDao
        val eventDetailViewModelFactory = EventDetailViewModelFactory(id, eventDao, expenseDao)
        val eventDetailViewModel = ViewModelProviders.of(this, eventDetailViewModelFactory).get(EventDetailViewModel::class.java)

        binding.eventDetailViewModel = eventDetailViewModel

        binding.eventDetailViewModel!!.currentEvent.observe(this, Observer {currentEvent ->
            binding.toolbar.title = currentEvent.title
            if (currentEvent.participants.size < 7){
                var subTitle = ""

                for (i : Int in 0 until currentEvent.participants.size){
                    subTitle += currentEvent.participants[i]!!.name
                    if (i != currentEvent.participants.size-1){
                        subTitle += ", "
                    }
                }
                binding.toolbar.subtitle = subTitle
            } else {
                binding.toolbar.subtitle = "${currentEvent.participants.size} Participants"
            }

            setUpFragment(currentEvent.id, currentEvent.participants.size)
        })

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



    }

    private fun setUpFragment(id: Long, numUsers : Int){
        val adapter  = EventDetailFragmentAdapter(id, numUsers, supportFragmentManager)
        adapter.addFragment(ExpenseFragment.newInstance(id), "EXPENSES")
        adapter.addFragment(BalanceFragment.newInstance(id, numUsers), "BALANCES")

        binding.detailViewPager.adapter = adapter
        binding.detailTabLayout.setupWithViewPager(binding.detailViewPager)
        setUpTabIcon(binding.detailTabLayout, iconList)
    }

    private fun setUpTabIcon(tabLayout: TabLayout, iconList : ArrayList<Int>){
        tabLayout.getTabAt(0)!!.setIcon(iconList[0])
        tabLayout.getTabAt(1)!!.setIcon(iconList[1])
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_event_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.delete_all_expenses -> {
                binding.eventDetailViewModel!!.deleteAllExpenses()
                binding.eventDetailViewModel!!.currentEvent.observe(this, Observer {event ->
                    val newMemberList = hashMapOf<Int, User>()
                    for ((k,v) in event.participants){
                       newMemberList[k] = User(v.id, v.name, 0.0, true, 1)
                    }
                    val newEvent = Event(event.id, event.title, event.description, event.date, event.currency, newMemberList, 0.0)
                    binding.eventDetailViewModel!!.updateEvent(newEvent)
                })
                return true
            }

        }

        return false
    }

    override fun onResume() {
        binding.eventDetailViewModel!!.currentEvent.observe(this, Observer { currentEvent ->
            setUpFragment(currentEvent.id, currentEvent.participants.size)
        })
        super.onResume()
    }

}
