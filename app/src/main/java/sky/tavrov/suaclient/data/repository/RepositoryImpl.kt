package sky.tavrov.suaclient.data.repository

import kotlinx.coroutines.flow.Flow
import sky.tavrov.suaclient.data.remote.KtorApi
import sky.tavrov.suaclient.domain.model.ApiRequest
import sky.tavrov.suaclient.domain.model.ApiResponse
import sky.tavrov.suaclient.domain.model.UserUpdate
import sky.tavrov.suaclient.domain.repository.DataStoreOperations
import sky.tavrov.suaclient.domain.repository.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val dataStoreOperations: DataStoreOperations,
    private val ktorApi: KtorApi
) : Repository {

    override suspend fun saveSignedInState(signedIn: Boolean) {
        dataStoreOperations.saveSignedInState(signedIn)
    }

    override fun readSignedInState(): Flow<Boolean> = dataStoreOperations.readSignedInState()

    override suspend fun verifyTokenOnBackend(request: ApiRequest): ApiResponse =
        safeApiCall { ktorApi.verifyTokenOnBackend(request) }

    override suspend fun getUserInfo(): ApiResponse =
        safeApiCall { ktorApi.getUserInfo() }

    override suspend fun updateUser(userUpdate: UserUpdate): ApiResponse =
        safeApiCall { ktorApi.updateUser(userUpdate) }

    override suspend fun deleteUser(): ApiResponse =
        safeApiCall { ktorApi.deleteUser() }

    override suspend fun clearSession(): ApiResponse =
        safeApiCall { ktorApi.clearSession() }

    private suspend fun safeApiCall(apiCall: suspend () -> ApiResponse): ApiResponse =
        try {
            apiCall.invoke()
        } catch (e: Exception) {
            ApiResponse(error = e)
        }
}