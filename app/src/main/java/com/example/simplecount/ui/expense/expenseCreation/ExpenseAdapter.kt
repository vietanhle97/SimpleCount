package com.example.simplecount.ui.expense.expenseCreation

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.R
import com.example.simplecount.data.db.entity.User
import kotlin.math.round

class ExpenseAdapter (
    private val participantList: HashMap<Int, User>,
    private val context: Context,
    private val currentFocus: View?
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var totalAmount : Double = 0.0

    fun setTotalAmount(amount : Double){
        this.totalAmount = amount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return participantList.size
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val base = calculateSmallestPortionAmount(participantList)
        val user = participantList[position]!!
        holder.apply {
            memberName.text = user.name
            memberCheck.isChecked = user.participate
            memberCheck.setOnClickListener {
                Log.e("click", "hmm")
                val cur = user.participate
                Log.e("par", cur.toString())
                if (cur) {
                    user.payAmount = 0.0
                    user.portion = 0
                } else {
                    user.portion = 1
                }
                user.participate = !cur
                notifyDataSetChanged()
            }

            memberPortion.setOnFocusChangeListener { _, b ->
                if (!b) {
                    val cur = memberPortion.text.toString()
                    var tmp = 0
                    if (cur.isNotEmpty()) {
                        tmp =cur.toInt()
                    }
                    if (tmp == 0) {
                        user.payAmount = 0.0
                        user.portion = 0
                        user.participate = false
                        memberCheck.isChecked = false
                    } else {
                        user.portion = tmp
                        user.participate = true
                        memberCheck.isChecked = true
                    }
                    notifyDataSetChanged()
                }
            }
            memberPortion.setOnKeyListener { _, keyCode, _ ->
                Log.e("portion code", keyCode.toString())
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyBoard()
                }
                false
            }

            memberAmount.setOnFocusChangeListener { _, b ->
                if (!b) {
                    val cur = memberAmount.text.toString()
                    var tmp = 0.0
                    if (cur.isNotEmpty()) {
                        tmp = cur.toDouble()
                    }
                    if (tmp == 0.0) {
                        user.payAmount = 0.0
                        user.portion = 0
                        user.participate = false
                        memberCheck.isChecked = false
                    } else {
                        user.payAmount = tmp
                        user.portion = 0
                        user.participate = true
                        memberCheck.isChecked = true
                    }

                    notifyDataSetChanged()
                }
            }
            memberAmount.setOnKeyListener { _, keyCode, _ ->
                Log.e("amount code", keyCode.toString())
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyBoard()
                }
                false
            }

            memberPortion.setText(user.portion.toString())
            if (user.participate) {
                if (user.portion != 0) {
                    val tmp = round(base * user.portion * 100) /100
                    user.payAmount = tmp
                    memberAmount.setText(tmp.toString())
                } else {
                    memberAmount.setText(user.payAmount.toString())
                }

            } else {
                memberAmount.setText(0.0.toString())
            }
        }
    }

    private fun calculateTotalPortion(portions : HashMap<Int, User>) : Int{
        var res = 0
        for ((_,v) in portions) {
            if (v.participate){
                res += v.portion
            }
        }

        return res
    }

    private fun calculateSmallestPortionAmount(portions : HashMap<Int, User>) : Double{
        val totalPortion = calculateTotalPortion(portions)
        var res = 0.0
        if (totalPortion == 0){
            return 0.0
        }

        for ((_,v) in portions){
            if (v.portion == 0) res += v.payAmount
        }

        return (totalAmount - res)/totalPortion
    }

    private fun hideKeyBoard() {
        if (currentFocus != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }

    }


    inner class ExpenseViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val memberCheck = view.findViewById<CheckBox>(R.id.member_checkbox)!!
        val memberName = view.findViewById<TextView>(R.id.member_name)!!
        val memberPortion = view.findViewById<EditText>(R.id.member_portion)!!
        val memberAmount = view.findViewById<EditText>(R.id.member_amount)!!
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    @Nullable
    override fun getChangePayload(oldItem: User, newItem: User): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}

