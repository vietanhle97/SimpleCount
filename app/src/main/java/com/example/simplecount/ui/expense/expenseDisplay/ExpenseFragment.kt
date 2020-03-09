package com.example.simplecount.ui.expense.expenseDisplay


import android.app.Application
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.simplecount.R
import com.example.simplecount.controller.ExpenseSwipeController
import com.example.simplecount.data.db.EventDatabase
import com.example.simplecount.data.db.ExpenseDatabase
import com.example.simplecount.data.db.dao.ExpenseDao
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.data.db.entity.Expense
import com.example.simplecount.data.db.entity.User
import com.example.simplecount.databinding.FragmentExpenseBinding
import com.example.simplecount.ui.expense.expenseCreation.ExpenseCreationActivity
import com.example.simplecount.ui.expense.expenseDetail.ExpenseDetailActivity
import com.example.simplecount.util.Coroutines
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class ExpenseFragment : Fragment() {

    companion object{
        fun newInstance(eventId : Long) : ExpenseFragment {
            val expenseFragment = ExpenseFragment()
            val args = Bundle().apply {
                eventId.let { putLong("eventId", eventId) }
            }
            expenseFragment.arguments = args
            return expenseFragment
        }
    }
    lateinit var binding: FragmentExpenseBinding
    private var eventId = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        eventId = arguments?.get("eventId") as Long
        Log.e("Id", eventId.toString())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_expense, container, false)

        val application = requireNotNull(this.activity).application
        val eventDao = EventDatabase.geInstance(application).eventDao
        val expenseDao = ExpenseDatabase.geInstance(application).expenseDao
        val expenseViewModelFactory = ExpenseViewModelFactory(eventId, eventDao, expenseDao)
        val expenseViewModel = ViewModelProviders.of(activity!!, expenseViewModelFactory).get(
            ExpenseViewModel::class.java)

        binding.expenseViewModel = expenseViewModel
        binding.lifecycleOwner = this

        bindUI(expenseViewModel, application)



        binding.newExpense.setOnClickListener(View .OnClickListener {
            val intent = Intent(this.activity, ExpenseCreationActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        })
        return binding.root
    }

    private fun bindUI(expenseViewModel: ExpenseViewModel, application: Application) = Coroutines.main{
        initRecyclerView(expenseViewModel, application)
    }

    private fun initRecyclerView(expenseViewModel: ExpenseViewModel, application: Application){


        binding.lifecycleOwner = viewLifecycleOwner
        binding.expenseRecycler.layoutManager = LinearLayoutManager(this.activity)
        binding.expenseRecycler.setHasFixedSize(true)

        expenseViewModel.expenses.observe(viewLifecycleOwner, Observer { expenses ->
            val adapter = ExpenseAdapter{expense ->  onExpenseClick(expense)}
            binding.expenseRecycler.adapter = adapter
            adapter.setExpenses(ArrayList(expenses))
            val expenseList = ArrayList(expenses)
            val swipeController = object : ExpenseSwipeController(expenseList, application.applicationContext){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val expense = adapter.getExpenseAt(viewHolder.adapterPosition)
                    showUndoSnackBar(expenseViewModel, expense)
                    expenseViewModel.deleteExpense(expense)
                    expenseViewModel.setUpdate()

                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(binding.expenseRecycler)

        })

    }

    private fun onExpenseClick(expense: Expense){
        val intent = Intent(this.activity, ExpenseDetailActivity::class.java)
        intent.putExtra("expenseId", expense.id)
        startActivity(intent)
    }

    private fun showUndoSnackBar(expenseViewModel: ExpenseViewModel, expense: Expense){
        binding.apply {
            expenseBottomBar.visibility = View.GONE
            newExpense.visibility = View.GONE
        }
        val view = binding.expenseLayout
        val snackBar = Snackbar.make(view, "You deleted an expense", Snackbar.LENGTH_LONG);
        snackBar.setAction("Undo", View.OnClickListener {
            undoDeleteExpense(expenseViewModel, expense)
        })
        snackBar.show()
        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                binding.apply {
                    expenseBottomBar.visibility = View.VISIBLE
                    newExpense.visibility = View.VISIBLE
                }
                if (event != DISMISS_EVENT_ACTION){
                    binding.apply {
                        expenseViewModel.update.observe(viewLifecycleOwner, Observer {update ->
                            if (update) {
                                expenseViewModel.currentEvent.observeOnce(viewLifecycleOwner, Observer { event ->
                                    expenseViewModel.getEvent(eventId)
                                    val updateEvent = Event(
                                        event.id,
                                        event.title,
                                        event.description,
                                        event.date,
                                        event.currency,
                                        event.participants,
                                        event.amount - expense.amount
                                    )
                                    expenseViewModel.resetUpdate()
                                    expenseViewModel.updateEvent(updateEvent)

                                })
                            }

                        })
                    }
                }

            }
        })
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }

    private fun undoDeleteExpense(expenseViewModel: ExpenseViewModel, expense: Expense){
        expenseViewModel.insertExpense(expense)

    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.delete_all_expenses -> {
                binding.expenseViewModel!!.deleteAllExpenses()

                return true
            }

        }

        return false
    }

}
