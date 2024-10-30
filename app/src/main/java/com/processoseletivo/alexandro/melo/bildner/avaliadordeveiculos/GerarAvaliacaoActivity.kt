package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

import android.Manifest
import android.app.AlertDialog
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
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GerarAvaliacaoActivity : AppCompatActivity() {

    private lateinit var placaInput: EditText
    private lateinit var chassiInput: EditText
    private lateinit var marcaModeloInput: EditText
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private lateinit var imageViewPlaca: ImageView
    private lateinit var btnPlaca: Button
    private lateinit var imageViewChassi: ImageView
    private lateinit var btnChassi: Button
    private lateinit var imageViewHodometro: ImageView
    private lateinit var btnHodometro: Button
    private lateinit var imageViewMotor: ImageView
    private lateinit var btnMotor: Button

    private var currentImageView: ImageView? = null

    private var lanternaLigada = false

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_CAMERA_PERMISSION = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerar_avaliacao)

        inicializarUI()
        configurarLanternaButton()
        configurarBotaoSalvar()
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id
                    break
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private fun inicializarUI() {
        placaInput = findViewById(R.id.placaInput)
        chassiInput = findViewById(R.id.chassiInput)
        marcaModeloInput = findViewById(R.id.marcaModeloInput)

        imageViewPlaca = findViewById<ImageView>(R.id.imageViewPlaca)
        btnPlaca = findViewById<Button>(R.id.btnPlaca).apply {
            setOnClickListener { onImageOptionSelected(imageViewPlaca) }
        }

        imageViewChassi = findViewById<ImageView>(R.id.imageViewChassi)
        btnChassi = findViewById<Button>(R.id.btnChassi).apply {
            setOnClickListener { onImageOptionSelected(imageViewChassi) }
        }

        imageViewHodometro = findViewById<ImageView>(R.id.imageViewHodometro)
        btnHodometro = findViewById<Button>(R.id.btnHodometro).apply {
            setOnClickListener { onImageOptionSelected(imageViewHodometro) }
        }

        imageViewMotor = findViewById<ImageView>(R.id.imageViewMotor)
        btnMotor = findViewById<Button>(R.id.btnMotor).apply {
            setOnClickListener { onImageOptionSelected(imageViewMotor) }
        }
    }

    private fun configurarLanternaButton() {
        findViewById<Button>(R.id.lanternaButton).apply {
            setOnClickListener {
                lanternaLigada = !lanternaLigada
                ligarLanterna(lanternaLigada)
                text = if (lanternaLigada) "Desligar Lanterna" else "Ligar Lanterna"
            }
        }
    }

    private fun onImageOptionSelected(targetImageView: ImageView) {
        val options = arrayOf("Tirar Foto", "Selecionar da Galeria")
        AlertDialog.Builder(this).apply {
            setTitle("Escolha uma opção")
            setItems(options) { _, which ->
                currentImageView = targetImageView
                when (which) {
                    0 -> verificarPermissaoECapturarImagem()
                    1 -> dispatchPickPictureIntent()
                }
            }
            show()
        }
    }

    private fun verificarPermissaoECapturarImagem() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun configurarBotaoSalvar() {
        findViewById<Button>(R.id.buttonSalvarAvaliacao).setOnClickListener {
            salvarAvaliacao()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun dispatchPickPictureIntent() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPictureIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && currentImageView != null) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    if (imageBitmap != null) {

                        val imagePath = saveImageToStorage(imageBitmap)
                        currentImageView?.setImageBitmap(imageBitmap)
                        currentImageView?.tag = imagePath
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        currentImageView?.setImageURI(imageUri)
                        currentImageView?.tag = imageUri.toString()
                    }
                }
            }
        }
    }

    private fun saveImageToStorage(bitmap: Bitmap): String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, imageFileName)

        try {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }

        return imageFile.absolutePath
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Permissão Necessária")
                    .setMessage("A permissão para acessar a câmera é necessária para tirar fotos. Por favor, ative-a nas configurações do aplicativo.")
                    .setPositiveButton("OK") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                        }
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
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

    private fun salvarAvaliacao() {
        val placa = placaInput.text.toString().trim()
        val marcaModelo = marcaModeloInput.text.toString().trim()
        val chassi = chassiInput.text.toString().trim()

        if (placa.isEmpty() || marcaModelo.isEmpty() || chassi.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val dataRegistro = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val novaAvaliacao = Avaliacao(
            UUID.randomUUID().toString(),
            placa,
            chassi,
            marcaModelo,
            dataRegistro,
            imageViewPlaca.tag?.toString() ?: "",
            imageViewChassi.tag?.toString() ?: "",
            imageViewHodometro.tag?.toString() ?: "",
            imageViewMotor.tag?.toString() ?: ""
        )

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

}

