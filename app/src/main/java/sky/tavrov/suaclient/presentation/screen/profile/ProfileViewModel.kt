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
import sky.tavrov.suaclient.domain.model.ApiRequest
import sky.tavrov.suaclient.domain.model.ApiResponse
import sky.tavrov.suaclient.domain.model.MessageBarState
import sky.tavrov.suaclient.domain.model.User
import sky.tavrov.suaclient.domain.model.UserUpdate
import sky.tavrov.suaclient.domain.repository.Repository
import sky.tavrov.suaclient.util.Constants.MAX_LENGTH
import sky.tavrov.suaclient.util.RequestState
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _user: MutableState<User?> = mutableStateOf(null)
    val user: State<User?> = _user
    private val _apiResponse = mutableStateOf(RequestState.Idle as RequestState<ApiResponse>)
    val apiResponse: State<RequestState<ApiResponse>> = _apiResponse
    private val _clearSessionResponse =
        mutableStateOf(RequestState.Idle as RequestState<ApiResponse>)
    val clearSessionResponse: State<RequestState<ApiResponse>> = _clearSessionResponse
    private val _messageBarState = mutableStateOf(MessageBarState())
    val messageBarState: State<MessageBarState> = _messageBarState

    init {
        getUserInfo()
    }

    fun deleteUser() {
        _apiResponse.value = RequestState.Loading
        _clearSessionResponse.value = RequestState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.deleteUser()

            updateUIState(response)
            updateSessionState(response)
        }
    }

    fun clearSession() {
        _apiResponse.value = RequestState.Loading
        _clearSessionResponse.value = RequestState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.clearSession()

            updateUIState(response)
            updateSessionState(response)
        }
    }

    fun updateUserInfo() {
        if (_user.value == null) return

        _apiResponse.value = RequestState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getUserInfo()

            response.user?.let {
                verifyAndUpdate(it)
            } ?: updateUIState(response)
        }
    }

    fun updateFirstName(newName: String) {
        if (newName.length < MAX_LENGTH) {
            _user.value = _user.value?.copy(firstName = newName)
        }
    }

    fun updateLastName(newName: String) {
        if (newName.length < MAX_LENGTH) {
            _user.value = _user.value?.copy(lastName = newName)
        }
    }

    fun saveSignedInState(signedIn: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSignedInState(signedIn)
        }
    }

    fun verifyTokenOnBackend(request: ApiRequest) {
        _apiResponse.value = RequestState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val response: ApiResponse = repository.verifyTokenOnBackend(request)

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

    private fun getUserInfo() {
        _apiResponse.value = RequestState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            updateUIState(repository.getUserInfo())
        }
    }

    private suspend fun verifyAndUpdate(currentUser: User) {
        val currentFirstName = _user.value?.firstName
        val currentLastName = _user.value?.lastName

        when {
            currentFirstName.isNullOrEmpty() || currentLastName.isNullOrEmpty() -> {
                updateUIState(ApiResponse(error = EmptyFieldException()))
            }

            currentUser.firstName == currentFirstName && currentUser.lastName == currentLastName -> {
                updateUIState(ApiResponse(error = NothingToUpdateException()))
            }

            else -> {
                val userUpdate = UserUpdate(currentFirstName, currentLastName)
                val response = repository.updateUser(userUpdate)

                updateUIState(response)
            }
        }
    }

    private suspend fun updateUIState(response: ApiResponse) {
        val (success, user, message, error) = response

        withContext(Dispatchers.Main) {
            when {
                !success && error != null -> {
                    _apiResponse.value = RequestState.Error(error)
                    _messageBarState.value = MessageBarState(error = error)
                }

                success && user != null -> {
                    _apiResponse.value = RequestState.Success(response)
                    _messageBarState.value = MessageBarState(message = message)
                    _user.value = user
                }

                else -> {
                    _apiResponse.value = RequestState.Idle
                    _messageBarState.value = MessageBarState()
                }
            }
        }
    }

    private suspend fun updateSessionState(response: ApiResponse) {
        val (success, user, message, error) = response

        withContext(Dispatchers.Main) {
            when {
                !success && error != null -> {
                    _clearSessionResponse.value = RequestState.Error(error)
                }

                success && error == null -> {
                    _clearSessionResponse.value = RequestState.Success(response)
                }

                else -> {
                    _clearSessionResponse.value = RequestState.Idle
                }
            }
        }
    }
}

class EmptyFieldException(
    override val message: String = "Empty Input Field."
) : Exception()

class NothingToUpdateException(
    override val message: String = "Nothing to Update."
) : Exception()