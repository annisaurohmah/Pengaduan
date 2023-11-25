package com.example.pengaduan.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pengaduan.Suara
import com.example.pengaduan.databinding.ListItemBinding

class ListAdapter(private val context: Context, private val listData: List<Suara>, private val onClickData: (Suara) -> Unit) :
    RecyclerView.Adapter<ListAdapter.ItemDataViewHolder>() {

    inner class ItemDataViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Suara) {
            with(binding) {
                // Lakukan binding data ke tampilan
                noteNama.text = "Nama Pengadu : " + data.nama
                noteJudul.text = "Judul Aduan : " + data.judul
                noteIsi.text = "Isi Aduan : " + data.isi

                // Atur listener untuk onClick
                itemView.setOnClickListener {
                    onClickData(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDataViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemDataViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size
}
