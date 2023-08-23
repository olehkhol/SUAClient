package sky.tavrov.suaclient.presentation.screen.login

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
import sky.tavrov.suaclient.domain.model.ApiRequest
import sky.tavrov.suaclient.domain.model.ApiResponse
import sky.tavrov.suaclient.domain.model.MessageBarState
import sky.tavrov.suaclient.domain.repository.Repository
import sky.tavrov.suaclient.util.RequestState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _signedInState: MutableState<Boolean> = mutableStateOf(false)
    val signedInState: State<Boolean> = _signedInState
    private val _messageBarState: MutableState<MessageBarState> = mutableStateOf(MessageBarState())
    val messageBarState: State<MessageBarState> = _messageBarState
    private val _apiResponse: MutableState<RequestState<ApiResponse>> =
        mutableStateOf(RequestState.Idle)
    val apiResponse: State<RequestState<ApiResponse>> = _apiResponse

    init {
        viewModelScope.launch {
            repository.readSignedInState().collect {
                _signedInState.value = it
            }
        }
    }

    fun saveSignedInState(signedIn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSignedInState(signedIn)
        }
    }

    fun updateMessageBarState() {
        _messageBarState.value = MessageBarState(error = GoogleAccountNotFoundException())
    }

    fun verifyTokenOnBackend(request: ApiRequest) {
        _apiResponse.value = RequestState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.verifyTokenOnBackend(request)

            withContext(Dispatchers.Main) {
                if (response.error != null) {
                    _apiResponse.value = RequestState.Error(response.error)
                    _messageBarState.value = MessageBarState(error = response.error)

                    Log.d("LoginViewModel", response.error.message.toString())
                } else {
                    _apiResponse.value = RequestState.Success(response)
                    _messageBarState.value = MessageBarState(message = response.message)

                    Log.d("LoginViewModel", response.message.toString())
                }
            }
        }
    }
}

class GoogleAccountNotFoundException(
    override val message: String? = "Google Account not found."
) : Exception()