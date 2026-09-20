package kz.evn.otbor.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kz.evn.otbor.R
import kz.evn.otbor.RetrofitKlient
import kz.evn.otbor.VhodZapros
import kz.evn.otbor.databinding.FragmentVhodBinding

class VhodFragment : Fragment(R.layout.fragment_vhod) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentVhodBinding.bind(view)

        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                b.tvError.text = "Введите email и пароль"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val otvet = RetrofitKlient.servis.vhod(VhodZapros(email, pass))
                    if (otvet.isSuccessful && otvet.body()?.token != null) {
                        RetrofitKlient.token = otvet.body()!!.token!!
                        findNavController().navigate(R.id.ProfilFragment)
                    } else {
                        b.tvError.text = RetrofitKlient.poluchitOshibku(otvet, "Неверный логин или пароль")
                    }
                } catch (e: Exception) {
                    b.tvError.text = "Ошибка подключения к серверу"
                }
            }
        }

        b.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.RegistraciyaFragment)
        }

        b.tvForgot.setOnClickListener {
            findNavController().navigate(R.id.ZabylParolFragment)
        }

        b.btnTest1.setOnClickListener {
            b.etEmail.setText("testclient@fitzone.kz")
            b.etPassword.setText("FitZonePass.2026")
        }
    }
}
