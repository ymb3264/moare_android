package kr.moare.android.view.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.getstream.sdk.chat.utils.extensions.isDirectMessaging
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Attachment
import io.getstream.chat.android.client.models.ChannelCapabilities
import kr.moare.android.R
import kr.moare.android.ui.theme.Moare
import io.getstream.chat.android.common.state.MessageMode
import io.getstream.chat.android.common.state.ValidationError
import io.getstream.chat.android.compose.state.messages.attachments.AttachmentsPickerMode
import io.getstream.chat.android.compose.state.messages.list.DateSeparatorState
import io.getstream.chat.android.compose.state.messages.list.MessageItemGroupPosition
import io.getstream.chat.android.compose.state.messages.list.MessageItemState
import io.getstream.chat.android.compose.ui.attachments.content.MessageAttachmentsContent
import io.getstream.chat.android.compose.ui.components.avatar.ChannelAvatar
import io.getstream.chat.android.compose.ui.components.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.components.composer.CoolDownIndicator
import io.getstream.chat.android.compose.ui.components.composer.MessageInput
import io.getstream.chat.android.compose.ui.components.messages.MessageBubble
import io.getstream.chat.android.compose.ui.messages.attachments.AttachmentsPicker
import io.getstream.chat.android.compose.ui.messages.attachments.factory.AttachmentsPickerTabFactory
import io.getstream.chat.android.compose.ui.messages.composer.MessageComposer
import io.getstream.chat.android.compose.ui.messages.list.MessageContainer
import io.getstream.chat.android.compose.ui.messages.list.MessageList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.theme.StreamShapes
import io.getstream.chat.android.compose.ui.util.mirrorRtl
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.Profile
import kr.moare.android.utils.StringResources
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
        val profile = intent.getStringExtra(PROFILE) ?: ""

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
                CustomMessageScreen(Json.decodeFromString(profile))
