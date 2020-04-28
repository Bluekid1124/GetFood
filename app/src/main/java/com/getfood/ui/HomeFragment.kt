package com.getfood.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.getfood.databinding.HomeFragmentBinding
import java.lang.Exception

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = HomeFragmentBinding.inflate(inflater, container, false)

        binding.find.setOnClickListener {
            val queryText: String = binding.searchView.query.toString()
            search(queryText, it)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val queryText: String = binding.searchView.query.toString()
                search(queryText, binding.searchView)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return binding.root
    }

    private fun search(queryText: String, view: View) {
        if (queryText.isNotEmpty()) {
            try {
                val inputMethodManager: InputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val directions =
                HomeFragmentDirections.actionHomeFragmentToRestaurantListFragment(
                    queryText
                )
            view.findNavController().navigate(directions)
        }
    }
}
