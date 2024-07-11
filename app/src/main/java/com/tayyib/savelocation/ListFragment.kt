package com.tayyib.savelocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tayyib.savelocation.Adapter.Adapter
import com.tayyib.savelocation.databinding.FragmentListBinding
import com.tayyib.savelocation.roomdb.Address
import com.tayyib.savelocation.viewModel.ListViewModel

class ListFragment : Fragment() {


    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val adapter = Adapter(arrayListOf())
    private lateinit var viewModel: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater,container,false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.getAllAddress()
        binding.recyclerRow.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRow.adapter = adapter

        observeLiveData()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun observeLiveData(){
        viewModel.addressList.observe(viewLifecycleOwner)
        {
            adapter.besinListesiniGuncelle(it as ArrayList<Address>)
        }

    }



}