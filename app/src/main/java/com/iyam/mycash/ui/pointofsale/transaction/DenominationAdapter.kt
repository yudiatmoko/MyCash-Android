package com.iyam.mycash.ui.pointofsale.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iyam.mycash.databinding.LayoutDenominationItemBinding
import com.iyam.mycash.utils.ViewHolderBinder

class DenominationAdapter(
    private val onDenominationClicked: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }
        }
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DenominationViewHolder {
        val binding =
            LayoutDenominationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DenominationViewHolder(
            binding,
            onDenominationClicked
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderBinder<String>).bind(differ.currentList[position])
    }

    fun setData(data: List<String>) {
        differ.submitList(data)
    }

    override fun getItemCount(): Int = differ.currentList.size

    class DenominationViewHolder(
        private val binding: LayoutDenominationItemBinding,
        private val onDenominationClicked: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<String> {

        override fun bind(item: String) {
            binding.btnDenomination.text = item
            binding.btnDenomination.setOnClickListener {
                onDenominationClicked(item)
            }
        }
    }
}
