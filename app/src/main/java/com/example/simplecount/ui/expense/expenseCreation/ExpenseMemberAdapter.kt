package com.example.simplecount.ui.expense.expenseCreation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.PointerIcon
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.R
import com.example.simplecount.data.db.entity.Expense
import com.example.simplecount.data.db.entity.User
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit
import kotlin.math.round

class ExpenseMemberAdapter (
    private var members : HashMap<Int, User>,
    private val totalAmount : Double,
    private val onCheckBoxClick : (User, Int, Boolean, String, String) -> Unit,
    private val onPortionEdit : (User, Int, String) -> Unit

) : RecyclerView.Adapter<ExpenseMemberAdapter.ExpenseMemberViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseMemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member, parent, false)
        return ExpenseMemberViewHolder(view)
    }

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onBindViewHolder(holder: ExpenseMemberViewHolder, position: Int) {
        val user = members[position]!!
        holder.apply {
            memberName.text = user.name
            if (user.participate){
                memberCheck.isChecked = true
            }
//            memberCheck.isChecked = user.participate
            if (user.portion == 0){
                memberPortion.text.clear()
            } else {

                memberPortion.setText(user.portion.toString())
            }
            memberCheck.setOnClickListener(View.OnClickListener {
                Log.e("co chay vao day ko?", "co")
                onCheckBoxClick(user, position, memberCheck.isChecked, memberPortion.text.toString(), "")

            })

//                notifyDataSetChanged()
            RxTextView.textChanges(memberPortion)
                .debounce(500, TimeUnit.MILLISECONDS)
                .skip(1)
                .map{charSequence ->  charSequence.toString()}
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{string ->
                    if (string.isNotEmpty()){
                        Log.e("string", string)
                        onPortionEdit(user, position, string)
                    }

                }



        }
    }

    inner class ExpenseMemberViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val memberCheck = view.findViewById<CheckBox>(R.id.member_checkbox)
        val memberName = view.findViewById<TextView>(R.id.member_name)
        val memberPortion = view.findViewById<EditText>(R.id.member_portion)
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