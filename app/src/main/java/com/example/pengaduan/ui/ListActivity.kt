package com.example.pengaduan.ui

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pengaduan.Suara
import com.example.pengaduan.databinding.ActivityListBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ListActivity : AppCompatActivity() {
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityListBinding
    private lateinit var itemAdapter: ListAdapter
    private val listViewData = ArrayList<Suara>()
    private val firestore = FirebaseFirestore.getInstance()
    private val suaraCollectionRef = firestore.collection("suaras")
    //nampung id
    private var updateId = ""
    //nampung list dari data suara
    private val suaraListLiveData: MutableLiveData<List<Suara>> by lazy {
        MutableLiveData<List<Suara>>()
    }

    companion object{
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_ID = "extra_id"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        manager = LinearLayoutManager(this)
        itemAdapter = ListAdapter(this, listViewData) { item ->
            // Handle item click event
            // Misalnya, buka detail catatan atau lakukan tindakan lain
            updateId = item.id
            val IntentToForm = Intent(this, MainActivity2::class.java)
                .apply {
                    putExtra(EXTRA_NAME, item.nama)
                    putExtra(EXTRA_TITLE, item.judul)
                    putExtra(EXTRA_DESC, item.isi)
                    putExtra(EXTRA_ID, item.id)
                }
            startActivity(IntentToForm)
        }

        with(binding){
            btnAdd.setOnClickListener {
                val IntentToForm = Intent(this@ListActivity, MainActivity::class.java)
                startActivity(IntentToForm)
            }
            observeSuaras()
            getAllSuaras()
            listView.layoutManager = LinearLayoutManager(this@ListActivity)
            listView.adapter = itemAdapter

            }

//            listView.onItemLongClickListener =
//                AdapterView.OnItemLongClickListener { adapterView, view, i, id ->
//                    val item = adapterView.adapter.getItem(i) as Note
//                    delete(item)
//                    true
//                }
            }

    private fun getAllSuaras() {
        observeSuaraChanges()
    }

    private fun observeSuaraChanges(){
        suaraCollectionRef.addSnapshotListener{ snapshot, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for suara change: ", error)
                return@addSnapshotListener
            }
            val suaras = snapshot?.toObjects(Suara::class.java)
            if (suaras!= null) {
                suaraListLiveData.postValue(suaras)
            }
        }
    }
    //update list data dari data budget yg diperbarui
    private fun observeSuaras() {
        suaraListLiveData.observe(this) { suaras ->
            listViewData.clear()
            listViewData.addAll(suaras)
            itemAdapter.notifyDataSetChanged()

            Log.d("ListActivity", "Number of suaras: ${suaras.size}")
        }
    }

    override fun onResume() {
        super.onResume()
        getAllSuaras()
    }
}


