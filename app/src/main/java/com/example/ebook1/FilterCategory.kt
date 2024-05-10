package com.example.ebook1

import android.widget.Filter

class FilterCategory: Filter { //????

    //arraylist который мы будем искать
    private lateinit var filterList: ArrayList<ModelCategory>

    //адаптер в который фильтруем нужные имплементы
    private var adapterCategory: AdapterCategory

    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) : super() {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults { //???
        var constraint = constraint
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()){
            //serched value is not null not empty

            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().uppercase()
            val filterModels: ArrayList<ModelCategory> = ArrayList()
            for (i in 0 until filterList.size) {
                //validate
                if (filterList[i].category.uppercase().contains(constraint)) {//????
                    //Добавляем в фильтр лист
                    filterModels.add(filterList[i])
                }
            }
            results.count = filterModels.size
            results.values = filterModels
        }else{
            //search value is either null or empty
            results.count = filterList.size
            results.values = filterList
        }

        return  results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //Принимаем изменения фильтра
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory> //????

        //notify changes
        adapterCategory.notifyDataSetChanged() //????
    }
}