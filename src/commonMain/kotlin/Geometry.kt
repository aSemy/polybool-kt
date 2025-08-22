package dev.adamko.polybool

import dev.adamko.polybool.internal.DoubleList
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sqrt

////
//// polybool - Boolean operations on polygons (union, intersection, etc)
//// by Sean Connelly (@velipso), https://sean.fun
//// Project Home: https://github.com/velipso/polybool
//// SPDX-License-Identifier: 0BSD
////

sealed interface Vector {
  val length: Int
}


internal operator fun Vector.get(i: Int): Double {
  return when (this) {
    is Vec2 -> get(i)
    is Vec6 -> get(i)
  }
//  return when (i) {
//    0    -> x
//    1    -> y
//    else -> throw IndexOutOfBoundsException("Invalid index $i for Vec2")
//  }
}

//export type Vec2 = [number, number];
data class Vec2 internal constructor(
  val x: Double,
  val y: Double,
) : Vector {
  override val length: Int = 2
  internal operator fun get(i: Int): Double {
    return when (i) {
      0    -> x
      1    -> y
      else -> throw IndexOutOfBoundsException("Invalid index $i for Vec2")
    }
  }

  operator fun plus(other: Vec2): Vec2 =
    Vec2(x + other.x, y + other.y)

  operator fun minus(other: Vec2): Vec2 =
    Vec2(x - other.x, y - other.y)

  fun equals(other: Vec2, epsilon: Double): Boolean {
    return (
      abs(this.x - other.x) < epsilon &&
        abs(this.y - other.y) < epsilon
      )
  }

  override fun toString(): String = "Vec2${toBracketString()}"
  fun toBracketString(): String = "[$x, $y]"
}

//export type Vec6 = [number, number, number, number, number, number];
data class Vec6 internal constructor(
  val a: Double,
  val b: Double,
  val c: Double,
  val d: Double,
  val e: Double,
  val f: Double,
) : Vector {
  override val length: Int = 6

  internal operator fun get(i: Int): Double {
    return when (i) {
      0    -> a
      1    -> b
      2    -> c
      3    -> d
      4    -> e
      5    -> f
      else -> throw IndexOutOfBoundsException("Invalid index $i for Vec6")
    }
  }

  operator fun plus(other: Vec6): Vec6 =
    Vec6(
      a = a + other.a,
      b = b + other.b,
      c = c + other.c,
      d = d + other.d,
      e = e + other.e,
      f = f + other.f
    )

  operator fun minus(other: Vec6): Vec6 =
    Vec6(
      a = a - other.a,
      b = b - other.b,
      c = c - other.c,
      d = d - other.d,
      e = e - other.e,
      f = f - other.f
    )

  fun equals(other: Vec6, epsilon: Double): Boolean {
    return (
      abs(this.a - other.a) < epsilon &&
        abs(this.b - other.b) < epsilon &&
        abs(this.c - other.c) < epsilon &&
        abs(this.d - other.d) < epsilon &&
        abs(this.e - other.e) < epsilon &&
        abs(this.f - other.f) < epsilon
      )
  }


  override fun toString(): String {
    return "Vec6[$a, $b, $c, $d, $e, $f]"
  }
}

fun Vec6(
  a: Int,
  b: Int,
  c: Int,
  d: Int,
  e: Int,
  f: Int,
): Vec6 =
  Vec6(
    a = a.toDouble(),
    b = b.toDouble(),
    c = c.toDouble(),
    d = d.toDouble(),
    e = e.toDouble(),
    f = f.toDouble(),
  )

data class BoundingBox internal constructor(
  val min: Vec2,
  val max: Vec2,
)

//export function lerp(a: number, b: number, t: number) {
//  return a + (b - a) * t;
//}
fun lerp(a: Double, b: Double, t: Double): Double {
  return a + (b - a) * t
}

//export function lerpVec2(a: Vec2, b: Vec2, t: number): Vec2 {
//  return [lerp(a[0], b[0], t), lerp(a[1], b[1], t)];
//}
fun lerpVec2(a: Vec2, b: Vec2, t: Double): Vec2 {
  return Vec2(lerp(a.x, b.x, t), lerp(a.y, b.y, t))
}

//export function boundingBoxesIntersect(
//  bbox1: [Vec2, Vec2],
//  bbox2: [Vec2, Vec2],
//) {
//  const [b1min, b1max] = bbox1;
//  const [b2min, b2max] = bbox2;
//  return !(
//    b1min[0] > b2max[0] ||
//    b1max[0] < b2min[0] ||
//    b1min[1] > b2max[1] ||
//    b1max[1] < b2min[1]
//  );
//}
fun boundingBoxesIntersect(
  bbox1: BoundingBox,
  bbox2: BoundingBox,
): Boolean {
  val (b1min, b1max) = bbox1
  val (b2min, b2max) = bbox2
  return !(
    b1min.x > b2max.x ||
      b1max.x < b2min.x ||
      b1min.y > b2max.y ||
      b1max.y < b2min.y
    )
}

//export abstract class Geometry {
//  abstract snap0(v: number): number;
//  abstract snap01(v: number): number;
//  abstract isCollinear(p1: Vec2, p2: Vec2, p3: Vec2): boolean;
//  abstract solveCubic(a: number, b: number, c: number, d: number): number[];
//  abstract isEqualVec2(a: Vec2, b: Vec2): boolean;
//  abstract compareVec2(a: Vec2, b: Vec2): number;
//}
abstract class Geometry {
  abstract fun snap0(v: Double): Double
  abstract fun snap01(v: Double): Double
  abstract fun isCollinear(p1: Vec2, p2: Vec2, p3: Vec2): Boolean
  abstract fun solveCubic(a: Double, b: Double, c: Double, d: Double): DoubleList
  abstract fun isEqualVec2(a: Vec2, b: Vec2): Boolean
  abstract fun compareVec2(a: Vec2, b: Vec2): Int
}


