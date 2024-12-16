package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepositoryImpl
import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointRepositoryImpl
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.service.PointService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PointServiceTest: FunSpec({

    lateinit var pointService: PointService

    beforeEach{
        pointService = PointService(
            PointHistoryRepositoryImpl(CustomPointHistoryTable()),
            UserPointRepositoryImpl(CustomUserPointTable())
        )
    }

    test("유저 포인트를 충전한다"){
         val chargedPoint = pointService.chargeUserPoint(1, 100)

        chargedPoint.point shouldBe 100L
    }

    test("포인트 충전은 최대 200만 포인트까지만 가능하다"){
        val exception = shouldThrow<RuntimeException> {
            pointService.chargeUserPoint(1, 3_000_000)
        }
        exception.message shouldBe "포인트는 최대 200만 포인트를 초과할 수 없습니다"
    }

    test("유저 포인트를 조회한다"){
        pointService.chargeUserPoint(1, 100)

        val point = pointService.getUserPoint(1)

        point.point shouldBe 100L
    }

    test("유저 포인트를 사용한다"){
        pointService.chargeUserPoint(1, 100)

        val usedPoint = pointService.useUserPoint(1,50)

        usedPoint.point shouldBe 50L
    }

    test("보유 포인트보다 더 많은 포인트를 사용할 수 없다"){
        pointService.chargeUserPoint(1,50)
        val exception = shouldThrow<RuntimeException> {
            pointService.useUserPoint(1,100)
        }

        exception.message shouldBe "보유중인 포인트보다 더 많은 양은 사용할 수 없습니다"
    }

    test("포인트 사용 내역을 조회한다"){
        pointService.chargeUserPoint(1, 100)
        pointService.chargeUserPoint(1, 200)
        pointService.useUserPoint(1,80)

        val histories = pointService.getHistories(1)

        histories.size shouldBe 3
        histories.get(0).type shouldBe TransactionType.CHARGE
        histories.get(2).type shouldBe TransactionType.USE
    }

})

class CustomUserPointTable: UserPointTable() {
    private val table = HashMap<Long, UserPoint>()

    override fun selectById(id: Long): UserPoint {
        Thread.sleep(Math.random().toLong() * 200L)
        return table[id] ?: UserPoint(id = id, point = 0, updateMillis = System.currentTimeMillis())
    }

    override fun insertOrUpdate(id: Long, amount: Long): UserPoint {
        Thread.sleep(Math.random().toLong() * 300L)
        val userPoint = UserPoint(id = id, point = amount, updateMillis = System.currentTimeMillis())
        table[id] = userPoint
        return userPoint
    }
}

class CustomPointHistoryTable: PointHistoryTable() {
    private val table = mutableListOf<PointHistory>()
    private var cursor: Long = 1L

    override fun insert(
        id: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long,
    ): PointHistory {
        Thread.sleep(Math.random().toLong() * 300L)
        val history = PointHistory(
            id = cursor++,
            userId = id,
            amount = amount,
            type = transactionType,
            timeMillis = updateMillis,
        )
        table.add(history)
        return history
    }

    override fun selectAllByUserId(userId: Long): List<PointHistory> {
        return table.filter { it.userId == userId }
    }
}