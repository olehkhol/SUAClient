package sky.tavrov.suaclient.presentation.screen.profile

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

    fun updateUserInfo() {
        if (_user.value == null) return

        _apiResponse.value = RequestState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            verifyAndUpdate(repository.getUserInfo())
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

    private fun getUserInfo() {
        _apiResponse.value = RequestState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getUserInfo()

            updateUIState(response)
        }
    }

    private fun verifyAndUpdate(currentUser: ApiResponse) {
        val (verified, exception) = if (_firstName.value.isEmpty() || _lastName.value.isEmpty()) {
            false to EmptyFieldException()
        } else {
            val firstName = currentUser.user?.firstName
            val lastName = currentUser.user?.lastName

            if (firstName == _firstName.value && lastName == _lastName.value) {
                false to NothingToUpdateException()
            } else {
                true to null
            }
        }

        if (verified) {
            viewModelScope.launch(Dispatchers.IO) {
                val response = repository.updateUser(UserUpdate(_firstName.value, _lastName.value))

                updateUIState(response)
            }
        } else {
            _apiResponse.value = RequestState.Success(ApiResponse(error = exception))
            _messageBarState.value = MessageBarState(error = exception)
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
                    val firstName = user.firstName
                    val lastName = user.lastName
                    _apiResponse.value = RequestState.Success(response)
                    _messageBarState.value = MessageBarState(message = message)
                    _user.value = user
                    _firstName.value = firstName
                    _lastName.value = lastName
                }

                else -> {
                    _apiResponse.value = RequestState.Idle
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