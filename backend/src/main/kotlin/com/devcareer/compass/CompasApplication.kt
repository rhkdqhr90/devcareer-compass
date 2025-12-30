package com.devcareer.compass

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CompasApplication

fun main(args: Array<String>) {
	runApplication<CompasApplication>(*args)
}
