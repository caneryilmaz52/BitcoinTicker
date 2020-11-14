package com.caneryilmazapps.bitcointicker.ui.fragments.detail

import android.app.Activity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.caneryilmazapps.bitcointicker.R
import com.caneryilmazapps.bitcointicker.data.models.response.CoinDetailResponse
import com.caneryilmazapps.bitcointicker.ui.main.MainActivity
import com.caneryilmazapps.bitcointicker.ui.main.MainViewModel
import com.caneryilmazapps.bitcointicker.utils.Resource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CoinDetailFragment : Fragment(R.layout.fragment_coin_detail) {

    private lateinit var coinImage: ImageView
    private lateinit var refreshInterval: EditText
    private lateinit var refreshIntervalDone: ImageButton
    private lateinit var coinName: TextView
    private lateinit var hashAlgorithm: TextView
    private lateinit var description: TextView
    private lateinit var currentPrice: TextView
    private lateinit var priceChangePercentage: TextView
    private lateinit var addFavorite: FloatingActionButton

    @Inject
    lateinit var mainViewModel: MainViewModel

    private val args: CoinDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_coin_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coinImage = view.findViewById(R.id.fr_coin_detail_image_view)
        coinName = view.findViewById(R.id.fr_coin_detail_name)
        hashAlgorithm = view.findViewById(R.id.fr_coin_detail_hash)
        description = view.findViewById(R.id.fr_coin_detail_description)
        currentPrice = view.findViewById(R.id.fr_coin_detail_price)
        priceChangePercentage = view.findViewById(R.id.fr_coin_detail_price_change)
        refreshInterval = view.findViewById(R.id.fr_coin_detail_refresh_interval)
        refreshIntervalDone = view.findViewById(R.id.fr_coin_detail_refresh_interval_done)
        addFavorite = view.findViewById(R.id.fr_coin_detail_add_favorite)

        (requireActivity() as MainActivity).supportActionBar?.title = "Coin Detail"

        setupObserver()
        setupIntervalChange()
        setupAddFavorite()

        mainViewModel.getCoinsDetail(requireContext(), args.coinResponse.coinId)
    }

    private fun setupObserver() {
        //api return a coin detail and update coin detail
        mainViewModel.coinDetail.observe(viewLifecycleOwner, { response ->
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    (requireActivity() as MainActivity).hideLoadingView()
                    if (response.data != null)
                        setCoinDetail(response.data)
                }
                Resource.Status.ERROR -> {
                    (requireActivity() as MainActivity).hideLoadingView()
                    AlertDialog.Builder(requireContext())
                        .setMessage(response.message)
                        .setPositiveButton("Try Again") { dialog, _ ->
                            dialog.dismiss()
                            mainViewModel.getCoinsDetail(requireContext(), args.coinResponse.coinId)
                        }.show()
                }
                Resource.Status.LOADING -> {
                    (requireActivity() as MainActivity).showLoadingView()
                }
            }
        })

        // if user click on add favorite fab than add clicked coin to firebase firestore
        mainViewModel.addFavoriteCoin.observe(viewLifecycleOwner, { response ->
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    (requireActivity() as MainActivity).hideLoadingView()
                    Toast.makeText(requireContext(), response.data, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    (requireActivity() as MainActivity).hideLoadingView()
                    AlertDialog.Builder(requireContext())
                        .setMessage(response.message)
                        .setPositiveButton("Try Again") { dialog, _ ->
                            dialog.dismiss()
                            addToFavoriteCoins()
                        }.show()
                }
                Resource.Status.LOADING -> {
                    (requireActivity() as MainActivity).showLoadingView()
                }
            }
        })

        // if user come from favorite coins fragment and click on add favorite fab than delete clicked coin from firebase firestore
        mainViewModel.deleteFavoriteCoin.observe(viewLifecycleOwner, { response ->
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    (requireActivity() as MainActivity).hideLoadingView()
                    Toast.makeText(requireContext(), response.data, Toast.LENGTH_SHORT).show()
                }
                Resource.Status.ERROR -> {
                    (requireActivity() as MainActivity).hideLoadingView()
                    AlertDialog.Builder(requireContext())
                        .setMessage(response.message)
                        .setPositiveButton("Try Again") { dialog, _ ->
                            dialog.dismiss()
                            deleteFromFavoriteCoins()
                        }.show()
                }
                Resource.Status.LOADING -> {
                    (requireActivity() as MainActivity).showLoadingView()
                }
            }
        })
    }

    private fun setupIntervalChange() {
        refreshIntervalDone.setOnClickListener {
            val interval = refreshInterval.text.toString().toIntOrNull()

            if (refreshInterval.text.isEmpty())
                Toast.makeText(
                    requireContext(),
                    "Enter a value for refresh interval",
                    Toast.LENGTH_SHORT
                ).show()

            if (interval != null) {
                setRefreshInterval(interval)
                Toast.makeText(
                    requireContext(),
                    "Refresh interval set to $interval minute",
                    Toast.LENGTH_SHORT
                ).show()
                refreshInterval.clearFocus()

                val inputMethodManager: InputMethodManager =
                    requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
            }
        }
    }

    private fun setRefreshInterval(interval: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                mainViewModel.getCoinsDetail(requireContext(), args.coinResponse.coinId)
                // 1 second = 1000 millisecond
                // 1 minute = 60000 millisecond
                val delay = interval * 60000
                delay(delay.toLong())
            }
        }
    }

    private fun setupAddFavorite() {
        addFavorite.setOnClickListener {

            if (args.fromFavoriteCoinsFragment)
                deleteFromFavoriteCoins()
            else
                addToFavoriteCoins()
        }
    }

    private fun addToFavoriteCoins() {
        val coin = hashMapOf(
            "id" to args.coinResponse.coinId,
            "name" to args.coinResponse.name,
            "symbol" to args.coinResponse.symbol
        )

        mainViewModel.saveFavoriteCoin(coin)
    }

    private fun deleteFromFavoriteCoins() {
        val coin = hashMapOf(
            "id" to args.coinResponse.coinId,
            "name" to args.coinResponse.name,
            "symbol" to args.coinResponse.symbol
        )

        mainViewModel.deleteFavoriteCoin(coin)
    }


    private fun setCoinDetail(coinDetailResponse: CoinDetailResponse) {
        coinName.text = args.coinResponse.name
        Glide.with(requireContext()).load(coinDetailResponse.image?.imageLarge).into(coinImage)

        if (coinDetailResponse.hashing_algorithm != null)
            hashAlgorithm.text = coinDetailResponse.hashing_algorithm
        else
            hashAlgorithm.text = "No data"

        if (coinDetailResponse.description?.description_en != null && coinDetailResponse.description.description_en != "") {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                description.text = Html.fromHtml(
                    coinDetailResponse.description.description_en,
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                description.text = Html.fromHtml(coinDetailResponse.description.description_en)
            }
        } else {
            description.text = "No data"
        }

        if (coinDetailResponse.marketData?.current_price?.usd != null)
            currentPrice.text = coinDetailResponse.marketData.current_price.usd.toString()
        else
            currentPrice.text = "No data"

        if (coinDetailResponse.marketData?.priceChancePercentage_24h != null)
            priceChangePercentage.text =
                coinDetailResponse.marketData.priceChancePercentage_24h.toString()
        else
            priceChangePercentage.text = "No data"
    }
}