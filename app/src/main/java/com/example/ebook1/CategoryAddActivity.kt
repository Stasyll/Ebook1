package com.example.ebook1

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ebook1.databinding.ActivityCategoryAddBinding
import com.example.ebook1.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CategoryAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryAddBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //Обработка нажатия кнопки назад
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //Обработка нажатия начать загрузку категории
        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        category = binding.categoryEt.text.toString().trim()

        if(category.isEmpty()){
            Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show()
        }else{
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        progressDialog.show()

        //Получаем timestamp
        val timestamp = System.currentTimeMillis()

        // Устанавливаем данные в базу данных firebase
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        //Добавляем в базу данных. База данных права > Категории > Категории id > Категории инфо
        val ref = FirebaseDatabase.getInstance().getReference("Categories") //??????
        ref.child("$timestamp") // ??????????
            .setValue(hashMap) // ????
            .addOnSuccessListener{
                //Добавлено успешно
                progressDialog.dismiss()
                Toast.makeText(this, "Added successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{ e->
                //Провалено добавление
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}