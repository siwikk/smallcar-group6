package com.hoperun.smartcar.car

import com.hoperun.smartcar.tcp.TcpClientManager

/**
 * 小车API封装
 * 与鸿蒙端 CarApi.ets 保持一致
 */
object CarApi {

    /** 按键方向控制 */
    fun carBtnCtrl(d: CarDirection) {
        send(CarEncode.buttonCar(d))
    }

    /** 获取电池电压 */
    fun getBattery() {
        send(CarEncode.getBattery())
    }

    /** 获取硬件版本 */
    fun getHardwareVersion() {
        send(CarEncode.getHardwareVersion())
    }

    /** 蜂鸣器控制 */
    fun buzzerCtrl(state: Boolean) {
        send(CarEncode.buzzer(state))
    }

    /** 速度档位控制 */
    fun speedCtrl(level: Int) {
        send(CarEncode.speedControl(level))
    }

    /** 自稳开关 */
    fun selfStabilize(state: Boolean) {
        send(CarEncode.selfStabilize(state))
    }

    /** 设置LED灯颜色 */
    fun setLedColor(r: Int, g: Int, b: Int) {
        send(CarEncode.setLedColor(r, g, b))
    }

    /** 摇杆控制 */
    fun rockerCtrl(speedX: Int, speedY: Int) {
        send(CarEncode.ctrlCar(speedX, speedY))
    }

    /** 四轮速度 */
    fun wheelSpeed(l1: Int, l2: Int, r1: Int, r2: Int) {
        send(CarEncode.upSpeedCar(l1, l2, r1, r2))
    }

    /** 发送原始消息 */
    fun send(message: String) {
        TcpClientManager.sendMessage(message)
    }
}
