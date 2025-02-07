import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
// To parse the JSON, install kotlin's serialization plugin and do:
//
// val json             = Json { allowStructuredMapKeys = true }
// val bubbelCodegenOut = json.parse(BubbelCodegenOut.serializer(), jsonString)



import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class BubbelCodegenOut (
    val t0: InCreateUser? = null,
    val t1: ResCreateUser? = null,
    val t10: InSetUserProfile? = null,
    val t11: ResSetUserProfile? = null,
    val t12: InDeleteUser? = null,
    val t13: ResDeleteUser? = null,
    val t2: InAuthUser? = null,
    val t3: ResAuthUser? = null,
    val t4: InDeauthUser? = null,
    val t5: ResDeauthUser? = null,
    val t6: InVerifyAccount? = null,
    val t7: ResVerifyAccount? = null,
    val t8: InSendVerify? = null,
    val t9: ResSendVerify? = null
)

@Serializable
data class InCreateUser (
    val email: String,
    val password: String,
    val username: String
)

@Serializable
data class ResCreateUser (
    val error: CreateUserError? = null,

    @SerialName("user_id")
    val userID: Long? = null
)

/**
 * Email is not valid by backend standards.
 *
 * Username is not valid by backend standards.
 *
 * Password is not valid by backend standards.
 *
 * Password failed to be encrypted.
 *
 * Email or Username already taken.
 */
@Serializable
data class CreateUserError (
    val type: PurpleType,
    val ierror: String? = null
)

@Serializable
enum class PurpleType(val value: String) {
    @SerialName("EmailOrUsernametaken") EmailOrUsernametaken("EmailOrUsernametaken"),
    @SerialName("Internal") Internal("Internal"),
    @SerialName("InvalidEmail") InvalidEmail("InvalidEmail"),
    @SerialName("InvalidPassword") InvalidPassword("InvalidPassword"),
    @SerialName("InvalidPasswordCryto") InvalidPasswordCryto("InvalidPasswordCryto"),
    @SerialName("InvalidUsername") InvalidUsername("InvalidUsername");
}

@Serializable
data class InSetUserProfile (
    val banner: String? = null,
    val description: String? = null,

    @SerialName("display_name")
    val displayName: String? = null,

    val name: String? = null,
    val pfp: String? = null,
    val token: String
)

@Serializable
data class ResSetUserProfile (
    val error: SetUserProfileError? = null
)

@Serializable
data class SetUserProfileError (
    val type: FluffyType,
    val ierror: String? = null
)

@Serializable
enum class FluffyType(val value: String) {
    @SerialName("Internal") Internal("Internal"),
    @SerialName("NoAuth") NoAuth("NoAuth");
}

@Serializable
data class InDeleteUser (
    val token: String
)

@Serializable
data class ResDeleteUser (
    val error: DeleteUserError? = null
)

@Serializable
data class DeleteUserError (
    val type: FluffyType,
    val ierror: String? = null
)

@Serializable
data class InAuthUser (
    val password: String,
    val username: String
)

@Serializable
data class ResAuthUser (
    val email: String? = null,
    val error: AuthUserError? = null,
    val token: String? = null,
    val username: String? = null
)

@Serializable
data class AuthUserError (
    val type: TentacledType,
    val ierror: String? = null
)

@Serializable
enum class TentacledType(val value: String) {
    @SerialName("Internal") Internal("Internal"),
    @SerialName("InvalidCredentials") InvalidCredentials("InvalidCredentials"),
    @SerialName("InvalidPasswordCryto") InvalidPasswordCryto("InvalidPasswordCryto"),
    @SerialName("UserNotFound") UserNotFound("UserNotFound"),
    @SerialName("UserNotVerified") UserNotVerified("UserNotVerified");
}

@Serializable
data class InDeauthUser (
    val token: String
)

@Serializable
data class ResDeauthUser (
    val error: JsonElement? = null
)

@Serializable
data class InVerifyAccount (
    val code: String,

    @SerialName("user_id")
    val userID: Long
)

@Serializable
data class ResVerifyAccount (
    val error: VerifyAccountError? = null
)

@Serializable
data class VerifyAccountError (
    val type: StickyType,
    val ierror: String? = null
)

@Serializable
enum class StickyType(val value: String) {
    @SerialName("CodeTimedOutOrInvalidUser") CodeTimedOutOrInvalidUser("CodeTimedOutOrInvalidUser"),
    @SerialName("Internal") Internal("Internal"),
    @SerialName("InvalidCode") InvalidCode("InvalidCode");
}

@Serializable
data class InSendVerify (
    @SerialName("user_id")
    val userID: Long
)

@Serializable
data class ResSendVerify (
    val error: SendVerifyError? = null
)

@Serializable
data class SendVerifyError (
    val type: IndigoType,
    val ierror: String? = null
)

@Serializable
enum class IndigoType(val value: String) {
    @SerialName("Internal") Internal("Internal"),
    @SerialName("ResendTooSoon") ResendTooSoon("ResendTooSoon"),
    @SerialName("SendVerification") SendVerification("SendVerification"),
    @SerialName("UserNotFound") UserNotFound("UserNotFound");
}

val BUBBEL_BATH_DEV = "https://bubbel-bath.onrender.com"

