package com.example.minijogosteste

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiniJogoApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniJogoApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mini's Jogos 🎲",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFB0C4DE))
            )
        },
        content = { padding ->
            NavHost(navController, startDestination = "home", modifier = Modifier.padding(padding)) {
                composable("home") { HomeScreen(navController) }
                composable("adivinhacao") { JogoAdivinhacaoScreen(navController) }
                composable("moeda") { JogoMoedaScreen(navController) }
                composable("pedrapapeltesoura") { PedraPapelTesouraScreen(navController) }
            }
        }
    )
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6E6FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { navController.navigate("adivinhacao") },
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            modifier = Modifier
                .width(300.dp) // Aumenta a largura fixa dos botões
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Jogar Adivinhação \uD83E\uDD14 ", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("moeda") },
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            modifier = Modifier
                .width(300.dp) // Aumenta a largura fixa dos botões
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Jogar Moeda \uD83E\uDE99 ", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("pedrapapeltesoura") },
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            modifier = Modifier
                .width(300.dp) // Aumenta a largura fixa dos botões
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Jogar Pedra ✊", color = Color.White)
                Text("Papel ✋ e Tesoura ✌\uFE0F ", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { android.os.Process.killProcess(android.os.Process.myPid()) },
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            modifier = Modifier
                .width(300.dp) // Aumenta a largura fixa dos botões
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Sair", color = Color.White)
        }
    }
}

@Composable
fun GameButton(text: String, navController: NavHostController, route: String) {
    Button(
        onClick = { navController.navigate(route) },
        colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
        modifier = Modifier
            .width(200.dp)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun JogoAdivinhacaoScreen(navController: NavHostController) {
    val randomNumber = remember { (1..10).random() }
    var guess by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Tente adivinhar o número entre 1 e 10") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFE6E6FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, style = TextStyle(fontSize = 20.sp))
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = guess,
            onValueChange = { guess = it },
            label = { Text("Digite seu palpite") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val guessedNumber = guess.toIntOrNull()
                if (guessedNumber != null) {
                    message = when {
                        guessedNumber < randomNumber -> "Muito baixo! Tente novamente."
                        guessedNumber > randomNumber -> "Muito alto! Tente novamente."
                        else -> "Parabéns, você acertou! \uD83D\uDC4F "
                    }
                } else {
                    message = "Por favor, insira um número válido."
                }
            },
            modifier = Modifier.width(200.dp), // Diminui a largura do botão
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Tentar Adivinhar", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.width(200.dp), // Diminui a largura do botão
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Voltar", color = Color.White)
        }
    }
}

@Composable
fun JogoMoedaScreen(navController: NavHostController) {
    val resultados = listOf("Cara", "Coroa")
    val resultado = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFE6E6FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (resultado.value.isEmpty()) "Clique para jogar a moeda! 🪙" else "Resultado: ${resultado.value} 🪙",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { resultado.value = resultados.random() },
            modifier = Modifier.width(200.dp), // Diminui a largura do botão
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Jogar Moeda", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.width(200.dp), // Diminui a largura do botão
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Voltar", color = Color.White)
        }
    }
}

@Composable
fun PedraPapelTesouraScreen(navController: NavHostController) {
    val choices = listOf("Pedra ✊", "Papel ✋", "Tesoura ✌️")
    val userChoice = remember { mutableStateOf("") }
    val computerChoice = remember { mutableStateOf("") }
    val result = remember { mutableStateOf("") }

    fun playRound(user: String) {
        val computer = choices.random()
        userChoice.value = user
        computerChoice.value = computer
        result.value = when {
            user == computer -> "Empate! \uD83E\uDD1D "
            (user == "Pedra ✊" && computer == "Tesoura ✌️") ||
                    (user == "Papel ✋" && computer == "Pedra ✊") ||
                    (user == "Tesoura ✌️" && computer == "Papel ✋") -> "Você ganhou! \uD83D\uDC4F "
            else -> "Você perdeu! \uD83D\uDC4E "
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFE6E6FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Escolha uma opção:", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { playRound("Pedra ✊") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Pedra ✊", color = Color.White)
                }
                Button(
                    onClick = { playRound("Papel ✋") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Papel ✋", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { playRound("Tesoura ✌️") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Tesoura ✌️", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (userChoice.value.isNotEmpty()) {
            Text("Sua escolha: ${userChoice.value}", fontSize = 20.sp)
            Text("Minha escolha: ${computerChoice.value}", fontSize = 20.sp)
            Text("Resultado: ${result.value}", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.width(200.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF4682B4)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Voltar", color = Color.White)
        }
    }
}