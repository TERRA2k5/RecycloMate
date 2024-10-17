package com.example.recyclomate.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class MainViewModel: ViewModel() {

    var isMediaManagerInit: Boolean = false
//    var picState: Any? = false

    private val firestoreRepository: FirestoreRepository =FirestoreRepository()

    fun updatePicState(state: Any) = viewModelScope.launch {
        firestoreRepository.updatePicState(state)
    }

//    fun getPicState(): Any?{
//        viewModelScope.launch {
//            picState = firestoreRepository.getPicState()
//        }
//        return picState
//    }

}