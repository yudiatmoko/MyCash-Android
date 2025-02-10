package com.iyam.mycash.ui.outlet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iyam.mycash.databinding.LayoutOutletItemBinding
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.utils.ViewHolderBinder

class OutletAdapter(
    private val itemClick: (Outlet) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<Outlet>() {
            override fun areItemsTheSame(
                oldItem: Outlet,
                newItem: Outlet
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Outlet,
                newItem: Outlet
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OutletViewHolder {
        val binding =
            LayoutOutletItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OutletViewHolder(
            binding,
            itemClick
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as ViewHolderBinder<Outlet>).bind(differ.currentList[position])
    }

    fun setData(data: List<Outlet>) {
        differ.submitList(data)
    }

    class OutletViewHolder(
        private val binding: LayoutOutletItemBinding,
        private val onClicked: (Outlet) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<Outlet> {

        override fun bind(item: Outlet) {
            binding.tvOutletName.text = item.name
            binding.tvOutletName.setOnClickListener {
                onClicked.invoke(item)
            }
        }
    }
}
