package com.shumikhin.kotlinkursgb.data

data class HumanDate(val name: String, var age: Int) {
    override fun toString(): String {
        return "Имя - $name, Лет = $age"
    }
}