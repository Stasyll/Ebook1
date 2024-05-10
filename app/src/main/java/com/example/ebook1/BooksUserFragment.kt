package com.example.ebook1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.ebook1.databinding.FragmentBooksUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BooksUserFragment : Fragment {

    private lateinit var binding: FragmentBooksUserBinding

    public companion object{
        private const val TAG ="BOOKS_USER_TAG"

        //receive data from activity to load books e.g.categoryId, category, uid
        public fun newInstance(categoryId : String, category : String, uid: String): BooksUserFragment {
            val fragment = BooksUserFragment()

            val args = Bundle()
            args.putString("categoryId",categoryId)
            args.putString("category",category)
            args.putString("uid",uid)
            fragment.arguments = args
            return fragment
        }
    }

    private var categoryId = ""
    private var category = ""
    private var uid = ""

    private lateinit var pdfArrayList : ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get argument that we passed in newInstance method
        val args = arguments
        if (args != null){
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentBooksUserBinding.inflate(LayoutInflater.from(context),container,false)

        //load pdf according to category, this fragment will have new instance to load each category pdfs
        Log.d(TAG, "onCreateView: Category $category")
        if (category == "All"){
            //load all books
            loadAllBooks()
        }else if (category == "Most viewed"){
            //load most viewed books
            Log.d(TAG, "DataSnapshot: $category")
            loadMostDownloadedBooks("viewsCount")
        }else if (category == "Most downloaded"){
            //load most downloaded books
            loadMostDownloadedBooks("downloadsCount")
        }else{
            //load selected category book
            loadCategorizedBooks()
        }

        //search
        binding.searchEt.addTextChangedListener { object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
               try {
                    adapterPdfUser.filter.filter(s)
               }
               catch (e: Exception){
                   Log.d(TAG, "onTextChanged: Search Exception ${e.message}")
               }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        }}
        
        return (binding.root)
    }

    private fun loadAllBooks() {
        //init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list
                pdfArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = snapshot.getValue(ModelPdf::class.java)
                    //add to list
                    pdfArrayList.add(model!!)
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(context!!,pdfArrayList)
                //set adapter to recyclerview
                binding.booksRv.adapter = adapterPdfUser

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadMostDownloadedBooks(orderBy: String) {
        //init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild(orderBy).limitToLast(10) //load 10 most viewed or most downloaded books
            .addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list
                pdfArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    Log.d(TAG, "DataSnapshot: $ds")
                    val model = ds.getValue(ModelPdf::class.java)
                    //add to list
                    pdfArrayList.add(model!!)
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(context!!,pdfArrayList)
                //set adapter to recyclerview
                binding.booksRv.adapter = adapterPdfUser

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loadCategorizedBooks() {
        //init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        //get data
                        Log.d(TAG, "DataSnapshot: $ds")
                        val model = ds.getValue(ModelPdf::class.java)
                        //add to list
                        pdfArrayList.add(model!!)
                    }
                    //setup adapter
                    adapterPdfUser = AdapterPdfUser(context!!,pdfArrayList)
                    //set adapter to recyclerview
                    binding.booksRv.adapter = adapterPdfUser

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


}