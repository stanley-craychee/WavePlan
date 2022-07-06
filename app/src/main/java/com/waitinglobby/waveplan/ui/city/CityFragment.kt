package com.waitinglobby.waveplan.ui.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.waitinglobby.waveplan.data.DataSource.cities
import com.waitinglobby.waveplan.databinding.FragmentCityBinding
import com.waitinglobby.waveplan.model.City

class CityFragment : Fragment() {

    private lateinit var binding: FragmentCityBinding
    private val args: CityFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCityBinding.inflate(inflater)
        val listView = binding.listViewCities
        val listOfCities = createApplicableCitiesList(args.selectedStateID)
        val arrayOfCities = createArrayOfCities(listOfCities)
        displayListOfCities(listView, arrayOfCities)
        createCitiesClickListener(listView, listOfCities)
        return binding.root
    }

    private fun createApplicableCitiesList(selectedStateID: Int): List<City> {
        val listOfCities: MutableList<City> = mutableListOf()
        for (i in cities.indices) {
            if (cities[i].parentStateID == selectedStateID) {
                listOfCities.add(cities[i])
            }
        }
        return listOfCities
    }

    private fun createArrayOfCities(listOfCities: List<City>): Array<String?> {
        val citiesArray: Array<String?> = arrayOfNulls(listOfCities.size)
        for (i in citiesArray.indices) {
            citiesArray[i] = (listOfCities[i].name)
        }
        return citiesArray
    }

    private fun displayListOfCities(listView: ListView, arrayOfCities: Array<String?>) {
        val arrayAdapter: ArrayAdapter<String>? = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_selectable_list_item,
                arrayOfCities
            )
        }
        listView.adapter = arrayAdapter
    }

    private fun createCitiesClickListener(listView: ListView, listOfCities: List<City>) {
        listView.setOnItemClickListener { _, _, _, id ->
            val action = CityFragmentDirections.actionCityFragmentToExploreFragment(
                listOfCities[id.toInt()].name,
                args.selectedContinentID,
                args.selectedCountryID,
                args.selectedStateID,
                listOfCities[id.toInt()].id
            )
            findNavController().navigate(action)
        }
    }

}