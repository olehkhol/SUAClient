package sky.tavrov.suaclient.domain.repository

import kotlinx.coroutines.flow.Flow
import sky.tavrov.suaclient.domain.model.ApiRequest
import sky.tavrov.suaclient.domain.model.ApiResponse
import sky.tavrov.suaclient.domain.model.UserUpdate

interface Repository {
    suspend fun saveSignedInState(signedIn: Boolean)
    fun readSignedInState(): Flow<Boolean>
    suspend fun verifyTokenOnBackend(request: ApiRequest): ApiResponse
    suspend fun getUserInfo(): ApiResponse
    suspend fun updateUser(userUpdate: UserUpdate): ApiResponse
    suspend fun deleteUser(): ApiResponse
    suspend fun clearSession(): ApiResponse
}