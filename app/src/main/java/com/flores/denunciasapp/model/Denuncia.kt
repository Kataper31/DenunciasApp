
package com.flores.denunciasapp.model

data class Denuncia(
    val id: Int? = null,
    val dni: String,
    val fecha: String? = null,
    val tipo: String,
    val direccion: String,
    val descripcion: String,
    val estado: Int = 1
)