//export class GeometryEpsilon extends Geometry {
//  readonly epsilon: number;
//
//  constructor(epsilon = 0.0000000001) {
//    super();
//    this.epsilon = epsilon;
//  }
//
//
//
//
//
//
//
//}
class GeometryEpsilon(
  val epsilon: Double = 0.0000000001,
) : Geometry() {

  override fun snap0(v: Double): Double {
    return if (v.absoluteValue < epsilon) {
      0.0
    } else {
      v
    }
  }

  override fun snap01(v: Double): Double {
    if (abs(v) < this.epsilon) {
      return 0.0
    }
    if (abs(1 - v) < this.epsilon) {
      return 1.0
    }
    return v
  }

  override fun isCollinear(p1: Vec2, p2: Vec2, p3: Vec2): Boolean {
    // does pt1->pt2->pt3 make a straight line?
    // essentially this is just checking to see if
    //   slope(pt1->pt2) === slope(pt2->pt3)
    // if slopes are equal, then they must be collinear, because they share pt2
    val dx1 = p1.x - p2.x
    val dy1 = p1.y - p2.y
    val dx2 = p2.x - p3.x
    val dy2 = p2.y - p3.y
    return abs(dx1 * dy2 - dx2 * dy1) < epsilon
  }

  private fun solveCubicNormalized(a: Double, b: Double, c: Double): DoubleList {
    // based somewhat on gsl_poly_solve_cubic from GNU Scientific Library
    val a3 = a / 3;
    val b3 = b / 3;
    val Q = a3 * a3 - b3;
    val R = a3 * (a3 * a3 - b / 2) + c / 2;
    if (abs(R) < this.epsilon && abs(Q) < this.epsilon) {
      return DoubleList(-a3);
    }
    val F =
      a3 * (a3 * (4 * a3 * c - b3 * b) - 2 * b * c) + 4 * b3 * b3 * b3 + c * c;
    if (abs(F) < this.epsilon) {
      val sqrtQ = sqrt(Q);
//      return R > 0
//        ? [-2 * sqrtQ - a / 3, sqrtQ - a / 3]
//        : [-sqrtQ - a / 3, 2 * sqrtQ - a / 3];
      return if (R > 0.0) {
        DoubleList(-2 * sqrtQ - a / 3, sqrtQ - a / 3)
      } else {
        DoubleList(-sqrtQ - a / 3, 2 * sqrtQ - a / 3)
      }
    }
    val Q3 = Q * Q * Q;
    val R2 = R * R;
    if (R2 < Q3) {
//      const ratio = (R < 0 ? -1 : 1) *  sqrt(R2 / Q3);
//      const theta = Math.acos(ratio);
//      const norm = -2 *  sqrt(Q);
//      const x0 = norm * Math.cos(theta / 3) - a3;
//      const x1 = norm * Math.cos((theta + 2 * Math.PI) / 3) - a3;
//      const x2 = norm * Math.cos((theta - 2 * Math.PI) / 3) - a3;
//      return [x0, x1, x2].sort((x, y) => x - y);
      TODO()
    } else {
//      const A =
//        (R < 0 ? 1 : -1) * Math.pow( abs(R) +  sqrt(R2 - Q3), 1 / 3);
//      const B =  abs(A) >= this.epsilon ? Q / A : 0;
//      return [A + B - a3];
      TODO()
    }
  }

  //  solveCubic(a: number, b: number, c: number, d: number) {
//  }
  override fun solveCubic(a: Double, b: Double, c: Double, d: Double): DoubleList {
    if (abs(a) < this.epsilon) {
      // quadratic
      if (abs(b) < this.epsilon) {
        // linear case
        if (abs(c) < this.epsilon) {
          // horizontal line
          return if (abs(d) < this.epsilon) DoubleList(0.0) else DoubleList();
        }
        return DoubleList(-d / c);
      }
      val b2 = 2 * b;
      var D = c * c - 4 * b * d;
      if (abs(D) < this.epsilon) {
        return DoubleList(-c / b2);
      } else if (D > 0) {
        D = sqrt(D);
        return DoubleList((-c + D) / b2, (-c - D) / b2)
          .sorted()
        // TODO is this the right sort? Or maybe it should be reversed???
//          .sort((x, y) => x - y);
      }
      return DoubleList();
    }
    return this.solveCubicNormalized(b / a, c / a, d / a);
  }

  //  isEqualVec2(a: Vec2, b: Vec2) {
//    return (
//       abs(a[0] - b[0]) < this.epsilon &&
//       abs(a[1] - b[1]) < this.epsilon
//    );
//  }
  override fun isEqualVec2(a: Vec2, b: Vec2): Boolean {
    return (
      abs(a.x - b.x) < epsilon &&
        abs(a.y - b.y) < epsilon
      )
  }

  /** returns `-1` if [a] is smaller, `1` if [b] is smaller, [0] if equal. */
  override fun compareVec2(a: Vec2, b: Vec2): Int {
    return if (abs(b.x - a.x) < this.epsilon) {
      if (abs(b.y - a.y) < this.epsilon) {
        0
      } else if (a.y < b.y) {
        -1
      } else {
        1
      }
    } else if (a.x < b.x) {
      -1
    } else {
      1
    }
  }

  override fun toString(): String = "GeometryEpsilon(epsilon=$epsilon)"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is GeometryEpsilon) return false

    if (epsilon != other.epsilon) return false

    return true
  }

  override fun hashCode(): Int {
    return epsilon.hashCode()
  }
}
