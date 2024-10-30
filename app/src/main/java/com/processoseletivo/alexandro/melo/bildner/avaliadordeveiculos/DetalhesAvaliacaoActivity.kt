package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DetalhesAvaliacaoActivity : AppCompatActivity() {

    private lateinit var imagemImageView: ImageView
    private lateinit var placaTextView: TextView
    private lateinit var chassiTextView: TextView
    private lateinit var marcaModeloTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_avaliacao)

        imagemImageView = findViewById(R.id.imagemImageView)
        placaTextView = findViewById(R.id.textPlaca)
        chassiTextView = findViewById(R.id.textChassi)
        marcaModeloTextView = findViewById(R.id.textMarcaModelo)


        val avaliacaoId = intent.getStringExtra("avaliacao_id")
        if (avaliacaoId != null) {
            mostrarDetalhes(avaliacaoId)
        }
    }

    private fun mostrarDetalhes(avaliacaoId: String) {
        val sharedPreferences = getSharedPreferences("avaliacoes", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("avaliacoes_lista", null)
        val type = object : TypeToken<MutableList<Avaliacao>>() {}.type
        val listaAvaliacoes: MutableList<Avaliacao> = gson.fromJson(json, type) ?: mutableListOf()

        val avaliacao = listaAvaliacoes.find { it.id == avaliacaoId }

        avaliacao?.let {
            placaTextView.text = it.placa
            chassiTextView.text = it.chassi
            marcaModeloTextView.text = it.marcaModelo

        }
    }
}