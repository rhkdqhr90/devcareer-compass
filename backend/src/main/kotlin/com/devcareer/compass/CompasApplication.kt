package com.devcareer.compass

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class CompasApplication

fun main(args: Array<String>) {
	runApplication<CompasApplication>(*args)
}
