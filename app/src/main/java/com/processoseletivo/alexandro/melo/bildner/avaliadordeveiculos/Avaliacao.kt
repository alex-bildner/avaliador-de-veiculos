package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

data class Avaliacao(
    val id: String,
    val placa: String,
    val chassi: String,
    val marcaModelo: String,
    val dataRegistro: String,
    val imagemUri: String
)

