package com.rayhan.trackmydiet.ui.receipe

import android.os.Bundle
 import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rayhan.trackmydiet.adapter.RecipeAdapter
import com.rayhan.trackmydiet.databinding.FragmentReceipeBinding

class ReceipeFragment : Fragment() {
    private var _binding: FragmentReceipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvRecipe: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var viewModel: ReceipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(ReceipeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReceipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecipeAdapter(ArrayList())

        rvRecipe = binding.rvReceipeResult
        rvRecipe.adapter = adapter
        rvRecipe.layoutManager = GridLayoutManager(context, 2)

        binding.btnSearch.setOnClickListener {
            val text = binding.etFoodSearch.text.toString()
            rvRecipe.removeAllViews()
            rvRecipe.adapter = adapter
            viewModel.retrieveEdamamFoodInformation(text)
        }

        viewModel.recipeResult.observe(viewLifecycleOwner, { recipeResult ->
            if (recipeResult.isNotEmpty()) {
                rvRecipe.isEnabled = true
                rvRecipe.isVisible = true
                binding.tvRecipeNotFound.isVisible = false
                binding.tvRecipeNotFound.isEnabled = false
            } else {
                rvRecipe.isVisible = false
                rvRecipe.isEnabled = false
                binding.tvRecipeNotFound.isVisible = true
            }

            adapter.setData(recipeResult)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
