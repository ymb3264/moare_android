package kr.moare.android.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val Context.locationDataStore: DataStore<Preferences> by preferencesDataStore(name = "location")
    private val Context.userInfoDataStore: DataStore<Preferences> by preferencesDataStore(name = "userInfo")

    private val masterKeys = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    @Singleton
    @Provides
    @LocationDataStore
    fun provideLocationDataStore(@ApplicationContext context: Context) : DataStore<Preferences>
     = context.locationDataStore

    @Singleton
    @Provides
    @UserInfoDataStore
    fun provideUserInfoDataStore(@ApplicationContext context: Context) : DataStore<Preferences>
    = context.userInfoDataStore

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

    @Singleton
    @Provides
    fun provideChatClient(@ApplicationContext context: Context) : ChatClient {
        val offlinePluginFactory = StreamOfflinePluginFactory(
            config = Config(),
            appContext = context,
        )

        return ChatClient.Builder("xyfmdhzjyjj7", context)
            .withPlugin(offlinePluginFactory)
            .logLevel(ChatLogLevel.ALL)
            .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserInfoDataStore

object PreferencesKey {
    val CURRENTLOCATION = stringPreferencesKey("currentLocation")
    val LOCATIONLIST = stringSetPreferencesKey("locationList")

    val USERNAME =  stringPreferencesKey("username")
    val PROFILE = stringPreferencesKey("profile")
    val ACCOUNTS = stringSetPreferencesKey("accounts")

}
