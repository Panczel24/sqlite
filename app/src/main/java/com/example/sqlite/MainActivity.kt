package com.example.sqlite

import android.R.attr.enabled
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sqlite.ui.theme.SqliteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                StudentScreen()
            }
        }
    }
}

@Composable
fun StudentScreen() {
    val context = LocalContext.current
    val db =  remember { StudentDatabaseHelper(context.applicationContext) }

    var students by remember { mutableStateOf(db.getAllStudents()) }

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var editingID by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp)
    ){
        Text(
            text = "Diáknyilvántartó",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Név") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Kor") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        Row{
            Button(
                enabled = name.isNotBlank() && (age.toIntOrNull()?:0) in 14..21 ,
                onClick = {
                    val studentAge = age.toIntOrNull()
                    if (studentAge != null){
                        val selectedId = editingID
                        if(selectedId== null){
                            db.addStudent(name.trim(), studentAge)
                        }else{
                            db.updateStudent(
                                selectedId,
                                name.trim(),
                                studentAge
                            )
                        }
                        students = db.getAllStudents()
                        name = "";
                        age="";
                        editingID = null;
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            {
                Text(
                    if(editingID == null)
                    "Hozzáadás"
                    else
                    "Módosítás"
                )
            }
            if(editingID != null)
                TextButton(
                    onClick = {
                    editingID = null
                    name = ""
                    age = ""

                }
            ) {Text("Mégse") }
        }


        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Diákok (${students.size})db)",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick={
                    students = db.getAllStudents()
                }
            ) { Text("Frissites")}
        }


        if(students.isNotEmpty()){
            Text("Még nincs felvett diák")
        }
        LazyColumn(
            verticalArrangement  = Arrangement.spacedBy(8.dp)
        ){
            items(students, key = {it.id})
            {
                student->
                Card(
                    modifier = Modifier.fillMaxWidth()

                ){
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ){
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = student.age.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row{
                            TextButton(
                                onClick={
                                    editingID = student.id
                                    name = student.name
                                    age = student.age.toString()
                                }
                            ){Text("Szerkesztés")}}
                        TextButton(
                            onClick={
                               db.deleteStudent(student.id)
                                students = db.getAllStudents()
                                if(editingID == student.id){
                                    editingID = null
                                    name = ""
                                    age = ""
                                }
                            }
                        ){Text("Törlés")}}
                        }
                    }
                }
            }

}



