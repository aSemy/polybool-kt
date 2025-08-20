import kotlin.math.max
import kotlin.math.min

////
//// polybool - Boolean operations on polygons (union, intersection, etc)
//// by Sean Connelly (@velipso), https://sean.fun
//// Project Home: https://github.com/velipso/polybool
//// SPDX-License-Identifier: 0BSD
////
//
//import {
//  type Vec2,
//  type Geometry,
//  lerpVec2,
//  boundingBoxesIntersect,
//} from "./Geometry";
//import { type IPolyBoolReceiver } from "./SegmentChainer";

sealed interface SegmentTValueOrRangePairs

//export interface SegmentTValuePairs {
//  kind: "tValuePairs";
//  tValuePairs: Vec2[]; // [seg1T, seg2T][]
//}
class SegmentTValuePairs(
  val tValuePairs: List<Vec2>,
) : SegmentTValueOrRangePairs

//export interface SegmentTRangePairs {
//  kind: "tRangePairs";
//  tStart: Vec2; // [seg1TStart, seg2TStart]
//  tEnd: Vec2; // [seg1TEnd, seg2TEnd]
//}
class SegmentTRangePairs(
  //  kind: "tRangePairs";
  val tStart: Vec2, // [seg1TStart, seg2TStart]
  val tEnd: Vec2, // [seg1TEnd, seg2TEnd]
) : SegmentTValueOrRangePairs

//export class SegmentTValuesBuilder {
//  tValues: number[] = [];
//  geo: Geometry;
//
//  constructor(geo: Geometry) {
//    this.geo = geo;
//  }
//
//  addArray(ts: number[]) {
//    for (const t of ts) {
//      this.tValues.push(t);
//    }
//    return this;
//  }
//
//  add(t: number) {
//    t = this.geo.snap01(t);
//    // ignore values outside 0-1 range
//    if (t < 0 || t > 1) {
//      return this;
//    }
//    for (const tv of this.tValues) {
//      if (this.geo.snap0(t - tv) === 0) {
//        // already have this location
//        return this;
//      }
//    }
//    this.tValues.push(t);
//    return this;
//  }
//
//  list() {
//    this.tValues.sort((a, b) => a - b);
//    return this.tValues;
//  }
//}

//export class SegmentTValuePairsBuilder {
internal class SegmentTValuePairsBuilder(
  val allowOutOfRange: Boolean,
  val geo: Geometry,
) {
  private val tValuePairs: ArrayDeque<Vec2> = ArrayDeque()

  fun add(t1: Double, t2: Double): SegmentTValuePairsBuilder {
    val t1 = this.geo.snap01(t1)
    val t2 = this.geo.snap01(t2)
    // ignore values outside 0-1 range
    if (!this.allowOutOfRange && (t1 < 0.0 || t1 > 1.0 || t2 < 0.0 || t2 > 1.0)) {
      return this
    }
    tValuePairs.forEach { tv ->
      if (
        this.geo.snap0(t1 - tv[0]) == 0.0 ||
        this.geo.snap0(t2 - tv[1]) == 0.0
      ) {
        // already have this location
        return this
      }
    }
    this.tValuePairs.addLast(Vec2(t1, t2))
    return this
  }

  fun list(): List<Vec2> {
//    this.tValuePairs.sort((a, b) => a[0] - b[0]);
    tValuePairs.sortWith { a, b -> a.x.compareTo(b.x) }
    return this.tValuePairs
  }

  fun done(): SegmentTValuePairs? {
//    return this.tValuePairs.length <= 0
//      ? null
//      : {
//          kind: "tValuePairs",
//          tValuePairs: this.list(),
//        };
    return if (tValuePairs.isEmpty()) {
      null
    } else {
      SegmentTValuePairs(list())
    }
  }
}

