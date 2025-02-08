package com.example.sudokugame

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sudokugame.ui.theme.SudokuGameTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SudokuGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SudokuGame(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SudokuGame(modifier: Modifier = Modifier) {

    // for snackbar
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    //

    val cards = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")

    val shuffledCards = remember { cards.shuffled() }
//    shuffledCards.value = shuffledCards.value.shuffled()

    var currentGrid by remember {
        mutableStateOf(
            MutableList(81) { index ->
                if (index < 9) {
                    shuffledCards[index] // moved outside for reset instead of inside lazyverticalgrid
                } else {
                    ""
                }
            }
        )
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        Column(modifier = modifier
            .fillMaxSize()) // so can see buttons as well
        { // need column so button is not on top of grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(9),
            ) {
                items(81) { index ->
                    // first 9 items
    //            var text by remember {
    //                if (index < 9){
    //                    mutableStateOf(shuffledCards[index])
    //                }else{
    //                    mutableStateOf("")
    //                }
    //            }
                    //https://developer.android.com/develop/ui/compose/text/user-input
                    // user input text numbers 1-9
                    TextField(
                        value = currentGrid[index],
                        onValueChange = { if (it in "123456789" && it.length <= 1){
    //                    currentGrid[index] = it // doesn't work
                            currentGrid = currentGrid.toMutableList().apply {
                                this[index] = it
                            }

                        }
                        })
                }
            }

            Row(modifier = Modifier.padding(16.dp)) {
                // Reset button
                Button(onClick = {
                    currentGrid = currentGrid.toMutableList().apply {
                        for (i in 9..80) { // reset our grid skipping the first 9 numbers
                            this[i] = ""
                        }
                    }
                }) {
                    Text(text = "Reset")
                }

                // Check button
                Button(onClick = {
                    if (checkGrid(currentGrid)) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Correct!")
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Incorrect!")
                        }
                    }
                }) {
                    Text(text = "Check Sudoku")
                }
            }
            Row(modifier = Modifier.padding(contentPadding)){ // to get rid of contentPadding warning...
                Text(text = "Sudoku Game")
            }
        }
    }
}


fun checkGrid(currentGrid: MutableList<String>): Boolean {
    // Columns
    for (col in 0..8) {
        val seenVals = mutableSetOf<String>()
        for (row in 0..8) {
            val index = row * 9 + col
            val individualVal = currentGrid[index]
            if (individualVal == ""){
                return false
            }
            if (individualVal in seenVals) { //
                return false
            }
            seenVals.add(individualVal)
        }
    }

    // Rows
    for (row in 0..8) {
        val seenVals = mutableSetOf<String>() // Store values in the row
        for (col in 0..8) {
            val index = row * 9 + col
            val individualVal = currentGrid[index]
            if (individualVal == ""){ // invalid if empty
                return false
            }
            if (individualVal in seenVals) { // invalid if seen in the row before
                return false
            }
            seenVals.add(individualVal)
        }
    }


    // 3x3 boxes
    for (row in 0..2) {
        for (col in 0..2) {
            val seenVals = mutableSetOf<String>()
            for (i in 0 ..2) {
                for (j in 0 ..2) {
                    // careful adding indices here to get each box
                    val row2 = row * 3 + i
                    val col2 = col * 3 + j
                    val index = row2 * 9 + col2
                    val individualVal = currentGrid[index]
                    if (individualVal == ""){
                        return false
                    }
                    if (individualVal in seenVals) {
                        return false
                    }
                    seenVals.add(individualVal)
                }
            }
        }
    }

    return true // correct grid!!!
}

