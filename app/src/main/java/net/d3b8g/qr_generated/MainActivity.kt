package net.d3b8g.qr_generated

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity(){

    /*
        Тут заполняется лист
        val catlist = arrayListOf()
     */

    val params_answer_1 = "Грязная%20камера"
    val params_answer_2 = "Затертый%20код"
    val params_answer_3 = "Неровная%20поверхность"

    // ВК НЕ ВОСПРИНИМАЕТ ПРИ ЗАПРОСЕ ПРОБЕЛЫ И ПРОЧИЕ СИМВОЛЫ %20 = ПРОБЕЛ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Если вам не нужен параметр генерации QR-кодов сотрите вызов функции startR()
        startR()

        btn1.setOnClickListener{
            deviceIs = params_answer_1
            mainR()
        }
        btn2.setOnClickListener {
            deviceIs = params_answer_2
            mainR()
        }
        btn3.setOnClickListener {
            deviceIs = params_answer_3
            mainR()
        }

    }

    private fun startR() {
        // Тут QR собирается по ссылке И меняет изображение каждые 3 секунды

        if(Random.nextInt(0,6)<3){
            Handler().postDelayed({
                Picasso.get()
                    .load("http://qrcoder.ru/code/?${generate3rand(0)}&4&0")
                    .resize(300,300)
                    .into(random_img)
                startR()
            },3000)
        }else{
            Handler().postDelayed({
                Picasso.get()
                    .load("http://qrcoder.ru/code/?https%3A%2F%2Fvk.com%2F${generate3rand(1)}&4&0")
                    .resize(300,300)
                    .into(random_img)
                startR()
            },3000)
        }
    }

    private fun generate3rand(from:Int): String {
        val rb = Random.nextInt(0,1)

        val chars1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val chars2 = "%DF %CB%DE%C1%CB%DE %D2%C5%D1%D2%C8%D0%CE%C2%C0%CD%C8%C5"
        val chars3 = "%F7%E0%F1%FB %F2%E8%EA%E0%FE %F2%E8%F5%EE"
        val chars4 = "%EA%E0%F8%E5%EB%FC %EC%E5%F8%E0%E5%F2 %F1%EF%E0%F2%FC"

        // chars - ру. строки для QR-генератора ( не воспринимает РУ. символику ибо)

        val ran = Random.nextInt(0,3)
        var passWord = ""
        when(from){
            0->{
                when(ran){
                    0->{
                        for (i in 0..Random.nextInt(6,24)) {
                            passWord += chars1[Math.floor(Math.random() * chars1.length).toInt()]
                        }
                    }
                    1->{
                        for (i in 0..2) {
                            passWord += chars2.split(" ")[Math.floor(Math.random() * chars2.split(" ").size).toInt()] + " "
                        }
                    }
                    2->{
                        for (i in 0..2) {
                            passWord += chars3.split(" ")[Math.floor(Math.random() * chars3.split(" ").size).toInt()] + " "
                        }
                    }
                    3->{
                        for (i in 0..2) {
                            passWord += chars4.split(" ")[Math.floor(Math.random() * chars4.split(" ").size).toInt()] + " "
                        }
                    }
                }
            }
            1->{
                for (i in 0..Random.nextInt(6,24)) {
                    passWord += chars1[Math.floor(Math.random() * chars1.length).toInt()]
                }
            }
        }

        return passWord
    }

}
