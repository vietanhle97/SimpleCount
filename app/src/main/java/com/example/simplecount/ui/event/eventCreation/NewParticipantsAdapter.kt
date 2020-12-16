package com.example.simplecount.ui.event.eventCreation

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.R
import com.example.simplecount.data.db.entity.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.view.clickable
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class NewParticipantsAdapter (
    private val participantList : HashMap<Int, User>,
    private val participantNameHolder: TextInputLayout,
    private val participantName : TextInputEditText
    ) : RecyclerView.Adapter<NewParticipantsAdapter.NewParticipantsViewHolder>() {


    private val emptyRequiredField = ObservableTransformer<String, String>{ observable ->
        observable.flatMap {input ->
            Observable.just(input).map { it.trim() }
                .filter { it.isNotEmpty() }
                .singleOrError()
                .onErrorResumeNext {
                    if (it is NoSuchElementException) {
                        Single.error(Exception("Please enter a valid name"))
                    } else {
                        Single.error(it)
                    }
                }
                .toObservable()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewParticipantsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.participant, parent, false)

        return NewParticipantsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return participantList.size
    }

    override fun onBindViewHolder(holder: NewParticipantsViewHolder, position: Int) {
        val user = participantList[position]
        if (user != null && position == 0){
            holder.delete.visibility = View.GONE
        }
        if (user != null) {
            holder.name.text = user.name

            holder.delete.setOnClickListener {
                participantList.remove(position)
                if (participantList.isEmpty()){
                    participantNameHolder.hint = "My name"
                } else {
                    participantNameHolder.hint = "Other Participant"
                }
                notifyDataSetChanged()
            }
        }
    }

    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }
        }
    }

    private fun validateField(user: User, holder:TextInputLayout, input: EditText){
        RxTextView.afterTextChangeEvents(input)
            .skipInitialValue()
            .map {
                holder.error = null
                it.view().text.toString() }
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(emptyRequiredField)
            .compose(retryWhenError {
                holder.error = it.message
            })
            .subscribe()
    }

    class NewParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.findViewById<TextView>(R.id.name)!!
        val delete = itemView.findViewById<ImageView>(R.id.delete)!!

    }
}