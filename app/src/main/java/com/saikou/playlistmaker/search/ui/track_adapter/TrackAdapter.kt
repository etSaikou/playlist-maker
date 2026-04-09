package com.saikou.playlistmaker.search.ui.track_adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saikou.playlistmaker.search.data.entity.Track

class TrackAdapter(
                   private val onItemClicked: (track: Track) -> Unit
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