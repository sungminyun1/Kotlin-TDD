package io.hhplus.tdd.service

import io.kotest.core.spec.style.FunSpec
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class PointServiceConcurrenceTest{

    @Autowired
    lateinit var pointService: PointService

    @Test
    fun `유저는 자신이 보유한 포인트보다 더 많은 포인트를 사용할 수 없다`(){
        val threadCount = 16
        val startLatch = CountDownLatch(1);
        val doneLatch = CountDownLatch(threadCount)

        val executor = Executors.newFixedThreadPool(threadCount)
        var exceptionCount = 0

        pointService.chargeUserPoint(1, 1000)

        repeat(threadCount) {
            executor.submit {
                try {
                    startLatch.await()

                    pointService.useUserPoint(1, 100)
                }catch(e: RuntimeException){
                    exceptionCount++
                } finally {
                    doneLatch.countDown()
                }
            }
        }

        startLatch.countDown()

        doneLatch.await()

        assertThat(exceptionCount).isEqualTo(6)
    }


    @Test
    fun `동시에 여러번 충전, 사용 요청이 있어도 올바른 금액을 가져야한다`(){
        val threadCount = 16
        val startLatch = CountDownLatch(1);
        val doneLatch = CountDownLatch(threadCount)

        val executor = Executors.newFixedThreadPool(threadCount)
        var exceptionCount = 0

        pointService.chargeUserPoint(2, 1000)

        repeat(threadCount / 2) {
            executor.submit {
                try {
                    startLatch.await()
                    pointService.useUserPoint(2, 100)
                }catch(e: RuntimeException){
                    exceptionCount++
                } finally {
                    doneLatch.countDown()
                }
            }

            executor.submit {
                try {
                    startLatch.await()
                    pointService.chargeUserPoint(2, 200)
                }catch(e: RuntimeException){
                    exceptionCount++
                } finally {
                    doneLatch.countDown()
                }
            }
        }

        startLatch.countDown()

        doneLatch.await()

        assertThat(exceptionCount).isEqualTo(0)
        assertThat(pointService.getUserPoint(2).point).isEqualTo(1800)
    }
}