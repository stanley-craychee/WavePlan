package com.waitinglobby.waveplan.ui.state

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.waitinglobby.waveplan.data.DataSource.states
import com.waitinglobby.waveplan.databinding.FragmentStateBinding
import com.waitinglobby.waveplan.model.State
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StateFragment : Fragment() {

    private lateinit var binding: FragmentStateBinding
    private val args: StateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStateBinding.inflate(inflater)
        val listView = binding.listViewStates
        val listOfStates = createApplicableStatesList(args.selectedCountryID)
        val arrayOfStates = createArrayOfStates(listOfStates)
        displayListOfStates(listView, arrayOfStates)
        createStatesClickListener(listView, listOfStates)
        return binding.root
    }

    private fun createApplicableStatesList(selectedCountryID: Int): List<State> {
        val listOfStates: MutableList<State> = mutableListOf()
        for (i in states.indices) {
            if (states[i].parentCountryID == selectedCountryID) {
                listOfStates.add(states[i])
            }
        }
        return listOfStates
    }

    private fun createArrayOfStates(listOfStates: List<State>): Array<String?> {
        val statesArray: Array<String?> = arrayOfNulls(listOfStates.size)
        for (i in statesArray.indices) {
            statesArray[i] = (listOfStates[i].name)
        }
        return statesArray
    }

    private fun displayListOfStates(listView: ListView, arrayOfStates: Array<String?>) {
        val arrayAdapter: ArrayAdapter<String>? = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_selectable_list_item,
                arrayOfStates
            )
        }
        listView.adapter = arrayAdapter
    }

    private fun createStatesClickListener(listView: ListView, listOfStates: List<State>) {
        listView.setOnItemClickListener { _, _, _, id ->
            val action = StateFragmentDirections.actionStateFragmentToCityFragment(
                listOfStates[id.toInt()].name,
                args.selectedContinentID,
                args.selectedCountryID,
                listOfStates[id.toInt()].id
            )
            findNavController().navigate(action)
        }
    }

}