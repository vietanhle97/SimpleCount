package com.example.simplecount.ui.balance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.R
import com.example.simplecount.data.db.entity.User

class UserDebtAdapter (
    private val users : HashMap<Int, Pair<String, Double>>,
    private val paymentMap : ArrayList<Pair<Int, Pair<Int, Double>>>
) : RecyclerView.Adapter<UserDebtAdapter.UserDebtViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDebtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.debt, parent, false)
        return UserDebtViewHolder(view)
    }

    override fun getItemCount(): Int {
        return paymentMap.size
    }

    override fun onBindViewHolder(holder: UserDebtViewHolder, position: Int) {
        val item = paymentMap[position]
        val credit = item.second
        holder.apply {
            debt.text = credit.second.toString()
            debtor.text = users[item.first-1]!!.first
            creditor.text = users[credit.first-1]!!.first
        }

    }

    class UserDebtViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val debtor = view.findViewById<TextView>(R.id.debtor)
        val creditor = view.findViewById<TextView>(R.id.creditor)
        val debt = view.findViewById<TextView>(R.id.debt)

    }

}