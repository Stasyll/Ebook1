package com.example.ebook1

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.ebook1.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Инициализируем firebase индификатор
        firebaseAuth = FirebaseAuth.getInstance()

        //То что мы будем показывать, когда будет логиниться пользователь
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //Нажатие кнопки. Нет учетной записи, чтобы перейти к экрану регистрации
        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        //Нажатие кнопки, чтобы начать логиниться
        binding.loginBtn.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email format...", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Enter your password....", Toast.LENGTH_SHORT).show()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {

        progressDialog.setMessage("Loggin In...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //Логин выполнен
                checkUser()
            }
            .addOnFailureListener{e->
                // Провален заход в аккаунт
                progressDialog.dismiss()
                Toast.makeText(this, "Loging failed account due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User...")

        val firebaseUser = firebaseAuth.currentUser //????

        val ref = FirebaseDatabase.getInstance().getReference("Users") //????
        ref.child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{ //?????

                override fun onDataChange(snapshot: DataSnapshot) { //????
                    progressDialog.dismiss()

                    //Получаем пользовательский или админский тип
                    val userType = snapshot.child("userType").value
                    if (userType == "user"){
                        //Это простой пользователь, открываем панель пользователя
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    }else if (userType == "admin"){
                        //Это админ, открываем панель админа
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
}


