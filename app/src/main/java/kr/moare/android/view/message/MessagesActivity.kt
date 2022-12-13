package kr.moare.android.view.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kr.moare.android.R
import kr.moare.android.ui.theme.Moare
import io.getstream.chat.android.common.state.MessageMode
import io.getstream.chat.android.compose.state.messages.list.DateSeparatorState
import io.getstream.chat.android.compose.state.messages.list.MessageItemGroupPosition
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.attachments.content.MessageAttachmentsContent
import io.getstream.chat.android.compose.ui.components.avatar.ChannelAvatar
import io.getstream.chat.android.compose.ui.components.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.components.composer.MessageInput
import io.getstream.chat.android.compose.ui.components.messages.MessageBubble
import io.getstream.chat.android.compose.ui.messages.attachments.AttachmentsPicker
import io.getstream.chat.android.compose.ui.messages.composer.MessageComposer
import io.getstream.chat.android.compose.ui.messages.list.MessageContainer
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.theme.StreamShapes
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class MessagesActivity : ComponentActivity() {
    private val factory by lazy {
        MessagesViewModelFactory(
            context = this,
            channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: ""
        )
    }

    private val listViewModel: MessageListViewModel by viewModels { factory }
    private val attachmentsPickerViewModel: AttachmentsPickerViewModel by viewModels { factory }
    private val composerViewModel: MessageComposerViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channelId = intent.getStringExtra(KEY_CHANNEL_ID)

        if (channelId == null) {
            finish()
            return
        }

        setContent {
            ChatTheme(
                shapes = StreamShapes.defaultShapes().copy(
                    avatar = RoundedCornerShape(8.dp),
                    attachment = RoundedCornerShape(16.dp),
                    myMessageBubble = RoundedCornerShape(16.dp),
                    otherMessageBubble = RoundedCornerShape(16.dp),
                    inputField = RectangleShape,
                ),
            ) {
                CustomMessageScreen()
//                MessagesScreen(channelId = channelId)
            }
        }
    }

    companion object {
        private const val KEY_CHANNEL_ID = "channelId"

        fun getIntent(context: Context, channelId: String): Intent {
            return Intent(context, MessagesActivity::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channelId)
            }
        }
    }

    @Composable
    fun CustomMessageScreen() {
        val isShowingAttachments = attachmentsPickerViewModel.isShowingAttachments
        val selectedMessagesState = listViewModel.currentMessagesState.selectedMessageState
        val user by listViewModel.user.collectAsState()
        
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
            topBar = {
                TopAppBar(
                    elevation = 0.dp,
                    backgroundColor = Color.Transparent
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                contentDescription = "goBackIcon"
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            ChannelAvatar(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(40.dp)
                                    .clip(CircleShape),
                                channel = listViewModel.channel,
                                currentUser = user
                            )
                            Text(
                                text = ChatTheme.channelNameFormatter.formatChannelName(listViewModel.channel, user),
                                style = ChatTheme.typography.bodyBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = ChatTheme.colors.textHighEmphasis,
                            )

                            Text(
                                text = " · ${listViewModel.channel.memberCount.toString()}",
                                style = ChatTheme.typography.body,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = ChatTheme.colors.textHighEmphasis,
                            )
                        }
                    }
                }
            },
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    CustomComposer()
                }
            ) {
                MessageList( // 4 - Build the MessageList and connect the actions
                    modifier = Modifier
                        .background(ChatTheme.colors.appBackground)
                        .padding(it)
                        .fillMaxSize(),
                    viewModel = listViewModel,
                    onThreadClick = { message ->
                        composerViewModel.setMessageMode(MessageMode.MessageThread(message))
                        listViewModel.openMessageThread(message)
                    },
                    itemContent = {
                        MessageContainer(
                            messageListItem = it,
                            messageItemContent = {
                                CustomMessageItem(
                                    it
                                )
                            },
                            dateSeparatorContent = {
                                CustomDateSeparator(it)
                            }
                        )
                    },
                )
            }

            if (isShowingAttachments) {
                AttachmentsPicker(
                    attachmentsPickerViewModel = attachmentsPickerViewModel,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(350.dp),
                    onAttachmentsSelected = { attachments ->
                        attachmentsPickerViewModel.changeAttachmentState(false)
                        composerViewModel.addSelectedAttachments(attachments)
                    },
                    onDismiss = {
                        attachmentsPickerViewModel.changeAttachmentState(false)
                        attachmentsPickerViewModel.dismissAttachments()
                    }
                )
            }
        }
    }

    @Composable
    fun CustomComposer() {
        MessageComposer(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            viewModel = composerViewModel,
//            integrations = {
//                IconButton(
//                    onClick = {  },
//                    modifier = Modifier
//                        .size(48.dp)
//                        .padding(12.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_arrow_back),
//                        contentDescription = "goBackIcon"
//                    )
//                }
//            },
            input = { inputState ->
                MessageInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(7f)
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically),
                    messageComposerState = inputState,
                    onValueChange = { composerViewModel.setMessageInput(it) },
                    onAttachmentRemoved = { composerViewModel.removeSelectedAttachment(it) },
                    label = {
                        Row(
                            Modifier.wrapContentWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = "Type something",
                                color = ChatTheme.colors.textLowEmphasis
                            )
                        }
                    }
                )
            },
            onAttachmentsClick = {
                attachmentsPickerViewModel.changeAttachmentState(true)
            }
        )
    }

    @Composable
    fun CustomMessageItem(
        messageItem: MessageItemState
    ) {
        val messageAlignment = ChatTheme.messageAlignmentProvider.provideMessageAlignment(messageItem)

        Box(
           modifier = Modifier
               .fillMaxWidth()
               .wrapContentHeight(),
            contentAlignment = messageAlignment.itemAlignment
        ) {
            Row(
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                MessageLeadingContent(messageItem)

                Column(horizontalAlignment = messageAlignment.contentAlignment) {
                    MessageCenterContent(messageItem)
                    MessageHeaderContent(messageItem)
                }
            }
        }
    }

    @Composable
    fun RowScope.MessageLeadingContent(
        messageItem: MessageItemState
    ) {
        val modifier = Modifier
            .padding(start = 8.dp, end = 12.dp)
            .size(28.dp)
            .align(Alignment.Bottom)

        if (
            !messageItem.isMine && (
                    messageItem.shouldShowFooter ||
                            messageItem.groupPosition == MessageItemGroupPosition.Bottom
                                    ||
                            messageItem.groupPosition == MessageItemGroupPosition.None
                    )
        ) {
            UserAvatar(
                modifier = modifier,
                user = messageItem.message.user,
                showOnlineIndicator = false
            )
        } else {
            Spacer(modifier = modifier)
        }
    }
    
    @Composable
    fun MessageHeaderContent(
        messageItem: MessageItemState
    ) {
        val message = messageItem.message
        val formatter = SimpleDateFormat("h:mm a", Locale.KOREA)
        val position = messageItem.groupPosition
        val spacerSize = if (position == MessageItemGroupPosition.None || position == MessageItemGroupPosition.Bottom) 4.dp else 2.dp
        
        if (messageItem.shouldShowFooter) {
//        if (messageItem.shouldShowFooter || messageItem.groupPosition == MessageItemGroupPosition.Top) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!messageItem.isMine) {
                    Text(
                        modifier = Modifier
                            .padding(end = 8.dp),
                        text = messageItem.message.user.name,
                        style = ChatTheme.typography.footnote,
                        color = ChatTheme.colors.textLowEmphasis,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                val date = message.updatedAt ?: message.createdAt ?: message.createdLocallyAt
                if (date != null) {
                    Text(
                        modifier = if (messageItem.isMine) Modifier.padding(end = 8.dp) else Modifier,
                        text = formatter.format(date),
                        style = ChatTheme.typography.footnote,
                        color = ChatTheme.colors.textLowEmphasis
                    )
                }
                
//                Spacer(Modifier.size(spacerSize))
            }
        }
    }

    @Composable
    fun MessageCenterContent(
        messageItem: MessageItemState
    ) {
        MessageBubble(
            modifier = Modifier.padding(end = 8.dp),
            color = Color.Transparent,
            shape = RectangleShape,
            border = BorderStroke(0.dp, Color.Transparent)
        ) {
            CustomMessageContent(messageItem)
        }
    }

    @Composable
    fun CustomMessageContent(
        messageItem: MessageItemState
    ) {
        val text = messageItem.message.text

        Column {
            MessageAttachmentsContent(
                message = messageItem.message,
                onLongItemClick = {
                    listViewModel.selectMessage(it)
                }
            )

            if (text.isNotEmpty()) {
                if (messageItem.isMine) {
                    Box() {
                        Text(
                            text = text,
                            modifier = Modifier.padding(end = 8.dp, top = 6.dp, bottom = 4.dp),
                            style = TextStyle.Default,
                            softWrap = true,
                            overflow = TextOverflow.Clip,
                            maxLines = Int.MAX_VALUE
                        )
                        Box(
                            Modifier
                                .clip(RectangleShape)
                                .size(2.dp, 8.dp)
                                .background(Moare)
                                .align(Alignment.TopEnd)
                        )

                        Box(
                            Modifier
                                .clip(RectangleShape)
                                .size(8.dp, 2.dp)
                                .background(Moare)
                                .align(Alignment.TopEnd)
                        )
                    }
                } else {
                    Box() {
                        Text(
                            text = text,
                            modifier = Modifier.padding(start = 8.dp, top = 6.dp, bottom = 4.dp),
                            style = TextStyle.Default,
                            softWrap = true,
                            overflow = TextOverflow.Clip,
                            maxLines = Int.MAX_VALUE
                        )
                        Box(
                            Modifier
                                .clip(RectangleShape)
                                .size(2.dp, 8.dp)
                                .background(Color.Gray)
                        )

                        Box(
                            Modifier
                                .clip(RectangleShape)
                                .size(8.dp, 2.dp)
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun CustomDateSeparator(dateSeparator: DateSeparatorState) {
        val formatter = SimpleDateFormat("M월 dd일", Locale.KOREA)

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                text = formatter.format(dateSeparator.date),
                color = Color.Gray,
                style = ChatTheme.typography.footnoteBold
            )
        }
    }
}

























