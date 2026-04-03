package com.kaixinchen.githubclient.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.kaixinchen.githubclient.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        Constants.Storage.AUTH_PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(Constants.Storage.KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(Constants.Storage.KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(Constants.Storage.KEY_TOKEN).apply()
    }
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }
}
