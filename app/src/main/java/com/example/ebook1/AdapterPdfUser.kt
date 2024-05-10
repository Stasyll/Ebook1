package com.example.ebook1

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.ebook1.databinding.RowPdfUserBinding

class AdapterPdfUser: RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser>, Filterable{

    private var context: Context
    public var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var binding: RowPdfUserBinding
    private var filterList: ArrayList<ModelPdf>

    //filter object
    private var filter: FilterPdfUser? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUser {
        //bind/inflate layout row_pdf_admin.xml
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderPdfUser(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfUser, position: Int) {
        //Get data,set data, Handle click etc

        //get data
        val model = pdfArrayList[position]
        val bookId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val uid = model.uid
        val url = model.url
        val timestamp = model.timestamp

        //convert timestamp to dd/mm/yyyy format
        val date = MyAppliction.formatTimeStamp(timestamp)

        //set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = date

        if (url != null && url.isNotEmpty()) {
            //load further details like category, pdf from url, pdf size
            MyAppliction.loadPdfFromUrlSinglePage(url, title, holder.pdfView, holder.progressbar, null)

            //load category
            MyAppliction.loadCategory(categoryId, holder.categoryTv)

            //load pdf size
            MyAppliction.loadPdfSize(url, title, holder.sizeTv)
        } else {
            // Handle the case when URL is null or empty (perhaps display an error message or placeholder)
        }

        //handle item click, open PdfDetail activity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", bookId) //will be used to load book details
            context.startActivity(intent)
        }
    }


    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterPdfUser(filterList, this)
        }

        return filter as FilterPdfUser
    }

    inner class HolderPdfUser(itemView: View) : RecyclerView.ViewHolder(itemView){
        //UI Views of row_pdf_admin.xml
        val pdfView = binding.pdfView
        val progressbar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
    }


}