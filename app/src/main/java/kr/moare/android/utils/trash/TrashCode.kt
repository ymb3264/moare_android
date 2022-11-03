package kr.moare.android.utils.trash

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.moare.android.utils.PostNavItem

//enum class NAV_ROUTE(val routeName: String, val description: String) {
//    START("START", "시작 화면"),
//    LOGIN("LOGIN", "로그인 화면"),
//    PHOREMAIL("PHOREMAIL", "전화번호 또는 이메일 작성 화면"),
//    AUTH("AUTH", "인증코드 작성 화면"),
//    PWD("PWD", "비밀번호 생성 화면"),
//    USERNAME("USERNAME", "사용자이름 생성 화면"),
//    SPORTSELECT("SPORTSELECT", "스포츠 선택 화면")
//}

//class RouteAction(navHostController: NavHostController) {
//    val navTo: (NAV_ROUTE) -> Unit = { route ->
//        navHostController.navigate(route.routeName)
//    }
//
//    val goBack: () -> Unit = {
//        navHostController.navigateUp()
//    }
//}

//@Composable
//fun NavigationGraph(navController: NavHostController, routeAction: RouteAction) {
//    NavHost(navController = navController, startDestination = NAV_ROUTE.START.routeName) {
//        composable(NAV_ROUTE.START.routeName) {
//            StartView(routeAction = routeAction)
//        }
//        composable(NAV_ROUTE.PHOREMAIL.routeName) {
//            PhOrEmailView(routeAction = routeAction)
//        }
//        composable(NAV_ROUTE.AUTH.routeName) {
//            AuthView(routeAction = routeAction)
//        }
//        composable(NAV_ROUTE.PWD.routeName) {
//            PwdView(routeAction = routeAction)
//        }
//        composable(NAV_ROUTE.USERNAME.routeName) {
//            UsernameView(routeAction = routeAction)
//        }
//        composable(NAV_ROUTE.SPORTSELECT.routeName) {
//            SportSelectView(routeAction = routeAction)
//        }
//    }
//}

