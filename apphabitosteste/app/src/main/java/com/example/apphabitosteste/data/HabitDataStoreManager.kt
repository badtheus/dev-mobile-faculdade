package com.example.apphabitosteste.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey // Importante para chaves booleanas
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Extensão para Context para criar uma instância de DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "habit_preferences")

class HabitDataStoreManager(private val context: Context) {

    // Chaves para o DataStore
    private val CALENDAR_TABS_KEY = stringPreferencesKey("calendar_tabs")
    private fun getCalendarDataKey(calendarName: String) = stringPreferencesKey("${calendarName}_data")

    // NOVO: Chave para a preferência do tema
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")

    private val gson = Gson()
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Carrega todos os nomes de calendários salvos.
     */
    suspend fun loadCalendarTabs(): MutableList<String> {
        val json = context.dataStore.data.map { preferences ->
            preferences[CALENDAR_TABS_KEY] ?: "[]"
        }.first()
        return gson.fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
    }

    /**
     * Salva a lista de nomes de calendários.
     */
    suspend fun saveCalendarTabs(tabs: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[CALENDAR_TABS_KEY] = gson.toJson(tabs)
        }
    }

    /**
     * Carrega os dados de marcação para um calendário específico.
     * Retorna um mapa de LocalDate para Boolean.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadCalendarData(calendarName: String): MutableMap<LocalDate, Boolean> {
        val json = context.dataStore.data.map { preferences ->
            preferences[getCalendarDataKey(calendarName)] ?: "{}"
        }.first()
        val type = object : TypeToken<Map<String, Boolean>>() {}.type
        val stringMap: Map<String, Boolean> = gson.fromJson(json, type) ?: emptyMap()

        return stringMap.mapKeys { (dateString, _) ->
            LocalDate.parse(dateString, dateFormatter)
        }.toMutableMap()
    }

    /**
     * Salva os dados de marcação para um calendário específico.
     * Recebe um mapa de LocalDate para Boolean.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveCalendarData(calendarName: String, data: Map<LocalDate, Boolean>) {
        context.dataStore.edit { preferences ->
            val stringMap = data.mapKeys { (localDate, _) ->
                localDate.format(dateFormatter)
            }
            preferences[getCalendarDataKey(calendarName)] = gson.toJson(stringMap)
        }
    }

    /**
     * Remove os dados de um calendário específico.
     */
    suspend fun removeCalendarData(calendarName: String) {
        context.dataStore.edit { preferences ->
            preferences.remove(getCalendarDataKey(calendarName))
        }
    }

    // NOVAS FUNÇÕES PARA GERENCIAR A PREFERÊNCIA DO TEMA

    /**
     * Carrega a preferência do tema do DataStore.
     * Retorna true se o modo noturno estiver ativado, false caso contrário (padrão é false - tema claro).
     */
    suspend fun loadDarkThemePreference(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }.first()
    }

    /**
     * Salva a preferência do tema no DataStore.
     */
    suspend fun saveDarkThemePreference(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDark
        }
    }

    /**
     * Limpa todos os dados armazenados no DataStore (calendários, marcações e tema).
     */
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear() // Remove todas as chaves e valores do DataStore
        }
    }
}