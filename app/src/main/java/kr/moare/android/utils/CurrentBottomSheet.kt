package kr.moare.android.utils

sealed class MainCurrentBottomSheet() {
    object Empty: MainCurrentBottomSheet()
    object FindLocation: MainCurrentBottomSheet()
//    object AddPost: MainCurrentBottomSheet()
    object CreateTeamProfile: MainCurrentBottomSheet()
    object UpdateProfile: MainCurrentBottomSheet()
    object MyAccounts: MainCurrentBottomSheet()
}

sealed class SubCurrentBottomSheet() {
    object Empty: SubCurrentBottomSheet()
    object FindLocation: SubCurrentBottomSheet()
    object SearchSport: SubCurrentBottomSheet()
    object Gallery: SubCurrentBottomSheet()
//    object ProfileGallery: SubCurrentBottomSheet()
}