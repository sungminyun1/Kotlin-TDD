package io.hhplus.tdd.service

import io.hhplus.tdd.point.UserPoint

interface UserPointRepository {

    fun selectById(id: Long): UserPoint

    fun insertOrUpdate(userPoint: UserPoint): UserPoint
}