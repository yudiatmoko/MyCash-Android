package com.iyam.mycash.ui.manage.category

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iyam.mycash.R
import com.iyam.mycash.databinding.LayoutCategoryItemBinding
import com.iyam.mycash.model.Category
import com.iyam.mycash.utils.ViewHolderBinder

class CategoryAdapter(
    private val context: Context,
    private val deleteListener: (Category) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val binding =
            LayoutCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(
            context,
            binding,
            deleteListener
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as ViewHolderBinder<Category>).bind(differ.currentList[position])
    }

    fun setData(data: List<Category>) {
        differ.submitList(data)
    }

    class CategoryViewHolder(
        private val context: Context,
        private val binding: LayoutCategoryItemBinding,
        private val deleteClicked: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<Category> {
        private fun showDialog(item: Category) {
            val dialogView =
                LayoutInflater.from(itemView.context).inflate(R.layout.layout_dialog, null)
            val dialog = AlertDialog.Builder(itemView.context)
                .setView(dialogView)
                .create()
            dialog.getWindow()?.setBackgroundDrawableResource(R.drawable.cv_background)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val tvTitle = dialogView.findViewById<TextView>(R.id.tv_title)
            val tvDesc = dialogView.findViewById<TextView>(R.id.tv_desc)
            val btnNegative = dialogView.findViewById<TextView>(R.id.btn_negative)
            val btnPositive = dialogView.findViewById<TextView>(R.id.btn_positive)

            tvTitle.text = context.getString(R.string.delete_category_dialog_title, item.name)
            tvDesc.text =
                context.getString(R.string.delete_category_dialog_desc, item.name)
            btnNegative.text = context.getString(R.string.cancel)
            btnPositive.text = context.getString(R.string.delete)

            btnNegative.setOnClickListener {
                dialog.dismiss()
            }
            btnPositive.setOnClickListener {
                deleteClicked.invoke(item)
                dialog.dismiss()
            }
            dialog.show()
        }

        override fun bind(item: Category) {
            binding.tvCategoryName.text = item.name
            binding.tvCategoryName.setOnClickListener {
                showDialog(item)
            }
        }
    }
}
