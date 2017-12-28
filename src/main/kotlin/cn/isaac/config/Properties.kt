package cn.isaac.config

import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.booleanType
import com.natpryce.konfig.getValue
import com.natpryce.konfig.stringType

object jdbc : PropertyGroup() {
    val driver by stringType
    val url by stringType
    val username by stringType
    val password by stringType
    val table by stringType
}

// package
object pkg : PropertyGroup() {
    val model by stringType
    val dao by stringType
    val serv by stringType
    val xml by stringType
}

// output
object out : PropertyGroup() {
    val target by stringType
}

// project
object proj : PropertyGroup() {
    val target by stringType
    val over by booleanType
}