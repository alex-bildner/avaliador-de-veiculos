package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView


class AvaliacaoAdapter(private val context: Context, private val avaliacaoList: List<Avaliacao>) : BaseAdapter() {
    override fun getCount(): Int = avaliacaoList.size

    override fun getItem(position: Int): Any = avaliacaoList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_avaliacao, parent, false)

        val placaTextView: TextView = view.findViewById(R.id.placaTextView)
        val chassiTextView: TextView = view.findViewById(R.id.chassiTextView)
        val marcaModeloTextView: TextView = view.findViewById(R.id.marcaModeloTextView)
        val dataRegistroTextView: TextView = view.findViewById(R.id.dataRegistroTextView)
        val visualizarButton: Button = view.findViewById(R.id.visualizarButton)

        val avaliacao = avaliacaoList[position]

        placaTextView.text = avaliacao.placa
        chassiTextView.text = avaliacao.chassi
        marcaModeloTextView.text = avaliacao.marcaModelo
        dataRegistroTextView.text = avaliacao.dataRegistro

        visualizarButton.setOnClickListener {

            val intent = Intent(context, DetalhesAvaliacaoActivity::class.java)
            intent.putExtra("avaliacao_id", avaliacao.id)
            context.startActivity(intent)
        }

        return view
    }
}