//                MessagesScreen(channelId = channelId)
            }
        }
    }

    companion object {
        private const val KEY_CHANNEL_ID = "channelId"
        private const val PROFILE = "profile"

        fun getIntent(context: Context, channelId: String, profile: Profile): Intent {
            return Intent(context, MessagesActivity::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channelId)
                putExtra(PROFILE, Json.encodeToString(profile))
            }
        }
    }

    @Composable
    fun CustomMessageScreen(
        profile: Profile
    ) {
        val isShowingAttachments = attachmentsPickerViewModel.isShowingAttachments
        val selectedMessagesState = listViewModel.currentMessagesState.selectedMessageState
        val user by listViewModel.user.collectAsState()

        var directMessageDeleteAlert by remember { mutableStateOf(false) }
        var teamChannelDeleteAlert by remember { mutableStateOf(false) }
        
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
            topBar = {
                TopAppBar(
                    elevation = 0.dp,
                    backgroundColor = Color.Transparent
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
//                        IconButton(
//                            onClick = { /*TODO*/ },
//                            modifier = Modifier.fillMaxHeight()
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_arrow_back),
//                                contentDescription = "goBackIcon"
//                            )
//                        }
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

                        Row() {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    if (listViewModel.channel.isDirectMessaging()) {
                                        directMessageDeleteAlert = true
                                    } else {
                                        teamChannelDeleteAlert = true
                                    }
                                },
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = "delete"
                                )
                            }
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
//                            messageItemContent = {
//                                CustomMessageItem(
//                                    it
//                                )
//                            },
                            dateSeparatorContent = {
                                CustomDateSeparator(it)
                            }
                        )
                    },
                )
            }

            if (isShowingAttachments) {
//                AttachmentsPicker(
//                    attachmentsPickerViewModel = attachmentsPickerViewModel,
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .height(350.dp),
//                    onAttachmentsSelected = { attachments ->
//                        attachmentsPickerViewModel.changeAttachmentState(false)
//                        composerViewModel.addSelectedAttachments(attachments)
//                    },
//                    onDismiss = {
//                        attachmentsPickerViewModel.changeAttachmentState(false)
//                        attachmentsPickerViewModel.dismissAttachments()
//                    }
//                )
                CustomAttachmentsPicker(
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

            if (directMessageDeleteAlert) {
                AlertDialog(
                    onDismissRequest = { directMessageDeleteAlert = false },
                    confirmButton = {
                        TextButton(onClick = {
                            directMessageDeleteAlert = false
                            ChatClient.instance().deleteChannel("messaging", listViewModel.channel.cid)
                        }) {
                            Text(text = StringResources.confirm, color = Color(0xFFF36575))
                        }
                    },
                    dismissButton = { TextButton(onClick = { directMessageDeleteAlert = false }) {
                        Text(text = StringResources.cancel, color = Color(0xFFF36575))
                    }
                    },
                    title = { Text(text = StringResources.deleteChannelAlertTitle) },
                    text = { Text(text = StringResources.deleteChannelAlertMessage) }
                )
            }

            if (teamChannelDeleteAlert) {
                AlertDialog(
                    onDismissRequest = { teamChannelDeleteAlert = false },
                    confirmButton = {
                        TextButton(onClick = {
                            teamChannelDeleteAlert = false
                        }) {
                            Text(text = StringResources.confirm)
                        }
                    },
                    title = { Text(text = StringResources.leaveTeamChannelAlertTitle) },
                    text = { Text(text = StringResources.leaveTeamChannelAlertMessage) }
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
            integrations = {
                IconButton(
                    onClick = { attachmentsPickerViewModel.changeAttachmentState(true) },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_photo),
                        contentDescription = "photo",
                        tint = Color.Gray
                    )
                }
            },
            input = { inputState ->
                MessageInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(7f)
                        .padding(start = 8.dp)
                        .border(border = BorderStroke(1.dp, ChatTheme.colors.borders), shape = RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
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
            trailingContent = {
                CustomMessageComposerTrailingContent(
                    value = it.inputValue,
                    coolDownTime = it.coolDownTime,
                    validationErrors = it.validationErrors,
                    attachments = it.attachments,
                    ownCapabilities = it.ownCapabilities,
                    onSendMessage = { input, attachments ->
                        val message = composerViewModel.buildNewMessage(input, attachments)
                        composerViewModel.sendMessage(message)
                    }
                )
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

    @Composable
    fun CustomAttachmentsPicker(
        attachmentsPickerViewModel: AttachmentsPickerViewModel,
        onAttachmentsSelected: (List<Attachment>) -> Unit,
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier,
        tabFactories: List<AttachmentsPickerTabFactory> = ChatTheme.attachmentsPickerTabFactories,
        shape: Shape = ChatTheme.shapes.bottomSheet,
    ) {
        var selectedTabIndex by remember { mutableStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ChatTheme.colors.overlay)
                .clickable(
                    onClick = onDismiss,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            Card(
                modifier = modifier.clickable(
                    indication = null,
                    onClick = {},
                    interactionSource = remember { MutableInteractionSource() }
                ),
                elevation = 4.dp,
                shape = shape,
                backgroundColor = ChatTheme.colors.inputBackground,
            ) {
                Column {
                    CustomAttachmentPickerOptions(
                        hasPickedAttachments = attachmentsPickerViewModel.hasPickedAttachments,
                        tabFactories = tabFactories,
                        tabIndex = selectedTabIndex,
                        onTabClick = { index, attachmentPickerMode ->
                            selectedTabIndex = index
                            attachmentsPickerViewModel.changeAttachmentPickerMode(attachmentPickerMode) { false }
                        },
                        onSendAttachmentsClick = {
                            onAttachmentsSelected(attachmentsPickerViewModel.getSelectedAttachments())
                        },
                    )

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        color = ChatTheme.colors.barsBackground,
                    ) {
                        tabFactories.getOrNull(selectedTabIndex)
                            ?.pickerTabContent(
                                attachments = attachmentsPickerViewModel.attachments,
                                onAttachmentItemSelected = attachmentsPickerViewModel::changeSelectedAttachments,
                                onAttachmentsChanged = { attachmentsPickerViewModel.attachments = it },
                                onAttachmentsSubmitted = {
                                    onAttachmentsSelected(attachmentsPickerViewModel.getAttachmentsFromMetaData(it))
                                },
                            )
                    }
                }
            }
        }
    }

    @Composable
    private fun CustomAttachmentPickerOptions(
        hasPickedAttachments: Boolean,
        tabFactories: List<AttachmentsPickerTabFactory>,
        tabIndex: Int,
        onTabClick: (Int, AttachmentsPickerMode) -> Unit,
        onSendAttachmentsClick: () -> Unit,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                enabled = hasPickedAttachments,
                onClick = onSendAttachmentsClick,
                content = {
                    val layoutDirection = LocalLayoutDirection.current

                    Icon(
                        modifier = Modifier
                            .weight(1f)
                            .mirrorRtl(layoutDirection = layoutDirection),
                        painter = painterResource(id = R.drawable.stream_compose_ic_circle_left),
                        contentDescription = stringResource(id = R.string.stream_compose_send_attachment),
                        tint = if (hasPickedAttachments) {
                            ChatTheme.colors.primaryAccent
                        } else {
                            ChatTheme.colors.textLowEmphasis
                        }
                    )
                }
            )
        }
    }

    @Composable
    fun CustomMessageComposerTrailingContent(
        value: String,
        coolDownTime: Int,
        attachments: List<Attachment>,
        validationErrors: List<ValidationError>,
        ownCapabilities: Set<String>,
        onSendMessage: (String, List<Attachment>) -> Unit,
    ) {
        val isSendButtonEnabled = ownCapabilities.contains(ChannelCapabilities.SEND_MESSAGE)
        val isInputValid by lazy { (value.isNotBlank() || attachments.isNotEmpty()) && validationErrors.isEmpty() }
        val description = stringResource(id = R.string.stream_compose_cd_send_button)

        if (coolDownTime > 0) {
            CoolDownIndicator(coolDownTime = coolDownTime)
        } else {
            IconButton(
                modifier = Modifier.semantics { contentDescription = description },
                enabled = isSendButtonEnabled && isInputValid,
                content = {
                    val layoutDirection = LocalLayoutDirection.current

                    Icon(
                        modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection),
                        painter = painterResource(id = R.drawable.stream_compose_ic_send),
                        contentDescription = stringResource(id = R.string.stream_compose_send_message),
                        tint = if (isInputValid) Color(0xFFF36575) else ChatTheme.colors.textLowEmphasis
                    )
                },
                onClick = {
                    if (isInputValid) {
                        onSendMessage(value, attachments)
                    }
                }
            )
        }
    }
}

























