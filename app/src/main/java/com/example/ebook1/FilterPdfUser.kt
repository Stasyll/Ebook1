package com.example.ebook1

import android.widget.Filter

class FilterPdfUser: Filter {

    //array list in which we want to search
    var filterList: ArrayList<ModelPdf>

    //adapter in which filter need to be implemented
    var adapterPdfUser: AdapterPdfUser

    //constructor
    constructor(filterList: ArrayList<ModelPdf>, adapterPdfUser: AdapterPdfUser) {
        this.filterList = filterList
        this.adapterPdfUser = adapterPdfUser
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint: CharSequence? = constraint // value to search
        val results = FilterResults()
        //value to be searched should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()){
            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().uppercase()
            var filteredModels = ArrayList<ModelPdf>()
            for (i in filterList.indices){
                //validate if match
                if (filterList[i].title.uppercase().contains(constraint)){
                    //searched value is similar to value in list, add to firebase list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }else{
            //searched value is either null or empty, return all data
            results.count = filterList.size
            results.values = filterList
        }

        return results //don't miss
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        //apply filter changes
        adapterPdfUser.pdfArrayList = results.values as ArrayList<ModelPdf>

        //notify changes
        adapterPdfUser.notifyDataSetChanged()

    }
}