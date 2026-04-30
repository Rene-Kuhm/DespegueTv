package com.despegue.tv.ui.screens.tmdb

import com.despegue.tv.core.tmdb.TmdbEntityBrowseData

sealed interface TmdbEntityBrowseUiState {
    data object Loading : TmdbEntityBrowseUiState
    data class Error(val message: String) : TmdbEntityBrowseUiState
    data class Success(val data: TmdbEntityBrowseData) : TmdbEntityBrowseUiState
}
