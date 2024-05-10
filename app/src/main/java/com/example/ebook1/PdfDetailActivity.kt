package com.example.ebook1

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.ebook1.databinding.ActivityPdfDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class PdfDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfDetailBinding

    private companion object{
        const val TAG = "BOOK_DETAIL_TAG"
    }

    private lateinit var progressDialog: ProgressDialog

    //get id, from intent
    private var bookId = ""
    //get from firebase
    private var bookTitle = ""
    private var bookUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent
        bookId = intent.getStringExtra("bookId")!!

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setTitle("Please wait")

        //increment book view count, whenever this page starts
        MyAppliction.incrementBookViewCount(bookId)

        loadBookDetails()

        //handle click,back button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, open pdf view activity
        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("bookId", bookId);
            startActivity(intent)
        }

        //handle click, download pdf/book
        binding.downloadBookBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "onCreate: STORAGE PERMISSION is already granted")
                downloadBook()
            }else{
                Log.d(TAG, "onCreate: STORAGE PERMISSION was not granted")
                requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted:Boolean->
        if (isGranted){
            Log.d(TAG, "onCreate: STORAGE PERMISSION is granted")
            downloadBook()
        }else{
            Log.d(TAG, "onCreate: STORAGE PERMISSION is denied")
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadBook(){
        Log.d(TAG, "downloadBook: Downloading book")
        progressDialog.setMessage("Downloading Book")
        progressDialog.show()

        //lets download book from firebase storage using url
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
        storageReference.getBytes(Constans.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes->
                Log.d(TAG, "downloadBook: Book downloaded...")
                saveToDownloadsFolder(bytes)
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Log.d(TAG, "downloadBook: Failed to download book due to ${e.message}")
                Toast.makeText(this, "Failed to download book due to", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToDownloadsFolder(bytes: ByteArray) {
        Log.d(TAG, "saveToDownloadsFolder: Saving downloaded book")

        val nameWithExtention = "${System.currentTimeMillis()}.pdf"

        try {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsFolder.mkdir()

            val filePath = downloadsFolder.path + "/" + nameWithExtention

            val out = FileOutputStream(filePath)
            out.write(bytes)
            out.close()

            Toast.makeText(this, "Saves to downloads Folder", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "saveToDownloadsFolder: Saves to downloads Folder")
            progressDialog.dismiss()
            incrementDownloadCount()
        }
        catch (e: Exception){
            progressDialog.dismiss()
            Log.d(TAG, "saveToDownloadsFolder: failed to save due to ${e.message}")
            Toast.makeText(this, "failed to save due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun incrementDownloadCount() {
        Log.d(TAG, "incrementDownloadCount: ")

        //Get previous downloads count
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get downloads count
                    var downloadsCount = "${snapshot.child("downloadsCount").value}"
                    Log.d(TAG, "onDataChange: Current downloads count $downloadsCount")

                    if (downloadsCount == "" || downloadsCount == "null"){
                        downloadsCount = "0"
                    }

                    val newDownloadCount : Long = downloadsCount.toLong() + 1
                    Log.d(TAG, "onDataChange: New downloads count $newDownloadCount")

                    val hashMap : HashMap<String,Any> = HashMap()
                    hashMap["downloadsCount"] = newDownloadCount

                    //update new incremented downloads count to db
                    val dfRef = FirebaseDatabase.getInstance().getReference("Books")
                    dfRef.child(bookId)
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            Log.d(TAG, "onDataChange: Downloads count incremented")
                        }
                        .addOnFailureListener{e->
                            Log.d(TAG, "onDataChange: Failed to increment due to ${e.message}")
                        }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun loadBookDetails() {
        //Books > bookId

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        Log.d(TAG, "Book Id: $bookId")
        Log.d(TAG, "Book Ref: $ref")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get data
                    Log.d(TAG, "Book Id: $bookId")
                    Log.d(TAG, "Book Ref: $ref")
                    val categoryId = "${snapshot.child("categoryId").value}"
                    bookTitle = "${snapshot.child("title").value}"
                    val description = "${snapshot.child("description").value}"
                    val uid = "${snapshot.child("uid").value}"
                    bookUrl = "${snapshot.child("url").value}"

                    val timestamp = "${snapshot.child("timestamp").value}"
                    val downloadsCount = "${snapshot.child("downloadsCount").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"

                    Log.d(TAG, "Book Id: $bookId")
                    Log.d(TAG, "Book Desc: $bookTitle")
                    Log.d(TAG, "Book Cate: $categoryId")
                    Log.d(TAG, "Book Down: $downloadsCount")
                    Log.d(TAG, "Book URL: $bookUrl")

                    //format date
                    //val date = MyAppliction.formatTimeStamp(timestamp.toLong())

                    //load pdf category
                    MyAppliction.loadCategory(categoryId,binding.categoryTv)

                    //load pdf thumbnail, pages count
                    MyAppliction.loadPdfFromUrlSinglePage("$bookUrl", "$bookTitle", binding.pdfView, binding.progressBar, binding.pagesTv)

                    //load pdf size
                    MyAppliction.loadPdfSize("$bookUrl", "$bookTitle", binding.sizeTv)

                    //set data
                   // binding.dateTv.text = date
                    binding.titleTv.text = bookTitle
                    binding.descriptionTv.text = description
                    binding.viewsTv.text = viewsCount
                    binding.downloadsTv.text = downloadsCount
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}