package kr.moare.android.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "place")

    val masterKeys = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) : DataStore<Preferences>
     = context.dataStore

    @Singleton
    @Provides
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context) : SharedPreferences
    = EncryptedSharedPreferences.create(
        "sharedPreferences",
        masterKeys,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

}