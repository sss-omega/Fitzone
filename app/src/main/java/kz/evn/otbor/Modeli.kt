package kz.evn.otbor

data class VhodZapros(
    val email: String,
    val password: String
)

data class VhodOtvet(
    val success: Boolean = false,
    val token: String? = null,
    val message: String? = null,
    val error: String? = null,
    val user: Polzovatel? = null
)

data class RegistraciyaZapros(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val security_question: String,
    val security_answer: String,
    val phone: String? = null,
    val is_subscribed_to_newsletter: Boolean = false
)

data class RegistraciyaOtvet(
    val success: Boolean = false,
    val token: String? = null,
    val message: String? = null,
    val error: String? = null,
    val user: Polzovatel? = null
)

data class ZabylParolZapros(
    val email: String
)

data class ZabylParolOtvet(
    val email: String? = null,
    val security_question: String? = null,
    val error: String? = null
)

data class SbrosParolyaZapros(
    val email: String,
    val security_answer: String,
    val new_password: String? = null
)

data class SbrosParolyaOtvet(
    val success: Boolean = false,
    val verified: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

data class Polzovatel(
    val id: Int? = null,
    val email: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val phone: String? = null,
    val profile_image: String? = null,
    val is_subscribed_to_newsletter: Boolean? = null,
    val subscription_type: String? = null,
    val preferred_workout_type: String? = null,
    val error: String? = null
)

data class ObnovitProfilZapros(
    val first_name: String? = null,
    val last_name: String? = null,
    val phone: String? = null,
    val profile_image: String? = null,
    val is_subscribed_to_newsletter: Boolean? = null,
    val password: String? = null
)

data class Klub(
    val id: Int,
    val name: String,
    val address: String? = null,
    val city: String? = null
)

data class IzbrannyKlub(
    val id: Int? = null,
    val club_id: Int,
    val name: String,
    val address: String? = null,
    val city: String? = null,
    val is_main: Boolean = false
)

data class DobavitKlubZapros(
    val club_id: Int,
    val is_main: Boolean = false
)

data class ProstoyOtvet(
    val success: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

data class Poseschenie(
    val id: Int? = null,
    val workout_name: String? = null,
    val visit_date: String? = null,
    val status: String? = null,
    val club_name: String? = null
)
