package com.example.todolist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todolist.databinding.CategoryListItemBinding


class CategoryAdapter(private val fragment: Fragment, private val categories: List<String>): Adapter<CategoryAdapter.CategoryViewHolder>(){
    private var selectedPosition = 0
    class CategoryViewHolder(val binding : CategoryListItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val viewModel = ViewModelProvider(fragment)[CategoryViewModel::class.java]
        holder.binding.categoryBtn.text = categories[position]
        // to change the color of the button when it is selected
        val selectedCategory = viewModel.category.value
        if (categories[position] == selectedCategory) {
            holder.binding.categoryBtn.backgroundTintList = fragment.resources.getColorStateList(R.color.blue, fragment.requireContext().theme)
        } else {
            holder.binding.categoryBtn.backgroundTintList = fragment.resources.getColorStateList(R.color.category_btn_background_selector, fragment.requireContext().theme)
        }
        holder.binding.categoryBtn.setOnClickListener {
            // Listener for button click to update the category in the ViewModel
            viewModel.updateCategory(categories[position])
            selectedPosition = holder.adapterPosition
            fragment.requireActivity().runOnUiThread {
                notifyDataSetChanged() // Update the category highlight
            }
            Log.d("TAG", "onBindViewHolder: ${categories[position]}")
        }
    }
}