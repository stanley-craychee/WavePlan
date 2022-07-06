package com.waitinglobby.waveplan.ui.beachdetail

import android.content.ContentValues
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.waitinglobby.waveplan.R
import com.waitinglobby.waveplan.data.DataSource.beaches
import com.waitinglobby.waveplan.databinding.FragmentBeachDetailBinding
import com.waitinglobby.waveplan.di.BaseApplication
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BeachDetailFragment : Fragment() {

    private lateinit var binding: FragmentBeachDetailBinding
    private val args: BeachDetailFragmentArgs by navArgs()
    private val viewModel: BeachDetailViewModel by activityViewModels {
        BeachDetailViewModelFactory(
            (activity?.application as BaseApplication).database.favoriteDao()
        )
    }
    private var beachIsFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeachDetailBinding.inflate(inflater)

        // Giving the binding access to the BeachDetailViewModel
        binding.viewModel = viewModel
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment's views
        binding.lifecycleOwner = viewLifecycleOwner

        // Setting the Header image to a picture of the selected beach
        val selectedBeachId = args.selectedBeachID
        binding.imageViewSelectedBeach.setImageResource(beaches[selectedBeachId].imageResourceId)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(ContentValues.TAG, "BeachDetailFragment: entered onViewCreated")

        // Grabbing the SafeArgs value of 'position' representing the list item that the user
        // selected on the Explore page, and pass the beach object to the ViewModel for an API call
        val selectedBeachID = args.selectedBeachID
        CoroutineScope(IO).launch {
            viewModel.callBeachDetailsAPI(beaches[selectedBeachID])
        }
        configureFavoriteButton(selectedBeachID)
        initiateLoadingScreen()
    }

    private fun initiateLoadingScreen() {
        viewModel.loading().observe(this.viewLifecycleOwner) { loading ->
            when (loading) {
                "LOADING..." -> {
                    binding.frameLayoutLoading.visibility = View.VISIBLE
                    binding.frameLayoutError.visibility = View.GONE
                }
                "ERROR" -> {
                    binding.frameLayoutLoading.visibility = View.GONE
                    binding.frameLayoutError.visibility = View.VISIBLE
                }
                "DONE" -> {
                    binding.frameLayoutLoading.visibility = View.GONE
                    binding.frameLayoutError.visibility = View.GONE
                }
            }
        }
    }

    // Sets up the click listener for the favorite icon button
    private fun configureFavoriteButton(position: Int) {

        //Set icon as filled or empty based on if the beach is a favorite
        viewModel.isBeachFavorite(position).observe(this.viewLifecycleOwner) { exists ->
            if (exists == 1) {
                binding.buttonFavorite.background =
                    context?.let { ContextCompat.getDrawable(it, R.drawable.ic_favorite_true) }
                beachIsFavorite = true
            } else if (exists == 0) {
                binding.buttonFavorite.background =
                    context?.let { ContextCompat.getDrawable(it, R.drawable.ic_favorite_false) }
                beachIsFavorite = false
            }
        }

        //Click listener
        binding.buttonFavorite.setOnClickListener {
            if (beachIsFavorite) {
                viewModel.removeFavorite(position)
                updateFavoriteButtonUI(false)
            } else if (!beachIsFavorite) {
                viewModel.addNewFavorite(position)
                updateFavoriteButtonUI(true)
            }
        }
    }

    // Method to evaluate if the current beach is a favorite, and adjust ui accordingly
    private fun updateFavoriteButtonUI(favorite: Boolean) {
        if (favorite) {
            binding.buttonFavorite.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_favorite_true) }
        } else if (!favorite) {
            binding.buttonFavorite.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.ic_favorite_false) }
        }
    }

}