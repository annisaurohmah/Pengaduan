package com.example.pengaduan.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.example.pengaduan.Suara
import com.example.pengaduan.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            btnAdd.setOnClickListener {
                val name = txtName.text.toString()
                val title = txtTitle.text.toString()
                val desc = txtDesc.text.toString()
                val newSuara = Suara(
                    nama = name,
                    judul = title,
                    isi = desc,
                )
                addData(newSuara)
                startActivity(Intent(this@MainActivity, ListActivity::class.java))
            }
            btnBack.setOnClickListener {
                startActivity(Intent(this@MainActivity, ListActivity::class.java))
            }

        }
    }

    private fun getAllBudgets(){
        observeBudgetChanges()
    }

    private fun observeBudgetChanges(){
        suaraCollectionRef.addSnapshotListener{ snapshot, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget change: ", error)
                return@addSnapshotListener
            }
            val suaras = snapshot?.toObjects(Suara::class.java)
            if (suaras!= null) {
                suaraListLiveData.postValue(suaras)
            }
        }
    }
    // dihandle untuk kondisi sukses, akan mereturn docRef
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
        suara.id = updateId
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