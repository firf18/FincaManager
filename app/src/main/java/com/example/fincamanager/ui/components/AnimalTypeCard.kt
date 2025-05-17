package com.example.fincamanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fincamanager.R
import com.example.fincamanager.ui.theme.PrimaryGreen

/**
 * Componente que muestra una tarjeta con información de un tipo de animal.
 * Al hacer clic en ella, se navega a la gestión de animales de ese tipo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalTypeCard(
    animalType: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageResource = when (animalType.lowercase()) {
        "vaca" -> R.drawable.ic_cow
        "caballo" -> R.drawable.ic_horse
        "toro" -> R.drawable.ic_bull
        "búfalo" -> R.drawable.ic_buffalo
        "cerdo" -> R.drawable.ic_pig
        "gallina ponedora" -> R.drawable.ic_hen
        "pollo de engorde" -> R.drawable.ic_chicken
        "pato" -> R.drawable.ic_duck
        "ganso" -> R.drawable.ic_goose
        "abeja" -> R.drawable.ic_bee
        "pez" -> R.drawable.ic_fish
        else -> R.drawable.ic_animal_default
    }
    
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Icono del animal
            Icon(
                painter = painterResource(id = imageResource),
                contentDescription = animalType,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 8.dp),
                tint = PrimaryGreen
            )
            
            // Nombre del tipo de animal
            Text(
                text = animalType,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Texto adicional
            Text(
                text = "Gestionar",
                fontSize = 14.sp,
                color = PrimaryGreen,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
} 