package com.hoperun.smartcar.tcp

import com.hoperun.smartcar.car.CarState

/**
 * TCP接收解析器
 * 在连接成功后注册，解析小车返回的数据
 * 与鸿蒙端 TCPClientReceiveUtils.ets 功能一致
 */
object TcpReceiveParser {

    fun register() {
        // 这里不需要额外操作，TcpClientManager 内部已经集成了接收处理
        // 如果需要额外处理可以在连接成功后设置回调
    }
}
