package com.example.ebook1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Класс отвечает за отображение экрана загрузки проверку состояния аутентификации пользователя с использованием Firebase.
class SplashActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем Макет activity_splash
        setContentView(R.layout.activity_splash)

        //Инициализируем firebaseAuth получаем допуск к командам
        firebaseAuth = FirebaseAuth.getInstance()

        // Ставим задержку перед загрузкой приложения
        Handler().postDelayed({
            checkUser()
        }, 1000)
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        Log.d("SplashActivity", "Is user authenticated: ${firebaseUser != null}")

        if (firebaseUser == null) {
            // Пользователь не залогинился, отправляем в главный экран
            startActivity(Intent(this, MainActivity::class.java))
            // Завершение активности
            finish()
        } else {
            // Пользователь залогинился, проверяем его уникальный индификатор
            val userId = firebaseUser.uid
            Log.d("SplashActivity", "Current user UID: $userId")

            // Получаем доступ к данным Users из базы данных
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            // Опускаем ниже до userId
            ref.child(userId)
                    // Используем ListeberForSingle чтобы обработать данные 1 раз
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("SplashActivity", "Data snapshot exists: ${snapshot.exists()}")
                        // Проверяем существую ли данные если да получаем userType и опускаемся дальше
                        if (snapshot.exists()) {
                            val userType = snapshot.child("userType").value
                            Log.d("SplashActivity", "User type: $userType")

                            when (userType) {
                                "user" -> {
                                    // Это простой пользователь, открываем панель пользователя
                                    startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                                }
                                "admin" -> {
                                    // Это админ, открываем панель админа
                                    startActivity(Intent(this@SplashActivity, DashboardAdminActivity::class.java))
                                }
                                else -> {
                                    // Неизвестный тип пользователя, отправляем на главный экран
                                    Log.e("SplashActivity", "Unknown user type: $userType")
                                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                }
                            }
                        } else {
                            // Пользователь не найден в базе данных
                            Log.e("SplashActivity", "User not found in database")
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        }
                        finish()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("SplashActivity", "Database error: ${error.message}")
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                })
        }
    }
}
