package com.example.juandaonlineshop.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.juandaonlineshop.R
import com.example.juandaonlineshop.helper.Helper
import com.example.juandaonlineshop.model.Produk
import com.example.juandaonlineshop.room.MyDatabase
import com.example.juandaonlineshop.util.Config
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class AdapterKeranjang(var activity: Activity, var data:ArrayList<Produk>, var listener: Listeners): RecyclerView.Adapter<AdapterKeranjang.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val layout = view.findViewById<CardView>(R.id.layout)

        val btnTambah = view.findViewById<ImageView>(R.id.btn_tambah)
        val btnKurang = view.findViewById<ImageView>(R.id.btn_kurang)
        val btnDelete = view.findViewById<ImageView>(R.id.btn_delete)

        val tvJumlah = view.findViewById<TextView>(R.id.tv_jml_angka)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //validasi penamaan
        val produk = data[position]
        val harga = Integer.valueOf(produk.harga)

        holder.tvNama.text = produk.name.toString()
        holder.tvHarga.text = Helper().gantiRupiah(harga * produk.jumlah)

        var jumlah = data[position].jumlah
        holder.tvJumlah.text = jumlah.toString()

//        holder.imgProduk.setImageResource(data[position].image)
        val image = Config.produkUrl + data[position].image
        Picasso.get()
            .load(image)
            .placeholder(R.drawable.product)
            .error(R.drawable.product)
            .into(holder.imgProduk)

        holder.btnTambah.setOnClickListener {
            jumlah++
            produk.jumlah = jumlah
            update(produk)

            holder.tvJumlah.text = jumlah.toString()
            holder.tvHarga.text = Helper().gantiRupiah(harga * jumlah)
        }

        holder.btnKurang.setOnClickListener {
            if (jumlah <= 1) return@setOnClickListener
            jumlah--
            produk.jumlah = jumlah
            update(produk)

            holder.tvJumlah.text = jumlah.toString()
            holder.tvHarga.text = Helper().gantiRupiah(harga * jumlah)
        }

        holder.btnDelete.setOnClickListener {
            delete(produk)
            listener.onDelete(position)
        }
    }

    interface Listeners{
        fun onUpdate()
        fun onDelete(position: Int)
    }

    private fun update(data: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().update(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listener.onUpdate()
            })
    }

    private fun delete(data: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().delete(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
            })
    }


}