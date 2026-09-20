package kz.evn.otbor.ui

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kz.evn.otbor.R
import kz.evn.otbor.RegistraciyaZapros
import kz.evn.otbor.RetrofitKlient
import kz.evn.otbor.databinding.FragmentRegistraciyaBinding

class RegistraciyaFragment : Fragment(R.layout.fragment_registraciya) {

    private val voprosy = arrayOf(
        "Кличка первого питомца?",
        "Девичья фамилия матери?",
        "Название вашего первого учебного заведения?"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentRegistraciyaBinding.bind(view)

        b.spQuestion.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, voprosy)

        b.btnRegister.setOnClickListener {
            val name = b.etName.text.toString().trim()
            val last = b.etLastName.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPass.text.toString().trim()
            val confirm = b.etConfirm.text.toString().trim()
            val question = b.spQuestion.selectedItem.toString()
            val answer = b.etAnswer.text.toString().trim()
            val sub = b.cbSub.isChecked

            if (name.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty() || answer.isEmpty()) {
                b.tvError.text = "Заполните все обязательные поля"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                b.tvError.text = "Некорректный формат email"
                return@setOnClickListener
            }
            if (pass.length < 4 || pass != confirm) {
                b.tvError.text = "Пароли не совпадают (мин. 4 символа)"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val zapros = RegistraciyaZapros(
                        first_name = name,
                        last_name = last,
                        email = email,
                        password = pass,
                        security_question = question,
                        security_answer = answer,
                        is_subscribed_to_newsletter = sub
                    )
                    val otvet = RetrofitKlient.servis.registraciya(zapros)
                    if (otvet.isSuccessful && otvet.body()?.token != null) {
                        RetrofitKlient.token = otvet.body()!!.token!!
                        findNavController().navigate(R.id.ProfilFragment)
                    } else {
                        b.tvError.text = RetrofitKlient.poluchitOshibku(otvet, "Ошибка регистрации")
                    }
                } catch (e: Exception) {
                    b.tvError.text = "Ошибка подключения к серверу"
                }
            }
        }

        b.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
