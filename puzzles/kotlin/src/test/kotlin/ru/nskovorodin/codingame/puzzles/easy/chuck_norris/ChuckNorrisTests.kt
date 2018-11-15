package ru.nskovorodin.codingame.puzzles.easy.chuck_norris

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ChuckNorrisTests: StringSpec({

    "Char to binary string"{
        ChuckNorrisPuzzle()
                .solve("%".byteInputStream())
                .solution shouldBe "00 0 0 0 00 00 0 0 00 0 0 0"
    }

    "Character C"{
        ChuckNorrisPuzzle()
                .solve("C".byteInputStream())
                .solution shouldBe "0 0 00 0000 0 00"
    }

    "Message CC"{
        ChuckNorrisPuzzle()
                .solve("CC".byteInputStream())
                .solution shouldBe "0 0 00 0000 0 000 00 0000 0 00"
    }

    "Character %"{
        ChuckNorrisPuzzle()
                .solve("%".byteInputStream())
                .solution shouldBe "00 0 0 0 00 00 0 0 00 0 0 0"
    }

    "Message from Chuck Norris"{
        ChuckNorrisPuzzle()
                .solve("Chuck Norris' keyboard has 2 keys: 0 and white space.".byteInputStream())
                .solution shouldBe "0 0 00 0000 0 0000 00 0 0 0 00 000 0 000 00 0 0 0 00 0 0 000 00 000 0 0000 00 0 0 0 00 0 0 00 00 0 0 0 00 00000 0 0 00 00 0 000 00 0 0 00 00 0 0 0000000 00 00 0 0 00 0 0 000 00 00 0 0 00 0 0 00 00 0 0 0 00 00 0 0000 00 00 0 00 00 0 0 0 00 00 0 000 00 0 0 0 00 00000 0 00 00 0 0 0 00 0 0 0000 00 00 0 0 00 0 0 00000 00 00 0 000 00 000 0 0 00 0 0 00 00 0 0 000000 00 0000 0 0000 00 00 0 0 00 0 0 00 00 00 0 0 00 000 0 0 00 00000 0 00 00 0 0 0 00 000 0 00 00 0000 0 0000 00 00 0 00 00 0 0 0 00 000000 0 00 00 00 0 0 00 00 0 0 00 00000 0 00 00 0 0 0 00 0 0 0000 00 00 0 0 00 0 0 00000 00 00 0 0000 00 00 0 00 00 0 0 000 00 0 0 0 00 00 0 0 00 000000 0 00 00 00000 0 0 00 00000 0 00 00 0000 0 000 00 0 0 000 00 0 0 00 00 00 0 0 00 000 0 0 00 00000 0 000 00 0 0 00000 00 0 0 0 00 000 0 00 00 0 0 0 00 00 0 0000 00 0 0 0 00 00 0 00 00 00 0 0 00 0 0 0 00 0 0 0 00 00000 0 000 00 00 0 00000 00 0000 0 00 00 0000 0 000 00 000 0 0000 00 00 0 0 00 0 0 0 00 0 0 0 00 0 0 000 00 0"
    }


})