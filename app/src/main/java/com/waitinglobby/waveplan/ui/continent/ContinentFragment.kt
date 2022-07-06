package com.waitinglobby.waveplan.ui.continent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import com.waitinglobby.waveplan.data.DataSource.continents
import com.waitinglobby.waveplan.databinding.FragmentContinentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContinentFragment : Fragment() {

    private lateinit var binding: FragmentContinentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContinentBinding.inflate(inflater)
        val listView = binding.listViewContinents
        displayListOfContinents(listView)
        createContinentsClickListener(listView)
        return binding.root
    }

    private fun createContinentsClickListener(listView: ListView) {
        listView.setOnItemClickListener { _, _, _, id ->
            val action = ContinentFragmentDirections.actionContinentFragmentToCountryFragment(
                id.toInt(),
                continents[id.toInt()].name
            )
            findNavController().navigate(action)
        }
    }

    private fun displayListOfContinents(listView: ListView) {
        val listOfContinents: Array<String?> = createContinentsList()
        val arrayAdapter: ArrayAdapter<String>? = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_selectable_list_item,
                listOfContinents
            )
        }
        listView.adapter = arrayAdapter
    }

    private fun createContinentsList(): Array<String?> {
        val continentsList: Array<String?> = arrayOfNulls(continents.size)
        for (i in continents.indices) {
            continentsList[i] = (continents[i].name)
        }
        return continentsList
    }

}