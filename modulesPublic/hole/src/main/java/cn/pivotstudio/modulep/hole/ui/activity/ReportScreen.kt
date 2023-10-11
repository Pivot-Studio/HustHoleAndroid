package cn.pivotstudio.modulep.hole.ui.activity

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.pivotstudio.moduleb.rebase.theme.HustHoleTheme
import cn.pivotstudio.modulep.hole.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    holeId: String,
    targetNickname: String,
    involvedTypes: List<String>,
    makeSure2Report: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "举报")
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(25.dp),
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)) {
                        append("举报")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("#$holeId")
                    }

                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer)) {
                        append("的")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("$targetNickname")
                    }
                    append("涉及")
                },
                fontWeight = FontWeight.Bold,
            )
            ReportSelection(
                types = involvedTypes,
                modifier = Modifier.padding(vertical = 25.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 25.dp),
                contentAlignment = Alignment.Center
            ) {
                ReportDescriptionEditText(hint = stringResource(id = R.string.report_content_hint))
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ReportButton(
                    text = stringResource(id = R.string.report),
                    onClick = makeSure2Report,
                )
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ReportScreenPreview() {
    HustHoleTheme {
        ReportScreen(
            holeId = "10371037",
            targetNickname = "紫菘酸菜鱼",
            involvedTypes = listOf(
                "紫菘酸菜",
                "紫鱼",
                "菘酸菜鱼",
                "aa紫菘酸菜鱼",
                "紫菘酸菜鱼",
                "紫菘酸菜鱼",
                "紫菘酸菜鱼",
                "紫菘酸菜鱼",
                "紫菘酸菜鱼",
            ),
            makeSure2Report = {}
        )
    }
}

@Preview
@Composable
fun ReportScreenPreview2() {
    HustHoleTheme {
        ReportScreen(
            holeId = "10371037",
            targetNickname = "紫菘酸菜鱼",
            involvedTypes = listOf(
                "紫菘酸菜",
                "紫鱼",
                "菘酸菜鱼",
                "aa紫菘酸菜鱼",
                "紫菘酸菜鱼",
                "紫菘酸菜鱼",
                "紫菘酸菜鱼",
                "紫菘酸菜鱼",
                "紫菘酸菜鱼",
            ),
            makeSure2Report = {}
        )
    }
}