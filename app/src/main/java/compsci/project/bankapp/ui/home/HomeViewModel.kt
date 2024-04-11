package compsci.project.bankapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel(private val username: String) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome back, $username!"
    }
    val text: LiveData<String> = _text
}