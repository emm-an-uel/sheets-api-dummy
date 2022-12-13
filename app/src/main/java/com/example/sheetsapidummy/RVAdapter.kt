package com.example.sheetsapidummy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVAdapter (
    private val listOfPersons: ArrayList<Person>
): RecyclerView.Adapter<RVAdapter.NewViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewViewHolder { // inflate the layout for task_rv_item.xml
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rv_item,
            parent, false
        )

        return NewViewHolder(itemView)
    }

    class NewViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAge: TextView = itemView.findViewById(R.id.tvAge)
    }

    override fun onBindViewHolder(holder: NewViewHolder, position: Int) { // populate views with data from list
        holder.tvName.text = listOfPersons[position].name
        holder.tvAge.text = listOfPersons[position].age.toString()
    }

    override fun getItemCount(): Int { // this function is required
        return listOfPersons.size
    }
}
