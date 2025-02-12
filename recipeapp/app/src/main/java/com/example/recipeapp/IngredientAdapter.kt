package com.example.recipeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientAdapter(
    private var ingredientes: List<Ingredient>,
    private val onItemClick: (Ingredient) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingrediente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingrediente = ingredientes[position]
        holder.bind(ingrediente)
    }

    override fun getItemCount(): Int = ingredientes.size

    fun actualizarIngredientes(nuevosIngredientes: List<Ingredient>) {
        ingredientes = nuevosIngredientes
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val cantidad: TextView = itemView.findViewById(R.id.txtCantidad)

        fun bind(ingrediente: Ingredient) {
            nombre.text = ingrediente.nombre
            cantidad.text = ingrediente.cantidad
            itemView.setOnClickListener { onItemClick(ingrediente) }
        }
    }
}