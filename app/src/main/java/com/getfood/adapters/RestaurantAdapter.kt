package com.getfood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.getfood.R
import com.getfood.data.models.FinalRestaurant
import com.getfood.data.models.Restaurant
import com.getfood.data.models.RestaurantX
import com.getfood.databinding.ListItemRestaurantBinding

class RestaurantAdapter :
    ListAdapter<FinalRestaurant, RecyclerView.ViewHolder>(RestaurantDiffCallback()) {

    private var showLoadingMore = false
    private val loadingMoreItemPosition: Int
        get() = if (showLoadingMore) itemCount - 1 else RecyclerView.NO_POSITION

    var items: Map<String, List<Restaurant>> = emptyMap()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LOADING_MORE -> LoadingMoreHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.infinite_loading, parent, false)
            )
            TYPE_RESTAURANT_VIEW -> RestaurantViewHolder(
                ListItemRestaurantBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            TYPE_HEADER_VIEW -> HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_header, parent, false)
            )
            else -> throw IllegalStateException("Unsupported View type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_LOADING_MORE -> bindLoadingViewHolder(holder as LoadingMoreHolder, position)
            TYPE_RESTAURANT_VIEW -> bindRestaurantViewHolder(
                getItem(position).restaurant.restaurant,
                holder as RestaurantViewHolder,
                position
            )
            TYPE_HEADER_VIEW -> bindHeaderViewHolder(
                getItem(position).headerText,
                holder as HeaderViewHolder
            )
            else -> throw IllegalStateException("Unsupported View type")

        }
    }

    private fun bindHeaderViewHolder(
        headerText: String,
        headerViewHolder: HeaderViewHolder
    ) {
        headerViewHolder.setHeader(headerText)
    }

    private fun bindRestaurantViewHolder(
        restaurant: RestaurantX,
        restaurantViewHolder: RestaurantViewHolder,
        position: Int
    ) {
        restaurantViewHolder.bind(restaurant, currentList.size)
    }

    private fun bindLoadingViewHolder(holder: LoadingMoreHolder, position: Int) {
        holder.setVisibility(if (position > 0 && showLoadingMore && currentList.size > 0) View.VISIBLE else View.INVISIBLE)
    }

    override fun getItemViewType(position: Int): Int {
        if (position < currentList.size && currentList.isNotEmpty()) {
            if (currentList[position].isHeader)
                return TYPE_HEADER_VIEW
            return TYPE_RESTAURANT_VIEW
        }
        return TYPE_LOADING_MORE
    }

    override fun getItemCount(): Int {

        return currentList.size + if (showLoadingMore) 1 else 0
    }

    fun dataStartedLoading() {
        if (showLoadingMore) return
        showLoadingMore = true
        notifyItemInserted(loadingMoreItemPosition)
    }

    fun dataFinishedLoading() {
        if (!showLoadingMore) return
        val loadingPos = loadingMoreItemPosition
        showLoadingMore = false
        notifyItemRemoved(loadingPos)
    }

    private class LoadingMoreHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val progress = itemView as ProgressBar

        fun setVisibility(visibility: Int) {
            progress.visibility = visibility
        }
    }

    private class HeaderViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val headerText = itemView as TextView

        fun setHeader(header: String) {
            headerText.text = header
        }
    }

    class RestaurantViewHolder(
        private val binding: ListItemRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { }
        }

        fun bind(item: RestaurantX, size: Int) {
            binding.apply {
                restaurant = item
                if (adapterPosition == size - 1) {
                    divider.visibility = View.GONE
                } else {
                    divider.visibility = View.VISIBLE
                }
                executePendingBindings()
            }
        }
    }

    companion object {
        private const val TYPE_LOADING_MORE = -1
        private const val TYPE_RESTAURANT_VIEW = 0
        private const val TYPE_HEADER_VIEW = 1
    }
}

private class RestaurantDiffCallback : DiffUtil.ItemCallback<FinalRestaurant>() {
    override fun areItemsTheSame(oldItem: FinalRestaurant, newItem: FinalRestaurant): Boolean {
        return oldItem.restaurant.restaurant.id == newItem.restaurant.restaurant.id
    }

    override fun areContentsTheSame(oldItem: FinalRestaurant, newItem: FinalRestaurant): Boolean {
        return oldItem == newItem
    }
}