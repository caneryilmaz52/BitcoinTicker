package com.caneryilmazapps.bitcointicker.ui.fragments.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caneryilmazapps.bitcointicker.R
import com.caneryilmazapps.bitcointicker.adapters.CoinListAdapter
import com.caneryilmazapps.bitcointicker.ui.main.MainActivity
import com.caneryilmazapps.bitcointicker.ui.main.MainViewModel
import com.caneryilmazapps.bitcointicker.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteCoinsFragment : Fragment(R.layout.fragment_favorite_coins) {

    private lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var coinListAdapter: CoinListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_coins, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.fr_favorite_coins_recycler_view)

        (requireActivity() as MainActivity).supportActionBar?.title = "Favorite Coins"

        setupObservers()
        setupRecyclerView()

        mainViewModel.getFavoriteCoins()

        coinListAdapter.setOnItemClickListener { coinItem ->
            val bundle = Bundle().apply {
                putSerializable("coinResponse", coinItem)
                putBoolean("fromFavoriteCoinsFragment", true)
            }

            findNavController().navigate(
                R.id.action_favoriteCoinsFragment_to_coinDetailFragment,
                bundle
            )
        }
    }

    private fun setupObservers() {
        //if firebase firestore return a coin list than show that list via recycler view
        mainViewModel.getFavoriteCoins.observe(viewLifecycleOwner, { response ->
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    (requireActivity() as MainActivity).hideLoadingView()
                    coinListAdapter.differ.submitList(response.data!!)
                    recyclerView.scheduleLayoutAnimation()
                }
                Resource.Status.ERROR -> {
                    (requireActivity() as MainActivity).hideLoadingView()
                    AlertDialog.Builder(requireContext())
                        .setMessage(response.message)
                        .setPositiveButton("Try Again") { dialog, _ ->
                            dialog.dismiss()
                            mainViewModel.getFavoriteCoins()
                        }.setNegativeButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }
                Resource.Status.LOADING -> {
                    (requireActivity() as MainActivity).showLoadingView()
                }
            }
        })
    }

    private fun setupRecyclerView() {
        val layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down)
        recyclerView.apply {
            adapter = coinListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            layoutAnimation = layoutAnimationController
        }
    }
}