//export abstract class SegmentBase<T> {
//  abstract copy(): T;
//  abstract isEqual(other: T): boolean;
//  abstract start(): Vec2;
//  abstract start2(): Vec2;
//  abstract end2(): Vec2;
//  abstract end(): Vec2;
//  abstract setStart(p: Vec2): void;
//  abstract setEnd(p: Vec2): void;
//  abstract point(t: number): Vec2;
//  abstract split(t: number[]): T[];
//  abstract reverse(): T;
//  abstract boundingBox(): [Vec2, Vec2];
//  abstract pointOn(p: Vec2): boolean;
//  abstract draw<TRecv extends IPolyBoolReceiver>(ctx: TRecv): TRecv;
//}
abstract class SegmentBase<T> {
  abstract fun copy(): T
  abstract fun isEqual(other: T): Boolean
  abstract fun start(): Vec2
  abstract fun start2(): Vec2
  abstract fun end2(): Vec2
  abstract fun end(): Vec2
  abstract fun setStart(p: Vec2)
  abstract fun setEnd(p: Vec2)
  abstract fun point(t: Double): Vec2
  abstract fun split(ts: DoubleArray): List<T>
  abstract fun reverse(): T
  abstract fun boundingBox(): BoundingBox
  abstract fun pointOn(p: Vec2): Boolean
  abstract fun <TRecv : IPolyBoolReceiver> draw(ctx: TRecv): TRecv
}

//export class SegmentLine extends SegmentBase<SegmentLine> {
data class SegmentLine internal constructor(
  var p0: Vec2,
  var p1: Vec2,
  val geo: Geometry,
) : SegmentBase<SegmentLine>(), Segment {

  //  copy() {
//    return new SegmentLine(this.p0, this.p1, this.geo);
//  }
//
//  isEqual(other: SegmentLine) {
//    return (
//      this.geo.isEqualVec2(this.p0, other.p0) &&
//      this.geo.isEqualVec2(this.p1, other.p1)
//    );
//  }
//
  override fun setStart(p: Vec2) {
    this.p0 = p
  }

  override fun setEnd(p: Vec2) {
    this.p1 = p
  }

  override fun point(t: Double): Vec2 {
    val p0 = this.p0
    val p1 = this.p1

    return when (t) {
      0.0  -> p0
      1.0  -> p1
      else -> Vec2(p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t)
    }
  }

  override fun split(ts: DoubleArray): List<SegmentLine> {
    if (ts.isEmpty()) {
      return listOf(this)
    }
//    const pts = ts.map((t) => this.point(t));
    val pts = ts.mapTo(ArrayDeque()) { t -> this.point(t) }
    pts.add(this.p1)
//    const result: SegmentLine[] = [];
    val result = ArrayDeque<SegmentLine>()
    var last = this.p0
//    for (const p of pts) {
    pts.forEach { p ->
      result.addLast(
        SegmentLine(
          p0 = last,
          p1 = p,
          geo = this.geo,
        )
      )
      last = p
    }
    return result
  }

  override fun reverse(): SegmentLine {
    return SegmentLine(
      p0 = this.p1,
      p1 = this.p0,
      geo = this.geo,
    )
  }

  //  boundingBox(): [Vec2, Vec2] {
//    const p0 = this.p0;
//    const p1 = this.p1;
//    return [
//      [Math.min(p0[0], p1[0]), Math.min(p0[1], p1[1])],
//      [Math.max(p0[0], p1[0]), Math.max(p0[1], p1[1])],
//    ];
//  }
//
  override fun pointOn(p: Vec2): Boolean {
    return this.geo.isCollinear(p, this.p0, this.p1)
  }
//
//  draw<TRecv extends IPolyBoolReceiver>(ctx: TRecv): TRecv {
//    const p0 = this.p0;
//    const p1 = this.p1;
//    ctx.moveTo(p0[0], p0[1]);
//    ctx.lineTo(p1[0], p1[1]);
//    return ctx;
//  }

  override fun copy(): SegmentLine {
    TODO("Not yet implemented")
  }

  override fun start(): Vec2 {
    return this.p0
  }

  override fun start2(): Vec2 {
    return this.p1
  }

  override fun end2(): Vec2 {
    return this.p0
  }

  override fun end(): Vec2 {
    return this.p1
  }

  override fun boundingBox(): BoundingBox {
    TODO("Not yet implemented")
  }

  override fun <TRecv : IPolyBoolReceiver> draw(ctx: TRecv): TRecv {
    TODO("Not yet implemented")
  }

  override fun isEqual(other: SegmentLine): Boolean {
    TODO("Not yet implemented")
  }
}

