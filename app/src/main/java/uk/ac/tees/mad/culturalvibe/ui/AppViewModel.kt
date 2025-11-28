package uk.ac.tees.mad.culturalvibe.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import uk.ac.tees.mad.culturalvibe.data.models.Event
import uk.ac.tees.mad.culturalvibe.data.models.User
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor( private val auth : FirebaseAuth,
                                        private val firestore : FirebaseFirestore) : ViewModel() {

    val loading = mutableStateOf(false)
    val isUserLoggedIn = MutableStateFlow(false)

    val events = MutableStateFlow<List<Event>>(emptyList())
    init {
        isUserLoggedIn.value = auth.currentUser != null
        getEvents()
    }

    fun registerToEvent(id: Int, context: Context) {
        val userUid = auth.currentUser?.uid
        if (userUid != null){
            firestore.collection("events").document(id.toString()).update("registeredUser", FieldValue.arrayUnion(userUid)).addOnSuccessListener {
                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                getEvents()
            }.addOnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    fun isRegistered(event: Event): Boolean {
        return auth.currentUser?.uid in event.registeredUser
    }

    fun unregisterFromEvent(id: Int, context: Context) {
        val userUid = auth.currentUser?.uid
        if (userUid != null){
            firestore.collection("events").document(id.toString()).update("registeredUser", FieldValue.arrayRemove(userUid)).addOnSuccessListener {
                Toast.makeText(context, "Unregistered Successfully", Toast.LENGTH_SHORT).show()
                getEvents()
            }.addOnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun getEvents(){
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

    fun signup(context : Context,name : String, email : String, password : String){
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
                    fetchUserDetails()
                    Toast.makeText(context, "Signup Successful", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    loading.value = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun login(context : Context, email : String, password : String){
        loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loading.value = false
                isUserLoggedIn.value = true
                fetchUserDetails()
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                loading.value = false
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun fetchUserDetails(){
        firestore.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
            }
    }

    fun logout(){
        auth.signOut()
        isUserLoggedIn.value = false
    }
}