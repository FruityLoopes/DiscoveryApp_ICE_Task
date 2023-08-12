package com.example.discoveryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class UserAdapter : ListAdapter<User, UserAdapter.UserAdapter>(UserViewHolder()){
    class UserAdapter (view : View): RecyclerView.ViewHolder(view) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter
    {
        val inflater = LayoutInflater.from(parent.context)
        return com.example.discoveryapp.UserAdapter.UserAdapter(inflater.inflate(R.layout.user_layout, parent , false))
    }
    override fun onBindViewHolder(holder: UserAdapter, position: Int)
    {
        var user = getItem(position)
        holder.itemView.findViewById<TextView>(R.id.text1).text =  "Client: "+ user.Name + " " + user.Surname
        holder.itemView.findViewById<TextView>(R.id.text2).text =  "PlanID: "+ user.PlanID.toString()
        holder.itemView.findViewById<TextView>(R.id.text8).text =  "Amount: R"+user.Amount.toString()
    }
    class UserViewHolder : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean
        {
            return areItemsTheSame(oldItem,newItem)
        }

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean
        {
            return oldItem.PlanID == newItem.PlanID
        }
    }
}