//export class SegmentCurve extends SegmentBase<SegmentCurve> {
data class SegmentCurve internal constructor(
  val p0: Vec2,
  val p1: Vec2,
  val p2: Vec2,
  val p3: Vec2,
  val geo: Geometry,
) : SegmentBase<SegmentCurve>(), Segment {

  //  fun   copy() {
//    return new SegmentCurve(this.p0, this.p1, this.p2, this.p3, this.geo);
//  }
//
//  fun   isEqual(other: SegmentCurve) {
//    return (
//      this.geo.isEqualVec2(this.p0, other.p0) &&
//      this.geo.isEqualVec2(this.p1, other.p1) &&
//      this.geo.isEqualVec2(this.p2, other.p2) &&
//      this.geo.isEqualVec2(this.p3, other.p3)
//    );
//  }
//
  override fun start(): Vec2 = this.p0

  override fun start2(): Vec2 = this.p2

  override fun end2(): Vec2 = this.p2

  override fun end(): Vec2 = this.p3

//  fun   setStart(p0: Vec2) {
//    this.p0 = p0;
//  }
//
//  fun   setEnd(p3: Vec2) {
//    this.p3 = p3;
//  }

  override fun point(t: Double): Vec2 {
//    const p0 = this.p0;
//    const p1 = this.p1;
//    const p2 = this.p2;
//    const p3 = this.p3;
//
//    if (t === 0) {
//      return p0;
//    } else if (t === 1) {
//      return p3;
//    }
//
//    const t1t = (1 - t) * (1 - t);
//    const tt = t * t;
//    const t0 = t1t * (1 - t);
//    const t1 = 3 * t1t * t;
//    const t2 = 3 * tt * (1 - t);
//    const t3 = tt * t;
//
//    return [
//      p0[0] * t0 + p1[0] * t1 + p2[0] * t2 + p3[0] * t3,
//      p0[1] * t0 + p1[1] * t1 + p2[1] * t2 + p3[1] * t3,
//    ];
    TODO()
  }

  override fun split(ts: DoubleArray): List<SegmentCurve> {
    if (ts.size <= 0) {
      return listOf(this)
    }
//    const result: SegmentCurve[] = [];
    val result = ArrayDeque<SegmentCurve>()
    //    const splitSingle = (
//      pts: [Vec2, Vec2, Vec2, Vec2],
//      t: number,
//    ): [Vec2, Vec2, Vec2, Vec2] => {
//      const [p0, p1, p2, p3] = pts;
//      const p4 = lerpVec2(p0, p1, t);
//      const p5 = lerpVec2(p1, p2, t);
//      const p6 = lerpVec2(p2, p3, t);
//      const p7 = lerpVec2(p4, p5, t);
//      const p8 = lerpVec2(p5, p6, t);
//      const p9 = lerpVec2(p7, p8, t);
//      result.push(new SegmentCurve(p0, p4, p7, p9, this.geo));
//      return [p9, p8, p6, p3];
//    };
    fun splitSingle(pts: List<Vec2>, t: Double): List<Vec2> {
      val (p0, p1, p2, p3) = pts
      val p4 = lerpVec2(p0, p1, t)
      val p5 = lerpVec2(p1, p2, t)
      val p6 = lerpVec2(p2, p3, t)
      val p7 = lerpVec2(p4, p5, t)
      val p8 = lerpVec2(p5, p6, t)
      val p9 = lerpVec2(p7, p8, t)
      result.addLast(SegmentCurve(p0, p4, p7, p9, this.geo))
      return listOf(p9, p8, p6, p3)
    }
//    let last: [Vec2, Vec2, Vec2, Vec2] = [this.p0, this.p1, this.p2, this.p3];
    var last = listOf(this.p0, this.p1, this.p2, this.p3)
    var lastT = 0
//    for (const t of ts) {
//      last = splitSingle(last, (t - lastT) / (1 - lastT));
//      lastT = t;
//    }
    for (t in ts) {
      last = splitSingle(last, (t - lastT) / (1 - lastT))
    }
    result.addLast(SegmentCurve(last[0], last[1], last[2], last[3], this.geo))
    return result
  }

  override fun reverse(): SegmentCurve {
    return SegmentCurve(this.p3, this.p2, this.p1, this.p0, this.geo)
  }

//  fun   getCubicCoefficients(axis: number): [number, number, number, number] {
//    const p0 = this.p0[axis];
//    const p1 = this.p1[axis];
//    const p2 = this.p2[axis];
//    const p3 = this.p3[axis];
//    return [
//      p3 - 3 * p2 + 3 * p1 - p0,
//      3 * p2 - 6 * p1 + 3 * p0,
//      3 * p1 - 3 * p0,
//      p0,
//    ];
//  }

  fun boundingTValues() {
//    const result = new SegmentTValuesBuilder(this.geo);
//    const bounds = (x0: number, x1: number, x2: number, x3: number) => {
//      const a = 3 * x3 - 9 * x2 + 9 * x1 - 3 * x0;
//      const b = 6 * x0 - 12 * x1 + 6 * x2;
//      const c = 3 * x1 - 3 * x0;
//      if (this.geo.snap0(a) === 0) {
//        result.add(-c / b);
//      } else {
//        const disc = b * b - 4 * a * c;
//        if (disc >= 0) {
//          const sq = Math.sqrt(disc);
//          result.add((-b + sq) / (2 * a));
//          result.add((-b - sq) / (2 * a));
//        }
//      }
//      return result;
//    };
//
//    const p0 = this.p0;
//    const p1 = this.p1;
//    const p2 = this.p2;
//    const p3 = this.p3;
//    bounds(p0[0], p1[0], p2[0], p3[0]);
//    bounds(p0[1], p1[1], p2[1], p3[1]);
//
//    return result.list();
    TODO()
  }

//  fun  inflectionTValues(): number[] {
//    const result = new SegmentTValuesBuilder(this.geo);
//    result.addArray(this.boundingTValues());
//    const p0 = this.p0;
//    const p1 = this.p1;
//    const p2 = this.p2;
//    const p3 = this.p3;
//    const p10x = 3 * (p1[0] - p0[0]);
//    const p10y = 3 * (p1[1] - p0[1]);
//    const p21x = 6 * (p2[0] - p1[0]);
//    const p21y = 6 * (p2[1] - p1[1]);
//    const p32x = 3 * (p3[0] - p2[0]);
//    const p32y = 3 * (p3[1] - p2[1]);
//    const p210x = 6 * (p2[0] - 2 * p1[0] + p0[0]);
//    const p210y = 6 * (p2[1] - 2 * p1[1] + p0[1]);
//    const p321x = 6 * (p3[0] - 2 * p2[0] + p1[0]);
//    const p321y = 6 * (p3[1] - 2 * p2[1] + p1[1]);
//    const qx = p10x - p21x + p32x;
//    const qy = p10y - p21y + p32y;
//    const rx = p21x - 2 * p10x;
//    const ry = p21y - 2 * p10y;
//    const sx = p10x;
//    const sy = p10y;
//    const ux = p321x - p210x;
//    const uy = p321y - p210y;
//    const vx = p210x;
//    const vy = p210y;
//    const A = qx * uy - qy * ux;
//    const B = qx * vy + rx * uy - qy * vx - ry * ux;
//    const C = rx * vy + sx * uy - ry * vx - sy * ux;
//    const D = sx * vy - sy * vx;
//    for (const s of this.geo.solveCubic(A, B, C, D)) {
//      result.add(s);
//    }
//    return result.list();
//  }
//
//  fun boundingBox(): [Vec2, Vec2] {
//    const p0 = this.p0;
//    const p3 = this.p3;
//    const min: Vec2 = [Math.min(p0[0], p3[0]), Math.min(p0[1], p3[1])];
//    const max: Vec2 = [Math.max(p0[0], p3[0]), Math.max(p0[1], p3[1])];
//    for (const t of this.boundingTValues()) {
//      const p = this.point(t);
//      min[0] = Math.min(min[0], p[0]);
//      min[1] = Math.min(min[1], p[1]);
//      max[0] = Math.max(max[0], p[0]);
//      max[1] = Math.max(max[1], p[1]);
//    }
//    return [min, max];
//  }

  // fun   mapXtoT(x: number, force: Boolean = false): number | false {
  fun mapXtoT(x: Double, force: Boolean = false): Double? {
//    if (this.geo.snap0(this.p0[0] - x) === 0) {
//      return 0;
//    }
//    if (this.geo.snap0(this.p3[0] - x) === 0) {
//      return 1;
//    }
//    const p0 = this.p0[0] - x;
//    const p1 = this.p1[0] - x;
//    const p2 = this.p2[0] - x;
//    const p3 = this.p3[0] - x;
//    const R = [
//      p3 - 3 * p2 + 3 * p1 - p0,
//      3 * p2 - 6 * p1 + 3 * p0,
//      3 * p1 - 3 * p0,
//      p0,
//    ];
//    for (const t of this.geo.solveCubic(R[0], R[1], R[2], R[3])) {
//      const ts = this.geo.snap01(t);
//      if (ts >= 0 && ts <= 1) {
//        return t;
//      }
//    }
//    // force a solution if we know there is one...
//    if (
//      force ||
//      (x >= Math.min(this.p0[0], this.p3[0]) &&
//        x <= Math.max(this.p0[0], this.p3[0]))
//    ) {
//      for (let attempt = 0; attempt < 4; attempt++) {
//        // collapse an R value to 0, this is so wrong!!!
//        let ii = -1;
//        for (let i = 0; i < 4; i++) {
//          if (R[i] !== 0 && (ii < 0 || Math.abs(R[i]) < Math.abs(R[ii]))) {
//            ii = i;
//          }
//        }
//        if (ii < 0) {
//          return 0;
//        }
//        R[ii] = 0;
//
//        // solve again, but with another 0 to help
//        for (const t of this.geo.solveCubic(R[0], R[1], R[2], R[3])) {
//          const ts = this.geo.snap01(t);
//          if (ts >= 0 && ts <= 1) {
//            return t;
//          }
//        }
//      }
//    }
//    return false;
    TODO()
  }

  internal fun mapXtoY(x: Double, force: Boolean = false): Double? {
    val t = this.mapXtoT(x, force)
      ?: return null
    return this.point(t).y
  }

  override fun pointOn(p: Vec2): Boolean {
//    if (this.geo.isEqualVec2(this.p0, p) || this.geo.isEqualVec2(this.p3, p)) {
//      return true;
//    }
//    const y = this.mapXtoY(p[0]);
//    if (y === false) {
//      return false;
//    }
//    return this.geo.snap0(y - p[1]) === 0;
    TODO()
  }

  //  toLine(): SegmentLine | null {
//    // note: this won't work for arbitrary curves, because they could loop back on themselves,
//    // but will work fine for curves that have already been split at all inflection points
//    const p0 = this.p0;
//    const p1 = this.p1;
//    const p2 = this.p2;
//    const p3 = this.p3;
//    if (
//      // vertical line
//      (this.geo.snap0(p0[0] - p1[0]) === 0 &&
//        this.geo.snap0(p0[0] - p2[0]) === 0 &&
//        this.geo.snap0(p0[0] - p3[0]) === 0) || // horizontal line
//      (this.geo.snap0(p0[1] - p1[1]) === 0 &&
//        this.geo.snap0(p0[1] - p2[1]) === 0 &&
//        this.geo.snap0(p0[1] - p3[1]) === 0)
//    ) {
//      return new SegmentLine(p0, p3, this.geo);
//    }
//    return null;
//  }
//
//  draw<TRecv extends IPolyBoolReceiver>(ctx: TRecv): TRecv {
//    const p0 = this.p0;
//    const p1 = this.p1;
//    const p2 = this.p2;
//    const p3 = this.p3;
//    ctx.moveTo(p0[0], p0[1]);
//    ctx.bezierCurveTo(p1[0], p1[1], p2[0], p2[1], p3[0], p3[1]);
//    return ctx;
//  }
  override fun copy(): SegmentCurve {
    TODO("Not yet implemented")
  }

  override fun setStart(p: Vec2) {
    TODO("Not yet implemented")
  }

  override fun setEnd(p: Vec2) {
    TODO("Not yet implemented")
  }

  override fun boundingBox(): BoundingBox {
    TODO("Not yet implemented")
  }

  override fun <TRecv : IPolyBoolReceiver> draw(ctx: TRecv): TRecv {
    TODO("Not yet implemented")
  }

  override fun isEqual(other: SegmentCurve): Boolean {
    TODO("Not yet implemented")
  }
}

