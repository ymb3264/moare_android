package kr.moare.android.utils.network

object APIRoutes {
    private const val host = "http://10.0.2.2:8080"
//    private const val host = "http://moare-test.us-east-1.elasticbeanstalk.com"

    // join
    const val join = "$host/join"
    const val emailcode = "$join/emailcode"
    const val username = "$join/username"
    const val sport = "$join/sport"

    // login
    const val login = "$host/login"

    // post
    const val post = "$host/post"
    const val userPost = "$post/user"
    const val onePost = "$post/a"

    // profile
    const val profile = "$host/profile"
    const val userProfile = "$profile/user"
    const val teamProfile = "$profile/team"
    const val myAccounts = "$profile/accounts"

    // follow
    const val follow = "$host/follow"

    // search
    const val search = "$host/search"
    const val hashtag = "$search/hashtag"
    const val searchUser = "$search/username"

    // location
    private const val kakaoLocal = "https://dapi.kakao.com/v2/local"
    const val location = "$kakaoLocal/search/address.json"
    const val coordinate = "$kakaoLocal/geo/coord2address.json"
}