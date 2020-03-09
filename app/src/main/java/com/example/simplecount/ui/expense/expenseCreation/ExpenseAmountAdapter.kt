package com.example.simplecount.ui.expense.expenseCreation

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.R
import com.example.simplecount.data.db.entity.User
import kotlinx.android.synthetic.main.amount.*
import kotlin.math.round
import kotlin.math.roundToLong

class ExpenseAmountAdapter (
    private val portions : HashMap<Int, User>,
    private val onAmountEdit : (User, String, Int) -> Unit,
    private val updateAmount : (User, Double, Int) -> Unit) : RecyclerView.Adapter<ExpenseAmountAdapter.ExpenseAmountViewHolder>(){

    private var totalAmount : Double = 0.0

    fun setTotalAmount(amount : Double){
        this.totalAmount = amount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseAmountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.amount, parent, false)
        return ExpenseAmountViewHolder(view)
    }

    override fun getItemCount(): Int {
        return portions.size
    }

    override fun onBindViewHolder(holder: ExpenseAmountViewHolder, position: Int) {
        val user = portions[position]!!
        val smallestPortion = calculateSmallestPortionAmount(portions)

        holder.apply {
            if (user.participate){
                if (user.portion != 0){
                    val tmp = round(smallestPortion * user.portion * 100)/100
                    amount.setText(tmp.toString())
                    updateAmount(user, tmp, position)
                } else {
                    amount.setText(user.payAmount.toString())
                }
            } else {
                Log.e("in o day", "why???")
                updateAmount(user, 0.0, position)
                amount.setText(0.0.toString())
            }

            amount.setOnFocusChangeListener(View.OnFocusChangeListener { view, b ->
                if (!b){
                    onAmountEdit(user, amount.text.toString(), position)
                    Log.e("focus", "focus lost")
                } else {
                    Log.e("focus", "focused")
                }

//                notifyDataSetChanged()

            })
        }

    }


    private fun calculateTotalPortion(portions : HashMap<Int, User>) : Int{
        var res = 0
        for ((k,v) in portions) {
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

        for ((k,v) in portions){
            if (v.portion == 0) res += v.payAmount
        }

        return (totalAmount - res)/totalPortion
    }

    class ExpenseAmountViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val amount = view.findViewById<EditText>(R.id.amount)
    }
}