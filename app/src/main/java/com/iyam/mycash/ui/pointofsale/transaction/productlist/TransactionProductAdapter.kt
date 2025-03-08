package com.iyam.mycash.ui.pointofsale.transaction.productlist

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iyam.mycash.R
import com.iyam.mycash.databinding.LayoutTransactionProductItemBinding
import com.iyam.mycash.model.Product
import com.iyam.mycash.utils.ViewHolderBinder
import com.iyam.mycash.utils.toCurrencyFormat

class TransactionProductAdapter(
    private val context: Context,
    private val addToCartListener: (Product, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionProductViewHolder {
        val binding =
            LayoutTransactionProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TransactionProductViewHolder(
            context,
            binding,
            addToCartListener
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as ViewHolderBinder<Product>).bind(differ.currentList[position])
    }

    fun setData(data: List<Product>) {
        differ.submitList(data)
    }

    fun getCurrentList(): List<Product> {
        return differ.currentList
    }

    class TransactionProductViewHolder(
        private val context: Context,
        private val binding: LayoutTransactionProductItemBinding,
        private val addToCartClicked: (Product, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<Product> {

        private fun showDialog(item: Product) {
            val dialogView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.layout_add_to_cart_dialog, null)
            val dialog = AlertDialog.Builder(itemView.context)
                .setView(dialogView)
                .create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.cv_background)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val tvTitle = dialogView.findViewById<TextView>(R.id.tv_title)
            val tilQuantity = dialogView.findViewById<TextInputLayout>(R.id.til_quantity)
            val etQuantity = dialogView.findViewById<TextInputEditText>(R.id.et_quantity)
            val btnMinus = dialogView.findViewById<ImageButton>(R.id.btn_minus)
            val btnPlus = dialogView.findViewById<ImageButton>(R.id.btn_plus)
            val btnPositive = dialogView.findViewById<TextView>(R.id.btn_positive)

            tvTitle.text = item.name
            etQuantity.setText(context.getString(R.string.quantity_value, 1))

            btnPlus.setOnClickListener {
                val currentQuantity = etQuantity.text.toString().toIntOrNull() ?: 1
                val maxStock = item.stock ?: Int.MAX_VALUE

                if (currentQuantity < maxStock) {
                    etQuantity.setText(
                        context.getString(
                            R.string.quantity_value,
                            currentQuantity + 1
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.max_stock_reached, maxStock),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnMinus.setOnClickListener {
                val currentQuantity = etQuantity.text.toString().toIntOrNull() ?: 1
                if (currentQuantity > 1) {
                    etQuantity.setText(
                        context.getString(
                            R.string.quantity_value,
                            currentQuantity - 1
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.min_quantity_reached),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnPositive.setOnClickListener {
                val quantity = etQuantity.text.toString().toIntOrNull() ?: 1
                val maxStock = item.stock ?: Int.MAX_VALUE

                when {
                    quantity > maxStock -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.max_stock_reached, maxStock),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    quantity < 1 -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.min_quantity_reached),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        addToCartClicked.invoke(item, quantity)
                        dialog.dismiss()
                    }
                }
            }

            dialog.show()
        }

        override fun bind(item: Product) {
            binding.tvProductName.text = item.name
            binding.tvProductPrice.text = context.getString(
                R.string.price_per_pcs,
                item.price?.toCurrencyFormat()
            )
            binding.ivProduct.load(item.image)

            when {
                item.stock == null -> {
                    binding.tvProductStock.text = context.getString(
                        R.string.stock_with_title,
                        context.getString(R.string.unlimited)
                    )
                    binding.btnAdd.isVisible = true
                }

                item.stock > 0 -> {
                    binding.tvProductStock.text =
                        context.getString(R.string.stock_with_title, item.stock.toString())
                    binding.btnAdd.isVisible = true
                }

                item.stock == 0 -> {
                    binding.tvProductStock.text = context.getString(R.string.out_of_stock)
                    binding.btnAdd.isVisible = false
                }
            }

            binding.btnAdd.setOnClickListener {
                showDialog(item)
            }
        }
    }
}
