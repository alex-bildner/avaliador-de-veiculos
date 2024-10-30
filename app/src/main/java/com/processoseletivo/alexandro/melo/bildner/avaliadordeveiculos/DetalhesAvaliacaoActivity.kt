package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class DetalhesAvaliacaoActivity : AppCompatActivity() {

    private lateinit var placaTextView: TextView
    private lateinit var chassiTextView: TextView
    private lateinit var marcaModeloTextView: TextView
    private lateinit var fotoPlacaTextView: TextView
    private lateinit var fotoChassiTextView: TextView
    private lateinit var fotoHodometroTextView: TextView
    private lateinit var fotoMotorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_avaliacao)

        placaTextView = findViewById(R.id.textPlaca)
        chassiTextView = findViewById(R.id.textChassi)
        marcaModeloTextView = findViewById(R.id.textMarcaModelo)

        fotoPlacaTextView = findViewById(R.id.textViewFotoPlaca)
        fotoChassiTextView = findViewById(R.id.textViewFotoChassi)
        fotoHodometroTextView = findViewById(R.id.textViewFotoHodometro)
        fotoMotorTextView = findViewById(R.id.textViewFotoMotor)

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

            carregarDetalhesAvaliacao(it)
        }
    }

    private fun carregarDetalhesAvaliacao(avaliacao: Avaliacao) {
        // Para a foto da placa
        val textViewFotoPlaca = findViewById<TextView>(R.id.textViewFotoPlaca)
        val buttonVisualizarPlaca = findViewById<Button>(R.id.buttonVisualizarPlaca)

        if (!avaliacao.fotoPlaca.isNullOrEmpty()) {
            textViewFotoPlaca.text = "Foto Placa: Clique para visualizar"
            buttonVisualizarPlaca.visibility = View.VISIBLE
            buttonVisualizarPlaca.setOnClickListener {
                abrirImagem(avaliacao.fotoPlaca)
            }
        } else {
            textViewFotoPlaca.text = "Foto Placa: Sem imagem"
            buttonVisualizarPlaca.visibility = View.GONE
        }

        // Para a foto do chassi
        val textViewFotoChassi = findViewById<TextView>(R.id.textViewFotoChassi)
        val buttonVisualizarChassi = findViewById<Button>(R.id.buttonVisualizarChassi)

        if (!avaliacao.fotoChassi.isNullOrEmpty()) {
            textViewFotoChassi.text = "Foto Chassi: Clique para visualizar"
            buttonVisualizarChassi.visibility = View.VISIBLE
            buttonVisualizarChassi.setOnClickListener {
                abrirImagem(avaliacao.fotoChassi)
            }
        } else {
            textViewFotoChassi.text = "Foto Chassi: Sem imagem"
            buttonVisualizarChassi.visibility = View.GONE
        }

        // Para a foto do hodômetro
        val textViewFotoHodometro = findViewById<TextView>(R.id.textViewFotoHodometro)
        val buttonVisualizarHodometro = findViewById<Button>(R.id.buttonVisualizarHodometro)

        if (!avaliacao.fotoHodometro.isNullOrEmpty()) {
            textViewFotoHodometro.text = "Foto Hodômetro: Clique para visualizar"
            buttonVisualizarHodometro.visibility = View.VISIBLE
            buttonVisualizarHodometro.setOnClickListener {
                abrirImagem(avaliacao.fotoHodometro)
            }
        } else {
            textViewFotoHodometro.text = "Foto Hodômetro: Sem imagem"
            buttonVisualizarHodometro.visibility = View.GONE
        }

        // Para a foto do motor
        val textViewFotoMotor = findViewById<TextView>(R.id.textViewFotoMotor)
        val buttonVisualizarMotor = findViewById<Button>(R.id.buttonVisualizarMotor)

        if (!avaliacao.fotoMotor.isNullOrEmpty()) {
            textViewFotoMotor.text = "Foto Motor: Clique para visualizar"
            buttonVisualizarMotor.visibility = View.VISIBLE
            buttonVisualizarMotor.setOnClickListener {
                abrirImagem(avaliacao.fotoMotor)
            }
        } else {
            textViewFotoMotor.text = "Foto Motor: Sem imagem"
            buttonVisualizarMotor.visibility = View.GONE
        }
    }

        private fun abrirImagem(caminho: String?) {
            if (caminho.isNullOrEmpty()) {
                Toast.makeText(this, "Sem imagem", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                val intent = Intent(Intent.ACTION_VIEW)

                if (caminho.startsWith("content://")) {
                    intent.setDataAndType(Uri.parse(caminho), "image/*")
                } else {
                    val file = File(caminho)
                    if (file.exists()) {
                        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
                        intent.setDataAndType(uri, "image/*")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } else {
                        Toast.makeText(this, "Arquivo da imagem não encontrado.", Toast.LENGTH_LONG).show()
                        return
                    }
                }

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Não há aplicativo disponível para abrir a imagem.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "A foto não pode ser carregada.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Ocorreu um erro ao abrir a imagem.", Toast.LENGTH_LONG).show()
            }
        }

}





