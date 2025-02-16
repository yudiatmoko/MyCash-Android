package com.iyam.mycash.ui.manage.statement

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.recap.TopProductDataRecap
import com.iyam.mycash.databinding.LayoutTopProductItemBinding
import com.iyam.mycash.utils.ViewHolderBinder

class TopProductAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<TopProductDataRecap>() {
            override fun areItemsTheSame(
                oldItem: TopProductDataRecap,
                newItem: TopProductDataRecap
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: TopProductDataRecap,
                newItem: TopProductDataRecap
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopProductViewHolder {
        val binding =
            LayoutTopProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopProductViewHolder(
            context,
            binding
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as ViewHolderBinder<TopProductDataRecap>).bind(differ.currentList[position])
    }

    fun setData(data: List<TopProductDataRecap>) {
        differ.submitList(data)
    }

    class TopProductViewHolder(
        private val context: Context,
        private val binding: LayoutTopProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<TopProductDataRecap> {

        override fun bind(item: TopProductDataRecap) {
            binding.tvProductName.text = item.name
            binding.tvTransactionSum.text =
                context.getString(R.string.transaction_count, item.quantity)
        }
    }
}
