package sky.tavrov.suaclient.presentation.screen.profile

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sky.tavrov.suaclient.domain.model.ApiResponse
import sky.tavrov.suaclient.domain.model.MessageBarState
import sky.tavrov.suaclient.domain.model.User
import sky.tavrov.suaclient.domain.repository.Repository
import sky.tavrov.suaclient.util.Constants.MAX_LENGTH
import sky.tavrov.suaclient.util.RequestState
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> get() = _user
    private val _firstName: MutableState<String> = mutableStateOf("")
    val firstName: State<String> get() = _firstName
    private val _lastName: MutableState<String> = mutableStateOf("")
    val lastName: State<String> get() = _lastName
    private val _apiResponse = mutableStateOf(RequestState.Idle as RequestState<ApiResponse>)
    val apiResponse: State<RequestState<ApiResponse>> get() = _apiResponse
    private val _messageBarState = mutableStateOf(MessageBarState())
    val messageBarState: State<MessageBarState> get() = _messageBarState

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        _apiResponse.value = RequestState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getUserInfo()

            withContext(Dispatchers.Main) {
                when {
                    response.error != null -> {
                        _apiResponse.value = RequestState.Error(response.error)
                        _messageBarState.value = MessageBarState(error = response.error)

                        Log.d("ProfileViewModel", response.error.message.toString())
                    }
                    response.user != null -> {
                        val (first, last) = response.user.name.split(" ", limit = 2)
                        _apiResponse.value = RequestState.Success(response)
                        _messageBarState.value = MessageBarState(message = response.message)
                        _user.value = response.user
                        _firstName.value = first
                        _lastName.value = last

                        Log.d("ProfileViewModel", response.message.toString())
                    }
                    else -> {
                        _apiResponse.value = RequestState.Idle

                        Log.d("ProfileViewModel", "Undefined state")
                    }
                }
            }
        }
    }

    fun updateFirstName(newName: String) {
        if (newName.length < MAX_LENGTH) {
            _firstName.value = newName
        }
    }

    fun updateLastName(lastName: String) {
        if (lastName.length < MAX_LENGTH) {
            _lastName.value = lastName
        }
    }


}