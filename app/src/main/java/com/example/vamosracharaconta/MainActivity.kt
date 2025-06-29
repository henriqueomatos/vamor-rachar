package com.example.vamosracharaconta

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vamosracharaconta.ui.theme.VamosRacharAContaTheme
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale("pt", "BR")
            }
        }

        setContent {
            VamosRacharAContaTheme {
                AppContent(onSpeak = { tts.speak(it, TextToSpeech.QUEUE_FLUSH, null, null) })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}

@Composable
fun AppContent(onSpeak: (String) -> Unit) {
    val context = LocalContext.current
    var totalValue by remember { mutableStateOf("") }
    var peopleCount by remember { mutableStateOf("") }
    val individualValue = remember(totalValue, peopleCount) {
        val total = totalValue.toFloatOrNull() ?: 0f
        val people = peopleCount.toIntOrNull() ?: 0
        if (people > 0) total / people else 0f
    }

    Scaffold(
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Cada um deve pagar: R$ %.2f".format(individualValue))
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar valor"))
                }) {
                    Text("Compartilhar")
                }
                Button(onClick = {
                    onSpeak("Cada pessoa deve pagar R$ %.2f".format(individualValue))
                }) {
                    Text("Falar")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Imagem Superior",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Vamos Rachar!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E88E5)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = totalValue,
                onValueChange = { totalValue = it },
                label = { Text("Valor total da conta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = peopleCount,
                onValueChange = { peopleCount = it },
                label = { Text("Número de pessoas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "R$ %.2f".format(individualValue),
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
