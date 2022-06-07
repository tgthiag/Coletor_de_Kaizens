package com.tgapps.sgakaizen

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class KaizenAdapter(var ctx: Context,var dadosFire : ArrayList<KaizenData>,var editKaizen : Boolean) : RecyclerView.Adapter<KaizenAdapter.viewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.ideias_item,parent,false)
        val holder =  viewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        var ideia: KaizenData = dadosFire[position]

        holder.nome.text = ideia.nome
        holder.fre.text = ideia.fre
        holder.setor.text = ideia.setor
        holder.area.text = ideia.destino
        holder.aprovada.text = ideia.aprovada
        holder.implementada.text = ideia.implementada
        holder.problema.text = ideia.problema
        holder.solucao.text = ideia.solucao
        var view: View = holder.itemView

        val database = FirebaseDatabase.getInstance()
        if (editKaizen){
        view.setOnClickListener {
            val showPopUp = PopupMenu(ctx, view)
            showPopUp.menu.add(Menu.NONE, 0, 0, "Marcar como aprovado")
            showPopUp.menu.add(Menu.NONE, 1, 1, "Marcar como não aprovado")
            showPopUp.menu.add(Menu.NONE, 2, 2, "Marcar como implementado")
            showPopUp.menu.add(Menu.NONE, 3, 3, "Marcar como não implementado")
            showPopUp.menu.add(Menu.NONE, 4, 4, "Destinar para Qualidade")
            showPopUp.menu.add(Menu.NONE, 5, 5, "Destinar para EHS")
            showPopUp.menu.add(Menu.NONE, 6, 6, "Destinar para Eficiência Industrial")
            showPopUp.menu.add(Menu.NONE, 7, 7, "Destinar para GA/5S")

            showPopUp.setOnMenuItemClickListener { menuItem ->
                val  id = menuItem.itemId
                if (id==0){
                    var mRef = database.reference.child("ideias").child("2022-04").child(ideia.key!!).child("aprovada")
                    mRef.setValue("Sim")
                }
                else if(id==1){
                    var mRef = database.reference.child("ideias").child("2022-04").child(ideia.key!!).child("aprovada")
                    mRef.setValue("Não")
                }
                else if (id==2){
                    var mRef = database.reference.child("ideias").child("2022-04").child(ideia.key!!).child("implementada")
                    mRef.setValue("Sim")
                }
                else if (id==3){
                    var mRef = database.reference.child("ideias").child("2022-04").child(ideia.key!!).child("implementada")
                    mRef.setValue("Não")
                }
                else if (id==4){
                    var mRef = database.reference.child("ideias").child("2022-04").child(ideia.key!!).child("destino")
                    mRef.setValue("Qualidade")
                }
                else if (id==5){
                    var mRef = database.reference.child("ideias").child("2022-04").child(ideia.key!!).child("destino")
                    mRef.setValue("EHS")
                }
                else if (id==6){
                    var mRef = database.reference.child("ideias").child("2022-04").child(ideia.key!!).child("destino")
                    mRef.setValue("Eficiência Industrial")
                }
                else if (id==7){
                    var mRef = database.reference.child("ideias").child("2022-04").child(ideia.key!!).child("destino")
                    mRef.setValue("GA/5S")
                }
                false
            }
            view.setOnClickListener {
                showPopUp.show()
            }
        }
        }
        if (holder.aprovada.text.toString() == "Sim" && holder.implementada.text.toString() == "Sim"){
            view.setBackgroundColor(ContextCompat.getColor(ctx,R.color.greenTr))
        }else if (holder.aprovada.text.toString() == "Não" && holder.implementada.text.toString() == "Não"){
            view.setBackgroundColor(ContextCompat.getColor(ctx,R.color.red_tr))
        }

    }

    override fun getItemCount(): Int {
        return  dadosFire.size
    }

    class viewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var nome: TextView = view.findViewById(R.id.nome)
        var fre: TextView = view.findViewById(R.id.fre)
        var setor: TextView = view.findViewById(R.id.setor)
        var area: TextView = view.findViewById(R.id.area)
        var aprovada: TextView =view.findViewById(R.id.aprovado)
        var implementada: TextView = view.findViewById(R.id.implementado)
        var problema: TextView = view.findViewById(R.id.problema)
        var solucao: TextView = view.findViewById(R.id.solucao)
    }
}