//export type Segment = SegmentLine | SegmentCurve;
sealed interface Segment {
  fun start(): Vec2
  fun start2(): Vec2
  fun end(): Vec2
  //  fun point(): Vec2
  fun point(t: Double): Vec2
  fun pointOn(p: Vec2): Boolean
  fun split(ts: DoubleArray): List<Segment>

  fun setStart(p: Vec2)
  fun setEnd(p: Vec2)
  fun reverse(): Segment
}

//export function projectPointOntoSegmentLine(p: Vec2, seg: SegmentLine) {
//  const dx = seg.p1[0] - seg.p0[0];
//  const dy = seg.p1[1] - seg.p0[1];
//  const px = p[0] - seg.p0[0];
//  const py = p[1] - seg.p0[1];
//  const dist = dx * dx + dy * dy;
//  const dot = px * dx + py * dy;
//  return dot / dist;
//}

internal fun projectPointOntoSegmentLine(p: Vec2, seg: SegmentLine): Double {
  val dx = seg.p1[0] - seg.p0[0]
  val dy = seg.p1[1] - seg.p0[1]
  val px = p[0] - seg.p0[0]
  val py = p[1] - seg.p0[1]
  val dist = dx * dx + dy * dy
  val dot = px * dx + py * dy
  return dot / dist
}

