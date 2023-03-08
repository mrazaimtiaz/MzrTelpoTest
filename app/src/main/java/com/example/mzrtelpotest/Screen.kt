package com.gicproject.onepagequeuekioskapp

sealed class Screen(val route: String){
    object SettingScreen: Screen("setting_screen")
    object SelectServiceScreen: Screen("select_service_screen")
}
