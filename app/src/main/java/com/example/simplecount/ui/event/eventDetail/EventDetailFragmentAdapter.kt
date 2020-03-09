package com.example.simplecount.ui.event.eventDetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.simplecount.ui.balance.BalanceFragment
import com.example.simplecount.ui.expense.expenseDisplay.ExpenseFragment

class EventDetailFragmentAdapter(
    val eventId : Long,
    val numUsers : Int,
    manager: FragmentManager
   ) : FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private val fragmentTitleList: ArrayList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
        when (position){
            0 -> {
                return ExpenseFragment.newInstance(eventId)
            }

            1 -> {
                return BalanceFragment.newInstance(eventId, numUsers)
            }
        }
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String){
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }
}