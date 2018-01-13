package cn.isaac.context

import cn.isaac.config.config
import cn.isaac.config.jdbc
import cn.isaac.jdbc.ColumnResult
import cn.isaac.jdbc.OracleConnector2
import java.io.File

/**
 *
 * create by isaac at 2018/1/11 20:57
 */

class TableContext(var name: String,
                   var comments: String?,
                   var columnsList: List<ColumnResult>?)

class ContextFactory {
    val tables = arrayListOf<TableContext>()

    init {
        val connector = OracleConnector2()
        connector.getTables(config[jdbc.table]).forEach {
            val t = TableContext(it.name,it.comments,connector.getColumns(it.name))
            tables.add(t)
        }
    }

    fun getClassName(tableName: String): String {
        return getJavaName(tableName, firstUpper = true)
    }

    fun getShortClassName(tableName: String): String {
        if (tableName.startsWith("TB_")) {
            return getJavaName(tableName.substring(3), firstUpper = true)
        }
        return getJavaName(tableName, firstUpper = true)
    }

    fun getFieldName(name: String): String {
        return getJavaName(name, firstUpper = false)
    }

    fun getShortFieldName(name: String): String {
        if (name.startsWith("TB_")) {
            return getJavaName(name.substring(3), firstUpper = false)
        }
        return getJavaName(name, firstUpper = false)
    }

    private fun getJavaName(name: String, firstUpper: Boolean): String {
        val jdbcFlag = "_@$# /&"
        var result = ""
        var upper = false

        name.toLowerCase().forEach {
            when {
                jdbcFlag.contains(it) -> upper = true
                upper -> {
                    result += it.toUpperCase()
                    upper = false
                }
                else -> result += it
            }
        }

        if (firstUpper) {
            result = result.substring(0,1).toUpperCase() + result.substring(1)
        }
        return result
    }

    fun getComment(title: String?): String {
        var w = ""
        w += "    /**\n"
        w += "     * $title\n"
        w += "     */\n"
        return w
    }

    fun getImports(imports: HashSet<String>): String {
        var w = ""
        imports.sorted().forEach {
            w += "import $it;\n"
        }
        return w
    }

    fun getPathFromPackage(path: String): String {
        return path.replace(".", File.separator)
    }

}

val context = ContextFactory()