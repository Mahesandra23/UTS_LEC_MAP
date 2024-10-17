package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BannerPagerAdapter
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.FragmentHomeBinding
import com.example.uts_lec_map.models.Book
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupViewPager()
        setupRecyclerViews()
        setupBottomNavigation() // Menyiapkan BottomNavigationView

        return binding.root
    }

    private fun setupViewPager() {
        // Set banner ViewPager dengan drawable placeholder
        val bannerImages = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
        val bannerPagerAdapter = BannerPagerAdapter(requireContext(), bannerImages)
        binding.viewPager.adapter = bannerPagerAdapter
    }

    private fun setupRecyclerViews() {
        // Set RecyclerView untuk trending books
        val trendingBooks = getTrendingBooks()
        binding.trendingRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val trendingAdapter = BookAdapter(requireContext(), trendingBooks) { book ->
            // Navigate to detail book page when a trending book is clicked
            findNavController().navigate(R.id.action_homeFragment_to_detailBookFragment)
        }
        binding.trendingRecyclerView.adapter = trendingAdapter

        // Set RecyclerView untuk preference books
        val preferenceBooks = getPreferenceBooks()
        binding.preferencesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val preferenceAdapter = BookAdapter(requireContext(), preferenceBooks) { book ->
            // Navigate to detail book page when a preference book is clicked
            findNavController().navigate(R.id.action_homeFragment_to_detailBookFragment)
        }
        binding.preferencesRecyclerView.adapter = preferenceAdapter
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.home // Mark Search as selected
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Navigate to Home
                    true
                }
                R.id.search -> {
                    findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                    true
                }
                R.id.history -> {
                    findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun getTrendingBooks(): List<Book> {
        return listOf(
            Book("Solo Leveling", android.R.drawable.ic_menu_gallery),
            Book("Harry Potter", android.R.drawable.ic_menu_camera)
        )
    }

    private fun getPreferenceBooks(): List<Book> {
        return listOf(
            Book("Bukan Aku yang Dia Inginkan", android.R.drawable.ic_menu_gallery),
            Book("Harry Potter", android.R.drawable.ic_menu_camera)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
