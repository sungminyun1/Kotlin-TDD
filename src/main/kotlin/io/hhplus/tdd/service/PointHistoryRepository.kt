package io.hhplus.tdd.service

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint

interface PointHistoryRepository {
    fun insert(
        userPoint: UserPoint, amount: Long, type: TransactionType
    ): PointHistory

    fun selectByUserId(userId: Long): List<PointHistory>
}