internal fun segmentLineIntersectSegmentLine(
  segA: SegmentLine,
  segB: SegmentLine,
  allowOutOfRange: Boolean,
//): SegmentTValuePairs | SegmentTRangePairs | null {
): SegmentTValueOrRangePairs? {
  val geo = segA.geo
  val a0 = segA.p0
  val a1 = segA.p1
  val b0 = segB.p0
  val b1 = segB.p1
  val adx = a1[0] - a0[0]
  val ady = a1[1] - a0[1]
  val bdx = b1[0] - b0[0]
  val bdy = b1[1] - b0[1]

  val axb = adx * bdy - ady * bdx
  if (geo.snap0(axb) == 0.0) {
    // lines are coincident or parallel
    if (!geo.isCollinear(a0, a1, b0)) {
      // they're not coincident, so they're parallel, with no intersections
      return null
    }
    // otherwise, segments are on top of each other somehow (aka coincident)
    val tB0onA = projectPointOntoSegmentLine(segB.p0, segA)
    val tB1onA = projectPointOntoSegmentLine(segB.p1, segA)
    val tAMin = geo.snap01(min(tB0onA, tB1onA))
    val tAMax = geo.snap01(max(tB0onA, tB1onA))
    if (tAMax < 0 || tAMin > 1) {
      return null
    }

    val tA0onB = projectPointOntoSegmentLine(segA.p0, segB)
    val tA1onB = projectPointOntoSegmentLine(segA.p1, segB)
    val tBMin = geo.snap01(min(tA0onB, tA1onB))
    val tBMax = geo.snap01(max(tA0onB, tA1onB))
    if (tBMax < 0 || tBMin > 1) {
      return null
    }

//    return {
//      kind: "tRangePairs",
//      tStart: [Math.max(0, tAMin), Math.max(0, tBMin)],
//      tEnd: [Math.min(1, tAMax), Math.min(1, tBMax)],
//    };
    return SegmentTRangePairs(
      tStart = Vec2(max(0.0, tAMax), max(0.0, tBMin)),
      tEnd = Vec2(min(1.0, tAMax), min(1.0, tBMax)),
    )
  }

  // otherwise, not coincident, so they intersect somewhere
  val dx = a0[0] - b0[0]
  val dy = a0[1] - b0[1]
  return SegmentTValuePairsBuilder(allowOutOfRange, geo)
    .add((bdx * dy - bdy * dx) / axb, (adx * dy - ady * dx) / axb)
    .done()
}

