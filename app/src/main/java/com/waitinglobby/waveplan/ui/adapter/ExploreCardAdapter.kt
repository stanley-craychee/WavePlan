package com.waitinglobby.waveplan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.waitinglobby.waveplan.R
import com.waitinglobby.waveplan.data.DataSource.beaches
import com.waitinglobby.waveplan.data.DataSource.cities
import com.waitinglobby.waveplan.data.DataSource.states
import com.waitinglobby.waveplan.model.Beach

class ExploreCardAdapter(datasetOfBeaches: List<Beach>) :
    RecyclerView.Adapter<ExploreCardAdapter.ExploreCardViewHolder>() {

    // Initialize the data using the List found in data/DataSource
    private val dataset: List<Beach> = datasetOfBeaches

    //Set up a click listener to handle selecting a card in the RecyclerView
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class ExploreCardViewHolder(private val view: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        // Declare and initialize all of the list item UI components
        val nameTextView: TextView = view.findViewById(R.id.text_view_list_item_name)
        val locationTextView: TextView = view.findViewById(R.id.text_view_list_item_location)
        val beachImageView: ImageView = view.findViewById(R.id.image_view_list_item)

        //Set up a click listener to handle selecting a card in the RecyclerView
        init {
            view.setOnClickListener { listener.onItemClick(adapterPosition) }
        }
    }

    override fun getItemCount() = dataset.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreCardViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_explore, parent, false)
        return ExploreCardViewHolder(adapterLayout, mListener)
    }

    override fun onBindViewHolder(holder: ExploreCardViewHolder, position: Int) {
        // Get the data at the current position, and set the view attributes
        val beach = dataset[position]
        holder.beachImageView.setImageResource(beach.imageResourceId)
        holder.nameTextView.text = beach.name
        holder.locationTextView.text =
            cities[beach.parentCityID].name + ", " + states[beach.parentStateID].name
    }
}