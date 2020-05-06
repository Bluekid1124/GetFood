package com.getfood.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.getfood.adapters.RestaurantAdapter
import com.getfood.databinding.FragmentRestaurantListBinding
import com.getfood.utilities.InjectorUtils
import com.getfood.viewmodels.restaurantlist.RestaurantListViewModel
import com.google.android.material.snackbar.Snackbar

class RestaurantListFragment : Fragment() {

    private val args: RestaurantListFragmentArgs by navArgs()
    private lateinit var binding: FragmentRestaurantListBinding

    private val viewModel: RestaurantListViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory(args.queryString)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantListBinding.inflate(inflater, container, false)

        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        val infiniteScrollListener = object : InfiniteScrollListener(linearLayoutManager) {
            override fun isDataLoading(): Boolean {
                val progressModel = viewModel.searchProgress.value
                return progressModel?.isLoading ?: false
            }

            override fun onLoadMore() {
                viewModel.loadMoreData()
            }
        }
        val restaurantAdapter = RestaurantAdapter()
        with(binding.list) {
            layoutManager = linearLayoutManager
            adapter = restaurantAdapter
            addOnScrollListener(infiniteScrollListener)
        }
        initViewModelObservers(restaurantAdapter)
    }

    /**
     * updating data to the adapter.
     * */
    private fun initViewModelObservers(
        adapter: RestaurantAdapter
    ) {
        viewModel.searchProgress.observe(viewLifecycleOwner) {
            if (it.isLoading && adapter.itemCount > 0) {
                adapter.dataStartedLoading()
            } else {
                adapter.dataFinishedLoading()
            }
        }

        viewModel.results.observe(viewLifecycleOwner) { restaurants ->
            binding.progressBar.visibility = View.GONE
            adapter.submitList(restaurants)
        }

        viewModel.errorResult.observe(viewLifecycleOwner) { error ->
            println(error)
            binding.progressBar.visibility = View.GONE
            binding.emptyText.visibility = View.VISIBLE
            Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
        }
    }
}