internal fun segmentLineIntersectSegmentCurve(
  segA: SegmentLine,
  segB: SegmentCurve,
  allowOutOfRange: Boolean,
  invert: Boolean,
): SegmentTValuePairs? {
//  const geo = segA.geo;
//  const a0 = segA.p0;
//  const a1 = segA.p1;
//
//  const A = a1[1] - a0[1];
//  const B = a0[0] - a1[0];
//
//  if (geo.snap0(B) === 0) {
//    // vertical line
//    const t = segB.mapXtoT(a0[0], false);
//    if (t === false) {
//      return null;
//    }
//    const y = segB.point(t)[1];
//    const s = (y - a0[1]) / A;
//    const result = new SegmentTValuePairsBuilder(allowOutOfRange, geo);
//    if (invert) {
//      result.add(t, s);
//    } else {
//      result.add(s, t);
//    }
//    return result.done();
//  }
//
//  const C = A * a0[0] + B * a0[1];
//
//  const bx = segB.getCubicCoefficients(0);
//  const by = segB.getCubicCoefficients(1);
//
//  const rA = A * bx[0] + B * by[0];
//  const rB = A * bx[1] + B * by[1];
//  const rC = A * bx[2] + B * by[2];
//  const rD = A * bx[3] + B * by[3] - C;
//
//  const roots = geo.solveCubic(rA, rB, rC, rD);
//
//  const result = new SegmentTValuePairsBuilder(allowOutOfRange, geo);
//
//  if (geo.snap0(A) === 0) {
//    // project curve's X component onto line
//    for (const t of roots) {
//      const X = bx[0] * t * t * t + bx[1] * t * t + bx[2] * t + bx[3];
//      const s = (a0[0] - X) / B;
//      if (invert) {
//        result.add(t, s);
//      } else {
//        result.add(s, t);
//      }
//    }
//  } else {
//    // project curve's Y component onto line
//    for (const t of roots) {
//      const Y = by[0] * t * t * t + by[1] * t * t + by[2] * t + by[3];
//      const s = (Y - a0[1]) / A;
//      if (invert) {
//        result.add(t, s);
//      } else {
//        result.add(s, t);
//      }
//    }
//  }
//
//  return result.done();
  TODO()
}

