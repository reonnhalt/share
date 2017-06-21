
using System;
using System.Collections.Generic;
using System.Linq;

public class Hello{
    //カウンタ用変数
    static int cnt = 0;
    
    public static void Main(){

        execute(84,5);
        execute(255,5);
        execute(256,8);
        execute(456,7);
        execute(852,8);
        execute(1281,7);
        execute(5412,9);
        execute(25456,17);
        execute(65534,14);
        
        /*
        84：5回(5回)以下
        255：5回(5回)以下
        256：8回(8回)以下
        456：7回(7回)以下
        852：8回(8回)以下
        1281：7回(7回)以下
        5412：10回(9回)以下
        25456：24回(17回)以下
        65534：19回(14回)以下
        */
    }
    
    //実行
    public static void execute(int n, int limit)
    {
        cnt = 0;
        Console.WriteLine("");
        Console.WriteLine("入力：" + n.ToString());
        Console.WriteLine("結果："+ PrimeFactorizations(n).Select(x => x.ToString()).Aggregate((a, b) => string.Format("{0}, {1}", a, b)));
        Console.WriteLine("カウンタ：" + cnt.ToString() + "回");
        Console.WriteLine("ノルマ：" + limit.ToString() + "回");
        Console.WriteLine("");
    }
    
    //素因数分解
    public static List<int> PrimeFactorizations(int n)
    {
        var firstPrimeList = GetPrimeList(n);
        return sub(firstPrimeList.First(), firstPrimeList, n);
    }
    
    //再帰処理
    public static List<int> sub(int divide, List<int> primeList, int n)
    {
        cnt++;
        if (divide * divide > n || divide == null || divide == 0)
        {
            return Enumerable.Repeat(n,1).Where(x => x >= 2).ToList();
        }
        else if (n % divide == 0)
        {
            //Console.WriteLine("debug:" + "素因数:"+divide.ToString()+"指数:"+GetIndex(divide, 1, n).ToString()+"残りの値:"+n);
            //return Enumerable.Repeat(divide,1).ToList().Concat(sub(divide, primeList, n / divide)).ToList();
            return Enumerable.Repeat(divide, GetIndex(divide, 1, n)).ToList().Concat(sub(primeList.Where(x => x != divide).FirstOrDefault(), primeList.Where(x => x != divide).ToList(), (int)(n / Math.Pow(divide, GetIndex(divide, 1, n))))).ToList();
        }
        else
        {
            return sub(primeList.Where(x => x != divide).First(), primeList.Where(x => x != divide).ToList(), n);
        }
    }
    
    //素数リスト返却
    public static List<int> GetPrimeList(int num)
    {
        cnt++;
        return Enumerable.Range(2, num).Where(x => num % x == 0).Where(x => IsPrime(x)).ToList();
    }
    
    //素数判定関数
    public static bool IsPrime(int num)
    {
        return Enumerable.Range(2, (int)(Math.Sqrt(num) - 1)).All(x => num % x != 0);
    }
    
    //指数返却
    public static int GetIndex(int divide, int index, int n)
    {
        if (n % Math.Pow(divide, index) != 0) { return index - 1; }
        else { return GetIndex(divide, index + 1, n); }
    }
}
