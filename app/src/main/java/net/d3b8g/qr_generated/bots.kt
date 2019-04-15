package net.d3b8g.qr_generated

import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.util.Log
import com.google.gson.JsonParser
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

//путь к дефаулт папке скриншотов 
val path_img = "${Environment.getExternalStorageDirectory().absolutePath}/DCIM/Screenshots"

var using_id = 0

val token = "Ну тут естествеено свой токен с доступом к смс"

data class photoSaveMessage(val server:Int,val photo:String,val hash:String)
data class resultPhoto(val id:String)
lateinit var getMessagesUpload:URL
lateinit var onPhotoSavedParams:photoSaveMessage
lateinit var messagePhoto:resultPhoto

//Тут естественно ссылка на пользователя с которого messages.getHistory . Группы с -id, просто юзер id
val user_ident = -84585194

val listFiles = File(path_img).listFiles()

//Дефолт значение, которое все равно меняется перед вызовом mainR()
var deviceIs = "Default"

var stateChechek = 0

fun mainR(){

    //Все фоновые процедуры начинают свой путь от сюда
    //Обратите внимание на класс checkForAnswer() и измените значения на те, которые вам нужны
    uploadForServer().execute("")
}

class uploadForServer:AsyncTask<String,String,String>(){
    override fun doInBackground(vararg params: String?): String {
        Thread.sleep(1000)
        var response:String = ""
        val url = URL("https://api.vk.com/method/photos.getMessagesUploadServer?peer_id=1&version=5.92&access_token=$token")

        var connection = url.openConnection() as HttpsURLConnection
        try{
            connection.connect()
            response = connection.inputStream.use { it.reader().use { reader-> reader.readText()} }
        }finally {
            connection.disconnect()
        }
        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        val parser = JsonParser()
        val model = parser.parse(result).asJsonObject.get("response")
        getMessagesUpload = URL(model.asJsonObject.get("upload_url").asString)
        saveForServer().execute(getMessagesUpload.toString())
    }
}
class saveForServer:AsyncTask<String,String,String>(){
    override fun doInBackground(vararg params: String?): String {
        val connection: HttpURLConnection
        val dataOutputStream: DataOutputStream
        val lineEnd = "\r\n"
        val twoHyphens = "--"
        val boundary = "*****"
        var selectedFilePath = ""
        try{
            selectedFilePath = listFiles[using_id].path
        }catch (e:Exception){
            Log.e("RRR","ФОТОК НЕТ")
        }
        Log.e("RRR","$using_id")
        var bytesRead:Int
        var bytesAvailable:Int
        var bufferSize:Int
        var buffer:ByteArray
        var maxBufferSize = 1 * 1024 * 1024
        var selectedFile = File(selectedFilePath)
        try
        {
            val fileInputStream = FileInputStream(selectedFile)
            val url = URL(params[0])
            connection = url.openConnection() as HttpURLConnection
            connection.doInput = true//Allow Inputs
            connection.doOutput = true//Allow Outputs
            connection.useCaches = false//Don't use a cached Copy
            connection.requestMethod = "POST"
            connection.setRequestProperty("Connection", "Keep-Alive")
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            connection.setRequestProperty("photo", selectedFilePath)


            //creating new dataoutputstream
            dataOutputStream = DataOutputStream(connection.outputStream)

            //writing bytes to data outputstream
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
            dataOutputStream.writeBytes(
                "Content-Disposition: file; name=\"photo\";filename=\""
                        + selectedFilePath + "\"" + lineEnd
            )
            dataOutputStream.writeBytes(lineEnd)
            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            buffer = ByteArray(bufferSize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            while (bytesRead > 0)
            {
                dataOutputStream.write(buffer, 0, bufferSize)
                bytesAvailable = fileInputStream.available()
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            }

            dataOutputStream.writeBytes(lineEnd)
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

            val answer = connection.inputStream.use { it.reader().use { reader-> reader.readText()} }

            fileInputStream.close()
            dataOutputStream.flush()
            dataOutputStream.close()

            listFiles[using_id].delete()

            return  answer
        }
        catch (e:FileNotFoundException) {
            e.printStackTrace()
        }
        catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.e("RRR", "$e")
        }
        catch (e:IOException) {
            e.printStackTrace()
            Log.e("RRR", "$e")
        }
        return "NULL"
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        val parser = JsonParser()
        onPhotoSavedParams =photoSaveMessage(
            parser.parse(result).asJsonObject.get("server").asInt,
            parser.parse(result).asJsonObject.get("photo").asString,
            parser.parse(result).asJsonObject.get("hash").asString
        )
        saveResult().execute(onPhotoSavedParams)
    }
}
class saveResult:AsyncTask<photoSaveMessage,String,String>(){
    override fun doInBackground(vararg params: photoSaveMessage?): String {
        var response:String = ""
        val url = URL("https://api.vk.com/method/photos.saveMessagesPhoto?photo=${params[0]?.photo}" +
                "&hash=${params[0]?.hash}&server=${params[0]?.server}&version=5.92&access_token=$token")
        var connection = url.openConnection() as HttpsURLConnection
        try{
            connection.connect()
            response = connection.inputStream.use { it.reader().use { reader-> reader.readText()} }
        }finally {
            connection.disconnect()
        }
        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        Thread.sleep(600)
        val parser = JsonParser()
        val mr = parser.parse(result).asJsonObject
        messagePhoto = resultPhoto(
                mr.get("response").asJsonArray.get(0).asJsonObject.get("id").asString
        )
        sendResultMessage().execute(messagePhoto)

        using_id += 1
    }
}
class sendResultMessage:AsyncTask<resultPhoto,String,String>(){
    override fun doInBackground(vararg params: resultPhoto?): String {
        val photo = params[0]?.id
        var response:String = ""
        val url = URL("https://api.vk.com/method/messages.send?attachment=$photo&user_id=$user_ident&version=5.92&access_token=$token")
        var connection = url.openConnection() as HttpsURLConnection
        try{
            connection.connect()
            response = connection.inputStream.use { it.reader().use { reader-> reader.readText()} }
        }finally {
            connection.disconnect()
        }
        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        stateChechek = 0
        checkForAnswer().execute("")
    }
}
class checkForAnswer:AsyncTask<String,String,String>(){
    override fun doInBackground(vararg params: String?): String {
        Thread.sleep(2000)
        var response:String = ""
        val url = URL("https://api.vk.com/method/messages.getHistory?count=1&user_id=$user_ident&rev=0&version=5.92&access_token=$token")
        var connection = url.openConnection() as HttpsURLConnection
        try{
            connection.connect()
            response = connection.inputStream.use { it.reader().use { reader-> reader.readText()} }
        }finally {
            connection.disconnect()
        }
        return response
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        Log.e("RRR","$result")

        //Тут думаю без комментариев
        val last_message_for_anwer = "последняя часть сообщения "

        if(stateChechek == 0){
            if(result.contains(last_message_for_anwer)){
                answerLikeDevice().execute("")
            }else{
                Handler().postDelayed({answerLikeDevice().execute("")},11*60*1010)
            }
        }
        else{
            if(deviceIs == "Грязная%20камера"){
                when {
                    result.contains("Пусть на камере") -> uploadForServer().execute("")
                    result.contains("мы проверим фото") -> answerLikeDevice().execute("")
                    else -> Handler().postDelayed({answerLikeDevice().execute("")},11*60*1010)
                }
            }else{
                when{
                    result.contains("Часто флаеры") -> uploadForServer().execute("")
                    result.contains("мы проверим фото") -> answerLikeDevice().execute("")
                    else -> Handler().postDelayed({answerLikeDevice().execute("")},11*60*1010)
                }
            }

        }
    }
}
class answerLikeDevice:AsyncTask<String,String,String>(){
    override fun doInBackground(vararg params: String?): String {
        var response:String = ""
        var connection = URL("https://api.vk.com/method/messages.send?&user_id=$user_ident&message=$deviceIs&version=5.92&access_token=$token").openConnection() as HttpsURLConnection
        try{
            connection.connect()
            response = connection.inputStream.use { it.reader().use { reader-> reader.readText()} }
        }finally {
            connection.disconnect()
        }
        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        stateChechek=1
        checkForAnswer().execute("")
    }
}

class TimerWithAnswer: TimerTask() {

    var counter = 0

    override fun run() {
        val timer = Timer("MyTimer")
        timer.scheduleAtFixedRate(TimerWithAnswer(), 30, 3000)
        System.out.println("TimerTask executing counter is: " + counter)
        counter++
    }
}



