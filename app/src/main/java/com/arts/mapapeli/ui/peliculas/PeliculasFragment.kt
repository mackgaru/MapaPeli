package com.arts.mapapeli.ui.peliculas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arts.mapapeli.R
import com.arts.mapapeli.application.Resource
import com.arts.mapapeli.data.model.MovieNowPlaying
import com.arts.mapapeli.data.model.Result
import com.arts.mapapeli.data.source.DataSource
import com.arts.mapapeli.databinding.FragmentPeliculasBinding
import com.arts.mapapeli.domain.remote.RepositoryMovieImpl
import com.arts.mapapeli.presentation.PeliculasViewModel
import com.arts.mapapeli.presentation.PeliculasViewModelFactory

class PeliculasFragment : Fragment(), AdapterMovies.onMovieClickListener {

    private var _binding: FragmentPeliculasBinding? = null
    private val binding get() = _binding!!
    private val peliculaViewModel by viewModels<PeliculasViewModel> {PeliculasViewModelFactory(
        RepositoryMovieImpl(DataSource())
    )}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPeliculasBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupRecyclerView(){
        binding.rvMovieNowPlaying.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvMovieNowPlaying.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }
    fun setupObservers(){
        peliculaViewModel.movieNowPlaying().observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading ->{
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success ->{
                    binding.progressBar.visibility = View.GONE
                    binding.rvMovieNowPlaying.adapter = AdapterMovies(requireContext(),  it.data.data.results, this)
                }
                is Resource.Failure ->{
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error extracion de datos ${it.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onMovieClick(result: Result, position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("result", result)
        findNavController().navigate(R.id.action_navigation_peliculas_to_peliculaDetalleFragment, bundle)
    }
}