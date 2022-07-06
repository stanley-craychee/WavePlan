package com.waitinglobby.waveplan.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.waitinglobby.waveplan.data.DataSource
import com.waitinglobby.waveplan.data.DataSource.beaches
import com.waitinglobby.waveplan.data.FavoriteDao
import com.waitinglobby.waveplan.databinding.FragmentWatchlistBinding
import com.waitinglobby.waveplan.di.BaseApplication
import com.waitinglobby.waveplan.model.Beach
import com.waitinglobby.waveplan.ui.adapter.WatchlistCardAdapter
import com.waitinglobby.waveplan.ui.beachdetail.BeachDetailViewModel
import com.waitinglobby.waveplan.ui.beachdetail.BeachDetailViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class WatchlistFragment() : Fragment() {

    private lateinit var binding: FragmentWatchlistBinding
    private val viewModel: WatchlistViewModel by activityViewModels {
        WatchlistViewModelFactory(
            (activity?.application as BaseApplication).database.favoriteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWatchlistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Ensures that the ViewModel has the latest list of favorites when the view is recreated
        CoroutineScope(IO).launch {
            updateUI()
        }
    }

    private suspend fun updateUI() {
        updateViewModel()
        updateRecyclerView()
    }

    private suspend fun updateViewModel() {
        viewModel.updateDatasetOfFavorites()
    }

    private suspend fun updateRecyclerView() {
        CoroutineScope(Main).launch {
            // Set the RecyclerView adapter using the custom ExploreCardAdapter
            val adapter = WatchlistCardAdapter(viewModel.datasetOfFavorites)
            binding.recyclerViewWatchlist.adapter = adapter

            if (viewModel.datasetOfFavorites.isNotEmpty()) {
                binding.textViewNoFavoritesMessage.visibility=View.GONE
            }

            // RecyclerView click handling. Navigate to the BeachDetailFragment screen using the
            // ID of the Beach that the user selected, and also pass the name of the beach as
            // an arg so the fragment label (in the nav graph) can use it in the app bar title
            adapter.setOnItemClickListener(object : WatchlistCardAdapter.OnItemClickListener {
                override fun onItemClick(positionClicked: Int) {
                    CoroutineScope(Main).launch {
                        val selectedBeachID = viewModel.getIdOfFavorite(positionClicked)
                        val userSelectedBeach: String = beaches[selectedBeachID].name
                        val action =
                            WatchlistFragmentDirections.actionWatchlistFragmentToBeachDetailFragment(
                                selectedBeachID,
                                userSelectedBeach
                            )
                        findNavController().navigate(action)
                    }
                }
            })
        }
    }

}

