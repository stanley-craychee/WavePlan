package com.waitinglobby.waveplan.ui.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.waitinglobby.waveplan.data.DataSource
import com.waitinglobby.waveplan.ui.adapter.ExploreCardAdapter
import com.waitinglobby.waveplan.data.DataSource.beaches
import com.waitinglobby.waveplan.databinding.FragmentExploreBinding
import com.waitinglobby.waveplan.model.Beach
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private lateinit var binding: FragmentExploreBinding
    private val args: ExploreFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(inflater)

        // Create a dataset of beaches that apply, given the city that the user selected in the
        // previous fragment.
        val listOfBeaches = createApplicableBeachesList(args.selectedCityID)
        // Set the RecyclerView adapter using the custom ExploreCardAdapter
        val adapter = ExploreCardAdapter(listOfBeaches)
        // Display the list of beaches by assigning the customer adapter to the RecyclerView
        binding.recyclerViewExplore.adapter = adapter
        // Create a click listener
        createBeachesClickListener(listOfBeaches, adapter)
        return binding.root
    }

    private fun createBeachesClickListener(
        listOfBeaches: List<Beach>,
        adapter: ExploreCardAdapter
    ) {
        //RecyclerView click handling. Navigate to the BeachDetailFragment screen using
        //the position of the list item that the user selected, and also pass the name of the
        //selected beach as an arg so the fragment label can use it in the app bar title
        adapter.setOnItemClickListener(object : ExploreCardAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val userSelectedBeach: String = listOfBeaches[position].name
                val selectedBeachID: Int = listOfBeaches[position].id
                val action = ExploreFragmentDirections.actionExploreFragmentToBeachDetailFragment(
                    selectedBeachID,
                    userSelectedBeach
                )
                findNavController().navigate(action)
            }
        })
    }

    private fun createApplicableBeachesList(selectedCityID: Int): List<Beach> {
        val listOfBeaches: MutableList<Beach> = mutableListOf()
        for (i in DataSource.beaches.indices) {
            if (DataSource.beaches[i].parentCityID == selectedCityID) {
                listOfBeaches.add(DataSource.beaches[i])
            }
        }
        return listOfBeaches
    }
}