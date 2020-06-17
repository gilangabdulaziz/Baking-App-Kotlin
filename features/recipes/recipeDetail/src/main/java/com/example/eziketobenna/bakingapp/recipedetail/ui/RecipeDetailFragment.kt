package com.example.eziketobenna.bakingapp.recipedetail.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eziketobenna.bakingapp.core.ext.logD
import com.example.eziketobenna.bakingapp.presentation.mvi.MVIView
import com.example.eziketobenna.bakingapp.recipe.actionBarTitle
import com.example.eziketobenna.bakingapp.recipe.observe
import com.example.eziketobenna.bakingapp.recipe.onBackPress
import com.example.eziketobenna.bakingapp.recipedetail.R
import com.example.eziketobenna.bakingapp.recipedetail.databinding.FragmentRecipeDetailBinding
import com.example.eziketobenna.bakingapp.recipedetail.inject
import com.example.eziketobenna.bakingapp.recipedetail.presentation.RecipeDetailIntent
import com.example.eziketobenna.bakingapp.recipedetail.presentation.RecipeDetailViewModel
import com.example.eziketobenna.bakingapp.recipedetail.presentation.RecipeDetailViewState
import com.example.eziketobenna.bakingapp.recipedetail.ui.adapter.IngredientStepAdapter
import com.example.eziketobenna.bakingapp.views.viewBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import reactivecircus.flowbinding.lifecycle.events

class RecipeDetailFragment : Fragment(R.layout.fragment_recipe_detail),
    MVIView<RecipeDetailIntent, RecipeDetailViewState> {

    private val args by navArgs<RecipeDetailFragmentArgs>()

    @Inject
    lateinit var viewModel: RecipeDetailViewModel

    @Inject
    lateinit var ingredientStepAdapter: IngredientStepAdapter

    private val binding: FragmentRecipeDetailBinding by viewBinding(FragmentRecipeDetailBinding::bind)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.processIntent(intents)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPress(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        actionBarTitle = args.recipe.name
        binding.detailRv.adapter = ingredientStepAdapter
        viewModel.viewState.observe(viewLifecycleOwner, ::render)
    }

    override fun render(state: RecipeDetailViewState) {
        when (state) {
            RecipeDetailViewState.Idle -> {
            }
            is RecipeDetailViewState.Success -> {
                logD(state.model.toString())
                ingredientStepAdapter.submitList(state.model)
            }
        }
    }

    private val loadRecipeDetailIntent: Flow<RecipeDetailIntent.LoadRecipeDetailIntent>
        get() = lifecycle.events().filter {
            it == Lifecycle.Event.ON_CREATE
        }.map {
            RecipeDetailIntent.LoadRecipeDetailIntent(args.recipe)
        }

    override val intents: Flow<RecipeDetailIntent>
        get() = merge(loadRecipeDetailIntent)
}
