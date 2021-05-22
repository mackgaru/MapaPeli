package com.arts.mapapeli.ui.peliculas

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.RecyclerView
import com.arts.mapapeli.application.BaseViewHolder
import com.arts.mapapeli.application.Constants
import com.arts.mapapeli.data.model.Result
import com.arts.mapapeli.databinding.MovieCardBinding
import com.bumptech.glide.Glide

class AdapterMovies(
    private val context: Context,
    private val moviesList: List<Result>,
    private val itemClickListener: onMovieClickListener
): RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface onMovieClickListener{
        fun onMovieClick(result: Result, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = MovieCardBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = MainViewHolder(itemBinding)
        //return MainViewHolder(LayoutInflater.from(context).inflate(R.layout.tragos_row, parent, false)) //funciona con synthetic

        itemBinding.root.setOnClickListener{
            val position = holder.adapterPosition.takeIf { it != NO_POSITION } ?: return@setOnClickListener
            itemClickListener.onMovieClick(moviesList[position], position)
        }

        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is MainViewHolder -> holder.bind(moviesList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    inner class MainViewHolder(val binding: MovieCardBinding): BaseViewHolder<Result>(binding.root){
        override fun bind(item: Result, position: Int) = with(binding){
            Glide.with(context).load("${Constants.IMG_MOVIE_DB}${item.posterPath}").centerCrop().into(imvMovie)
            tvTituloPelicula.text = item.title
        }
    }
}