package com.tayyib.savelocation.Adapter



import com.tayyib.savelocation.roomdb.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tayyib.savelocation.databinding.RecyclerRowBinding

class Adapter(val list: ArrayList<Address>) : RecyclerView.Adapter<Adapter.RecyclerViewHolder>(){

    class RecyclerViewHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        holder.binding.recyclerRow.text = list[position].fullAddress.toString()

        holder.itemView.setOnClickListener {



        }

    }

    fun besinListesiniGuncelle(newList: ArrayList<Address>){



        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

}