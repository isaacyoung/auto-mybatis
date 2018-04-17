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

    override fun getTables(tableName: String): List<TableResult> {
        val toTable: (Row) -> TableResult = { row ->
            TableResult(
                    row.string("TABLE_NAME"),
                    row.stringOrNull("COMMENTS")
            )
        }

        val tablesQuery = when {
            config[jdbc.table].isNotEmpty() -> queryOf("SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME=? "
                    , config[jdbc.table]).map(toTable).asList
            else -> queryOf("SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS ").map(toTable).asList
        }

        return session.run(tablesQuery)
    }

    override fun getColumns(tableName: String): List<ColumnResult> {
        val toColumn: (Row) -> ColumnResult = { row ->
            ColumnResult(
                    row.string("TABLE_NAME"),
                    row.string("COLUMN_NAME"),
                    row.string("DATA_TYPE"),
                    row.int("DATA_LENGTH"),
                    row.stringOrNull("COMMENTS")
            )
        }

        val sql = """SELECT t.TABLE_NAME,t.COLUMN_NAME,t.DATA_TYPE,t.DATA_LENGTH,c.COMMENTS
        |FROM USER_TAB_COLUMNS t
        |LEFT JOIN USER_COL_COMMENTS c
        |ON t.TABLE_NAME=c.TABLE_NAME AND t.COLUMN_NAME=c.COLUMN_NAME
        |WHERE t.TABLE_NAME=?""".trimMargin()

        val columnsQuery = queryOf(sql, tableName).map(toColumn).asList
        return session.run(columnsQuery)
    }

    fun getPrimaryKey(tableName: String): String {
        val sql = """
            select a.column_name
            from user_cons_columns a, user_constraints b
            where a.constraint_name = b.constraint_name
            and b.constraint_type = 'P'
            and a.table_name =?""".trimIndent()
        var query = queryOf(sql,tableName)
                .map { row:Row -> row.string("COLUMN_NAME") }
                .asList
        val result = session.run(query)
        return if (result.isNotEmpty()) {
            result[0]
        } else {
            ""
        }
    }
}


