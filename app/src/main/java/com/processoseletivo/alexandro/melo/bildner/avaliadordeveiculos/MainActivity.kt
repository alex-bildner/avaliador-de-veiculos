package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var botaoGerarAvaliacao: Button
    private lateinit var botaoHistorico: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        botaoGerarAvaliacao = findViewById(R.id.botaoGerarAvaliacao)
        botaoHistorico = findViewById(R.id.botaoHistorico)

        botaoGerarAvaliacao.setOnClickListener {
            val intent = Intent(this, GerarAvaliacaoActivity::class.java)
            startActivity(intent)
        }

        botaoHistorico.setOnClickListener {
            val intent = Intent(this, HistoricoAvaliacaoActivity::class.java)
            startActivity(intent)
        }
    }
}
