
package com.flores.denunciasapp

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flores.denunciasapp.adapter.DenunciaAdapter
import com.flores.denunciasapp.model.Denuncia
import com.flores.denunciasapp.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: DenunciaAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var txtVacio: TextView
    private lateinit var btnCargarMas: Button

    private var offset = 0
    private val limite = 5
    private var pantallaActual = "inicio"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (pantallaActual != "inicio") {
                        mostrarInicio()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )

        mostrarInicio()
    }

    // PANTALLA PRINCIPAL
    private fun mostrarInicio() {
        pantallaActual = "inicio"
        setContentView(R.layout.activity_main)

        val rvDenuncias =
            findViewById<RecyclerView>(R.id.rvDenuncias)

        val etDni = findViewById<EditText>(R.id.etDni)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnMostrarTodas =
            findViewById<Button>(R.id.btnMostrarTodas)

        btnCargarMas = findViewById(R.id.btnCargarMas)
        progressBar = findViewById(R.id.progressBar)
        txtVacio = findViewById(R.id.txtVacio)

        adapter = DenunciaAdapter { denuncia ->
            mostrarDetalle(denuncia.id)
        }

        rvDenuncias.layoutManager = LinearLayoutManager(this)
        rvDenuncias.adapter = adapter

        btnBuscar.setOnClickListener {
            val dni = etDni.text.toString().trim()

            if (dni.length != 8 || !dni.all { it.isDigit() }) {
                etDni.error = "Ingrese un DNI de 8 digitos"
            } else {
                buscarPorDni(dni)
            }
        }

        btnRegistrar.setOnClickListener {
            mostrarRegistro()
        }

        btnMostrarTodas.setOnClickListener {
            etDni.text.clear()
            offset = 0
            cargarDenuncias(0, false)
        }

        btnCargarMas.setOnClickListener {
            cargarDenuncias(offset + limite, true)
        }

        cargarDenuncias(0, false)
    }

    // LISTAR DENUNCIAS PAGINADAS
    private fun cargarDenuncias(
        nuevoOffset: Int,
        agregar: Boolean
    ) {
        progressBar.visibility = View.VISIBLE

        ApiClient.service.listar(nuevoOffset, limite)
            .enqueue(object : Callback<List<Denuncia>> {

                override fun onResponse(
                    call: Call<List<Denuncia>>,
                    response: Response<List<Denuncia>>
                ) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val lista = response.body().orEmpty()

                        if (agregar) {
                            adapter.agregarDenuncias(lista)
                        } else {
                            adapter.actualizarLista(lista)
                        }

                        offset = nuevoOffset

                        txtVacio.visibility =
                            if (adapter.itemCount == 0)
                                View.VISIBLE
                            else View.GONE

                        btnCargarMas.visibility =
                            if (lista.size == limite)
                                View.VISIBLE
                            else View.GONE

                    } else {
                        mensaje("Error al listar denuncias")
                    }
                }

                override fun onFailure(
                    call: Call<List<Denuncia>>,
                    t: Throwable
                ) {
                    progressBar.visibility = View.GONE
                    mensaje("No se pudo conectar con la API")
                }
            })
    }

    // BUSCAR DENUNCIAS POR DNI
    private fun buscarPorDni(dni: String) {
        progressBar.visibility = View.VISIBLE

        ApiClient.service.buscarPorDni(dni)
            .enqueue(object : Callback<List<Denuncia>> {

                override fun onResponse(
                    call: Call<List<Denuncia>>,
                    response: Response<List<Denuncia>>
                ) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val lista = response.body().orEmpty()
                        adapter.actualizarLista(lista)

                        btnCargarMas.visibility = View.GONE
                        txtVacio.visibility =
                            if (lista.isEmpty()) View.VISIBLE
                            else View.GONE
                    } else {
                        mensaje("Error en la busqueda")
                    }
                }

                override fun onFailure(
                    call: Call<List<Denuncia>>,
                    t: Throwable
                ) {
                    progressBar.visibility = View.GONE
                    mensaje("Error de conexion")
                }
            })
    }

    // PANTALLA DE REGISTRO
    private fun mostrarRegistro() {
        pantallaActual = "registro"
        val contenedor = crearPantalla("Registrar denuncia")

        val etDni = EditText(this).apply {
            hint = "DNI (8 digitos)"
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(8))
        }
        contenedor.addView(etDni)

        agregarTexto(contenedor, "Tipo de denuncia")

        val tipos = listOf(
            "ROBO", "ASALTO", "VANDALISMO", "OTRO"
        )

        val spinner = Spinner(this)
        val opciones = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tipos
        )
        spinner.adapter = opciones
        contenedor.addView(spinner)

        val etDireccion = EditText(this).apply {
            hint = "Direccion del incidente"
        }
        contenedor.addView(etDireccion)

        val etDescripcion = EditText(this).apply {
            hint = "Descripcion de la denuncia"
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
            gravity = android.view.Gravity.TOP
        }
        contenedor.addView(etDescripcion)

        val btnGuardar = Button(this).apply {
            text = "Guardar denuncia"
        }
        contenedor.addView(btnGuardar)

        btnGuardar.setOnClickListener {
            val dni = etDni.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val tipo = spinner.selectedItem.toString()

            if (dni.length != 8 || !dni.all { it.isDigit() }) {
                etDni.error = "DNI invalido"
                return@setOnClickListener
            }

            if (direccion.isEmpty()) {
                etDireccion.error = "Ingrese la direccion"
                return@setOnClickListener
            }

            if (descripcion.isEmpty()) {
                etDescripcion.error = "Ingrese la descripcion"
                return@setOnClickListener
            }

            val denuncia = Denuncia(
                dni = dni,
                tipo = tipo,
                direccion = direccion,
                descripcion = descripcion
            )

            btnGuardar.isEnabled = false

            ApiClient.service.registrar(denuncia)
                .enqueue(object : Callback<Denuncia> {

                    override fun onResponse(
                        call: Call<Denuncia>,
                        response: Response<Denuncia>
                    ) {
                        btnGuardar.isEnabled = true

                        if (response.isSuccessful) {
                            mensaje("Denuncia registrada correctamente")
                            mostrarInicio()
                        } else {
                            mensaje("Error al registrar denuncia")
                        }
                    }

                    override fun onFailure(
                        call: Call<Denuncia>,
                        t: Throwable
                    ) {
                        btnGuardar.isEnabled = true
                        mensaje("No se pudo conectar con la API")
                    }
                })
        }
    }

    // CONSULTAR DETALLE POR ID
    private fun mostrarDetalle(id: Int?) {
        if (id == null) return

        progressBar.visibility = View.VISIBLE

        ApiClient.service.obtenerPorId(id)
            .enqueue(object : Callback<Denuncia> {

                override fun onResponse(
                    call: Call<Denuncia>,
                    response: Response<Denuncia>
                ) {
                    progressBar.visibility = View.GONE

                    val denuncia = response.body()

                    if (response.isSuccessful && denuncia != null) {
                        dibujarDetalle(denuncia)
                    } else {
                        mensaje("Denuncia no encontrada")
                    }
                }

                override fun onFailure(
                    call: Call<Denuncia>,
                    t: Throwable
                ) {
                    progressBar.visibility = View.GONE
                    mensaje("Error de conexion")
                }
            })
    }

    // PANTALLA DE DETALLE
    private fun dibujarDetalle(denuncia: Denuncia) {
        pantallaActual = "detalle"
        val contenedor = crearPantalla("Detalle de denuncia")

        agregarTexto(contenedor, "Codigo: ${denuncia.id}")
        agregarTexto(contenedor, "DNI: ${denuncia.dni}")
        agregarTexto(contenedor, "Fecha: ${denuncia.fecha}")
        agregarTexto(contenedor, "Tipo: ${denuncia.tipo}")
        agregarTexto(contenedor, "Direccion: ${denuncia.direccion}")
        agregarTexto(
            contenedor,
            "Descripcion: ${denuncia.descripcion}"
        )

        val estado =
            if (denuncia.estado == 1) "PENDIENTE"
            else "ATENDIDA"

        agregarTexto(contenedor, "Estado: $estado")

        if (denuncia.estado == 1) {
            val btnAtender = Button(this).apply {
                text = "Marcar como atendida"
            }
            contenedor.addView(btnAtender)

            btnAtender.setOnClickListener {
                val id = denuncia.id ?: return@setOnClickListener
                btnAtender.isEnabled = false

                ApiClient.service.atender(id)
                    .enqueue(object : Callback<Denuncia> {

                        override fun onResponse(
                            call: Call<Denuncia>,
                            response: Response<Denuncia>
                        ) {
                            if (response.isSuccessful) {
                                mensaje("Denuncia atendida")
                                val actualizada = response.body()
                                if (actualizada != null) {
                                    dibujarDetalle(actualizada)
                                } else {
                                    mostrarInicio()
                                }
                            } else {
                                btnAtender.isEnabled = true
                                mensaje("Error al atender denuncia")
                            }
                        }

                        override fun onFailure(
                            call: Call<Denuncia>,
                            t: Throwable
                        ) {
                            btnAtender.isEnabled = true
                            mensaje("Error de conexion")
                        }
                    })
            }
        }
    }

    // CREAR PANTALLAS SECUNDARIAS
    private fun crearPantalla(titulo: String): LinearLayout {
        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            setBackgroundColor(Color.parseColor("#F5F7FA"))
        }

        val scroll = ScrollView(this)
        scroll.addView(contenido)
        setContentView(scroll)

        val encabezado = TextView(this).apply {
            text = titulo
            textSize = 24f
            setTextColor(Color.parseColor("#183153"))
            setPadding(0, 0, 0, dp(16))
        }
        contenido.addView(encabezado)

        val btnVolver = Button(this).apply {
            text = "Volver al inicio"
        }
        contenido.addView(btnVolver)

        btnVolver.setOnClickListener {
            mostrarInicio()
        }

        return contenido
    }

    private fun agregarTexto(
        contenedor: LinearLayout,
        texto: String
    ) {
        val vista = TextView(this).apply {
            text = texto
            textSize = 17f
            setTextColor(Color.DKGRAY)
            setPadding(0, dp(10), 0, dp(10))
        }
        contenedor.addView(vista)
    }

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }

    private fun mensaje(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show()
    }
}
