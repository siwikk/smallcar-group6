package com.hoperun.smartcar.car

/**
 * 小车运动方向枚举
 * 与鸿蒙端 CarEnum.ets 保持一致
 */
enum class CarDirection(val code: Int) {
    /** 停车 */
    Stop(0),
    /** 前进 */
    Front(1),
    /** 后退 */
    After(2),
    /** 左平移 */
    Left(3),
    /** 右平移 */
    Right(4),
    /** 左旋转 */
    LeftRotate(5),
    /** 右旋转 */
    RightRotate(6),
    /** 刹车停止 */
    Brake(7)
}