internal fun segmentCurveIntersectSegmentCurve(
  segA: SegmentCurve,
  segB: SegmentCurve,
  allowOutOfRange: Boolean,
//): SegmentTValuePairs | SegmentTRangePairs | null {
): SegmentTValueOrRangePairs? {
//  const geo = segA.geo;
//
//  // dummy coincident calculation for now
//  // TODO: implement actual range/equality testing
//  if (geo.isEqualVec2(segA.p0, segB.p0)) {
//    if (geo.isEqualVec2(segA.p3, segB.p3)) {
//      if (
//        geo.isEqualVec2(segA.p1, segB.p1) &&
//        geo.isEqualVec2(segA.p2, segB.p2)
//      ) {
//        return {
//          kind: "tRangePairs",
//          tStart: [0, 0],
//          tEnd: [1, 1],
//        };
//      } else {
//        return {
//          kind: "tValuePairs",
//          tValuePairs: [
//            [0, 0],
//            [1, 1],
//          ],
//        };
//      }
//    } else {
//      return {
//        kind: "tValuePairs",
//        tValuePairs: [[0, 0]],
//      };
//    }
//  } else if (geo.isEqualVec2(segA.p0, segB.p3)) {
//    return {
//      kind: "tValuePairs",
//      tValuePairs: [[0, 1]],
//    };
//  } else if (geo.isEqualVec2(segA.p3, segB.p0)) {
//    return {
//      kind: "tValuePairs",
//      tValuePairs: [[1, 0]],
//    };
//  } else if (geo.isEqualVec2(segA.p3, segB.p3)) {
//    return {
//      kind: "tValuePairs",
//      tValuePairs: [[1, 1]],
//    };
//  }
//
//  const result = new SegmentTValuePairsBuilder(allowOutOfRange, geo);
//
//  const checkCurves = (
//    c1: SegmentCurve,
//    t1L: number,
//    t1R: number,
//    c2: SegmentCurve,
//    t2L: number,
//    t2R: number,
//  ) => {
//    const bbox1 = c1.boundingBox();
//    const bbox2 = c2.boundingBox();
//
//    if (!boundingBoxesIntersect(bbox1, bbox2)) {
//      return;
//    }
//
//    const t1M = (t1L + t1R) / 2;
//    const t2M = (t2L + t2R) / 2;
//
//    if (geo.snap0(t1R - t1L) === 0 && geo.snap0(t2R - t2L) === 0) {
//      result.add(t1M, t2M);
//      return;
//    }
//
//    const [c1L, c1R] = c1.split([0.5]);
//    const [c2L, c2R] = c2.split([0.5]);
//    checkCurves(c1L, t1L, t1M, c2L, t2L, t2M);
//    checkCurves(c1R, t1M, t1R, c2L, t2L, t2M);
//    checkCurves(c1L, t1L, t1M, c2R, t2M, t2R);
//    checkCurves(c1R, t1M, t1R, c2R, t2M, t2R);
//  };
//
//  checkCurves(segA, 0, 1, segB, 0, 1);
//  return result.done();
  TODO()
}

