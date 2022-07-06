package com.waitinglobby.waveplan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.waitinglobby.waveplan.R
import com.waitinglobby.waveplan.data.DataSource
import com.waitinglobby.waveplan.data.DataSource.beaches
import com.waitinglobby.waveplan.data.Favorite
import com.waitinglobby.waveplan.data.FavoriteDao
import com.waitinglobby.waveplan.model.Beach

class WatchlistCardAdapter(datasetOfFavorites: List<Beach>) :
    RecyclerView.Adapter<WatchlistCardAdapter.WatchlistCardViewHolder>() {

    // Initializes the data using the List of favorites which was passed into this class from the Fragment
    private val dataset: List<Beach> = datasetOfFavorites

    //Set up a click listener to handle selecting a card in the RecyclerView
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(positionClicked: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class WatchlistCardViewHolder(private val view: View, listener: OnItemClickListener) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistCardViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_explore, parent, false)
        return WatchlistCardViewHolder(adapterLayout, mListener)
    }

    override fun onBindViewHolder(holder: WatchlistCardViewHolder, position: Int) {
        // Get the data at the current position, and set the view attributes
        val beach = dataset[position]
        holder.beachImageView.setImageResource(beach.imageResourceId)
        holder.nameTextView.text = beach.name
        holder.locationTextView.text =
            DataSource.cities[beach.parentCityID].name + ", " + DataSource.states[beach.parentStateID].name
    }
}