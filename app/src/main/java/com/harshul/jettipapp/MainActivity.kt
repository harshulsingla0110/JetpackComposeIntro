package com.harshul.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.harshul.jettipapp.components.InputField
import com.harshul.jettipapp.ui.theme.JetTipAppTheme
import com.harshul.jettipapp.util.calculateTipAmount
import com.harshul.jettipapp.util.calculateTotalPerPerson
import com.harshul.jettipapp.widgets.RoundedIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {

                val totalPerPersonState = remember { mutableDoubleStateOf(0.0) }

                Column {
                    TopHeader(totalPerPersonState.doubleValue)
                    MainContent(totalPerPersonState)
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Preview
@Composable
fun TopHeader(totalValue: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0XFF153CD7)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "JetTip", color = Color(0XFF3974E7),
                style = MaterialTheme.typography.displaySmall.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    fontWeight = FontWeight.Bold
                ),
                fontSize = TextUnit(16f, TextUnitType.Sp)
            )
            Spacer(modifier = Modifier.height(120.dp))
            val value = "%.2f".format(totalValue)
            Text(
                text = "Total (incl. taxes & fees)",
                style = MaterialTheme.typography.bodyLarge.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                color = Color(0XFF5DADF8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    modifier = Modifier.padding(top = 2.dp, end = 2.dp),
                    text = "₹",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    ),
                    fontWeight = FontWeight.ExtraBold, color = Color.White,
                )
                Text(
                    text = value, style = MaterialTheme.typography.bodyLarge.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    fontWeight = FontWeight.ExtraBold, color = Color.White,
                    fontSize = TextUnit(36f, TextUnitType.Sp)
                )
            }
        }
    }
}


@Composable
fun MainContent(totalPerPersonState: MutableDoubleState) {
    BillForm(totalPerPersonState = totalPerPersonState) { billAmt ->

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    totalPerPersonState: MutableDoubleState,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember { mutableStateOf("") }
    val validState = remember(totalBillState.value) { totalBillState.value.trim().isNotEmpty() }

    val splitPersonCount = remember { mutableIntStateOf(1) }
    val splitRange = IntRange(start = 1, endInclusive = 30)

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember { mutableFloatStateOf(0f) }
    val tipPercentage = (sliderPositionState.floatValue * 100).toInt()
    val tipAmtState = remember { mutableDoubleStateOf(0.0) }

    Surface(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            corner = CornerSize(8.dp)
        ),
        border = BorderStroke(width = 1.dp, color = Color.Gray)
    ) {
        Column {
            InputField(
                modifier = Modifier.fillMaxWidth(),
                valueSate = totalBillState,
                labelId = "Enter Bill Amount",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                })
            if (validState) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Split")
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundedIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitPersonCount.intValue > splitRange.first) splitPersonCount.intValue -= 1
                                updateTotalPerPersonValue(
                                    totalPerPersonState,
                                    totalBillState,
                                    splitPersonCount,
                                    tipPercentage
                                )
                            })
                        Text(
                            text = "${splitPersonCount.intValue}",
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp)
                                .align(Alignment.CenterVertically)
                        )
                        RoundedIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitPersonCount.intValue < splitRange.last) splitPersonCount.intValue += 1
                                updateTotalPerPersonValue(
                                    totalPerPersonState,
                                    totalBillState,
                                    splitPersonCount,
                                    tipPercentage
                                )
                            })
                    }
                }

                // adding tip row
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "₹${tipAmtState.doubleValue}")
                }
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage%")
                    Spacer(modifier = Modifier.height(16.dp))
                    //adding slider
                    Slider(value = sliderPositionState.floatValue, onValueChange = { newVal ->
                        sliderPositionState.floatValue = newVal
                        //calculating tip amount
                        val currTipPerc = (newVal * 100).toInt()
                        tipAmtState.doubleValue = calculateTipAmount(
                            totalBillState.value.toDoubleOrNull(),
                            currTipPerc
                        )
                        updateTotalPerPersonValue(
                            totalPerPersonState,
                            totalBillState,
                            splitPersonCount,
                            currTipPerc
                        )
                    })
                }
            }
        }
    }

}


private fun updateTotalPerPersonValue(
    totalPerPersonState: MutableDoubleState,
    totalBillState: MutableState<String>,
    splitPersonCount: MutableIntState,
    tipPerc: Int
) {
    totalPerPersonState.doubleValue = calculateTotalPerPerson(
        totalBillValue = totalBillState.value.toDouble(),
        splitBy = splitPersonCount.intValue,
        tipPerc = tipPerc
    )
}