// return value:
//   null               => no intersection
//   SegmentTValuePairs => the segments intersect along a series of points, whose position is
//                         represented by T values pairs [segA_tValue, segB_tValue]
//                         note: a T value pair is returned even if it's just a shared vertex!
//   SegmentTRangePairs => the segments are coincident (on top of each other), and intersect along a
//                         segment, ranged by T values
internal fun segmentsIntersect(
  segA: Segment,
  segB: Segment,
  allowOutOfRange: Boolean,
//): SegmentTValuePairs | SegmentTRangePairs | null {
): SegmentTValueOrRangePairs? {
//  if (segA instanceof SegmentLine) {
//    if (segB instanceof SegmentLine) {
//      return segmentLineIntersectSegmentLine(segA, segB, allowOutOfRange);
//    } else if (segB instanceof SegmentCurve) {
//      return segmentLineIntersectSegmentCurve(
//        segA,
//        segB,
//        allowOutOfRange,
//        false,
//      );
//    }
//  } else if (segA instanceof SegmentCurve) {
//    if (segB instanceof SegmentLine) {
//      return segmentLineIntersectSegmentCurve(
//        segB,
//        segA,
//        allowOutOfRange,
//        true,
//      );
//    } else if (segB instanceof SegmentCurve) {
//      return segmentCurveIntersectSegmentCurve(segA, segB, allowOutOfRange);
//    }
//  }
//  throw new Error("PolyBool: Unknown segment instance in segmentsIntersect");
  return when (segA) {
    is SegmentLine  ->
      when (segB) {
        is SegmentLine  -> {
          segmentLineIntersectSegmentLine(
            segA = segA,
            segB = segB,
            allowOutOfRange = allowOutOfRange,
          )
        }

        is SegmentCurve ->
          segmentLineIntersectSegmentCurve(
            segA = segA,
            segB = segB,
            allowOutOfRange = allowOutOfRange,
            invert = false,
          )
      }

    is SegmentCurve ->
      when (segB) {
        is SegmentLine  ->
          segmentLineIntersectSegmentCurve(
            segA = segB,
            segB = segA,
            allowOutOfRange = allowOutOfRange,
            invert = true,
          )

        is SegmentCurve ->
          segmentCurveIntersectSegmentCurve(
            segA = segA,
            segB = segB,
            allowOutOfRange = allowOutOfRange
          )
      }
  }
}
