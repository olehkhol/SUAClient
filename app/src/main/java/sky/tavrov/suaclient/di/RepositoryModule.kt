package sky.tavrov.suaclient.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sky.tavrov.suaclient.data.remote.KtorApi
import sky.tavrov.suaclient.data.repository.DataStoreOperationsImpl
import sky.tavrov.suaclient.data.repository.RepositoryImpl
import sky.tavrov.suaclient.domain.repository.DataStoreOperations
import sky.tavrov.suaclient.domain.repository.Repository
import sky.tavrov.suaclient.util.Constants.PREFERENCES_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDataStorePreferences(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(PREFERENCES_NAME) }

    @Provides
    @Singleton
    fun provideDataStoreOperations(dataStore: DataStore<Preferences>): DataStoreOperations =
        DataStoreOperationsImpl(dataStore)

    @Provides
    @Singleton
    fun provideRepository(dataStoreOperations: DataStoreOperations, ktorApi: KtorApi): Repository =
        RepositoryImpl(dataStoreOperations, ktorApi)
}