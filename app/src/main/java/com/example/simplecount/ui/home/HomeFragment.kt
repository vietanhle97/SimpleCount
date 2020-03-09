package com.example.simplecount.ui.home


import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.simplecount.R
import com.example.simplecount.controller.EventSwipeController
import com.example.simplecount.data.db.EventDatabase
import com.example.simplecount.data.db.ExpenseDatabase
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.databinding.FragmentHomeBinding
import com.example.simplecount.ui.event.eventCreation.EventCreationActivity
import com.example.simplecount.ui.event.eventDetail.EventDetailActivity
import com.example.simplecount.util.Coroutines
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var homeBinding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val application = requireNotNull(this.activity).application
        val eventDao = EventDatabase.geInstance(application).eventDao
        val expenseDao = ExpenseDatabase.geInstance(application).expenseDao
        val homeViewModelFactory = HomeViewModelFactory(eventDao, expenseDao, application)
        val homeViewModel = ViewModelProviders.of(this, homeViewModelFactory).get(HomeViewModel::class.java)


        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeBinding.fab.setOnClickListener {
            val intent = Intent(context, EventCreationActivity::class.java)
            startActivity(intent)

        }
        homeBinding.homeViewModel = homeViewModel
        homeBinding.lifecycleOwner = this

        bindUI(homeViewModel, application)
        return homeBinding.root
    }

    private fun bindUI(homeViewModel: HomeViewModel, application: Application) = Coroutines.main{
        initRecyclerView(homeViewModel, application)
    }

    private fun initRecyclerView(homeViewModel: HomeViewModel, application: Application){

        homeViewModel.idEvent.observe(this, Observer {id ->
            id?.let{
                Log.e("id", id.toString())
                val intent = Intent(this.activity, EventDetailActivity::class.java)
                intent.putExtra("eventId", id)
                startActivity(intent)
                homeViewModel.onEventNavigated()
            }

        })
        homeBinding.eventRecycler.setHasFixedSize(true)
        homeBinding.eventRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = HomeEventAdapter(EventClickListener { eventId -> homeViewModel.onEventClicked(eventId)})
        homeBinding.eventRecycler.adapter = adapter

        homeViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            adapter.submitList(events)
        })

        homeViewModel.events.observe(viewLifecycleOwner, Observer {events ->
            val swipeController = object : EventSwipeController(application.applicationContext){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    homeViewModel.deleteEvent(events[position])
                    showUndoSnackbar(homeViewModel, events[position])
                    homeViewModel.addDeleteEvent(events[position].id)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(homeBinding.eventRecycler)
        })

    }

    private fun showUndoSnackbar(homeViewModel: HomeViewModel, event: Event) {
        val view = homeBinding.mainLayout
        val snackbar = Snackbar.make(view, "You deleted an event", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", View.OnClickListener {
            undoDelete(homeViewModel, event)
        })
        snackbar.show()
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event == DISMISS_EVENT_TIMEOUT){
                    homeViewModel.deleteAllExpenseOfEvent(homeViewModel.deleteEventId.value!!)
                }
            }
        })
    }

    private fun undoDelete(homeViewModel: HomeViewModel, event: Event) {
        homeViewModel.insert(event)
        homeViewModel.resetDeleteEvent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_all_events ->{
                homeBinding.homeViewModel!!.deleteAllEvents()
                homeBinding.homeViewModel!!.deleteAllExpense()
                return true
            }

            else -> {return false}
        }
    }


}
