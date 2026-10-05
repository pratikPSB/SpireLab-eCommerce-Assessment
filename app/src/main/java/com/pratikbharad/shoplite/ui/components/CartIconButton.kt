package com.pratikbharad.shoplite.ui.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.pratikbharad.shoplite.R

private const val MAX_BADGE_COUNT = 99

@Composable
fun CartIconButton(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = if (itemCount > 0) {
        pluralStringResource(R.plurals.cart_content_description, itemCount, itemCount)
    } else {
        stringResource(R.string.cart_title)
    }
    IconButton(onClick = onClick, modifier = modifier) {
        BadgedBox(
            badge = {
                if (itemCount > 0) {
                    Badge {
                        Text(
                            text = if (itemCount > MAX_BADGE_COUNT) "$MAX_BADGE_COUNT+" else itemCount.toString(),
                            modifier = Modifier.clearAndSetSemantics {},
                        )
                    }
                }
            },
        ) {
            Icon(painter = painterResource(R.drawable.ic_shopping_cart), contentDescription = description)
        }
    }
}
