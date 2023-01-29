package kr.moare.android.utils

import kr.moare.android.R

enum class LoadingNavItem {
    StartLoading, Main, Start
}

enum class StartNavItem {
    Start, Login, EmailForNewPwd, AuthForNewPwd, NewPwd, Email, Auth, Pwd, Username, JoinSportAdd, TOS, LOGININFOSAVE, JOINSPLASH
}

enum class MainNavItem {
    MAIN, POSTCREATE, POSTCREATEDETAIL, MESSAGELIST, POSTUPDATE, POSTUPDATEDETAIL
}

enum class BottomTabNavItem(val icon: Int?) {
    Post(R.drawable.ic_post),
    MyProfile(R.drawable.ic_profile),
    UserProfile(null), TeamProfile(null), FollowList(null)
}

enum class PostNavItem {
    POST, POSTDETAIL, USERPROFILE, DEEPLINKPOSTDETAIL
}

enum class MyProfileNavItem {
    MYPROFILE, POSTDETAIL, SETTINGS, ACCOUNTINFO, INFO, CONTACT, INFODETAIL
}

enum class UserProfileNavItem {
    USERPROFILE, FOLLOWLIST, POSTDETAIL
}


