package com.example.ebook1

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.ebook1.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var name = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Инициализируем firebase индификатор
        firebaseAuth = FirebaseAuth.getInstance()

        //То что мы будем показывать, когда будет регистрироваться пользователь
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //обработка нажатия кнопки возврата
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        //Нажатие кнопки начать регистрацию
        binding.resisterBtn.setOnClickListener{
            validateData()
        }
    }

    private fun validateData() {
        //Ввод данных
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        //Проверка данных
        if (name.isEmpty()){
            // Пустое имя
            Toast.makeText(this,"Enter your name...",Toast.LENGTH_SHORT).show()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // Недопустимый шаблон электронной почты
            Toast.makeText(this,"Invalid Email Pattern...",Toast.LENGTH_SHORT).show()
        }else if (password.isEmpty()){
            //Пустой пароль
            Toast.makeText(this, "Enter your password....", Toast.LENGTH_SHORT).show()
        }else if (cPassword.isEmpty()){
            //Пустой пароль
            Toast.makeText(this, "Confirm password....", Toast.LENGTH_SHORT).show()
        }else if (password != cPassword){
            Toast.makeText(this, "Password doesn't match....", Toast.LENGTH_SHORT).show()
        }else{
            createUserAccount()
        }

    }

    private fun createUserAccount() {
        //Создаем аккаунт на firebase

        //Показываем прогресс
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        //Создаем пользователя на firebase
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //Аккаунт создан
                updateUserInfo()
            }
            .addOnFailureListener{e->
                // Провалено создание аккаунта
                progressDialog.dismiss()
                Toast.makeText(this, "Failed creating account due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        //Сохраняем информацию пользователя firebase realtime database
        progressDialog.setMessage("Saving user info")

        //Отметка времени
        val timestamp = System.currentTimeMillis()

        //Получить текущий uid пользователя, поскольку пользователь зарегистрирован мы можем получить его прямо сейчас
        val uid = firebaseAuth.uid

        //Установочные данные для добавляения в базу данных
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        //Устанавливаем данные в базу данных
        val ref = FirebaseDatabase.getInstance().getReference("Users") //??????
        ref.child(uid!!) // ??????????
            .setValue(hashMap) // ????
            .addOnSuccessListener{
                //Информация о пользователе сохранена, открываем пользовательскую приборную панель
                progressDialog.dismiss()
                Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity,DashboardActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                //Провалено добавление данных в базу данных
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }
}