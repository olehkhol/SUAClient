package sky.tavrov.suaclient.data.repository

import kotlinx.coroutines.flow.Flow
import sky.tavrov.suaclient.domain.repository.DataStoreOperations
import sky.tavrov.suaclient.domain.repository.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val dataStore: DataStoreOperations
) : Repository {

    override suspend fun saveSignedInState(signedIn: Boolean) {
        dataStore.saveSignedInState(signedIn)
    }

    override fun readSignedInState(): Flow<Boolean> = dataStore.readSignedInState()
}