package cn.isaac.code

import cn.isaac.code.ibatis.*

/**
 *
 * create by isaac at 2018/1/11 20:29
 */
object Generate {
    fun code() {
        GeneratePojo.code()
        GenerateDao.code()
        GenerateDaoImpl.code()
        GenerateService.code()
        GenerateServiceImpl.code()
        GenerateIbatisOracleXml.code()
    }
}