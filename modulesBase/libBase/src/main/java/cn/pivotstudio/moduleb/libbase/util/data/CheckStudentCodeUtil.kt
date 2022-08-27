package cn.pivotstudio.moduleb.libbase.util.data

object CheckStudentCodeUtil {

    fun isStudentCode(code: String?): Boolean {

        code?.run {
            // 学号长度 == 10
            if (code.length != 10) return false

            // 学号开头不能是数字 V2登录不能是小写
            if (code.first().isDigit() || code.first().isLowerCase()) return false

            // 学号除了开头只能是数字
            code.substring(1).forEach {
                if (!it.isDigit()) return false
            }

            return true
        }

        return false
    }

    fun isLegalPassword(password: String?): Boolean {
        password?.run {
            if (length in 6..16) return true

            return false
        }
        return false
    }
}