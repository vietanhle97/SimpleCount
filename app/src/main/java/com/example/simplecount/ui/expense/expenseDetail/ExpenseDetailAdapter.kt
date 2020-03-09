package com.example.simplecount.ui.expense.expenseDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.R
import com.example.simplecount.data.db.entity.Expense
import com.example.simplecount.data.db.entity.User
import kotlin.math.round

class ExpenseDetailAdapter (private val users: HashMap<Int, User>, private val expense: Expense) : RecyclerView.Adapter<ExpenseDetailAdapter.ExpenseDetailViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.balance, parent, false)
        return ExpenseDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ExpenseDetailViewHolder, position: Int) {
        val user = users[position]!!
        if (user.participate){
            holder.apply {
                if (user.id == 0){
                    userName.text = "${user.name} (me)"
                } else {
                    userName.text = user.name
                }

                if (user.id == expense.payer){
                    userAmount.text = (round((expense.amount - user.payAmount) * 100) / 100).toString()
                } else {
                    userAmount.text = (-user.payAmount).toString()
                }

            }
        }

    }

    class ExpenseDetailViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val userName = view.findViewById<TextView>(R.id.user_name)
        val userAmount = view.findViewById<TextView>(R.id.user_balance)
    }


}