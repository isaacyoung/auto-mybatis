package cn.isaac

import cn.isaac.config.config
import cn.isaac.config.jdbc
import cn.isaac.jdbc.OracleConnector2

/**
 * Created by isaac on 17-7-1.
 */

fun main(args: Array<String>) {

    println(config[jdbc.url])

    // database
    val connector = OracleConnector2()
    connector.getTables(config[jdbc.table]).forEach {
        println(it.name)
        connector.getColumns(it.name).forEach {
            println("${it.name} -------------- ${it.comments}")
        }
    }


}
