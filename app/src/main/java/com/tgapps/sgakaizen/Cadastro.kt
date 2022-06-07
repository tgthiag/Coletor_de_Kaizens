package com.tgapps.sgakaizen

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tgapps.sgakaizen.databinding.ActivityCadastroBinding
//TELA DE CADASTRO DE COLABORADORES E GESTORES

private lateinit var binding: ActivityCadastroBinding
class Cadastro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()

        //SELEÇÃO DE SETOR
        val showPopUp = PopupMenu(this, binding.editSetor)
        showPopUp.menu.add(Menu.NONE, 0, 0, "FlapDisk")
        showPopUp.menu.add(Menu.NONE, 1, 1, "Folhas")
        showPopUp.menu.add(Menu.NONE, 2, 2, "Discos pluma")
        showPopUp.menu.add(Menu.NONE, 3, 3, "Cintas")
        showPopUp.menu.add(Menu.NONE, 4, 4, "Rolos")
        showPopUp.menu.add(Menu.NONE, 5, 5, "Qualidade")
        showPopUp.menu.add(Menu.NONE, 6, 6, "EHS")
        showPopUp.menu.add(Menu.NONE, 7, 7, "Gestão de produção")
        showPopUp.menu.add(Menu.NONE, 8, 8, "Manutenção")
        showPopUp.menu.add(Menu.NONE, 9, 9, "Logística")
        showPopUp.menu.add(Menu.NONE, 10, 10, "PCP")
        showPopUp.menu.add(Menu.NONE, 11, 11, "RH")
        showPopUp.menu.add(Menu.NONE, 12, 12, "Eficiência industrial")
        showPopUp.menu.add(Menu.NONE, 13, 13, "GA/5S")
        showPopUp.menu.add(Menu.NONE, 14, 14, "Gerência")

        showPopUp.setOnMenuItemClickListener { menuItem ->
            val  id = menuItem.itemId
            binding.editSetor.setText(menuItem.title)
            false
        }
        binding.editSetor.setOnClickListener {
            showPopUp.show()
        }
        //CHAMANDO BANCO DE DADOS
        var db = LocalDatabase(this).writableDatabase
        var query = "SELECT * FROM kaizen"
        var cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        var opt1 = cursor.getString(4)

        //RadioGroup DE SELEÇÃO DE CARGO
        binding.rdg1.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            var radio: RadioButton = findViewById(checkedId)
            when (radio) {
                binding.rdgBtn1 -> {

                }
                binding.rdgBtn2 -> {
                    senhaGestorRh("Gestor",getString(R.string.senhaGestor))
                }
                binding.rdgBtn3 -> {
                    senhaGestorRh("RH",getString(R.string.senhaRh))
                }
                binding.rdgBtn4 -> {
                    senhaGestorRh("DEV",getString(R.string.senhaDesenvolvedor))
                }
            }
        }

        //BOTÃO CONFIRMAR FINALIZAR CADASTRO
        binding.btConfirm.setOnClickListener {
            var optVisualização : String
            if (binding.rdgBtn1.isChecked){
                optVisualização = "colaborador"
            }else if (binding.rdgBtn2.isChecked){
                optVisualização = "gestor"
            }else if (binding.rdgBtn3.isChecked){
                optVisualização = "rh"
            }else if (binding.rdgBtn4.isChecked){
                optVisualização = "dev"
            }else{optVisualização = "colaborador"}
            var name = binding.editName.text
            var fre = binding.editFre.text
            var setor = binding.editSetor.text
            var fabrica = binding.editFabrica.text
            if (name.isNullOrBlank() || fre.isNullOrBlank() || setor.isNullOrBlank() || fabrica.isNullOrBlank()){
                Toast.makeText(this,"Preencha todas as informações corretamente.", Toast.LENGTH_SHORT).show()
            }else{
                if (fabrica.toString().lowercase() == "cumbica"){
                    var db = LocalDatabase(this).writableDatabase
                    var cv = ContentValues()
                    cv.put(NAME,name.toString())
                    cv.put(FRE,fre.toString())
                    cv.put(SETOR,setor.toString())
                    cv.put(OPT1,optVisualização)
                    cv.put(OPT2,fabrica.toString().lowercase())
                    db.update(TABLE_NAME,cv,null,null)
                    Toast.makeText(this,"Informações Inseridas.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    this@Cadastro.finish()
                }else{
                    Toast.makeText(this,"Esta planta ainda não tem acesso ao aplicativo de kaizens.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun senhaGestorRh(cargo: String, hardCodedPass: String) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Digite sua senha de $cargo")


        val input = EditText(this)

        input.setHint("Senha fornecida pelo desenvolvedor.")
        input.transformationMethod = AsteriskTransformationMethod()
        builder.setView(input)
//        builder.setOnDismissListener{
//            binding.rdgBtn1.isChecked = true
//        }
        builder.setOnCancelListener {
            binding.rdgBtn1.isChecked = true
        }

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            var m_Text = input.text.toString()
            if (m_Text == hardCodedPass){

            }else{
                Toast.makeText(this,"Senha de $cargo incorreta.",Toast.LENGTH_LONG).show()
                binding.rdgBtn1.isChecked = true
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel()
            binding.rdgBtn1.isChecked = true
        })
        builder.show()
    }
}
//CARACTERES DE SENHA EM ASTERISCO
class AsteriskTransformationMethod : PasswordTransformationMethod() {

    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return object : CharSequence {
            override val length: Int
                get() = source.length

            override fun get(index: Int) = '*'

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return source.subSequence(startIndex, endIndex)
            }
        }
    }
}