package io.hhplus.tdd.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

@Service
class PointService(
    val pointHistoryRepository: PointHistoryRepository,
    val userPointRepository: UserPointRepository
){
    val userLock: ConcurrentHashMap<Long, ReentrantLock> = ConcurrentHashMap()

    fun getUserPoint(
        id: Long
    ): UserPoint {
        return userPointRepository.selectById(id)
    }

    fun chargeUserPoint(
        id: Long,
        amount: Long
    ): UserPoint {

        try{
            doWithLock(id)

            val userPoint = getUserPoint(id)
            val chargedPoint = userPoint.chargePoint(amount)

            val result = userPointRepository.insertOrUpdate(chargedPoint)
            pointHistoryRepository.insert(
                result, amount, TransactionType.CHARGE
            )

            return result
        }finally {
            unLock(id)
        }
    }

    fun useUserPoint(
        id: Long,
        amount: Long
    ): UserPoint {
        try{
            doWithLock(id)

            val userPoint = getUserPoint(id)
            val usedPoint = userPoint.usePoint(amount)

            val result = userPointRepository.insertOrUpdate(usedPoint)
            pointHistoryRepository.insert(
                result, amount, TransactionType.USE
            )

            return result
        } finally {
            unLock(id)
        }

    }

    fun getHistories(
        id: Long
    ): List<PointHistory> {
        return pointHistoryRepository.selectByUserId(id)
    }

    fun doWithLock(id: Long){
        val lock = userLock.computeIfAbsent(id) { ReentrantLock() }

        lock.lock();
    }

    fun unLock(id: Long){
        userLock[id]?.unlock();
    }
}