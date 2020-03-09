package com.example.simplecount.ui.expense.expenseDetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecount.R
import com.example.simplecount.data.db.ExpenseDatabase
import com.example.simplecount.databinding.ActivityExpenseDetailBinding
import com.example.simplecount.util.Coroutines
import com.google.android.material.appbar.AppBarLayout

class ExpenseDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityExpenseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.extras?.get("expenseId") as Long
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expense_detail, null)
        val expenseDao = ExpenseDatabase.geInstance(this).expenseDao
        val expenseDetailViewModelFactory = ExpenseDetailViewModelFactory(id, expenseDao)
        val expenseDetailViewModel = ViewModelProviders.of(this, expenseDetailViewModelFactory).get(ExpenseDetailViewModel::class.java)

        setSupportActionBar(binding.expenseDetailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        bindUI(expenseDetailViewModel)


    }

    private fun bindUI(expenseDetailViewModel: ExpenseDetailViewModel) = Coroutines.main{
        setUpCollapsingToolbar(expenseDetailViewModel)
        initExpenseDetailRecycler(expenseDetailViewModel)
    }

    private fun setUpCollapsingToolbar(expenseDetailViewModel: ExpenseDetailViewModel){
        expenseDetailViewModel.expense.observe(this, Observer {expense ->
            binding.apply {
                expenseDetailCollapsingToolbar.title = expense.title
                expenseDate.text = expense.date
                expenseAmount.text = "${expense.amount} ${expense.currency}"
                var name : String = ""
                for ((k,v) in expense.participants){
                    if (k == expense.payer){
                        name = v.name
                    }
                }
                expensePayer.text = "Paid by $name"
            }
        })
    }

    private fun initExpenseDetailRecycler(expenseDetailViewModel: ExpenseDetailViewModel){
        binding.expenseDetailRecycler.layoutManager = LinearLayoutManager(this)
        binding.expenseDetailRecycler.setHasFixedSize(true)
        expenseDetailViewModel.expense.observe(this, Observer {expense ->
            val adapter = ExpenseDetailAdapter(expense.participants, expense)
            binding.expenseDetailRecycler.adapter = adapter
        })
    }

}
