package com.example.simplecount.ui.balance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.R
import com.example.simplecount.data.db.entity.User
import com.example.simplecount.databinding.BalanceBinding
import kotlin.math.round

class UserBalanceAdapter (private val users: HashMap<Int, Pair<String, Double>>) : RecyclerView.Adapter<UserBalanceAdapter.UserBalanceViewHolder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserBalanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.balance, parent, false)
        return UserBalanceViewHolder((view))
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserBalanceViewHolder, position: Int) {
        val user = users[position]!!
        holder.apply {
            if (position == 0){
                userName.text = "${user.first} (me)"
            } else{
                userName.text = user.first
            }

            userBalance.text = (round(user.second *100) / 100).toString()
        }
    }

    class UserBalanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName = view.findViewById<TextView>(R.id.user_name)
        val userBalance = view.findViewById<TextView>(R.id.user_balance)
    }
}