package io.hhplus.tdd.point

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
){
    val MAX_POINT = 2_000_000L

    fun usePoint(amount : Long) : UserPoint {
        if(amount >= point ){
            throw RuntimeException("보유중인 포인트보다 더 많은 양은 사용할 수 없습니다")
        }

        return UserPoint(id, point - amount, updateMillis)
    }

    fun chargePoint(amount : Long) : UserPoint {
        if(amount + point > MAX_POINT){
            throw RuntimeException("포인트는 최대 200만 포인트를 초과할 수 없습니다")
        }

        return UserPoint(id, amount + point, updateMillis)
    }
}
