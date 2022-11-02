package kr.moare.android.utils

import kr.moare.android.R

enum class SplashNavItem {
    Loading, JoinSplash, Main, Start
}

enum class StartNavItem {
    Start, Login, Email, Auth, Pwd, Username, SportSelect;
}

enum class MainNavItem {
    MAIN, POSTCREATE, MESSAGELIST
}

enum class BottomTabNavItem(val icon: Int?) {
    Post(R.drawable.ic_post),
    MyProfile(R.drawable.ic_profile),
    UserProfile(null), TeamProfile(null), FollowList(null)
}

enum class PostNavItem {
    POST, POSTDETAIL, USERPROFILE, TEAMPROFILE
}

enum class PostCreateNavItem {
    POSTCREATE, POSTCCREATEDETAIL
}

enum class MyProfileNavItem {
    MYPROFILE
}

enum class UserProfileNavItem {
    USERPROFILE, FOLLOWLIST, POSTDETAIL
}


