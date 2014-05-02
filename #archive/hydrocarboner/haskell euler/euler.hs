-- began: 2012/01/09 23:40

import qualified Prelude
import Prelude hiding (max,min)
import Data.Numbers.Primes -- http://hackage.haskell.org/packages/archive/primes/0.2.1.0/doc/html/Data-Numbers-Primes.html
import Data.Char
import Data.List
import Data.Tree
import Data.Ratio
import Data.Function
import Numeric

-- langish

fi = fromIntegral
denom = denominator
numer = numerator

ceil = ceiling
len = length
cmp = compare
max = maximum
min = minimum
maxBy = maximumBy
minBy = minimumBy
(//) = div
fst3 (a,b,c) = a
snd3 (a,b,c) = b
thd3 (a,b,c) = c
fst4 (a,b,c,d) = a
snd4 (a,b,c,d) = b
trd4 (a,b,c,d) = c
fth4 (a,b,c,d) = d

both = \f x -> (x, f x)
allEq (x:xs) = all (== x) xs -- I feel like this should be built-in somehow...
sum' = foldl' (+) 0 -- useful for GHCi, which suxorz at optimizations
safeIntDiv a b = if snd t == 0 then fst t else error "oh dear" where t = a `divMod` b
unratio x = (numer x, denom x)

-- (++^++) xs ys = (reverse xs) ++ ys
[]     ++^++ ys = ys
(x:xs) ++^++ ys = xs ++^++ (x:ys)

-- euler

diag xss = concat $ sub xss 1 -- pick elements from a list of lists diagonal-style
	where
	sub xss n = let t = splitAt n xss in (map head $ filter (not.null) $ fst t) : sub ((map tail' $ fst t) ++ snd t) (n+1)
	tail' [] = []
	tail' xs = tail xs

isSquare n = (round $ sqrt $ fi n)^2 == n
isPent n = let a = 24*n + 1 in isSquare a && (let b = (round.sqrt.fi) a + 1 in b `mod` 6 == 0)

arbSqrt = arbRoot 2
arbRoot root n digits = head . dropWhile (<10^digits) . (map fst) $ iterate (_arbRoot root n) (1,1)
_arbRoot root n (ga,gb) = if (ga%gb)^root < n then (ga+1, gb) else (ga * 10 - 10, gb*10)

fib = map f [0..] where
	f 0 = 0
	f 1 = 1
	f n = fib !! (n-2) + fib !! (n-1)
	
isPalindrome xs = xs == reverse xs
isPandigital xs = sort xs == [1 .. len xs]

rotate (x:xs) = xs ++ [x]
rotations xs = take (len xs) (iterate rotate xs)

groups n xs =
	let _n = take n xs in
	if len _n < n then [] else _n : groups n (tail xs)

digit c = ord c - ord '0'

digits = reverse . digits'
undigits = undigits' . reverse
digits' n = if n < 10 then [n] else (n `mod` 10) : digits' (n // 10)
undigits' [] = 0
undigits' (n:ns) = n + 10 * undigits' ns

showBase base v = (showIntAtBase base (\i -> chr (i + ord '0')) v) ""

collatz :: Int -> Int
collatz n =
	if odd n
	then 3 * n + 1
	else n // 2
collatzLen = (map f [0..] !!) where
	f 1 = 1
	f n = 1 + (collatzLen . collatz $ n)

numDivisors = product . map ((+1) . snd) . primeFactors_
sumDivisors = product . map (\(a,b) -> sum $ map ((^) a) [0..b]) . primeFactors_
eulerTotient = product . map (\(a,b) -> a^(b-1) * (a-1)) . primeFactors_
phi = eulerTotient
primeFactors_ = map (\xs -> (head xs, len xs)) . group . primeFactors -- assumes primeFactors yields a sorted list

choose n 0 = 1
choose 0 k = 0
choose n k = ((choose (n-1) (k-1)) * n) // k

[]     /\/ ys = ys -- "interleave"? why do we have this. should this not be built-in.
(x:xs) /\/ ys = x : (ys /\/ xs)
powerset [] = [[]]
powerset (x:xs) =
	xss /\/ map (x:) xss
	where xss = powerset xs

coinCombos 0 _ = 1
coinCombos _ [] = 0
coinCombos n xs =
	sum $ map (\x -> coinCombos (n - x) (tail xs)) $ map (*coin) [0 .. n // coin]
	where coin = head xs

_combos2 0 _ = 1
_combos2 _ 0 = 0
_combos2 n x = sum $ map (\a -> _combos2 (n - a) (x-1)) $ map (*x) [0 .. n // x]
partitions n = _combos2 n n

rotateToMinBy f xs = let r2 = sub f xs in (snd r2) ++ (fst r2)
	where
	sub f v@(x:[]) = ([], v)
	sub f v@(x:xs) = let r = sub f xs in if f x < (f $ head $ snd r) then ([], v) else (x : fst r, snd r)
	
-- http://projecteuler.net/problem=64
continuedFractionSqrt n = ([ni], sub n (-ni) 1)
	where
	ni = floor . sqrt . fi $ n
	sub n a b = m : rest
		where
		c = ni - a
		d = (n - a^2) `safeIntDiv` b
		m = c//d
		rest | d == 1 && a /= 0 = []
		     | otherwise        = sub n ((c `mod` d) - ni) d

-- http://projecteuler.net/problem=66
sqrtConvergents n = map conv [1..]
	where
	conv m = sub $ take m t
	sub [x] = x%1
	sub (x:xs) = x%1 + 1 / sub xs
	t = fst tt ++ (cycle $ snd tt)
	tt = continuedFractionSqrt n
	
{- http://projecteuler.net/problem=160
w n = if n `mod` 10 == 0 then w (n//10) else n
q n = w (n^10) `mod` 100000
last $ take (7+1) $ iterate q 62496
-}

{- http://projecteuler.net/problem=104
let l9pan n = (sort $ digits' (n `mod` 10^9)) == [1..9]
let f9pan n = (sort $ take 9 $ digits n) == [1..9]
filter (\x -> l9pan x && f9pan x) fib
-}

{- http://projecteuler.net/problem=44
filter (\(a,b) -> isPent (a+b) && isPent (a+b+a)) w
w = diag $ zipWith (\a bs -> zip (repeat a) bs) pent (tail $ q pent)
q xs = xs : (q $ tail xs) 
pent = map (\n -> (n * (3*n-1))//2) [1..]
groups' n xs =
	let _n = take n xs in
	if len _n < n then [] else (head _n, last _n) : groups' n (tail xs)
-}

{- http://projecteuler.net/problem=125
let q m n = sum' $ map (^2) [n..n+m-1]
let w x = filter (isPalindrome . digits) $ takeWhile (<10^8) $ map (q x) [1..]
sum' $ concat $ take 668 $ map w [2..]
-}

{- http://projecteuler.net/problem=92
let r = map (\x -> if x == 1 then False else if x == 89 then True else r !! (nxt x)) [1..]
-}