package com.waitinglobby.waveplan.ui.country

import android.R.layout
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.waitinglobby.waveplan.data.DataSource.countries
import com.waitinglobby.waveplan.databinding.FragmentCountryBinding
import com.waitinglobby.waveplan.model.Country
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountryFragment : Fragment() {

    private lateinit var binding: FragmentCountryBinding
    private val args: CountryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCountryBinding.inflate(inflater)
        val listView = binding.listViewCountries
        val listOfCountries = createApplicableCountriesList(args.selectedContinentID)
        val arrayOfCountries = createArrayOfCountries(listOfCountries)
        displayListOfCountries(listView, arrayOfCountries)
        createCountriesClickListener(listView, listOfCountries)
        return binding.root
    }

    private fun createApplicableCountriesList(selectedContinentID: Int): List<Country> {
        val listOfCountries: MutableList<Country> = mutableListOf()
        for (i in countries.indices) {
            if (countries[i].parentContinentID == selectedContinentID) {
                listOfCountries.add(countries[i])
            }
        }
        return listOfCountries
    }

    private fun createArrayOfCountries(listOfCountries: List<Country>): Array<String?> {
        val countriesArray: Array<String?> = arrayOfNulls(listOfCountries.size)
        for (i in countriesArray.indices) {
            countriesArray[i] = (listOfCountries[i].name)
        }
        return countriesArray
    }

    private fun displayListOfCountries(listView: ListView, arrayOfCountries: Array<String?>) {
        val arrayAdapter: ArrayAdapter<String>? = activity?.let {
            ArrayAdapter(
                it,
                layout.simple_selectable_list_item,
                arrayOfCountries
            )
        }
        listView.adapter = arrayAdapter
    }

    private fun createCountriesClickListener(listView: ListView, listOfCountries: List<Country>) {
        listView.setOnItemClickListener { _, _, _, id ->
            val action = CountryFragmentDirections.actionCountryFragmentToStateFragment(
                listOfCountries[id.toInt()].id,
                listOfCountries[id.toInt()].name,
                args.selectedContinentID
            )
            findNavController().navigate(action)
        }
    }

}