package com.example.simplecount.ui.balance


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.simplecount.R
import com.example.simplecount.data.db.ExpenseDatabase
import com.example.simplecount.data.db.entity.Expense
import com.example.simplecount.databinding.FragmentBalanceBinding
import com.example.simplecount.util.Coroutines
import kotlin.math.round

/**
 * A simple [Fragment] subclass.
 */
class BalanceFragment : Fragment() {

    lateinit var binding : FragmentBalanceBinding
    var amount = arrayListOf<Double>()
    var paymentMap : ArrayList<Pair<Int, Pair<Int, Double>>> = arrayListOf()
    var userAmountMap : HashMap<Int, Pair<String, Double>> = hashMapOf()

    companion object {
        fun newInstance(eventId : Long, numUsers : Int) : BalanceFragment {
            val fragment = BalanceFragment()
            val args = Bundle()
            eventId.let { args.putLong("eventId", eventId)}
            numUsers.let { args.putInt("numUsers", numUsers) }
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val id = arguments?.getLong("eventId") as Long
        val numUsers = arguments?.getInt("numUsers") as Int

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_balance, container, false)
        val application = requireNotNull(this.activity).application
        val expenseDao = ExpenseDatabase.geInstance(application).expenseDao

        val balanceViewModelFactory = BalanceViewModelFactory(id, expenseDao)
        val balanceViewModel = ViewModelProviders.of(this, balanceViewModelFactory).get(BalanceViewModel::class.java)

        binding.balanceViewModel = balanceViewModel
        binding.lifecycleOwner = this
        bindUI(numUsers)
        return binding.root
    }

    private fun bindUI(numUsers: Int) = Coroutines.main {

        initializeUserAmountMap(numUsers)

        initBalanceRecyclerView()

        initDebtRecyclerView(numUsers)

    }

    private fun getMinInd(arr : ArrayList<Double>) : Int {
        var minInd = 0
        for (i : Int in 0 until arr.size){
            if (arr[i] < arr[minInd]){
                minInd = i
            }
        }
        return minInd
    }

    private fun getMaxInd(arr : ArrayList<Double>) : Int {
        var maxInd = 0
        for (i : Int in 0 until arr.size){
            if (arr[i] > arr[maxInd]){
                maxInd = i
            }
        }
        return maxInd

    }

    private fun minOf2(d1 : Double, d2 : Double) : Double {
        return if (d1 < d2) d1 else d2
    }

    private fun minCostFlow(result : ArrayList<Pair<Int, Pair<Int, Double>>>, amount : ArrayList<Double>, n : Int) {

        val maxCredit = getMaxInd(amount)
        val maxDebit  = getMinInd(amount)

        if (round(amount[maxCredit]*100)/100 <= 0.02 && round(amount[maxDebit]*100)/100 <= 0.02){
            return
        }

        val min_ = round(minOf2(-amount[maxDebit], amount[maxCredit])*100)/100
        amount[maxCredit] -= min_
        amount[maxDebit] += min_

        result.add(Pair(maxDebit, Pair(maxCredit, min_)))
        minCostFlow(result, amount, n)

    }

    private fun createAmountArrayList(amount : ArrayList<Double>, expenses : ArrayList<Expense>, numUsers: Int){
        if (amount.isEmpty()){
            for (i : Int in 0..numUsers){
                amount.add(0.0)
            }
        }

        if (expenses.isNotEmpty()){
            for (expense : Expense in expenses){
                for ((k,v) in expense.participants){
                    amount[k+1] += v.payAmount
                }
            }
        }

    }

    private fun initializeUserAmountMap(numUsers: Int){
        binding.balanceViewModel!!.expenses.observe(viewLifecycleOwner, Observer {expenses ->
            userAmountMap.clear()
            if (expenses.isNotEmpty()){
                binding.separator.visibility = View.VISIBLE
                for (expense : Expense in expenses){
                    for ((k,v) in expense.participants){
                        if (userAmountMap[k] == null){
                            userAmountMap[k] = Pair(v.name, v.payAmount)
                        } else {
                            userAmountMap[k] = Pair(v.name, userAmountMap[k]!!.second + v.payAmount)
                        }

                    }
                }
            } else {
                binding.separator.visibility = View.GONE
            }
        })
    }

    private fun initializeAmountArrayList(numUsers: Int){
        binding.balanceViewModel!!.expenses.observe(viewLifecycleOwner, Observer {expenses ->
            paymentMap.clear()
            amount.clear()
            createAmountArrayList(amount, ArrayList(expenses), numUsers)
            minCostFlow(paymentMap, amount, numUsers)
        })
    }

    private fun initBalanceRecyclerView(){

        binding.balanceViewModel!!.expenses.observe(viewLifecycleOwner, Observer{expenses ->
            binding.balanceRecycler.layoutManager = LinearLayoutManager(context)
            binding.balanceRecycler.setHasFixedSize(true)
            val adapter = UserBalanceAdapter(userAmountMap)
            binding.balanceRecycler.adapter = adapter

        })

    }

    private fun initDebtRecyclerView(numUsers: Int){
        binding.balanceViewModel!!.expenses.observe(viewLifecycleOwner, Observer{expenses ->
            initializeAmountArrayList(numUsers)
            binding.debtRecycler.layoutManager = LinearLayoutManager(context)
            binding.debtRecycler.setHasFixedSize(true)
            val adapter = UserDebtAdapter(userAmountMap, paymentMap)
            binding.debtRecycler.adapter = adapter
            adapter.notifyDataSetChanged()

        })
    }

}
