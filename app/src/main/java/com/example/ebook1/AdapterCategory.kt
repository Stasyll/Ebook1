package com.example.ebook1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ebook1.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory: RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable { //????

    private val context: Context
    public var categoryArrayList: ArrayList<ModelCategory> //?????
    private var filterList: ArrayList<ModelCategory>
    private var filter: FilterCategory? = null
    private lateinit var binding: RowCategoryBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        //inflate bind row_category
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false) //????

        return HolderCategory(binding.root)
    }

    override fun getItemCount(): Int {
        //Возвращаем число айтемов в листе
       return categoryArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        // Получаем данные, Устанавливаем данные, обрабатываем нажатия

        //Получаем данные
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        //Устанавливаем данные
        holder.categoryTv.text = category

        //Обработка нажатия удаления категории
        holder.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context) //????
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Confirm"){a, b-> //???
                    Toast.makeText(context,"Deleting...", Toast.LENGTH_SHORT).show()
                    deleteCategory(model,category)
                }
                .setNegativeButton("Cancel"){a, b->
                    a.dismiss()
                }
                .show()
        }

        //handle click start pdf list admin activity, also pas pdf id, title
        holder.itemView.setOnClickListener{
            val intent = Intent(context, PdfListAdminActivity::class.java)
            intent.putExtra("categoryId",id)
            intent.putExtra("category",category)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model: ModelCategory, category: String) {
        //Получаем айди категории для удаления
        val id = model.id

        //Firebase база данных > Категории > Категория айди
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,"Deleted...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context,"Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){ ///????
        //Инициализируем пользовательский интерфейс
        var categoryTv:TextView = binding.categoryTv
        var deleteBtn:ImageView = binding.deleteBtn
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterCategory(filterList, this) //????
        }
        return filter as FilterCategory
    }


}