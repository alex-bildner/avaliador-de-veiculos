package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GerarAvaliacaoActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private lateinit var imageView: ImageView
    private lateinit var placaInput: EditText
    private lateinit var chassiInput: EditText
    private lateinit var marcaModeloInput: EditText

    private var lanternaLigada = false

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_CAMERA_PERMISSION = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerar_avaliacao)

        placaInput = findViewById(R.id.placaInput)
        chassiInput = findViewById(R.id.chassiInput)
        marcaModeloInput = findViewById(R.id.marcaModeloInput)
        imageView = findViewById(R.id.imageView)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                val isRearFacing = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
                if (hasFlash && isRearFacing) {
                    cameraId = id
                    break
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        findViewById<Button>(R.id.tirarFotoButton).setOnClickListener {
            verificarPermissaoCamera()
        }

        findViewById<Button>(R.id.selecionarFotoButton).setOnClickListener {
            abrirGaleria()
        }

        findViewById<Button>(R.id.buttonSalvarAvaliacao).setOnClickListener {
            salvarAvaliacao()
        }

        findViewById<Button>(R.id.lanternaButton).setOnClickListener {
            ligarLanterna(false)
        }

        val lanternaButton = findViewById<Button>(R.id.lanternaButton)
        lanternaButton.setOnClickListener {
            lanternaLigada = !lanternaLigada
            ligarLanterna(lanternaLigada)
            lanternaButton.text = if (lanternaLigada) "Desligar Lanterna" else "Ligar Lanterna"
        }


        val salvarButton = findViewById<Button>(R.id.buttonSalvarAvaliacao)
        salvarButton.setOnClickListener {
            salvarAvaliacao()
        }
    }

    private fun verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            abrirCamera()
        }
    }

    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun ligarLanterna(ligar: Boolean) {
        cameraId?.let {
            try {
                cameraManager.setTorchMode(it, ligar)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(imageBitmap)
                    val imagePath = saveImageToInternalStorage(imageBitmap)
                    imageView.tag = imagePath
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        try {
                            Glide.with(this)
                                .load(selectedImageUri)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.error_image)
                                .into(imageView)
                        } catch (e: Exception) {
                            Log.e("ErroGlide", "Erro ao carregar a imagem: ${e.message}")
                        }
                    } else {
                        Log.e("SelecionarImagem", "URI da imagem selecionada é nula.")
                    }
                }
            }
        }
    }

    fun salvarAvaliacao() {
        val placa = placaInput.text.toString()
        val marcaModelo = marcaModeloInput.text.toString()
        val chassi = chassiInput.text.toString().trim()
        val imagemUri = imageView.tag?.toString() ?: ""

        if (placa.isEmpty() || chassi.isEmpty() || marcaModelo.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val dataRegistro = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        val novaAvaliacao = Avaliacao(UUID.randomUUID().toString(), placa, chassi, marcaModelo, dataRegistro, imagemUri)

        val sharedPreferences = getSharedPreferences("avaliacoes", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val json = sharedPreferences.getString("avaliacoes_lista", null)
        val type = object : TypeToken<MutableList<Avaliacao>>() {}.type
        val listaAvaliacoes: MutableList<Avaliacao> = gson.fromJson(json, type) ?: mutableListOf()

        listaAvaliacoes.add(novaAvaliacao)
        editor.putString("avaliacoes_lista", gson.toJson(listaAvaliacoes))
        editor.apply()
        Toast.makeText(this, "Avaliação salva com sucesso!", Toast.LENGTH_SHORT).show()

        finish()
    }


    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val filename = "avaliacao_image_${System.currentTimeMillis()}.png"
        val file = File(filesDir, filename)
        try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirCamera()
                } else {
                    Toast.makeText(this, "Permissão para acessar a câmera negada", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_IMAGE_PICK -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirGaleria()
                } else {

                    Toast.makeText(this, "Permissão para acessar a galeria negada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

