package com.example.connect.viewModel

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.connect.fragments.FeedFragment

class HomeActivityViewModel : ViewModel() {
    private var fragmentMutable : MutableLiveData<Fragment> = MutableLiveData(FeedFragment())
    fun fragment(): LiveData<Fragment>{
        return fragmentMutable
    }

    fun setCurrFragment(fragment: Fragment){
        fragmentMutable.value = fragment
    }
}