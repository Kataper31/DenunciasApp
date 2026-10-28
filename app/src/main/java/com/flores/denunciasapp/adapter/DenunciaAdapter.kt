
package com.flores.denunciasapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flores.denunciasapp.R
import com.flores.denunciasapp.model.Denuncia

class DenunciaAdapter(
    private val onDetalleClick: (Denuncia) -> Unit
) : RecyclerView.Adapter<DenunciaAdapter.DenunciaViewHolder>() {

    private val denuncias = mutableListOf<Denuncia>()

    class DenunciaViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val txtCodigoTipo: TextView =
            view.findViewById(R.id.txtCodigoTipo)

        val txtDni: TextView =
            view.findViewById(R.id.txtDni)

        val txtDireccion: TextView =
            view.findViewById(R.id.txtDireccion)

        val txtEstado: TextView =
            view.findViewById(R.id.txtEstado)

        val btnDetalle: Button =
            view.findViewById(R.id.btnDetalle)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DenunciaViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_denuncia, parent, false)

        return DenunciaViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DenunciaViewHolder,
        position: Int
    ) {
        val denuncia = denuncias[position]

        holder.txtCodigoTipo.text =
            "Denuncia #${denuncia.id} - ${denuncia.tipo}"

        holder.txtDni.text = "DNI: ${denuncia.dni}"

        holder.txtDireccion.text = denuncia.direccion

        holder.txtEstado.text =
            if (denuncia.estado == 1) "Pendiente"
            else "Atendida"

        holder.btnDetalle.setOnClickListener {
            onDetalleClick(denuncia)
        }
    }

    override fun getItemCount(): Int {
        return denuncias.size
    }

    fun actualizarLista(nuevaLista: List<Denuncia>) {
        val cantidadAnterior = denuncias.size
        denuncias.clear()

        if (cantidadAnterior > 0) {
            notifyItemRangeRemoved(0, cantidadAnterior)
        }

        agregarDenuncias(nuevaLista)
    }

    fun agregarDenuncias(nuevas: List<Denuncia>) {
        val inicio = denuncias.size
        denuncias.addAll(nuevas)

        if (nuevas.isNotEmpty()) {
            notifyItemRangeInserted(inicio, nuevas.size)
        }
    }
}
