package uk.ac.tees.mad.culturalvibe.ui

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.ac.tees.mad.culturalvibe.data.local.EventDao
import uk.ac.tees.mad.culturalvibe.data.models.Event
import uk.ac.tees.mad.culturalvibe.data.models.User
import uk.ac.tees.mad.culturalvibe.data.repository.GalleryRepository
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

@HiltViewModel
class AppViewModel @Inject constructor(
    val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val eventDao: EventDao,
    private val repository: GalleryRepository
) : ViewModel() {

    val loading = mutableStateOf(false)
    val isUserLoggedIn = MutableStateFlow(false)

    val userData = mutableStateOf<User?>(null)
    val events = MutableStateFlow<List<Event>>(emptyList())
    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images
    val bookmarkedEvents: StateFlow<List<Event>> =
        eventDao.getAllBookmarks()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        isUserLoggedIn.value = auth.currentUser != null
        getEvents()
        getUserData()
        fetchImages()
    }

    fun vibratePhone(context: Context, unregister: Boolean = false, removal: Boolean = false) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when {
                unregister -> vibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
                )
                removal -> vibrator.vibrate(
                    VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                )
                else -> vibrator.vibrate(
                    VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(if (unregister) 300 else if (removal) 80 else 150)
        }
    }



    fun uploadImageToSupabase(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@launch
                val fileName = "img_${System.currentTimeMillis()}.jpg"
                val bucket = repository.storage.from("CulturalVibe")

                bucket.upload(fileName, bytes)

                Toast.makeText(context, "Uploaded successfully!", Toast.LENGTH_SHORT).show()
                fetchImages()
            } catch (e: Exception) {
                Toast.makeText(context, "Upload failed: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
                Log.d("error", e.localizedMessage)
            }
        }
    }


    fun uploadImageToSupabase(bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                val bytes = stream.toByteArray()
                val fileName = "img_${System.currentTimeMillis()}.jpg"
                val bucket = repository.storage.from("CulturalVibe")
                bucket.upload(fileName, bytes)
                Toast.makeText(context, "Uploaded successfully!", Toast.LENGTH_SHORT).show()
                vibratePhone(context)
                fetchImages()
            } catch (e: Exception) {
                Toast.makeText(context, "Upload failed: ${e.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun fetchImages() {
        viewModelScope.launch {
            _images.value = repository.getGalleryImages()
        }
    }

    fun addBookmark(context: Context, event: Event) {
        viewModelScope.launch {
            eventDao.addBookmark(event)
            vibratePhone(context)
            Toast.makeText(context, "Added to bookmarks", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeBookmark(context: Context, event: Event) {
        viewModelScope.launch {
            eventDao.deleteBookmark(event)
            vibratePhone(context, removal = true)
            Toast.makeText(context, "Removed from bookmarks", Toast.LENGTH_SHORT).show()
        }
    }

    fun registerToEvent(id: Int, context: Context, onSuccess: () -> Unit) {
        val userUid = auth.currentUser?.uid
        if (userUid != null) {
            firestore.collection("events").document(id.toString())
                .update("registeredUser", FieldValue.arrayUnion(userUid)).addOnSuccessListener {
                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                getEvents()
                    vibratePhone(context)
                    onSuccess()
            }.addOnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    fun isRegistered(event: Event): Boolean {
        return auth.currentUser?.uid in event.registeredUser
    }

    fun unregisterFromEvent(id: Int, context: Context, onSuccess: () -> Unit) {
        val userUid = auth.currentUser?.uid
        if (userUid != null) {
            firestore.collection("events").document(id.toString())
                .update("registeredUser", FieldValue.arrayRemove(userUid)).addOnSuccessListener {
                Toast.makeText(context, "Unregistered Successfully", Toast.LENGTH_SHORT).show()
                getEvents()
                    vibratePhone(context, unregister = true)
                    onSuccess()
            }.addOnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun updateUser(name: String, email: String, context: Context, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        val updates = mapOf(
            "name" to name,
            "email" to email
        )

        firestore.collection("users").document(uid)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                getUserData() // 🔹 Immediately refresh local state
                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    fun getUserData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener {
                userData.value = it.toObject(User::class.java)
            }
            .addOnFailureListener {
                Log.d("error", it.localizedMessage)
            }
    }


    fun getEvents() {
        firestore.collection("events").get()
            .addOnSuccessListener {
                val eventList = it.documents.mapNotNull {
                    it.toObject(Event::class.java)
                }
                Log.d("events", eventList.toString())
                events.value = eventList
                Log.d("events", events.value.toString())
            }
            .addOnFailureListener {
                Log.d("error", it.localizedMessage)
            }
    }

    fun signup(context: Context, name: String, email: String, password: String) {
        loading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                firestore.collection("users").document(it.user!!.uid).set(
                    hashMapOf(
                        "name" to name,
                        "email" to email,
                        "password" to password
                    )
                ).addOnSuccessListener {
                    loading.value = false
                    isUserLoggedIn.value = true
                    getEvents()
                    getUserData()
                    fetchImages()
                    Toast.makeText(context, "Signup Successful", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    loading.value = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun login(context: Context, email: String, password: String) {
        loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loading.value = false
                isUserLoggedIn.value = true
                getEvents()
                getUserData()
                fetchImages()
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                loading.value = false
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun logout() {
        auth.signOut()
        isUserLoggedIn.value = false
    }
}