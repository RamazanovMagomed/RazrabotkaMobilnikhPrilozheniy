package ru.example.lab10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Используем стандартную тему Android, никаких Lab10Theme
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LabScreen()
                }
            }
        }
    }
}

@Composable
fun LabScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("Нажмите для запроса") }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Button(onClick = {
            scope.launch {
                text = withContext(Dispatchers.IO) {
                    SchoolDb(context).use { helper ->
                        val db = helper.writableDatabase
                        try {
                            db.beginTransaction()

                            db.execSQL("INSERT OR IGNORE INTO `groups` VALUES(1,'ИС-21')")
                            db.execSQL("INSERT OR IGNORE INTO `student`(id,name,group_id) VALUES(1,'Иван',1)")

                            // throw RuntimeException("Тест отката!")

                            db.setTransactionSuccessful()
                        } catch (e: Exception) {
                            return@withContext "Ошибка: ${e.message}"
                        } finally {
                            db.endTransaction()
                        }

                        db.rawQuery("SELECT s.name, g.title FROM `student` s INNER JOIN `groups` g ON s.group_id = g.id", null).use { c ->
                            buildList {
                                while (c.moveToNext()) {
                                    add(c.getString(0) + " / " + c.getString(1))
                                }
                            }.joinToString("\n")
                        }
                    }
                }
            }
        }) {
            Text("Прочитать JOIN")
        }

        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}