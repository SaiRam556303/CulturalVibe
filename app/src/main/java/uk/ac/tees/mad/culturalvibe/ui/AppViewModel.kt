package uk.ac.tees.mad.culturalvibe.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import uk.ac.tees.mad.culturalvibe.data.models.User
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor( private val auth : FirebaseAuth,
                                        private val firestore : FirebaseFirestore) : ViewModel() {

    val loading = mutableStateOf(false)
    val isUserLoggedIn = MutableStateFlow(false)

    init {
        isUserLoggedIn.value = auth.currentUser != null
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