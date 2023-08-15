package sky.tavrov.suaclient.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import sky.tavrov.suaclient.domain.repository.DataStoreOperations
import sky.tavrov.suaclient.util.Constants
import java.io.IOException
import javax.inject.Inject

class DataStoreOperationsImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DataStoreOperations {

    private object PreferencesKey {
        val signedInKey = booleanPreferencesKey(name = Constants.PREFERENCES_SIGNED_IN_KEY)
    }

    override suspend fun saveSignedInState(signedIn: Boolean) {
        dataStore.edit {
            it[PreferencesKey.signedInKey] = signedIn
        }
    }

    override fun readSignedInState(): Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            it[PreferencesKey.signedInKey] ?: false
        }
}