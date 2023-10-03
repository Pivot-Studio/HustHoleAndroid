package cn.pivotstudio.modulep.report.ui.activity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.pivotstudio.moduleb.rebase.theme.HustHoleTheme

@Composable
fun OutlinedButtonChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    AssistChip(modifier = modifier, onClick = onClick, leadingIcon = {
        Icon(
            imageVector = icon,
            contentDescription = null, Modifier.size(AssistChipDefaults.IconSize)
        )
    }, label = { Text(text = label) })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    label: String,
    leadingIcon: ImageVector = Icons.Outlined.Check
) {
    FilterChip(
        modifier = modifier.padding(horizontal = 4.dp),
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Row {
                AnimatedVisibility(visible = selected) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            }
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFilterChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    label: String, leadingIcon: ImageVector? = null
) {
    FilterChip(
        modifier = modifier.padding(horizontal = 4.dp),
        selected = selected, enabled = enabled,
        onClick = onClick,
        label = {
            Text(text = label)
        },
        leadingIcon = { leadingIcon?.let { Icon(imageVector = it, contentDescription = null) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutChip(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
) {
    AssistChip(
        modifier = modifier.padding(horizontal = 4.dp),
        onClick = { onClick?.invoke() },
        label = { Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        trailingIcon = {
            onRemove?.let {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(InputChipDefaults.IconSize)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(InputChipDefaults.IconSize)
                    )
                }
            }
        })
}


@Composable
fun ReportButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportChip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    OutlinedButtonChip(icon = Icons.Outlined.Clear, label = text, modifier = Modifier.padding(horizontal = 4.dp)) {

    }
}

@Composable
fun ReportSelection(
    types: List<String>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier
    ) {
        items(types) { type ->
            ReportChip(
                text = type,
                selected = true,
                onClick = {}
            )
        }
    }
}

@Composable
fun ReportDescriptionEditText(
    hint: String,
) {
    Card(
        border = BorderStroke(
            width = 1.5f.dp,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .height(250.dp)
    ) {
        var text by rememberSaveable { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(hint) },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
fun ReportDescriptionEditTextPreview() {
    HustHoleTheme {
        ReportDescriptionEditText("请详细描述你遇到的问题")
    }
}

@Preview
@Composable
fun ReportChipPreview() {
    HustHoleTheme {
        ReportChip(
            text = "暴力",
            selected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportSelectionPreview() {
    HustHoleTheme {
        ReportSelection(
            types = listOf(
                "aaaaaaaa", "a", "a", "a", "a",
            )
        )
    }
}

@Preview
@Composable
private fun PlantNamePreview() {
    HustHoleTheme {
        Button(
            onClick = { },
            modifier = Modifier
                .size(width = 256.dp, height = 48.dp)
        ) {
            Text(text = "确认举报")
        }
    }
}