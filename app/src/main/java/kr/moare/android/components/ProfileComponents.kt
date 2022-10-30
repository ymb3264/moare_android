package kr.moare.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.moare.android.entities.Post

@Composable
fun ProfileDivideLine() {
    Box(
        Modifier
            .padding(top = 12.dp)
            .height(1.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .clip(RectangleShape)
    )
}

fun LazyListScope.ProfilePostListView(
    postList: List<List<Post>>,
    subNavController: NavController
) {
    items(postList) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            PostListItemView(subNavController = subNavController, post = it[0])
            if (it.count() > 2) {
                PostListItemView(subNavController = subNavController, post = it[1])
                PostListItemView(subNavController = subNavController, post = it[2])
            } else if (it.count() > 1){
                PostListItemView(subNavController = subNavController, post = it[1])
                EmptyPostView()
            } else {
                EmptyPostView()
                EmptyPostView()
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (it.count() > 3) {
                PostListItemView(subNavController = subNavController, post = it[3])
                if (it.count() > 5) {
                    PostListItemView(subNavController = subNavController, post = it[4])
                    PostListItemView(subNavController = subNavController, post = it[5])
                } else if (it.count() > 4){
                    PostListItemView(subNavController = subNavController, post = it[4])
                    EmptyPostView()
                } else {
                    EmptyPostView()
                    EmptyPostView()
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (it.count() > 6) {
                PostListItemView(subNavController = subNavController, post = it[6])
                if (it.count() > 8) {
                    PostListItemView(subNavController = subNavController, post = it[7])
                    PostListItemView(subNavController = subNavController, post = it[8])
                } else if (it.count() > 7){
                    PostListItemView(subNavController = subNavController, post = it[7])
                    EmptyPostView()
                } else {
                    EmptyPostView()
                    EmptyPostView()
                }
            }
        }
    }
}