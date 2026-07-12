package com.hoperun.smartcar.car

/**
 * 小车指令编码器
 * 协议格式: $01{cmd}{size}{data}{checksum}#
 * 与鸿蒙端 CarEncode.ets 保持一致
 */
object CarEncode {

    private const val CAR_TYPE = "01"

    /** 获取电池电压 */
    fun getBattery(): String = baseEncode("02")

    /** 获取硬件版本号 */
    fun getHardwareVersion(): String = baseEncode("01")

    /** 蜂鸣器控制 */
    fun buzzer(state: Boolean): String =
        baseEncode("13", numberToHex(if (state) 1 else 0, 2))

    /** 速度档位控制 */
    fun speedControl(level: Int): String =
        baseEncode("16", numberToHex(level, 2))

    /** 自稳开关 */
    fun selfStabilize(state: Boolean): String =
        baseEncode("17", numberToHex(if (state) 1 else 0, 2))

    /** 设置LED灯颜色 */
    fun setLedColor(r: Int, g: Int, b: Int): String =
        baseEncode("30", numberToHex(r, 2) + numberToHex(g, 2) + numberToHex(b, 2))

    /** 开始循迹 */
    fun trackingOpen(): String = baseEncode("63")

    /** 关闭循迹 */
    fun trackingClose(): String = baseEncode("64")

    /** 拍摄照片 */
    fun takePhotos(): String = baseEncode("60")

    /** 开始录像 */
    fun startRecording(): String = baseEncode("61")

    /** 结束录像 */
    fun closeRecording(): String = baseEncode("62")

    /** 摇杆自由控制 */
    fun ctrlCar(speedX: Int, speedY: Int): String {
        var x = speedX.coerceIn(-100, 100)
        var y = speedY.coerceIn(-100, 100)
        if (x < 0) x += 256
        if (y < 0) y += 256
        return baseEncode("10", numberToHex(x, 2) + numberToHex(y, 2))
    }

    /** 按键方向控制 */
    fun buttonCar(direction: CarDirection): String =
        baseEncode("15", numberToHex(direction.code, 2))

    /** 四轮独立速度控制 */
    fun upSpeedCar(l1: Int, l2: Int, r1: Int, r2: Int): String {
        fun format(v: Int): Int {
            var x = v.coerceIn(-100, 100)
            if (x < 0) x += 256
            return x
        }
        return baseEncode(
            "21",
            numberToHex(format(l1), 2),
            numberToHex(format(l2), 2),
            numberToHex(format(r1), 2),
            numberToHex(format(r2), 2)
        )
    }

    /** 基础编码 */
    private fun baseEncode(type: String, vararg datas: String): String {
        val info = datas.joinToString("")
        // 数据长度 = info十六进制字符数 + 2（与鸿蒙端完全一致）
        val size = numberToHex(info.length + 2, 2)
        var code = CAR_TYPE + type + size + info
        code += numberToHex(checkChecksum(code), 2)
        return "$$code#"
    }

    /** 数字转十六进制字符串 */
    private fun numberToHex(num: Int, len: Int): String {
        var hex = num.toString(16).uppercase()
        while (hex.length < len) hex = "0$hex"
        return hex
    }

    /** 校验和计算 */
    private fun checkChecksum(data: String): Int {
        var sum = 0
        var i = 0
        while (i < data.length) {
            val byte = data.substring(i, i + 2).toInt(16)
            sum = (sum + byte) % 256
            i += 2
        }
        return sum
    }
}
