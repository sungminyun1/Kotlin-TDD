package io.hhplus.tdd

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SampleTest : FunSpec({

    test("1 + 1 = 2"){
        val calc = Calculator()
        calc.add(1,1) shouldBe 2
    }
})

class Calculator {
    fun add(a: Int, b: Int) = a + b
}