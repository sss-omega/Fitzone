package kz.evn.otbor.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kz.evn.otbor.R
import kz.evn.otbor.RetrofitKlient
import kz.evn.otbor.SbrosParolyaZapros
import kz.evn.otbor.ZabylParolZapros
import kz.evn.otbor.databinding.FragmentZabylParolBinding

class ZabylParolFragment : Fragment(R.layout.fragment_zabyl_parol) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentZabylParolBinding.bind(view)

        b.btnFindQuestion.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                b.tvError.text = "Введите email"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val otvet = RetrofitKlient.servis.zabylParol(ZabylParolZapros(email))
                    if (otvet.isSuccessful && !otvet.body()?.security_question.isNullOrEmpty()) {
                        b.tvQuestion.text = otvet.body()!!.security_question
                        b.layoutAnswer.visibility = View.VISIBLE
                        b.tvError.text = ""
                    } else {
                        b.tvError.text = RetrofitKlient.poluchitOshibku(otvet, "Пользователь не найден")
                    }
                } catch (e: Exception) {
                    b.tvError.text = "Ошибка подключения к серверу"
                }
            }
        }

        b.btnCheckAnswer.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val ans = b.etAnswer.text.toString().trim()

            lifecycleScope.launch {
                try {
                    val otvet = RetrofitKlient.servis.sbrosParolya(SbrosParolyaZapros(email, ans))
                    if (otvet.isSuccessful && (otvet.body()?.verified == true || otvet.body()?.success == true)) {
                        b.layoutNewPass.visibility = View.VISIBLE
                        b.btnCheckAnswer.visibility = View.GONE
                        b.tvError.text = ""
                    } else {
                        b.tvError.text = RetrofitKlient.poluchitOshibku(otvet, "Неверный ответ на секретный вопрос")
                    }
                } catch (e: Exception) {
                    b.tvError.text = "Ошибка подключения к серверу"
                }
            }
        }

        b.btnSavePass.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val ans = b.etAnswer.text.toString().trim()
            val pass = b.etNewPass.text.toString().trim()
            val conf = b.etConfirmPass.text.toString().trim()

            if (pass.length < 4 || pass != conf) {
                b.tvError.text = "Пароли не совпадают (мин. 4 символа)"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val otvet = RetrofitKlient.servis.sbrosParolya(SbrosParolyaZapros(email, ans, pass))
                    if (otvet.isSuccessful && otvet.body()?.success == true) {
                        Toast.makeText(requireContext(), "Пароль успешно изменен!", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } else {
                        b.tvError.text = RetrofitKlient.poluchitOshibku(otvet, "Ошибка смены пароля")
                    }
                } catch (e: Exception) {
                    b.tvError.text = "Ошибка подключения к серверу"
                }
            }
        }

        b.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        b.btnTest2.setOnClickListener {
            b.etEmail.setText("testclient2@fitzone.kz")
            b.etAnswer.setText("Абай")
            b.btnFindQuestion.performClick()
        }
    }
}
