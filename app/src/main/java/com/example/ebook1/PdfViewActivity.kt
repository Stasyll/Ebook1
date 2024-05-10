package com.example.ebook1

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ebook1.databinding.ActivityPdfViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import java.nio.charset.Charset

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    private lateinit var pdfView: PDFView
    private lateinit var progressBar: View
    private lateinit var bookId: String
    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение идентификатора книги из Intent
        bookId = intent.getStringExtra("bookId")!!

        pdfView = findViewById(R.id.pdfView)
        progressBar = findViewById(R.id.progressBar)

        loadBookDetails()

        // Обработка клика по кнопке "Назад"
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Получение содержимого книги из базы данных")

        // Получение URL книги из базы данных Firebase
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Получение URL книги
                val pdfUrl = snapshot.child("url").getValue(String::class.java)
                Log.d(TAG, "onDataChange: PDF_URL $pdfUrl")

                // Загрузка содержимого книги по URL из Firebase Storage
                loadBookFromUrl(pdfUrl)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }

    private fun loadBookFromUrl(pdfUrl: String?) {
        pdfUrl?.let {
            Log.d(TAG, "loadBookFromUrl: Получение PDF из Firebase Storage по URL")

            val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            reference.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                    Log.d(TAG, "loadBookFromUrl: PDF получен из URL")

                    // Отображение PDF файла
                    pdfView.fromBytes(bytes)
                        .defaultPage(0)
                        .onPageChange(object : OnPageChangeListener {
                            override fun onPageChanged(page: Int, pageCount: Int) {
                                // Обновление информации о текущей странице, если нужно
                            }
                        })
                        .scrollHandle(DefaultScrollHandle(this))
                        .load()

                    // Скрытие индикатора загрузки
                    progressBar.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "loadBookFromUrl: Не удалось получить PDF из-за ${e.message}")

                    // Скрытие индикатора загрузки в случае ошибки
                    progressBar.visibility = View.GONE
                }
        } ?: run {
            Log.d(TAG, "loadBookFromUrl: URL книги отсутствует")

            // Скрытие индикатора загрузки в случае отсутствия URL книги
            progressBar.visibility = View.GONE
        }
    }
}
