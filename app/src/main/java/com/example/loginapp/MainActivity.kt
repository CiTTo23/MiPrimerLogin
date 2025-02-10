package com.example.loginapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.LoginAppTheme
import javax.security.auth.callback.PasswordCallback
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(Modifier.padding(innerPadding).background(Color.LightGray).fillMaxSize()){//box me pide un modifier, y lo he sobreescrito, le he puesto padding y el background de color gris
                        //es decir, he modificado el modifier
                        //con fillMaxSize hemos hecho que la caja ocupe el 100% de la pantalla
                        login1()
                    }
                }
            }
        }
    }
}

fun validateText(textToValidate : String, limitChars : Int) : Boolean{//devuelve true si el tecto es mas pequeño que lo permitido
    return textToValidate.length < limitChars
}

@Composable
fun login1(){

    var condiciones by remember { mutableStateOf(false)}
    var textoUser by remember { mutableStateOf("")}
    var textoPass by remember { mutableStateOf("")}
    var errorInName by remember { mutableStateOf(true) }
    var errorInPass by remember { mutableStateOf(true) }
    val context = LocalContext.current//no puedes referirte al contexto desde cualquier punto
    var exito by remember { mutableStateOf(false) }

    fun onLoginButtonClicked(){
        //funcion que valida los errores que puedan haber en el login, si hay algun error, el boton muestra un error
        if(!condiciones){
            Toast.makeText(context, "No has aceptado las condiciones", Toast.LENGTH_LONG).show()//el contexto es donde estas
        }
        if(errorInName){
            Toast.makeText(context, "Usuario inválido!", Toast.LENGTH_LONG).show()
        }
        if(errorInPass){
            Toast.makeText(context, "Contraseña inválida!", Toast.LENGTH_LONG).show()
        }else{
            exito = true
        }
    }

    if(!exito){//si no ha habido exito
        Column (Modifier.fillMaxSize(), Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){

            MiMarco {
                MostrarImagen()
            }
            Spacer(modifier = Modifier.height(70.dp))

            UsernameTextField(textoUser, { textoUser = it }, {errorInName = it})



            Spacer(modifier = Modifier.height(20.dp))

            MiMarco {
                PasswordTextField(textoPass, { textoPass = it }, { errorInPass=it })
            }

            Row(modifier = Modifier.fillMaxWidth().padding(8.dp),//fila que contiene el checkbox y su label
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {

                CheckBoxCondiciones(condiciones, { condiciones = it })
            }
            Spacer(modifier = Modifier.height(20.dp))
            MiBoton("Login", { onLoginButtonClicked() })

        }
    }else{
        Column (Modifier.fillMaxSize(), Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Greeting("$textoUser")
        }
    }


}

@Composable
fun MiMarco(content : @Composable () -> Unit){//para poner un marco a lo que queramos
    Surface(
        color = Color.Black,
        border = BorderStroke(4.dp, Color.Black),
        shadowElevation = 16.dp,
        modifier = Modifier.clip(RoundedCornerShape(10.dp))
    ){
        content()
    }
}

@Composable
fun miTextField(textoUser : String){
    TextField(
        value = textoUser, {})
}

@Composable
fun PasswordTextField(password : String, updatePassword : (String) -> Unit, updateError: (Boolean) -> Unit){
    //funcion composable que recibe un passord y una funcion que cambia el valor del campo
    //la funcion que recibiremos recibirá un string, que será lo que escribiremos en el textfield, y no devolverá nada porque solo actualizará el valor

    var hayError by remember {mutableStateOf(true)}
    val limite = 10

    TextField(
        value = password,
        placeholder = {Text(stringResource(R.string.placeHolder_pass))},
        trailingIcon = {if(hayError){//icono si la contraseña no es válida
            Icon(Icons.Filled.Warning, contentDescription = "error")
        }},
        visualTransformation = PasswordVisualTransformation(),//con esto podemos poner los puntitos de ocultacion
        leadingIcon = {Icon(Icons.Filled.Lock, contentDescription = "pass")},//icono a la izquierda del textfield
        isError = hayError,
        onValueChange = {
            updatePassword(it)
            hayError = validateText(it, limite)
            updateError(hayError)
        }
    )
}


@Composable
fun UsernameTextField(userName : String, updateUsername : (String) -> Unit, updateError : (Boolean) -> Unit){//hacemos una funcion de orden superior

    var hayError by remember {mutableStateOf(true)}
    val limite = 8

    TextField(//tengo un textfield que tiene un valor, y que cuando se usa hace una funcionalidad
        value = userName,
        trailingIcon = {if(hayError){
            Icon(Icons.Filled.Warning, contentDescription = "errorUser")
        } },
        placeholder = {Text(stringResource(R.string.placeHolder_userName))},
        supportingText = {//texto que aparece abajo del textfield, aclarativo
            if(hayError){
                SupportingText(userName, limite)
            }
                         },
        leadingIcon = {Icon(Icons.Filled.Face, contentDescription = "username")},
        isError = hayError,
        onValueChange = {
            updateUsername(it)
            hayError = validateText(it, limite)
            updateError(hayError)
        }//ejecutate, funcionalidad
    )
}

@Composable
fun SupportingText(text : String, limit : Int){//funcion que recibe un texto y un limite de caracteres y genera el supporting text
    Text("Caracteres mínimos: ${text.length} / $limit")
}

@Composable
fun MostrarImagen() {

    val userImage = R.drawable.dooley

    Image(
        painter = painterResource(id = userImage),
        contentDescription = "Imagen de ejemplo",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(250.dp) // Tamaño de la imagen
            .clip(RoundedCornerShape(150.dp))


    )
}

@Composable//hago otro composable boton que recibe el simbolo y ejecuta la función que le pasemos
fun MiBoton(simbolo : String, op : () -> Unit){//ELEVACION DE ESTADO
    //cuando me clicken, ejecutaré una función que desconozco
    Button(modifier = Modifier.width(150.dp), onClick = {op()}){
        Text(modifier = Modifier.padding(16.dp), text = simbolo, fontSize = 20.sp)
    }
}

@Composable
fun CheckBoxCondiciones(estado : Boolean, op : (Boolean) -> Unit){//se le pasa el estado del checkbox y la operacion que realices
    Checkbox(checked = estado, {op(it)})
    Text(
        text = "Aceptar términos"
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hola $name!",
        modifier = Modifier,
        fontSize = 30.sp,
        color = Color.Red
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoginAppTheme {
        Greeting("Android")
    }
}