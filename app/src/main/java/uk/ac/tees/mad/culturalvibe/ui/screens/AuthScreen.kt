package uk.ac.tees.mad.culturalvibe.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.culturalvibe.ui.AppViewModel
import uk.ac.tees.mad.culturalvibe.ui.theme.OnSecondary
import uk.ac.tees.mad.culturalvibe.ui.theme.PrimaryColor
import uk.ac.tees.mad.culturalvibe.ui.theme.SecondaryColor

@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AppViewModel
) {
    var isLogin by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val isLoggedin = authViewModel.isUserLoggedIn.collectAsState().value
    val loading  = authViewModel.loading

    LaunchedEffect(isLoggedin) {
        if (isLoggedin) {
            navController.navigate("home"){
                popUpTo(0)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(

                painter = painterResource(id = uk.ac.tees.mad.culturalvibe.R.drawable.designer),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome to CulturalVibe",
                fontSize = 18.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isLogin) "Login to Continue" else "Create an Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthForm(
                isLogin = isLogin,
                isloading = loading.value,
                onSubmit = { name, email, password ->
                    if (isLogin) {
                        if (!email.isEmpty() || !password.isEmpty()) {
                            authViewModel.login(
                                context = context,
                                email = email,
                                password = password,
                            )
                        }else{
                            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                        }
                        authViewModel.signup(context = context,name = name,email = email,password = password)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(
                    text = if (isLogin) "Don't have an account? " else "Already registered? ",
                    color = PrimaryColor
                )
                Text(
                    text = if (isLogin) "Sign Up" else "Login",
                    color = SecondaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isLogin = !isLogin }
                )
            }
        }
    }
}

@Composable
fun AuthForm(
    isLogin: Boolean,
    isloading: Boolean,
    onSubmit: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (!isLogin) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                focusedLabelColor = PrimaryColor
            ),
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor,
            focusedLabelColor = PrimaryColor
        ),
        shape = RoundedCornerShape(24.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor,
            focusedLabelColor = PrimaryColor
        ),
        shape = RoundedCornerShape(24.dp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    Button(
        onClick = {
            onSubmit(name, email, password) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SecondaryColor,
            contentColor = OnSecondary
        ),
        enabled = !isloading
    ) {
        if (isloading){
            LinearProgressIndicator()
        }else {
            Text(
                text = if (isLogin) "Login" else "Sign Up",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}



@Preview(showBackground = true, name = "CulturalVibe – Login Screen")
@Composable
fun AuthScreenPreview_Login() {
    var isLogin by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = uk.ac.tees.mad.culturalvibe.R.drawable.designer),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome to CulturalVibe",
                fontSize = 18.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Login to Continue",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = "alex@example.com",
                onValueChange = {},
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    focusedLabelColor = PrimaryColor
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            OutlinedTextField(
                value = "••••••••",
                onValueChange = {},
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    focusedLabelColor = PrimaryColor
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryColor,
                    contentColor = OnSecondary
                )
            ) {
                Text("Login", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Don't have an account? ", color = PrimaryColor)
                Text(
                    "Sign Up",
                    color = SecondaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isLogin = false }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "CulturalVibe – Sign Up Screen")
@Composable
fun AuthScreenPreview_SignUp() {
    var isLogin by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = uk.ac.tees.mad.culturalvibe.R.drawable.designer),
                contentDescription = "App Icon",
                modifier = Modifier.size(90.dp).clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Welcome to CulturalVibe",
                fontSize = 18.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create an Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            OutlinedTextField(
                value = "Alex Johnson",
                onValueChange = {},
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    focusedLabelColor = PrimaryColor
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = "alex.j@example.com",
                onValueChange = {},
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    focusedLabelColor = PrimaryColor
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = "••••••••",
                onValueChange = {},
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    focusedLabelColor = PrimaryColor
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryColor,
                    contentColor = OnSecondary
                )
            ) {
                Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Already registered? ", color = PrimaryColor)
                Text(
                    "Login",
                    color = SecondaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isLogin = true }
                )
            }
        }
    }
}