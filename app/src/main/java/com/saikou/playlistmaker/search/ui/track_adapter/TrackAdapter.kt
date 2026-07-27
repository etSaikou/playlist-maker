package com.saikou.playlistmaker.search.ui.track_adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saikou.playlistmaker.search.data.entity.Track
import java.util.ArrayList

class TrackAdapter(
    private val onItemClicked: (track: Track) -> Unit,
    private val onItemLongClicked: ((track: Track) -> Unit)? = null
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    private val list: MutableList<Track> = ArrayList<Track>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            onItemClicked(list[position])
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClicked?.invoke(list[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun load(list: List<Track>?) {
        this.list.clear()
        this.list.addAll(list ?: ArrayList())
        notifyDataSetChanged()
    }


}