/*
    入力された方向にプレイヤーが移動するプログラムを作成する。
    オブジェクト指向プログラムとし、プレイヤークラス他、クラス設計を行う。
    すべてがimmutableである必要はないが、極力不変であるべき
    電卓、テンキーのキー配列の'5'を中心として各数字の方向にプレイヤーは移動する
    入力はキーバインドではなく、数値入力で良い
    プログラムは移動方向の入力、プレイヤーの移動後の位置の出力を繰り返し、数字以外が入力された場合に終了する
    '5'はプレイヤーがその場に留まることを表す（移動しない）
    1~9以外の数字が入力された場合は'5'が入力された場合と同じとする
    マップ全体は、各頂点が(0,0), (10, 0), (10,10), (0,10)である正方形であり、内部は整数座標節点を持つ格子状となっている
    プレイヤーは節点間を移動し、節点外に留まることはできず、マップ外を移動することも、マップ外に留まることもできない
    1回の移動可能距離は、水平及び垂直方向は±1、斜め方向の場合は±√2とし（節点移動）、移動後の位置は指定された方向の目標座標に対して最近接の移動可能な節点とする
    プレイヤーの初期位置は(0,0)とし、プレイヤークラスのコンストラクタは初期位置を受け取るものとする
    プレイヤークラスは移動方向を受け取り、移動後の位置を返す公開されたWalk（あるいはwalk）メソッドを持つ
*/

import scala.util.control.Breaks

case class Point(x: Int, y:Int)

class Field(minX:Int, maxX: Int, minY:Int, maxY: Int){
    // 移動判定
    def checkRange(point: Point): Boolean = {
        if(point.x < minX || point.x > maxX) return false
        if(point.y < minY || point.y > maxY) return false
        return true
    }
}
class Player(init: Point, f: Field){
    var currentPoint = init
    val moveMap = Map(
        1 -> { (point: Point) => Point(point.x-1 , point.y-1) },
        2 -> { (point: Point) => Point(point.x   , point.y-1) },
        3 -> { (point: Point) => Point(point.x+1 , point.y-1) },
        4 -> { (point: Point) => Point(point.x-1 , point.y  ) },
        5 -> { (point: Point) => Point(point.x   , point.y  ) },
        6 -> { (point: Point) => Point(point.x+1 , point.y  ) },
        7 -> { (point: Point) => Point(point.x-1 , point.y+1) },
        8 -> { (point: Point) => Point(point.x   , point.y+1) },
        9 -> { (point: Point) => Point(point.x+1 , point.y+1) }
    )
    def walk(move:Int): Point = {
        val afterPoint = moveMap.get(move).get(currentPoint)
        if(!f.checkRange(afterPoint)){ return currentPoint }
        currentPoint = afterPoint
        return afterPoint
    }
}
object Main extends App{
    val sc = new java.util.Scanner(System.in)
    val p1 = new Player(Point(5,5),new Field(0,10,0,10))
    val b1 = new Breaks
    b1.breakable {
        while(sc.hasNext){
            val s = sc.next
            if(!s.matches("""\d+""")) b1.break;
            println(p1.walk(if(s.toInt >= 1 && s.toInt <= 9) s.toInt else 5))
        }
    }
}
