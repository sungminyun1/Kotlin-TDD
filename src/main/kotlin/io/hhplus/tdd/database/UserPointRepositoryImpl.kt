package io.hhplus.tdd.database

import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.service.UserPointRepository
import org.springframework.stereotype.Repository

@Repository
class UserPointRepositoryImpl(
    private val userPointTable: UserPointTable
): UserPointRepository {
    override fun selectById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    override fun insertOrUpdate(userPoint: UserPoint): UserPoint {
        return userPointTable.insertOrUpdate(userPoint.id, userPoint.point)
    }
}