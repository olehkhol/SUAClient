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
    val user: State<User?> = _user
    private val _apiResponse = mutableStateOf(RequestState.Idle as RequestState<ApiResponse>)
    val apiResponse: State<RequestState<ApiResponse>> = _apiResponse
    private val _messageBarState = mutableStateOf(MessageBarState())
    val messageBarState: State<MessageBarState> = _messageBarState

    init {
        getUserInfo()
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