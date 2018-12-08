import io.kotlintest.specs.StringSpec

class ElvesTest: StringSpec({

    val defaultInput = "0\n" +          //Turn type
            "0110 1110 0110 0011\n" +   //Map
            "0110 0101 0011 1010\n" +
            "1100 1101 1010 0011\n" +
            "1101 1101 1110 0101\n" +
            "1 3 3 0101\n" + // Первый игрок [кол-во квестов, коордХ, коордY, тайл на котором стоит игрок]
            "1 0 3 1101\n" +// Оппонент [кол-во квестов, коордХ, коордY, тайл на котором стоит игрок]
            "2\n" +         // Кол-во предметов на карте и для какого игрока они нужны
            "SWORD 0 1 0\n" + // [Имя, коордX, коордY, idИгрока]
            "SWORD 0 0 1\n" + // [Имя, коордX, коордY, idИгрока]
            "2\n" + // Количество активных квестов
            "SWORD 0 SWORD 1" // Предмет и какому игроку он принадлежит
            "Char to binary string"

    "default test"{
        val game = ElvesDoStuff()
        game.main(defaultInput)
    }



})