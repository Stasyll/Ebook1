package com.example.ebook1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.ebook1.databinding.RowPdfAdminBinding

class AdapterPdfAdmin : RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>, Filterable{

    private var context: Context
    public var pdfArrayList: ArrayList<ModelPdf>
    private var filterList: ArrayList<ModelPdf>
    private lateinit var binding: RowPdfAdminBinding

    //filter object
    private var filter: FilterPdfAdmin? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        //bind/inflate layout row_pdf_admin.xml
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderPdfAdmin(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size//items count
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        //Get data,set data, Handle click etc

        //get data
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        //convert timestamp to dd/mm/yyyy format
        val formattedDate = MyAppliction.formatTimeStamp(timestamp)

        //set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate

        //load further details like category, pdf from url, pdf size

        //load category
        MyAppliction.loadCategory(categoryId, holder.categoryTv)

        //we don't need page number here, pus null for page number || load pdf thumbnail
        MyAppliction.loadPdfFromUrlSinglePage(pdfUrl,title,holder.pdfView, holder.progressbar, null)

        //load pdf size
        MyAppliction.loadPdfSize(pdfUrl,title,holder.sizeTv)

        //handle click, show dialog with option 1) Edit book 2) Delete book
        binding.moreBtn.setOnClickListener {
            moreOptionDialog(model, holder)
        }

        //handle item click, open PdfDetail activity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId",pdfId) //will be used to load book details
            context.startActivity(intent)
        }
    }

    private fun moreOptionDialog(model: ModelPdf, holder: AdapterPdfAdmin.HolderPdfAdmin) {
        //get url,id,title of book
        var bookId = model.id
        var bookTitle = model.title
        var bookUrl = model.url

        //option to show dialog
        var options = arrayOf("Edit", "Delete")

        //alert dialog
        var builder = AlertDialog.Builder(context)
        builder.setTitle("Choose option")
            .setItems(options){dialog, position->
                //handle item click
                if (position ==0){
                    //Edit is clicked
                    val intent = Intent(context, PdfEditActivity::class.java)
                    intent.putExtra("bookId", bookId) //passed bookId, will be used to edit the book
                    context.startActivity(intent)
                }else if (position ==1){
                    //Delete is clicked
                    MyAppliction.deleteBook(context,bookId,bookUrl,bookTitle)
                }
            }
            .show()
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterPdfAdmin(filterList, this)
        }

        return filter as FilterPdfAdmin
    }

    /*View Holder class for row_pdf_admin.xml*/
    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView){
        //UI Views of row_pdf_admin.xml
        val pdfView = binding.pdfView
        val progressbar = binding.progressbar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }
}