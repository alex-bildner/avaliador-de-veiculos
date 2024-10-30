package com.processoseletivo.alexandro.melo.bildner.avaliadordeveiculos

data class Avaliacao(
    val id: String,
    val placa: String,
    val chassi: String,
    val marcaModelo: String,
    val dataRegistro: String,
    val fotoPlaca: String,
    val fotoChassi: String,
    val fotoHodometro: String,
    val fotoMotor: String
)

