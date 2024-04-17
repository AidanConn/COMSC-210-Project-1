package compsci.project.bankapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel(private val name: String) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome back, $name!"
    }
    val text: LiveData<String> = _text
}