package com.example.ebook1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.ebook1.databinding.ActivityDashboardBinding
import com.example.ebook1.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler().postDelayed(Runnable {
            checkUser()
        },1000)
    }

    private fun checkUser() {
        //Получаем текущего пользователя, залогинился или нет
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            //Пользователь не залогинился, отправляем в главный экран
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else{
            //Пользователь залогинился, проверяем его тип
            val ref = FirebaseDatabase.getInstance().getReference("Users") //????
            ref.child(firebaseUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener { //?????

                    override fun onDataChange(snapshot: DataSnapshot) { //????
                        //Получаем пользовательский или админский тип
                        val userType = snapshot.child("userType").value
                        if (userType == "user"){
                            //Это простой пользователь, открываем панель пользователя
                            startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                            finish()
                        }else if (userType == "admin"){
                            //Это админ, открываем панель админа
                            startActivity(Intent(this@SplashActivity, DashboardAdminActivity::class.java))
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }
}