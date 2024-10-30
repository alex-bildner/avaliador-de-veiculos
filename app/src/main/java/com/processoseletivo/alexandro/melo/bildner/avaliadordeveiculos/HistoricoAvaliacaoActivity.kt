package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class HistoricoAvaliacaoActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var avaliacaoList: List<Avaliacao>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico_avaliacao)

        listView = findViewById(R.id.listView)


        val sharedPreferences = getSharedPreferences("avaliacoes", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("avaliacoes_lista", null)
        val type = object : TypeToken<MutableList<Avaliacao>>() {}.type
        avaliacaoList = gson.fromJson(json, type) ?: mutableListOf()

        val adapter = AvaliacaoAdapter(this, avaliacaoList)
        listView.adapter = adapter
    }
}