//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .background(Color.White)
//                .verticalScroll(state = scrollState),
//            verticalArrangement = Arrangement.spacedBy(2.dp),
//        ) {
//            Box(
//                modifier = Modifier
//                    .clip(RectangleShape)
//                    .fillMaxWidth()
//                    .aspectRatio(2f)
//                    .background(Color.Gray)
//            )
//
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(2.dp)
//            ) {
//                Box(
//                    modifier = Modifier.weight(1f),
//                    contentAlignment = Alignment.BottomStart
//                ) {
//                    PostListItemView()
//                }
//                Box(
//                    modifier = Modifier.weight(1f),
//                    contentAlignment = Alignment.BottomStart
//                ) {
//                    PostListItemView()
//                }
//            }
//
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(2.dp)
//            ) {
//                Column(
//                    verticalArrangement = Arrangement.spacedBy(2.dp),
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Box(
//                        contentAlignment = Alignment.BottomStart
//                    ) {
//                        PostListItemView()
//                    }
//                    Box(
//                        contentAlignment = Alignment.BottomStart
//                    ) {
//                        PostListItemView()
//                    }
//                }
//                BoxWithConstraints(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    val width = this.maxWidth
//                    Box(
//                        modifier = Modifier
//                            .clip(RectangleShape)
//                            .size(width = width, height = width * 2 + 2.dp)
//                            .background(Color.Gray)
//                    )
//                }
//            }
//
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(2.dp)
//            ) {
//                Box(
//                    modifier = Modifier.weight(1f),
//                    contentAlignment = Alignment.BottomStart
//                ) {
//                    PostListItemView()
//                }
//                Box(
//                    modifier = Modifier.weight(1f),
//                    contentAlignment = Alignment.BottomStart
//                ) {
//                    PostListItemView()
//                }
//            }
//
//        }

//LazyVerticalGrid(
//columns = object : GridCells {
//    override fun Density.calculateCrossAxisCellSizes(
//        availableSize: Int,
//        spacing: Int
//    ): List<Int> {
//        val firstColumn = (availableSize - spacing) * 1 / 2
//        val secondColumn = availableSize - spacing - firstColumn
//        return listOf(firstColumn, secondColumn)
//    }
//},
//horizontalArrangement = Arrangement.spacedBy(2.dp),
//verticalArrangement = Arrangement.spacedBy(2.dp),
//state = gridState
////            modifier = Modifier.padding(padding)
//) {
//    postList.forEachIndexed { index, post ->
//        if (index % 7 == 0) {
//            item(span = { GridItemSpan(maxLineSpan) }) {
//                Box(
//                    modifier = Modifier
//                        .background(Color.Gray)
//                        .aspectRatio(2f)
//                ) {
//                    Text("dd")
//                }
//            }
//        } else if (index % 7 == 3) {
//            val obj = post as MutableList<Post>
//            item(span = { GridItemSpan(1) }) {
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.spacedBy(2.dp)
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .background(Color.Gray)
//                            .aspectRatio(1f),
//                        contentAlignment = Alignment.BottomStart
//                    ) {
//                        AsyncImage(
//                            model = post[0].coilImage,
//                            placeholder = painterResource(R.drawable.ic_search),
//                            contentDescription = "image",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.clip(RectangleShape)
//                        )
//                        PostListItemView()
//                    }
//                    Box(
//                        modifier = Modifier
//                            .background(Color.Gray)
//                            .aspectRatio(1f),
//                        contentAlignment = Alignment.BottomStart
//                    ) {
//                        AsyncImage(
//                            model = post[1].coilImage,
//                            placeholder = painterResource(R.drawable.ic_search),
//                            contentDescription = "image",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.clip(RectangleShape)
//                        )
//                        PostListItemView()
//                    }
//                }
//            }
//        } else if (index % 7 == 4) {
//            item(span = { GridItemSpan(1) }) {
//                BoxWithConstraints(
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    val width = this.maxWidth
//                    Box(
//                        modifier = Modifier
//                            .clip(RectangleShape)
//                            .size(width = width, height = width * 2 + 2.dp)
//                            .background(Color.Gray)
//                    ) {
//                        Text("dd")
//                    }
//                }
//            }
//        } else {
//            val obj = post as Post
//            item(span = { GridItemSpan(1) }) {
//                Box(
//                    modifier = Modifier
//                        .background(Color.Gray)
//                        .aspectRatio(1f),
//                    contentAlignment = Alignment.BottomStart
//                ) {
//                    AsyncImage(
//                        model = post.coilImage,
//                        placeholder = painterResource(R.drawable.ic_search),
//                        contentDescription = "image",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.clip(RectangleShape)
//                    )
//                    PostListItemView()
//                }
//            }
//        }
//    }
//}

//kotlin.runCatching {
//    val place = UserDefaultPlace("장기동", "126.671359353779", "37.6397252396072")
//    api.getAllPost(place)
//}.onSuccess {
//    val list = it.map { post ->
//        val request = ImageRequest.Builder(context)
//            .data(post.image)
//            .crossfade(true)
//            .build()
//        post.coilImage = request
//        post
//    }
//    var arr = mutableListOf<Post>()
//    list.mapIndexed { index, post ->
//        if (index == 3 || index == 4) {
//            arr.add(post)
//        }
//    }
//    val newList = listOf(list[0], list[1], list[2],
//        arr,
//        list[5], list[6], list[7], list[8], list[9], list[10],
//    )
//    Log.d("success", "$it")
//    postListFlow.value = newList
//    Log.d("post", "${postListFlow.value}")
//}.onFailure {
//    Log.d("FAIL", "message: $it")
//}


// navgraph
//const val POST = "POST"
//const val MYPROFILE = "MYPROFILE"
//
//sealed class BottomNavItem(
//    val title: String, val icon: Int, val screenRoute: String
//) {
//    object Post : BottomNavItem("게시물", R.drawable.ic_post, POST)
//    object MyProfile : BottomNavItem("내 프로필", R.drawable.ic_profile, MYPROFILE)
//}

//@Composable
//fun NavigationGraph(navController: NavHostController) {
//    NavHost(navController = navController, startDestination = BottomNavItem.Post.screenRoute) {
//        composable(BottomNavItem.Post.screenRoute) {
//            PostView()
//        }
//        composable(BottomNavItem.MyProfile.screenRoute) {
//            MyProfileView()
//        }
//    }
//}

//class AssetParamType : NavType<UserProfile>(isNullableAllowed = false) {
//    override fun get(bundle: Bundle, key: String): UserProfile? {
//        return bundle.getParcelable(key)
//    }
//
//    override fun put(bundle: Bundle, key: String, value: UserProfile) {
//        bundle.putParcelable(key, value)
//    }
//
//    override fun parseValue(value: String): UserProfile {
//        return Json.encodeToString(UserProfile, value)
//    }
//}


// storagehelper
//public fun getMediaAttachments(context: Context): List<Attachment> {
//    var selection = (
//            MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
//                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
//                    "OR" +
//                    MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
//                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
//            )
//    return getFilteredAttachments(context, selection)
//}
//private fun getFilteredAttachments(context: Context, selection: String?): List<Attachment> {
//    val columns = arrayOf(
//        MediaStore.Files.FileColumns._ID,
//        MediaStore.Files.FileColumns.DISPLAY_NAME,
//        MediaStore.Files.FileColumns.MIME_TYPE,
//        MediaStore.Files.FileColumns.SIZE,
//        MediaStore.Files.FileColumns.DURATION
//    )
//    context.contentResolver.query(
//        MediaStore.Files.getContentUri("external"),
//        columns,
//        null,
//        null,
//        "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
//    )?.use { cursor ->
//        return mutableListOf<Attachment>().apply {
//            while (cursor.moveToNext()) {
//                add(getAttachmentFromCursor(cursor))
//            }
//        }
//    }
//    return emptyList()
//}
//private fun getAttachmentFromCursor(cursor: Cursor): Attachment {
//    val displayNameIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
//    val fileSizeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
//    val mimeTypeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
//    val durationIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DURATION)
//
//    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
//
//    val displayName = if (displayNameIndex != -1 && !cursor.isNull(displayNameIndex)) {
//        cursor.getString(displayNameIndex)
//    } else null
//
//    val fileSize = if (fileSizeIndex != -1 && !cursor.isNull(fileSizeIndex)) {
//        cursor.getLong(fileSizeIndex)
//    } else 0L
//
//    val mimeType = if (mimeTypeIndex != -1 && !cursor.isNull(mimeTypeIndex)) {
//        cursor.getString(mimeTypeIndex)
//    } else null
//
//    val duration = if (durationIndex != -1 && !cursor.isNull(fileSizeIndex)) {
//        cursor.getLong(durationIndex)
//    } else 0L
//
//    return Attachment(
//        uri = getContentUri(mimeType, id),
//        mimeType = mimeType
//    ).apply {
//        this.type = getMediaType(mimeType)
//        this.size = fileSize
//        this.title = displayName
//        this.videoLength = duration / 1000
//    }
//}

// gallery
//val contentResolver = LocalContext.current.contentResolver
//var uriList by remember { mutableStateOf(listOf<Uri>()) }
//var uriType by remember { mutableStateOf<String?>(null) }
//val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri ->
//    uriList = uri
//    uriType = contentResolver.getType(uri[0])
//}
//galleryLauncher.launch("*/*")

//val username = teamProfile.username.substringBefore("-t")

// username에 -t가 있는경우 검색시 나오는 리스트
//if (it.endsWith("-t")) {
//    TextButton(
//        onClick = {
//            postNavController.navigate(PostNavItem.TEAMPROFILE.name)
//        },
//        modifier = Modifier
//            .fillMaxWidth(),
//        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
//        contentPadding = PaddingValues(0.dp)
//    ) {
//        Text(text = it.substringBefore("-t"),
//            modifier = Modifier.padding(start = 10.dp),
//            fontSize = 18.sp
//        )
//        Text(text = "-t",
//            modifier = Modifier.padding(start = 5.dp),
//            color = Color.LightGray
//        )
//        Spacer(Modifier.weight(1f))
//    }
//}

// 처음 startviewButton(원이 움직이는버튼)
//            StartViewButton(
//                text = "다음",
//                onClick = {
//                    joinVM.checkUsername(username) {
//                        if (it) {
//                            navController.navigate(StartNavItem.SportSelect.name)
//                        }
//                    }
//                },
//                enabled = username.isNotEmpty(),
//                width = screenWidth
//            )