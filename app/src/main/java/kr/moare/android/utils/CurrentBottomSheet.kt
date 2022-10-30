package kr.moare.android.utils

sealed class MainCurrentBottomSheet() {
    object Empty: MainCurrentBottomSheet()
    object FindLocation: MainCurrentBottomSheet()
//    object AddPost: MainCurrentBottomSheet()
    object MakeTeamProfile: MainCurrentBottomSheet()
    object UpdateMyProfile: MainCurrentBottomSheet()
    object UpdateTeamProfile: MainCurrentBottomSheet()
}

sealed class SubCurrentBottomSheet() {
    object Empty: SubCurrentBottomSheet()
    object FindLocation: SubCurrentBottomSheet()
    object SearchSport: SubCurrentBottomSheet()
    object Gallery: SubCurrentBottomSheet()
//    object ProfileGallery: SubCurrentBottomSheet()
}