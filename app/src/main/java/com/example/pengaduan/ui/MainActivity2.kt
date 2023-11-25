package com.example.pengaduan.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.pengaduan.Suara
import com.example.pengaduan.databinding.ActivityMain2Binding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    //firebase
    private val firestore = FirebaseFirestore.getInstance()

    private val suaraCollectionRef = firestore.collection("suaras")
    //nampung id
    private var updateId = ""
    //nampung list dari data suara
    private val suaraListLiveData: MutableLiveData<List<Suara>> by lazy {
        MutableLiveData<List<Suara>>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){

            val name = intent.getStringExtra(ListActivity.EXTRA_NAME)
            val title = intent.getStringExtra(ListActivity.EXTRA_TITLE)
            val desc = intent.getStringExtra(ListActivity.EXTRA_DESC)
            val id = intent.getStringExtra(ListActivity.EXTRA_ID)
            Log.d("MainActivity3", "Intent ID: $id")


            txtName.setText(name)
            txtTitle.setText(title)
            txtDesc.setText(desc)

            btnUpdate.setOnClickListener {
                val updatedData = Suara(
                    id = id.toString(),
                    nama = txtName.getText().toString(),
                    judul = txtTitle.getText().toString(),
                    isi = txtDesc.getText().toString()
                )
                updateId = id.toString()
                updateData(updatedData)

                startActivity(Intent(this@MainActivity2, ListActivity::class.java))
            }

            btnDelete.setOnClickListener {
                val suaraToDelete = id?.let { it1 -> Suara(id = it1, "","","") }
                if (suaraToDelete != null) {
                    deleteData(suaraToDelete)
                }
                startActivity(Intent(this@MainActivity2, ListActivity::class.java))
            }
            btnBack.setOnClickListener {
                startActivity(Intent(this@MainActivity2, ListActivity::class.java))
            }


        }


    }
//    private fun getAllNotes() {
//        mNotesDao.allNotes.observe(this) { notes ->
//            val adapter: ArrayAdapter<Note> = ArrayAdapter<Note>(
//                this,
//                android.R.layout.simple_list_item_1, notes
//            )
//            binding.listView.adapter = adapter
//        }
//    }

    private fun addData(suara: Suara) {
        suaraCollectionRef.add(suara)
            .addOnSuccessListener { docRef ->
                val createSuaraId = docRef.id
                //id nya di update sesuai id yang berhasil
                suara.id = createSuaraId
                docRef.set(suara)
                    .addOnFailureListener{
                        Log.d("MainActivity", "Error update suara id", it)
                    }
                resetForm()
            }
            .addOnFailureListener{
                Log.d("MainActivity", "Error add suara", it)
            }
    }

    private fun updateData(suara: Suara){
        Log.d("MainActivity3", "Update ID: $updateId")
        suaraCollectionRef.document(updateId).set(suara)
            .addOnFailureListener{
                Log.d("MainActivity", "Error update data suara", it)
            }
    }

    private fun deleteData(suara: Suara){
        if (suara.id.isEmpty()) {
            Log.d("MainActivity", "Error delete data empty ID", return)
        }

        suaraCollectionRef.document(suara.id).delete()
            .addOnFailureListener{
                Log.d("MainActivity", "Error delete data budget")
            }
    }
    //    override fun onResume() {
//        super.onResume()
//        getAllNotes()
//    }
    private fun resetForm() {
        with(binding){
            txtName.setText("")
            txtTitle.setText("")
            txtDesc.setText("")
        }
    }
}