suspend fun bubbelApiCreateUser(request: InCreateUser): ResCreateUser = withContext(Dispatchers.IO) {
        val encoder = Json { ignoreUnknownKeys = true }
        val json = encoder.encodeToString(request)
        val url = URL("$BUBBEL_BATH_DEV/api/create_user")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.doOutput = true
        urlConnection.outputStream.use { outputStream ->
            outputStream.write(json.toByteArray())
        }

        val responseCode = urlConnection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = urlConnection.inputStream.bufferedReader().use { it.readText() }
            val decoder = Json { ignoreUnknownKeys = true }
            try {
                decoder.decodeFromString(responseString)
            } catch (ex: SerializationException) {
                throw Exception("Error decoding response: ${ex.message}")
            }
        } else {
            throw Exception("Error fetching data. Response code: $responseCode")
        }
    }
suspend fun bubbelApiAuthUser(request: InAuthUser): ResAuthUser = withContext(Dispatchers.IO) {
        val encoder = Json { ignoreUnknownKeys = true }
        val json = encoder.encodeToString(request)
        val url = URL("$BUBBEL_BATH_DEV/api/auth_user")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.doOutput = true
        urlConnection.outputStream.use { outputStream ->
            outputStream.write(json.toByteArray())
        }

        val responseCode = urlConnection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = urlConnection.inputStream.bufferedReader().use { it.readText() }
            val decoder = Json { ignoreUnknownKeys = true }
            try {
                decoder.decodeFromString(responseString)
            } catch (ex: SerializationException) {
                throw Exception("Error decoding response: ${ex.message}")
            }
        } else {
            throw Exception("Error fetching data. Response code: $responseCode")
        }
    }
suspend fun bubbelApiDeauthUser(request: InDeauthUser): ResDeauthUser = withContext(Dispatchers.IO) {
        val encoder = Json { ignoreUnknownKeys = true }
        val json = encoder.encodeToString(request)
        val url = URL("$BUBBEL_BATH_DEV/api/deauth_user")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.doOutput = true
        urlConnection.outputStream.use { outputStream ->
            outputStream.write(json.toByteArray())
        }

        val responseCode = urlConnection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = urlConnection.inputStream.bufferedReader().use { it.readText() }
            val decoder = Json { ignoreUnknownKeys = true }
            try {
                decoder.decodeFromString(responseString)
            } catch (ex: SerializationException) {
                throw Exception("Error decoding response: ${ex.message}")
            }
        } else {
            throw Exception("Error fetching data. Response code: $responseCode")
        }
    }
suspend fun bubbelApiVerifyAccount(request: InVerifyAccount): ResVerifyAccount = withContext(Dispatchers.IO) {
        val encoder = Json { ignoreUnknownKeys = true }
        val json = encoder.encodeToString(request)
        val url = URL("$BUBBEL_BATH_DEV/api/verify_account")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.doOutput = true
        urlConnection.outputStream.use { outputStream ->
            outputStream.write(json.toByteArray())
        }

        val responseCode = urlConnection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = urlConnection.inputStream.bufferedReader().use { it.readText() }
            val decoder = Json { ignoreUnknownKeys = true }
            try {
                decoder.decodeFromString(responseString)
            } catch (ex: SerializationException) {
                throw Exception("Error decoding response: ${ex.message}")
            }
        } else {
            throw Exception("Error fetching data. Response code: $responseCode")
        }
    }
suspend fun bubbelApiSendVerify(request: InSendVerify): ResSendVerify = withContext(Dispatchers.IO) {
        val encoder = Json { ignoreUnknownKeys = true }
        val json = encoder.encodeToString(request)
        val url = URL("$BUBBEL_BATH_DEV/api/send_verify")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.doOutput = true
        urlConnection.outputStream.use { outputStream ->
            outputStream.write(json.toByteArray())
        }

        val responseCode = urlConnection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = urlConnection.inputStream.bufferedReader().use { it.readText() }
            val decoder = Json { ignoreUnknownKeys = true }
            try {
                decoder.decodeFromString(responseString)
            } catch (ex: SerializationException) {
                throw Exception("Error decoding response: ${ex.message}")
            }
        } else {
            throw Exception("Error fetching data. Response code: $responseCode")
        }
    }
suspend fun bubbelApiSetUserProfile(request: InSetUserProfile): ResSetUserProfile = withContext(Dispatchers.IO) {
        val encoder = Json { ignoreUnknownKeys = true }
        val json = encoder.encodeToString(request)
        val url = URL("$BUBBEL_BATH_DEV/api/set_user_profile")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.doOutput = true
        urlConnection.outputStream.use { outputStream ->
            outputStream.write(json.toByteArray())
        }

        val responseCode = urlConnection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = urlConnection.inputStream.bufferedReader().use { it.readText() }
            val decoder = Json { ignoreUnknownKeys = true }
            try {
                decoder.decodeFromString(responseString)
            } catch (ex: SerializationException) {
                throw Exception("Error decoding response: ${ex.message}")
            }
        } else {
            throw Exception("Error fetching data. Response code: $responseCode")
        }
    }
suspend fun bubbelApiDeleteUser(request: InDeleteUser): ResDeleteUser = withContext(Dispatchers.IO) {
        val encoder = Json { ignoreUnknownKeys = true }
        val json = encoder.encodeToString(request)
        val url = URL("$BUBBEL_BATH_DEV/api/delete_user")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.doOutput = true
        urlConnection.outputStream.use { outputStream ->
            outputStream.write(json.toByteArray())
        }

        val responseCode = urlConnection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = urlConnection.inputStream.bufferedReader().use { it.readText() }
            val decoder = Json { ignoreUnknownKeys = true }
            try {
                decoder.decodeFromString(responseString)
            } catch (ex: SerializationException) {
                throw Exception("Error decoding response: ${ex.message}")
            }
        } else {
            throw Exception("Error fetching data. Response code: $responseCode")
        }
    }