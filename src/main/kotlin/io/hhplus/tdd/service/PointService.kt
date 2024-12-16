package io.hhplus.tdd.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Service

@Service
class PointService(
    val pointHistoryRepository: PointHistoryRepository,
    val userPointRepository: UserPointRepository
){

    fun getUserPoint(
        id: Long
    ): UserPoint {
        return userPointRepository.selectById(id)
    }

    fun chargeUserPoint(
        id: Long,
        amount: Long
    ): UserPoint {
        val userPoint = getUserPoint(id)
        val chargedPoint = userPoint.chargePoint(amount)

        val result = userPointRepository.insertOrUpdate(chargedPoint)
        pointHistoryRepository.insert(
            result, amount, TransactionType.CHARGE
        )

        return result
    }

    fun useUserPoint(
        id: Long,
        amount: Long
    ): UserPoint {
        val userPoint = getUserPoint(id)
        val usedPoint = userPoint.usePoint(amount)

        val result = userPointRepository.insertOrUpdate(usedPoint)
        pointHistoryRepository.insert(
            result, amount, TransactionType.USE
        )

        return result
    }

    fun getHistories(
        id: Long
    ): List<PointHistory> {
        return pointHistoryRepository.selectByUserId(id)
    }
}