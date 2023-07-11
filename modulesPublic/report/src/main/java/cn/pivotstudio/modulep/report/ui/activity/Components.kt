package cn.pivotstudio.modulep.report.ui.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ReportButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text)
    }
}

@Composable
fun ReportDescriptionEditText(
    hint: String,
) {
    Card(
        border = BorderStroke(
            width = 1.5f.dp,
            color = MaterialTheme.colors.primary
        ),
        modifier = Modifier
            .size(width = 300.dp, height = 250.dp)
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
    MaterialTheme {
        ReportDescriptionEditText("请详细描述你遇到的问题")
    }
}

@Preview
@Composable
private fun PlantNamePreview() {
    MaterialTheme {
        ReportButton(
            "确认举报",
            onClick = { },
            modifier = Modifier
                .size(width = 256.dp, height = 48.dp)
        )
    }
}