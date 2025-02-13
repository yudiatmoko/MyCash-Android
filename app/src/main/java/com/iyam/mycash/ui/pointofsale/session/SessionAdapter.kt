package com.iyam.mycash.ui.pointofsale.session

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.recap.SessionDataRecap
import com.iyam.mycash.databinding.LayoutSessionItemBinding
import com.iyam.mycash.utils.ViewHolderBinder
import com.iyam.mycash.utils.formatDayOnly
import com.iyam.mycash.utils.toCurrencyFormat

class SessionAdapter(
    private val context: Context,
    private val onClick: (SessionDataRecap) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<SessionDataRecap>() {
            override fun areItemsTheSame(
                oldItem: SessionDataRecap,
                newItem: SessionDataRecap
            ): Boolean {
                return oldItem.sessionId == newItem.sessionId
            }

            override fun areContentsTheSame(
                oldItem: SessionDataRecap,
                newItem: SessionDataRecap
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SessionDataRecapViewHolder {
        val binding =
            LayoutSessionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionDataRecapViewHolder(
            context,
            binding,
            onClick
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as ViewHolderBinder<SessionDataRecap>).bind(differ.currentList[position])
    }

    fun setData(data: List<SessionDataRecap>) {
        differ.submitList(data)
    }

    class SessionDataRecapViewHolder(
        private val context: Context,
        private val binding: LayoutSessionItemBinding,
        private val onClicked: (SessionDataRecap) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<SessionDataRecap> {

        override fun bind(item: SessionDataRecap) {
            val date = formatDayOnly(item.date.orEmpty())
            binding.tvDate.text = date
            val indonesianShift =
                if (item.shift == "MORNING") {
                    context.getString(R.string.morning)
                } else {
                    context.getString(
                        R.string.evening
                    )
                }
            binding.tvShift.text = indonesianShift
            binding.tvUser.text = item.user?.name
            if (item.checkOutTime == null) {
                binding.tvStatus.isVisible = true
                binding.tvStatus.text = context.getString(R.string.active)
            }
            binding.tvRevenue.text = item.totalRevenue?.toFloat()?.toCurrencyFormat()
            binding.root.setOnClickListener {
                onClicked.invoke(item)
            }
        }
    }
}
