package com.kaixinchen.githubclient.util

object Constants {
    object GitHub {
        const val BASE_URL = "https://api.github.com/"
        const val API_VERSION = "application/vnd.github.v3+json"
        const val AUTH_HEADER = "Authorization"
        const val AUTH_PREFIX = "Bearer"
    }

    object Storage {
        const val AUTH_PREFS_NAME = "github_auth_prefs"
        const val KEY_TOKEN = "KEY_TOKEN"
    }

    object Search {
        const val MIN_SEARCH_LENGTH = 1
        const val POPULAR_REPOS_QUERY = "stars:>50000"
        const val DEFAULT_SORT = "stars"
        const val DEFAULT_ORDER = "desc"
    }

    object Repositories {
        const val DEFAULT_SORT = "updated"
        const val DEFAULT_VISIBILITY = "all"
    }

    object UI {
        const val AVATAR_SIZE_DP = 24
        const val AVATAR_BORDER_WIDTH_DP = 0.5f
        const val BUTTON_CORNER_RADIUS_DP = 12
        const val ICON_SIZE_DP = 48
        const val SPACING_DP = 8
        const val PADDING_DP = 16
    }

    object Error {
        const val DEFAULT_ERROR_MESSAGE = "An error occurred"
        const val NETWORK_ERROR_MESSAGE = "Network error. Please check your connection."
        const val AUTH_ERROR_MESSAGE = "Authentication failed. Please check your token."
        const val UNKNOWN_ERROR_MESSAGE = "Unknown error occurred"
    }

    object Validation {
        const val MIN_TOKEN_LENGTH = 10
        const val MAX_TOKEN_LENGTH = 100
    }
}