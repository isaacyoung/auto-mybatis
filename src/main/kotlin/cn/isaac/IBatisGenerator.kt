package cn.isaac

import cn.isaac.code.Generate
import cn.isaac.config.config
import cn.isaac.config.out
import java.io.File

/**
 * Created by isaac on 17-7-1.
 */

fun main(args: Array<String>) {

    val file = File(config[out.target])
    if (file.exists()) {
        file.deleteRecursively()
    }

    Generate.code()

}
