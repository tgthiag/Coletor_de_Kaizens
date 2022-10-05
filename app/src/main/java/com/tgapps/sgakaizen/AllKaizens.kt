package com.tgapps.sgakaizen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.tgapps.sgakaizen.databinding.ActivityAllKaizensBinding
import com.tgapps.sgakaizen.databinding.ActivityMainBinding

private lateinit var binding: ActivityAllKaizensBinding
class AllKaizens : AppCompatActivity() {
    lateinit var fireRef : DatabaseReference
    lateinit var recycler : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAllKaizensBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        var db = LocalDatabase(this).writableDatabase
        var query = "SELECT * FROM kaizen"
        var cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        var setorUser = cursor.getString(3)

        getKaizenData("pendentes",setorUser)


        var fireDb = FirebaseDatabase.getInstance()
        fireRef = fireDb.reference

        recycler = binding.container2
        recycler.layoutManager = LinearLayoutManager(this)

        binding.btTodos.setOnClickListener {
            getKaizenData("todos",setorUser)
        }
        binding.btPendentes.setOnClickListener {
            getKaizenData("pendentes",setorUser)
        }
        binding.btAprovados.setOnClickListener {
            getKaizenData("aceitos",setorUser)
        }
    }

    private fun getKaizenData(switch: String, userSetor : String) {
        fireRef = FirebaseDatabase.getInstance().reference.child("ideias").child("2022-04")
        fireRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var listaDeIdeias = arrayListOf<KaizenData>()
                    for (kaizenSnapshot in snapshot.children){

                        val kaizen = kaizenSnapshot.getValue(KaizenData::class.java)
                        listaDeIdeias.add(kaizen!!)
                    }
                    var listaFiltrada : ArrayList<KaizenData>
                    if (switch == "todos"){
                        listaFiltrada = listaDeIdeias
                        recycler.adapter = KaizenAdapter(this@AllKaizens,listaFiltrada,false)
                    }else if (switch == "pendentes"){
                        listaFiltrada = ArrayList<KaizenData>()
                        listaDeIdeias.filterTo(listaFiltrada,{ it.aprovada!!.contains("Em análise") || it.implementada!!.contains("Em análise") || it.aprovada != it.implementada})
                        var listafiltrada2 : ArrayList<KaizenData> = ArrayList<KaizenData>()
                        listaFiltrada.filterTo(listafiltrada2,{userSetor == it.destino})

                        recycler.adapter = KaizenAdapter(this@AllKaizens,listafiltrada2,true)
                        Toast.makeText(this@AllKaizens,"Kaizens pendentes destinados a $userSetor",Toast.LENGTH_LONG).show()
                    }
                    else if (switch == "aceitos"){
                        listaFiltrada = ArrayList<KaizenData>()
                        listaDeIdeias.filterTo(listaFiltrada,{ it.aprovada!!.contains("Sim") && it.implementada!!.contains("Sim")})
                        recycler.adapter = KaizenAdapter(this@AllKaizens,listaFiltrada,false)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}