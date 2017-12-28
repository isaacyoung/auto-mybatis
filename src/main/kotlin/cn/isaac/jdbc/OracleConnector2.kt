package cn.isaac.jdbc

import cn.isaac.config.config
import cn.isaac.config.jdbc
import kotliquery.Row
import kotliquery.Session
import kotliquery.queryOf
import kotliquery.sessionOf

/**
 *
 * create by isaac at 2017/12/28 15:49
 */
class OracleConnector2 : Connector {

    private val session: Session = sessionOf(config[jdbc.url], config[jdbc.username], config[jdbc.password])

    private val toTable: (Row) -> TableResult = { row ->
        TableResult(
                row.string("TABLE_NAME"),
                row.stringOrNull("COMMENTS")
        )
    }

    private val tablesQuery = when {
        config[jdbc.table].isNotEmpty() -> queryOf("SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME=? "
                , config[jdbc.table]).map(toTable).asList
        else -> queryOf("SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS ").map(toTable).asList
    }

    override fun getTables(tableName: String): List<TableResult> {
        return session.run(tablesQuery)
    }

    private val toColumn: (Row) -> ColumnResult = { row ->
        ColumnResult(
                row.string("TABLE_NAME"),
                row.string("COLUMN_NAME"),
                row.string("DATA_TYPE"),
                row.int("DATA_LENGTH"),
                row.stringOrNull("COMMENTS")
        )
    }

    private val sql = """SELECT t.TABLE_NAME,t.COLUMN_NAME,t.DATA_TYPE,t.DATA_LENGTH,c.COMMENTS
        |FROM USER_TAB_COLUMNS t
        |LEFT JOIN USER_COL_COMMENTS c
        |ON t.TABLE_NAME=c.TABLE_NAME AND t.COLUMN_NAME=c.COLUMN_NAME
        |WHERE t.TABLE_NAME=?""".trimMargin()

    override fun getColumns(tableName: String): List<ColumnResult> {
        val columnsQuery = queryOf(sql, tableName).map(toColumn).asList
        return session.run(columnsQuery)
    }
}


