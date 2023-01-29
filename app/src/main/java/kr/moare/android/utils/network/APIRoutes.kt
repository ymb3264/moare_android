package kr.moare.android.utils.network

object APIRoutes {
//    private const val host = "http://10.0.2.2:8080"
    private const val host = "https://www.moare.kr"

    // join
    const val join = "$host/join"
    const val emailcode = "$join/emailcode"
    const val username = "$join/username"
    const val sport = "$join/sport"

    // login
    const val login = "$host/login"
    const val refresh = "$login/refresh"
    const val loginEmailCode = "$login/emailcode"
    const val newPwd = "$login/newpwd"

    // post, like
    const val post = "$host/post"
    const val postUpdate = "$post/update"
    const val postDelete = "$post/delete"
    const val morePost = "$post/more"
    const val onePost = "$post/one"
    const val postReport = "$post/report"

    const val like = "$post/like"
    const val unlike = "$post/unlike"

    const val userPost = "$post/user"
    const val moreUserPost = "$post/user_more"

    // profile
    const val profile = "$host/profile"
    const val userProfile = "$profile/user"
    const val teamProfile = "$profile/team"
    const val myAccounts = "$profile/accounts"
    const val profileDelete = "$profile/delete"
    const val profileReport = "$profile/report"
    const val profileBlock = "$profile/block"

    // follow
    const val follow = "$host/follow/add"
    const val unfollow = "$host/follow/delete"

    // search
    const val hashtag = "$host/search/hashtag"
    const val searchUser = "$host/search/username"

    // location
    private const val kakaoLocal = "https://dapi.kakao.com/v2/local"
    const val location = "$kakaoLocal/search/address.json"
    const val coordinate = "$kakaoLocal/geo/coord2address.json"
}