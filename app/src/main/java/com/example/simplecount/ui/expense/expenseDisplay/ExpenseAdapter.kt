package com.example.simplecount.ui.expense.expenseDisplay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.R
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.data.db.entity.Expense
import com.example.simplecount.databinding.EventBinding
import com.example.simplecount.databinding.ExpenseBinding
import com.example.simplecount.ui.home.EventClickListener
import com.example.simplecount.ui.home.EventDiffCallback
import com.example.simplecount.ui.home.HomeEventAdapter
import com.google.android.material.card.MaterialCardView


class ExpenseAdapter (
    private val onExpenseClickListener: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>(){

    private var expenses = ArrayList<Expense>()

    fun setExpenses(expenses: ArrayList<Expense>){
        this.expenses = expenses
    }

    fun getExpenseAt(position: Int) : Expense{
        return expenses[position]
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.apply {
            expenseTitle.text = expense.title
            expenseAmount.text = expense.amount.toString()
            if (expense.payer == 0){
                expensePayer.text = "me"
            } else {
                expensePayer.text = expense.participants[expense.payer]!!.name
            }
            expenseDate.text = expense.date
            expenseHolder.setOnClickListener(View.OnClickListener {
                onExpenseClickListener(expense)
            })
        }

    }

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val expenseHolder = view.findViewById<MaterialCardView>(R.id.expense_holder)
        val expenseTitle = view.findViewById<TextView>(R.id.expense_title)!!
        val expenseAmount = view.findViewById<TextView>(R.id.expense_amount)!!
        val expensePayer = view.findViewById<TextView>(R.id.expense_payer)
        val expenseDate = view.findViewById<TextView>(R.id.expense_date)
    }
}