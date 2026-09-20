package kz.evn.otbor.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kz.evn.otbor.DobavitKlubZapros
import kz.evn.otbor.Klub
import kz.evn.otbor.ObnovitProfilZapros
import kz.evn.otbor.R
import kz.evn.otbor.RetrofitKlient
import kz.evn.otbor.databinding.FragmentProfilBinding

class ProfilFragment : Fragment(R.layout.fragment_profil) {

    private lateinit var b: FragmentProfilBinding
    private var vseKluby = listOf<Klub>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b = FragmentProfilBinding.bind(view)

        zagruzitProfil()
        zagruzitIzbrannyeKluby()
        zagruzitPosescheniya()
        zagruzitVseKluby()

        b.btnEdit.setOnClickListener {
            rezhimRedaktirovaniya(true)
        }

        b.btnCancelEdit.setOnClickListener {
            rezhimRedaktirovaniya(false)
            zagruzitProfil()
        }

        b.btnSaveProfile.setOnClickListener {
            val name = b.etName.text.toString().trim()
            val last = b.etLastName.text.toString().trim()
            val phone = b.etPhone.text.toString().trim()
            val sub = b.switchSub.isChecked

            lifecycleScope.launch {
                try {
                    val zapros = ObnovitProfilZapros(
                        first_name = name,
                        last_name = last,
                        phone = phone,
                        is_subscribed_to_newsletter = sub
                    )
                    val otvet = RetrofitKlient.servis.obnovitProfil(zapros)
                    if (otvet.isSuccessful) {
                        Toast.makeText(requireContext(), "Профиль сохранен", Toast.LENGTH_SHORT).show()
                        rezhimRedaktirovaniya(false)
                        zagruzitProfil()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                }
            }
        }

        b.btnAddClub.setOnClickListener {
            pokazatDialogDobavleniyaKluba()
        }

        b.btnChangeAvatar.setOnClickListener {
            val avatars = arrayOf("/images/default_avatar.png", "/images/aigerim_n.jpg", "/images/rustem_a.jpg")
            val nazvaniya = arrayOf("Стандартный FitZone", "Айгерим (Фиолетовый)", "Рустем (Синий)")
            AlertDialog.Builder(requireContext())
                .setTitle("Выберите фото")
                .setItems(nazvaniya) { _, i ->
                    lifecycleScope.launch {
                        try {
                            RetrofitKlient.servis.obnovitProfil(ObnovitProfilZapros(profile_image = avatars[i]))
                            zagruzitProfil()
                        } catch (e: Exception) {
                        }
                    }
                }.show()
        }

        b.btnLogout.setOnClickListener {
            RetrofitKlient.token = ""
            findNavController().navigate(R.id.VhodFragment)
        }
    }

    private fun rezhimRedaktirovaniya(vklucheno: Boolean) {
        b.etName.isEnabled = vklucheno
        b.etLastName.isEnabled = vklucheno
        b.etPhone.isEnabled = vklucheno
        b.switchSub.isEnabled = vklucheno
        b.btnChangeAvatar.visibility = if (vklucheno) View.VISIBLE else View.GONE
        b.indicatorEdit.visibility = if (vklucheno) View.VISIBLE else View.GONE
        b.layoutEditButtons.visibility = if (vklucheno) View.VISIBLE else View.GONE
        b.btnEdit.visibility = if (vklucheno) View.GONE else View.VISIBLE
    }

    private fun zagruzitProfil() {
        lifecycleScope.launch {
            try {
                val otvet = RetrofitKlient.servis.poluchitProfil()
                if (otvet.isSuccessful && otvet.body() != null) {
                    val u = otvet.body()!!
                    b.tvFullName.text = "${u.first_name ?: ""} ${u.last_name ?: ""}".trim()
                    b.tvEmail.text = u.email ?: ""
                    b.tvSub.text = "Абонемент: ${u.subscription_type ?: "Standard"} (Только просмотр)"
                    b.etName.setText(u.first_name ?: "")
                    b.etLastName.setText(u.last_name ?: "")
                    b.etEmail.setText(u.email ?: "")
                    b.etPhone.setText(u.phone ?: "")
                    b.switchSub.isChecked = u.is_subscribed_to_newsletter == true
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun zagruzitIzbrannyeKluby() {
        lifecycleScope.launch {
            try {
                val otvet = RetrofitKlient.servis.poluchitIzbrannyeKluby()
                if (otvet.isSuccessful && otvet.body() != null) {
                    b.containerClubs.removeAllViews()
                    for (c in otvet.body()!!) {
                        val item = layoutInflater.inflate(R.layout.item_klub, b.containerClubs, false)
                        item.findViewById<TextView>(R.id.tvName).text = c.name
                        item.findViewById<TextView>(R.id.tvAddress).text = "${c.city ?: ""}, ${c.address ?: ""}".trim(' ', ',')

                        val btnMain = item.findViewById<Button>(R.id.btnMain)
                        val tvMain = item.findViewById<TextView>(R.id.tvMainTag)

                        if (c.is_main) {
                            tvMain.visibility = View.VISIBLE
                            btnMain.visibility = View.GONE
                        } else {
                            tvMain.visibility = View.GONE
                            btnMain.visibility = View.VISIBLE
                            btnMain.setOnClickListener {
                                lifecycleScope.launch {
                                    RetrofitKlient.servis.sdelatOsnovnymKlubom(c.club_id)
                                    zagruzitIzbrannyeKluby()
                                }
                            }
                        }

                        item.findViewById<View>(R.id.btnDelete).setOnClickListener {
                            lifecycleScope.launch {
                                RetrofitKlient.servis.udalitIzbrannyKlub(c.club_id)
                                zagruzitIzbrannyeKluby()
                            }
                        }

                        b.containerClubs.addView(item)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun zagruzitPosescheniya() {
        lifecycleScope.launch {
            try {
                val otvet = RetrofitKlient.servis.poluchitPosescheniya()
                if (otvet.isSuccessful && otvet.body() != null) {
                    b.containerVisits.removeAllViews()
                    for (v in otvet.body()!!) {
                        val item = layoutInflater.inflate(R.layout.item_poseschenie, b.containerVisits, false)
                        item.findViewById<TextView>(R.id.tvWorkout).text = v.workout_name ?: ""
                        item.findViewById<TextView>(R.id.tvDate).text = "${v.visit_date ?: ""} • ${v.club_name ?: ""}"
                        item.findViewById<TextView>(R.id.tvStatus).text = v.status ?: ""
                        b.containerVisits.addView(item)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun zagruzitVseKluby() {
        lifecycleScope.launch {
            try {
                val otvet = RetrofitKlient.servis.poluchitVseKluby()
                if (otvet.isSuccessful && otvet.body() != null) {
                    vseKluby = otvet.body()!!
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun pokazatDialogDobavleniyaKluba() {
        val names = vseKluby.map { it.name }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Выберите зал для добавления")
            .setItems(names) { _, i ->
                val id = vseKluby[i].id
                lifecycleScope.launch {
                    try {
                        RetrofitKlient.servis.dobavitIzbrannyKlub(DobavitKlubZapros(club_id = id))
                        zagruzitIzbrannyeKluby()
                    } catch (e: Exception) {
                    }
                }
            }.show()
    }
}
