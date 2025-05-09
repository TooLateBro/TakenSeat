-- KEYS[1] : 쿠폰 수량을 저장하는 Redis 키
-- KEYS[2] : 이미 쿠폰을 발급받은 유저들을 저장하는 Redis Set 키
-- ARGV[1] : 발급하려는 유저의 ID
local couponKey = KEYS[1]   -- 스크립트가 실행될 때 전달받은 첫 번째 키
local userKey = KEYS[2]  -- Redis Set의 키
local userId = ARGV[1]   -- 스크립트 실행 시 전달받은 첫 번째 인수 -> 동적인 값

local current = redis.call('GET', couponKey)

-- tonumber : 값을 숫자로 변환하는 함수
if current == nil or tonumber(current) <= 0 then
    return -1  -- -1을 반환하여 쿠폰 수량 부족을 알림
end

-- SISMEMBER : Redis의 Set 자료형에서 특정 값이 존재하는지 확인
if redis.call('SISMEMBER', userKey, userId) == 1 then
    return -2  -- -2를 반환하여 이미 발급된 유저임을 알림
end
redis.call('DECR', couponKey)
redis.call('SADD', userKey, userId)
redis.call('EXPIRE', userKey, 600)

-- 쿠폰 발급 후, 남은 수량을 반환 (현재 수량에서 하나 감소한 값)
return tonumber(current) - 1