package github.karchx.motto.views.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import github.karchx.motto.databinding.FragmentDashboardLayoutBinding
import github.karchx.motto.models.storages.Constants
import github.karchx.motto.models.storages.Constants.Companion.MOTTO_TYPES_ICONS
import github.karchx.motto.views.MainActivity
import github.karchx.motto.views.tools.adapters.ViewPagerAdapter

class DashboardLayoutFragment : Fragment() {

    private var _binding: FragmentDashboardLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViewPagerAndTabLayout()
    }

    private fun setViewPagerAndTabLayout() {
        initViewPagerAndTabLayout()

        val adapter = ViewPagerAdapter(requireActivity() as MainActivity)

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = Constants.getMottoTypesNames(requireContext())[position]
            tab.icon = ResourcesCompat.getDrawable(
                resources,
                MOTTO_TYPES_ICONS[position],
                requireContext().theme
            )
        }.attach()
    }

    private fun initViewPagerAndTabLayout() {
        viewPager = binding.pagerMottoTypes
        tabLayout = binding.tabLayoutMottoTypes
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
