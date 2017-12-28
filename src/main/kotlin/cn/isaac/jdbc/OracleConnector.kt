package cn.isaac.jdbc

import cn.isaac.config.config
import cn.isaac.config.jdbc
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 *  Only Oracle 12 supported
 * create by isaac at 2017/12/26 9:02
 */
class OracleConnector : Connector {

    init {
        Database.connect(config[jdbc.url], driver = config[jdbc.driver], user = config[jdbc.username], password = config[jdbc.password])
    }


    override fun getTables(tableName: String): List<TableResult> {
        val result = arrayListOf<TableResult>()
        transaction {
            Tables.select { Tables.name.eq(tableName) }.forEach {
                result.add(TableResult(it[Tables.name], it[Tables.comments]))
            }
        }
        return result
    }

    override fun getColumns(tableName: String): List<ColumnResult> {
        val result = arrayListOf<ColumnResult>()
        transaction {
            (Columns innerJoin ColComments).select { Columns.parentName.eq(tableName) }.forEach {
                result.add(ColumnResult(it[Columns.parentName], it[Columns.name], it[Columns.dataType],
                        it[Columns.dataLength], it[ColComments.comments]))
            }
        }
        return result
    }
}

object Tables : Table("USER_TAB_COMMENTS") {
    val name = varchar("TABLE_NAME", 30)
    val comments = varchar("COMMENTS", 50)
}

object Columns : Table("USER_TAB_COLUMNS") {
    val parentName = varchar("TABLE_NAME", 30) references Tables.name
    val name = varchar("COLUMN_NAME", 50)
    val dataType = varchar("DATA_TYPE", 20)
    val dataLength = integer("DATA_LENGTH")
}

object ColComments : Table("USER_COL_COMMENTS") {
    val parentName = varchar("TABLE_NAME", 30) references Columns.parentName
    val name = varchar("COLUMN_NAME", 50) references Columns.name
    val comments = varchar("COMMENTS", 50)
}

