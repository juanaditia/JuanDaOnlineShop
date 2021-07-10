package com.example.juandaonlineshop.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.juandaonlineshop.MainActivity
import com.example.juandaonlineshop.R
import com.example.juandaonlineshop.app.ApiConfig
import com.example.juandaonlineshop.helper.SharedPref
import com.example.juandaonlineshop.model.ResponOnkir
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {

    lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        s = SharedPref(this)

        btn_register.setOnClickListener {
            register()
        }

    }

    fun register() {
        if (edt_nama.text.isEmpty()) {
            edt_nama.error = "Kolom Nama tidak boleh kosong"
            edt_nama.requestFocus()
            return
        } else if (edt_email.text.isEmpty()) {
            edt_email.error = "Kolom Email tidak boleh kosong"
            edt_email.requestFocus()
            return
        } else if (edt_phone.text.isEmpty()) {
            edt_phone.error = "Kolom Nomor Telepon tidak boleh kosong"
            edt_phone.requestFocus()
            return
        } else if (edt_password.text.isEmpty()) {
            edt_password.error = "Kolom Password tidak boleh kosong"
            edt_password.requestFocus()
            return
        }

        pb.visibility = View.VISIBLE

        ApiConfig.instanceRetrofit.register(
            edt_nama.text.toString(),
            edt_email.text.toString(),
            edt_phone.text.toString(),
            edt_password.text.toString()
        ).enqueue(object : retrofit2.Callback<ResponOnkir> {

            override fun onResponse(call: Call<ResponOnkir>, response: Response<ResponOnkir>) {
                // Handel ketika berhasil
                pb.visibility = View.GONE
                val respon = response.body()!!

                if (respon.success == 1) {
                    // berhasil
                    s.setStatusLogin(true)

                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    // gagal
                    Toast.makeText(this@RegisterActivity, "Error:" + respon.message, Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResponOnkir>, t: Throwable) {
                // Handel ketika gagal
                pb.visibility = View.GONE
                Toast.makeText(this@RegisterActivity, "Error:" + t.message, Toast.LENGTH_SHORT).show()
            }

        })

    }
}