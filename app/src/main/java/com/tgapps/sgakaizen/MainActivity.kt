package com.tgapps.sgakaizen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.tgapps.sgakaizen.databinding.ActivityMainBinding


@IgnoreExtraProperties
data class KaizenData(
    val key: String? = null,
    val nome: String? = null,
    val fre: String? = null,
    val setor: String? = null,
    var destino: String? = null,
    val problema: String? = null,
    val solucao: String? = null,
    val planta: String? = null,
    val data: String? = null,
    var aprovada: String? = null,
    var implementada : String? = null,
    val feedback: String? = null,
    val rh: Boolean? = null,
    val userDismiss: Boolean? = null
) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
private lateinit var binding : ActivityMainBinding
class MainActivity : AppCompatActivity() {
    //INICIANDO VARIÁVEIS GLOBAIS
//    lateinit var listaDeIdeias : ArrayList<KaizenData>
    lateinit var fireRef : DatabaseReference
    lateinit var recycler : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //INICIANDO DATABASE LOCAL
        LocalDatabase(this).initializeRow()
        val mainClass = this
        val classdb = LocalDatabase(mainClass)
        val db = classdb.writableDatabase

        //VERIFICANDO NO BANCO DE DADOS SE AS INFORMAÇÕES DO USUÁRIO ESTÃO PREENCHIDAS
        var query = "SELECT * FROM $TABLE_NAME"
        var cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        var nameColumn = cursor.getString(1)
        var freColumn = cursor.getString(2)
        var setorColumn = cursor.getString(3)
        var optVisualização = cursor.getString(4)

        //SE NÃO PREENCHIDAS, IR PRA TELA DE CADASTRO
        if (nameColumn == "" || freColumn == "" || setorColumn == ""){
            intent = Intent(this, Cadastro::class.java)
            this.finish()
            startActivity(intent)
        }else{
            //SE PREENCHIDA, POPULAR OS DADOS NA TELA PRINCIPAL
            binding.textNome.text = nameColumn
            binding.textFre.text = freColumn
            binding.textSetor.text = setorColumn
        }

        //VERIFICANDO SE É GESTOR, RH OU DEVELOPER, PARA MOSTRAR PAINEL DE GESTOR
        if (optVisualização == "rh" || optVisualização == "gestor" || optVisualização == "dev"){
            binding.btSecret.visibility = View.VISIBLE
        }

        //BOTÃO DE SELEÇÃO KAIZEN
        val popupAreaKaizen = PopupMenu(this, binding.editAreaKaizen)
        popupAreaKaizen.menu.add(Menu.NONE, 0, 0, "Qualidade")
        popupAreaKaizen.menu.add(Menu.NONE, 1, 1, "EHS")
        popupAreaKaizen.menu.add(Menu.NONE, 2, 2, "Eficiência industrial")
        popupAreaKaizen.menu.add(Menu.NONE, 3, 3, "GA/5S")
//        popupAreaKaizen.menu.add(Menu.NONE, 2, 2, "5S")

        popupAreaKaizen.setOnMenuItemClickListener { menuItem ->
            val  id = menuItem.itemId
            binding.editAreaKaizen.setText(menuItem.title)
            false
        }
        binding.editAreaKaizen.setOnClickListener {
            popupAreaKaizen.show()
        }

        //BOTÃO DE ENVIAR KAIZEN PARA O BANCO DE DADOS
        binding.btEnviarkaizen.setOnClickListener {
            var problem = binding.editProblema.text
            var soluc = binding.editSolucao.text
            var destino = binding.editAreaKaizen.text
            if (problem.isNullOrBlank() || soluc.isNullOrBlank() || destino.isNullOrBlank()){
                Toast.makeText(this,"Escreva o problema, solução, e selecione a área destino antes de enviar seu kaizen.",Toast.LENGTH_LONG).show()
            }else{
                val database = FirebaseDatabase.getInstance()

                var nome = binding.textNome.text.toString();
                var setor = binding.textSetor.text.toString()
                var fre = binding.textFre.text.toString()
                var mRef = database.reference.child("ideias").child("2022-04").push()
                var key = mRef.key
                var gravarDados = KaizenData(key,nome,fre,setor,destino.toString(),problem.toString(), soluc.toString(),"cumbica",GetDate().getCurrentDate(),"Em análise","Em análise","", rh = false, userDismiss = false)
                mRef.setValue(gravarDados)
                Toast.makeText(this,"Seu kaizen foi enviado com sucesso, aguarde a análise.",Toast.LENGTH_LONG).show()
                problem.clear()
                soluc.clear()
                destino.clear()
            }
        }

        //BOTÃO PARA GESTORES, TELA SECRETA
        binding.btSecret.setOnClickListener {
            startActivity(Intent(this,AllKaizens::class.java))
        }
        //GERANDO A LISTA DE KAIZENS PREENCHIDOS PELO USER
//        listaDeIdeias = arrayListOf<KaizenData>()
        getKaizenData()

        //VERIFICANDO FIREBASE REALTIME DATABASE
        var fireDb = FirebaseDatabase.getInstance()
        fireRef = fireDb.reference
//        fireRef.child("ideias").child("2022-04").get().addOnSuccessListener {
//            Log.i("firebase", "Got value ${it.value}")
//        }.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }

        //INICIANDO RECYCLER VIEW DAS INFORMAÇÕES DO USER
        recycler = binding.container2
        recycler.layoutManager = LinearLayoutManager(this)



    }
    //COLOCANDO DADOS DO FIREBASE EM UMA ARRAY, ATUALIZANDO EM TEMPO REAL
    fun getKaizenData() {
        fireRef = FirebaseDatabase.getInstance().reference.child("ideias").child("2022-04")
        fireRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var listaDeIdeias = arrayListOf<KaizenData>()
                    var anotherLis : ArrayList<KaizenData> = arrayListOf(KaizenData())
                    for (kaizenSnapshot in snapshot.children){


                        val kaizen = kaizenSnapshot.getValue(KaizenData::class.java)
                        listaDeIdeias.add(kaizen!!)
                        //GERANDO OUTRA LISTA PARA GUARDAR KAIZENS FILTRADOS POR USER ATUAL
                        anotherLis = arrayListOf<KaizenData>()
                        var table: List<KaizenData>
                        listaDeIdeias.filterTo(anotherLis,{ it.nome!!.lowercase().contains(binding.textNome.text.toString().lowercase())})
                        table = anotherLis.distinctBy { it.problema }
                        recycler.adapter = KaizenAdapter(this@MainActivity,table as ArrayList<KaizenData>,false)

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Verifique sua conexão com a internet",Toast.LENGTH_LONG).show()
            }
        })
    }
}