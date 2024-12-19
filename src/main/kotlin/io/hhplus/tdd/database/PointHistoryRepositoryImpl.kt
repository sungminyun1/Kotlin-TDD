package io.hhplus.tdd.database

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.service.PointHistoryRepository
import org.springframework.stereotype.Repository

@Repository
class PointHistoryRepositoryImpl(
    private val pointHistoryTable: PointHistoryTable
) : PointHistoryRepository {
    override fun insert(
        userPoint: UserPoint,
        amount: Long,
        type: TransactionType
    ): PointHistory {
        return pointHistoryTable.insert(
            id = userPoint.id,
            amount = amount,
            transactionType = type,
            updateMillis = userPoint.updateMillis
        )
    }

    override fun selectByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }
}