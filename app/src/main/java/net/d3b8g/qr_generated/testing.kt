package net.d3b8g.qr_generated

import kotlin.random.Random


fun main(){
    val rb = Random.nextInt(0,1)

    val chars1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val chars2 = "Я ЛЮБЛЮ ТЕСТИРОВАНИЕ"
    val chars3 = "Лошадь кушает яблоко"
    val chars4 = "Мы добьемся всего "
    val ran = 1
    var passWord = ""
    for (i in 0..2) {
        passWord += chars2.split(" ")[Math.floor(Math.random() * chars2.split(" ").size).toInt()] + " "
    }
    print(passWord)
}