package dev.adamko.polybool

import  dev.adamko.polybool.internal.DoubleList
import kotlin.math.sign

////
//// polybool - Boolean operations on polygons (union, intersection, etc)
//// by Sean Connelly (@velipso), https://sean.fun
//// Project Home: https://github.com/velipso/polybool
//// SPDX-License-Identifier: 0BSD
////
//
//import { type Vec2, type Geometry } from "./Geometry";
//import type BuildLog from "./BuildLog";
//import {
//  type Segment,
//  SegmentLine,
//  SegmentCurve,
//  segmentsIntersect,
//} from "./Segment";

//export interface SegmentBoolFill {
//  above: boolean | null;
//  below: boolean | null;
//}
data class SegmentBoolFill internal constructor(
  var above: Boolean? = null,
  var below: Boolean? = null,
)

//export interface ListBoolTransition<T> {
//  before: T | null;
//  after: T | null;
//  insert: (node: T) => T;
//}
class ListBoolTransition<T : Any>(
  val before: T?,
  val after: T?,
  val insert: (node: T) -> T,
)

//export class SegmentBoolBase<T> {
abstract class SegmentBoolBase<T>  {
//  id: number;
//  data: T;
//  myFill: SegmentBoolFill;
//  otherFill: SegmentBoolFill | null = null;
//  closed: boolean;
//
//  constructor(
//    data: T,
//    fill: SegmentBoolFill | null = null,
//    closed = false,
//    log: BuildLog | null = null,
//  ) {
//    this.id = log?.segmentId() ?? -1;
//    this.data = data;
//    this.myFill = {
//      above: fill?.above ?? null,
//      below: fill?.below ?? null,
//    };
//    this.closed = closed;
//  }
}

//export class SegmentBoolLine extends SegmentBoolBase<SegmentLine> {}
data class SegmentBoolLine(
  override var data: SegmentLine,
  override val fill: SegmentBoolFill? = null,
  override var closed: Boolean = false,
  override val log: BuildLog? = null,
  override var myFill: SegmentBoolFill = SegmentBoolFill(
    above = fill?.above,
    below = fill?.below,
  ),
  override var otherFill: SegmentBoolFill? = null,
//  override val id: Int = log?.segmentId() ?: -1,
) :
//  SegmentBoolBase<SegmentLine>(),
  SegmentBool

//export class SegmentBoolCurve extends SegmentBoolBase<SegmentCurve> {}
data class SegmentBoolCurve(
  override var data: SegmentCurve,
  override val fill: SegmentBoolFill? = null,
  override var closed: Boolean = false,
  override val log: BuildLog? = null,
  override var myFill: SegmentBoolFill = SegmentBoolFill(
    above = fill?.above,
    below = fill?.below,
  ),
  override var otherFill: SegmentBoolFill? = null,
//  override val id: Int = log?.segmentId() ?: -1,
) :
//  SegmentBoolBase<SegmentLine>(),
  SegmentBool


//export type SegmentBool = SegmentBoolLine | SegmentBoolCurve;
sealed interface SegmentBool {
//  val id: Int

  val data: Segment
  val fill: SegmentBoolFill?
  var closed: Boolean

  val log: BuildLog?

  var myFill: SegmentBoolFill
  var otherFill: SegmentBoolFill?
}

//export function copySegmentBool(
//  seg: SegmentBool,
//  log: BuildLog | null,
//): SegmentBool {
//  if (seg is SegmentBoolLine) {
//    return new SegmentBoolLine(seg.data, seg.myFill, seg.closed, log);
//  } else if (seg is SegmentBoolCurve) {
//    return new SegmentBoolCurve(seg.data, seg.myFill, seg.closed, log);
//  }
//  throw new Error("PolyBool: Unknown SegmentBool in copySegmentBool");
//}
internal fun <T : SegmentBool> copySegmentBool(
  seg: T,
  log: BuildLog?,
): T {
  return when (seg) {
    is SegmentBoolCurve -> SegmentBoolCurve(seg.data, seg.myFill, seg.closed, log) as T
    is SegmentBoolLine  -> SegmentBoolLine(seg.data, seg.myFill, seg.closed, log) as T
    else                -> error("Unknown SegmentBool in copySegmentBool")
  }
}

//export class EventBool {
//  isStart: boolean;
//  p: Vec2;
//  seg: SegmentBool;
//  primary: boolean;
//  other!: EventBool;
//  status: EventBool | null = null;
//
//  constructor(isStart: Boolean, p: Vec2, seg: SegmentBool, primary: boolean) {
//    this.isStart = isStart;
//    this.p = p;
//    this.seg = seg;
//    this.primary = primary;
//  }
//}
class EventBool(
  val isStart: Boolean,
  var p: Vec2,
  val seg: SegmentBool,
  val primary: Boolean,
  var status: EventBool? = null,
) {
  lateinit var other: EventBool
}

//export class ListBool<T> {
class ListBool<T : Any> {
  private val nodes: ArrayDeque<T> = ArrayDeque()

  fun remove(node: T) {
//    const i = this.nodes.indexOf(node);
//    if (i >= 0) {
//      this.nodes.splice(i, 1);
//    }
    nodes.remove(node)
  }

  fun getIndex(node: T): Int {
    return this.nodes.indexOf(node)
  }

  fun isEmpty(): Boolean {
    return this.nodes.isEmpty()
  }

  fun getHead(): T {
    return this.nodes.first()
  }

  fun removeHead(): T {
    return this.nodes.removeFirst()
  }

  fun insertBefore(node: T, check: (node: T) -> Int): T {
    return findTransition(node, check).insert(node)
  }

  fun findTransition(
    node: T,
    check: (node: T) -> Int,
  ): ListBoolTransition<T> {
    // bisect to find the transition point
//    const compare = (a: T, b: T) => check(b) - check(a);
    val compare = { a: T, b: T -> check(b) - check(a) }
//    let i = 0;
    var i = 0
//    let high = this.nodes.length;
    var high = this.nodes.size
    while (i < high) {
//      const mid = (i + high) >> 1;
      val mid = (i + high) shr 1
      if (compare(this.nodes[mid], node) > 0) {
        high = mid
      } else {
        i = mid + 1
      }
    }
    return ListBoolTransition(
      before = if (i == 0) null else this.nodes[i - 1],
      after = this.nodes.getOrNull(i),
      insert = { node: T ->
        this.nodes.add(i, node)
        node
      }
    )
//    return {
//      before: i <= 0 ? null : this.nodes[i - 1] ?? null,
//      after: this.nodes[i] ?? null,
//      insert: (node: T) => {
//        this.nodes.splice(i, 0, node);
//        return node;
//      },
//    };
//    TODO()
  }
}

//export class Intersecter {
class Intersecter internal constructor(
  private val selfIntersection: Boolean,
  private val geo: Geometry,
  private val log: BuildLog? = null,
) {
  private val events = ListBool<EventBool>()
  private val status = ListBool<EventBool>()
  private val currentPath: ArrayDeque<SegmentBool> = ArrayDeque()

  fun compareEvents(
    aStart: Boolean,
    a1: Vec2,
    a2: Vec2,
    aSeg: Segment,
    bStart: Boolean,
    b1: Vec2,
    b2: Vec2,
    bSeg: Segment,
  ): Int {
    // compare the selected points first
//    const comp = this.geo.compareVec2(a1, b1);
    val comp = this.geo.compareVec2(a1, b1)
    if (comp != 0) {
      return comp
    }
    // the selected points are the same

    if (
      aSeg is SegmentLine &&
      bSeg is SegmentLine &&
      this.geo.isEqualVec2(a2, b2)
    ) {
      // if the non-selected points are the same too...
      // then the segments are equal
      return 0
    }

    if (aStart != bStart) {
      // if one is a start and the other isn't...
      // favor the one that isn't the start
      return if (aStart) 1 else -1
    }

    return this.compareSegments(bSeg, aSeg)
  }

  fun addEvent(ev: EventBool): EventBool {
//    this.events.insertBefore(ev, (here: EventBool) => {
//      if (here === ev) {
//        return 0;
//      }
//      return this.compareEvents(
//        ev.isStart,
//        ev.p,
//        ev.other.p,
//        ev.seg.data,
//        here.isStart,
//        here.p,
//        here.other.p,
//        here.seg.data,
//      );
//    });
    return this.events.insertBefore(ev) { here ->
      if (here == ev) {
        0
      } else {
        compareEvents(
          aStart = ev.isStart,
          a1 = ev.p,
          a2 = ev.other.p,
          aSeg = ev.seg.data,
          bStart = here.isStart,
          b1 = here.p,
          b2 = here.other.p,
          bSeg = here.seg.data,
        )
      }
    }
  }

  fun divideEvent(ev: EventBool, t: Double, p: Vec2): EventBool {
    this.log?.segmentDivide(ev.seg, p)

//    const [left, right] = ev.seg.data.split([t]) as [Segment, Segment];
    val (left, right) = ev.seg.data.split(DoubleList(t)).apply {
      require(this.size == 2) {
        "Splitting a segment did not return two segments! ev.seg.data=${ev.seg.data}"
      }
    }

    // set the *exact* intersection point
    left.setEnd(p)
    right.setStart(p)

//    const ns =
//      right is SegmentLine
//        ? new SegmentBoolLine(right, ev.seg.myFill, ev.seg.closed, this.log)
//        : right is SegmentCurve
//          ? new SegmentBoolCurve(right, ev.seg.myFill, ev.seg.closed, this.log)
//          : null;
    val ns: SegmentBool? =
      if (right is SegmentLine) {
        SegmentBoolLine(right, ev.seg.myFill, ev.seg.closed, this.log)
      } else {
        if (right is SegmentCurve) {
          SegmentBoolCurve(right, ev.seg.myFill, ev.seg.closed, this.log)
        } else {
          null
        }
      }
    if (ns == null) {
      error("Unknown segment data in divideEvent")
    }
    // slides an end backwards
    //   (start)------------(end)    to:
    //   (start)---(end)
    this.events.remove(ev.other)
    when (val evSeg = ev.seg) {
      is SegmentBoolCurve -> {
        require(left is SegmentCurve) { "left is not a SegmentCurve $left" }
        evSeg.data = left
      }

      is SegmentBoolLine  -> {
        require(left is SegmentLine) { "left is not a SegmentLine $left" }
        evSeg.data = left
      }
    }
    this.log?.segmentChop(ev.seg)
    ev.other.p = p
    this.addEvent(ev.other)
    return this.addSegment(ns, ev.primary)
  }

  fun beginPath() {
    currentPath.clear()
  }

  fun closePath() {
    for (seg in this.currentPath) {
      seg.closed = true
    }
  }

  fun addSegment(seg: SegmentBool, primary: Boolean): EventBool {
    val evStart = EventBool(true, seg.data.start(), seg, primary)
    val evEnd = EventBool(false, seg.data.end(), seg, primary)
    evStart.other = evEnd
    evEnd.other = evStart
    this.addEvent(evStart)
    this.addEvent(evEnd)
    return evStart
  }

  fun addLine(from: Vec2, to: Vec2, primary: Boolean = true) {
    val f = this.geo.compareVec2(from, to)
    if (f == 0) {
      // points are equal, so we have a zero-length segment, skip it
      return
    }
    val seg = SegmentBoolLine(
      data = SegmentLine(
        p0 = if (f < 0) from else to,
        p1 = if (f < 0) to else from,
        geo = this.geo,
      ),
      fill = null,
      closed = false,
      log = this.log,
    )
    this.currentPath.addLast(seg)
    this.addSegment(seg, primary)
  }

  fun addCurve(
    from: Vec2,
    c1: Vec2,
    c2: Vec2,
    to: Vec2,
    primary: Boolean = true
  ) {
    val original = SegmentCurve(from, c1, c2, to, this.geo)
    val curves = original.split(original.inflectionTValues())
    for (curve in curves) {
      val f = this.geo.compareVec2(curve.start(), curve.end())
      if (f == 0) {
        // points are equal AFTER splitting... this only happens for zero-length segments
        continue // skip it
      }
      // convert horizontal/vertical curves to lines
      val line = curve.toLine()
      if (line != null) {
        this.addLine(line.p0, line.p1, primary)
      } else {
        val seg = SegmentBoolCurve(
          data = if (f < 0) curve else curve.reverse(),
          fill = null,
          closed = false,
          log = this.log,
        )
        this.currentPath.addLast(seg)
        this.addSegment(seg, primary)
      }
    }
  }

  fun compareSegments(seg1: Segment, seg2: Segment): Int {
    // TODO:
    //  This is where some of the curve instability comes from... we need to reliably sort
    //  segments, but this is surprisingly hard when it comes to curves.
    //
    //  The easy case is something like:
    //
    //             C   A - - - D
    //               \
    //                 \
    //                   B
    //  A is clearly above line C-B, which is easily calculated... however, once curves are
    //  introduced, it's not so obvious without using some heuristic which will fail at times.

//    let A = seg1.start();
    var A = seg1.start()
//    let B = seg2.start2();
    var B = seg2.start2()
//    const C = seg2.start();
    val C = seg2.start()
    if (seg2.pointOn(A)) {
      // A intersects seg2 somehow (possibly sharing a start point, or maybe just splitting it)
      //
      //   AC - - - - D
      //      \
      //        \
      //          B
      //
      // so grab seg1's second point (D) instead
      A = seg1.start2()
      if (seg2.pointOn(A)) {
        if (seg1 is SegmentLine) {
          if (seg2 is SegmentLine) {
            // oh... D is on the line too... so these are the same
            return 0
          }
          if (seg2 is SegmentCurve) {
            A = seg1.point(0.5) // TODO: ???
          }
        }
        if (seg1 is SegmentCurve) {
          A = seg1.end()
        }
      }
      if (seg2 is SegmentCurve) {
        if (
          this.geo.snap0(A[0] - C[0]) == 0.0 &&
          this.geo.snap0(B[0] - C[0]) == 0.0
        ) {
          // seg2 is a curve, but the tangent line (C-B) at the start point is vertical, and
          // collinear with A... so... just sort based on the Y values I guess?
          return sign(C[1] - A[1]).toInt()
        }
      }
    } else {
      if (seg2 is SegmentCurve) {
        // find seg2's position at A[0] and see if it's above or below A[1]
//        const y = seg2.mapXtoY(A[0], true);
        val y = seg2.mapXtoY(A[0], true)
        if (y != null) {
          return sign(y - A[1]).toInt()
        }
//        TODO()
      }
      if (seg1 is SegmentCurve) {
        // unfortunately, in order to sort against curved segments, we need to check the
        // intersection point... this means a lot more intersection tests, but I'm not sure how else
        // to sort correctly
        val i = segmentsIntersect(seg1, seg2, true)
        if (i is SegmentTValuePairs) {
          // find the intersection point on seg1
          for (pair in i.tValuePairs) {
            val t = this.geo.snap01(pair[0])
            if (t > 0 && t < 1) {
              B = seg1.point(t)
              break
            }
          }
        }
      }
    }

    // fallthrough to this calculation which determines if A is on one side or another of C-B
//    const [Ax, Ay] = A;
//    const [Bx, By] = B;
//    const [Cx, Cy] = C;
    val (Ax, Ay) = A
    val (Bx, By) = B
    val (Cx, Cy) = C
//    return Math.sign((Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax));
    return sign((Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax)).toInt()
  }

  fun statusFindSurrounding(ev: EventBool): ListBoolTransition<EventBool> {
//    return this.status.findTransition(ev, (here: EventBool) => {
//      if (ev === here) {
//        return 0;
//      }
//      const c = this.compareSegments(ev.seg.data, here.seg.data);
//      return c === 0 ? -1 : c;
//    });
    return this.status.findTransition(ev) { here: EventBool ->
      if (ev == here) {
        0
      } else {
        val c = this.compareSegments(ev.seg.data, here.seg.data)
        if (c == 0) -1 else c
      }
    }
  }

  fun checkIntersection(ev1: EventBool, ev2: EventBool): EventBool? {
    // returns the segment equal to ev1, or null if nothing equal
    val seg1 = ev1.seg
    val seg2 = ev2.seg

    this.log?.checkIntersection(seg1, seg2)

    val i = segmentsIntersect(seg1.data, seg2.data, false)

    when (i) {
      is SegmentTRangePairs -> {

        // segments are parallel or coincident
//      const {
//        tStart: [tA1, tB1],
//        tEnd: [tA2, tB2],
//      } = i;
        val (tA1, tB1) = i.tStart
        val (tA2, tB2) = i.tEnd

        if (
          (tA1 == 1.0 && tA2 == 1.0 && tB1 == 0.0 && tB2 == 0.0) ||
          (tA1 == 0.0 && tA2 == 0.0 && tB1 == 1.0 && tB2 == 1.0)
        ) {
          return null // segments touch at endpoints... no intersection
        }

        if (tA1 == 0.0 && tA2 == 1.0 && tB1 == 0.0 && tB2 == 1.0) {
          return ev2 // segments are exactly equal
        }

        val a1 = seg1.data.start()
        val a2 = seg1.data.end()
        val b2 = seg2.data.end()

        if (tA1 == 0.0 && tB1 == 0.0) {
//        if (tA2 === 1) {
//          //  (a1)---(a2)
//          //  (b1)----------(b2)
//          this.divideEvent(ev2, tB2, a2);
//        } else {
//          //  (a1)----------(a2)
//          //  (b1)---(b2)
//          this.divideEvent(ev1, tA2, b2);
//        }
//        return ev2;
          TODO()
        } else if (tB1 > 0 && tB1 < 1) {
          if (tA2 == 1.0 && tB2 == 1.0) {
            //         (a1)---(a2)
            //  (b1)----------(b2)
            this.divideEvent(ev2, tB1, a1)
//          TODO()
          } else {
            // make a2 equal to b2
            if (tA2 == 1.0) {
              //         (a1)---(a2)
              //  (b1)-----------------(b2)
              this.divideEvent(ev2, tB2, a2)
            } else {
              //         (a1)----------(a2)
              //  (b1)----------(b2)
              this.divideEvent(ev1, tA2, b2)
            }
            //         (a1)---(a2)
            //  (b1)----------(b2)
            this.divideEvent(ev2, tB1, a1)
//          TODO()
          }
        }
        return null
      }

      is SegmentTValuePairs -> {
        if (i.tValuePairs.isEmpty()) {
          return null
        }
        // process a single intersection

        // skip intersections where endpoints meet
        var minPair = i.tValuePairs[0]
//      for (
//        let j = 1;
//        j < i.tValuePairs.length &&
//        ((minPair[0] === 0 && minPair[1] === 0) ||
//          (minPair[0] === 0 && minPair[1] === 1) ||
//          (minPair[0] === 1 && minPair[1] === 0) ||
//          (minPair[0] === 1 && minPair[1] === 1));
//        j++
//      ) {
//        minPair = i.tValuePairs[j];
//      }

        var j = 1
        while (j < i.tValuePairs.size) {
          if (
            (minPair.x == 0.0 && minPair.y == 0.0) ||
            (minPair.x == 0.0 && minPair.y == 1.0) ||
            (minPair.x == 1.0 && minPair.y == 0.0) ||
            (minPair.x == 1.0 && minPair.y == 1.0)
          ) {
            break
          }

          minPair = i.tValuePairs[j]
          j++
        }

        val (tA, tB) = minPair

        // even though *in theory* seg1.data.point(tA) === seg2.data.point(tB), that isn't exactly
        // correct in practice because intersections aren't exact... so we need to calculate a single
        // intersection point that everyone can share
//      const p =
//        tB === 0
//          ? seg2.data.start()
//          : tB === 1
//            ? seg2.data.end()
//            : tA === 0
//              ? seg1.data.start()
//              : tA === 1
//                ? seg1.data.end()
//                : seg1.data.point(tA);

        val p: Vec2 =
          if (tB == 0.0) {
            seg2.data.start()
          } else {
            if (tB == 1.0) {
              seg2.data.end()
            } else {
              if (tA == 0.0) {
                seg1.data.start()
              } else {
                if (tA == 1.0) {
                  seg1.data.end()
                } else {
                  seg1.data.point(tA)
                }
              }
            }
          }

        // is A divided between its endpoints? (exclusive)
        if (tA > 0 && tA < 1) {
          this.divideEvent(ev1, tA, p)
        }
        // is B divided between its endpoints? (exclusive)
        if (tB > 0 && tB < 1) {
          this.divideEvent(ev2, tB, p)
        }
        return null
      }

      null                  -> return null
    }
  }

  fun calculate(): List<SegmentBool> {
    val segments = ArrayDeque<SegmentBool>()
    while (!this.events.isEmpty()) {
      val ev = this.events.getHead()

      this.log?.vert(ev.p[0])

      if (ev.isStart) {
        this.log?.segmentNew(ev.seg, ev.primary)

        val surrounding = this.statusFindSurrounding(ev)
        val above = surrounding.before
        val below = surrounding.after

        this.log?.tempStatus(
          seg = ev.seg,
          above = above?.seg,
          below = below?.seg,
        )

        //        const checkBothIntersections = () => {
//          if (above) {
//            const eve = this.checkIntersection(ev, above);
//            if (eve) {
//              return eve;
//            }
//          }
//          if (below) {
//            return this.checkIntersection(ev, below);
//          }
//          return null;
//        };
        fun checkBothIntersections(): EventBool? {
          if (above != null) {
            val eve = this.checkIntersection(ev, above)
            if (eve != null) {
              return eve
            }
          }
          if (below != null) {
            return this.checkIntersection(ev, below)
          }
          return null
        }

        val eve = checkBothIntersections()
        if (eve != null) {
          // ev and eve are equal
          // we'll keep eve and throw away ev

          // merge ev.seg's fill information into eve.seg

          if (this.selfIntersection) {
//            let toggle: boolean; // are we a toggling edge?
//            if (ev.seg.myFill.below === null) {
//              toggle = ev.seg.closed;
//            } else {
//              toggle = ev.seg.myFill.above !== ev.seg.myFill.below;
//            }
//
//            // merge two segments that belong to the same polygon
//            // think of this as sandwiching two segments together, where
//            // `eve.seg` is the bottom -- this will cause the above fill flag to
//            // toggle
//            if (toggle) {
//              eve.seg.myFill.above = !eve.seg.myFill.above;
//            }
            TODO()
          } else {
            // merge two segments that belong to different polygons
            // each segment has distinct knowledge, so no special logic is
            // needed
            // note that this can only happen once per segment in this phase,
            // because we are guaranteed that all self-intersections are gone
            eve.seg.otherFill = ev.seg.myFill
          }

          this.log?.segmentUpdate(eve.seg)

          this.events.remove(ev.other)
          this.events.remove(ev)
        }

        if (this.events.getHead() != ev) {
          // something was inserted before us in the event queue, so loop back
          // around and process it before continuing
          this.log?.rewind(ev.seg)
          continue
        }


        // calculate fill flags

        if (this.selfIntersection) {
          // are we a toggling edge?
          val toggle: Boolean = if (ev.seg.myFill.below == null) {
            // if we are new then we toggle if we're part of a closed path
            ev.seg.closed
          } else {
            // we are a segment that has previous knowledge from a division
            // calculate toggle
            ev.seg.myFill.above != ev.seg.myFill.below
          }

          // next, calculate whether we are filled below us
          if (below == null) {
            // if nothing is below us, then we're not filled
            ev.seg.myFill.below = false
          } else {
            // otherwise, we know the answer -- it's the same if whatever is
            // below us is filled above it
            ev.seg.myFill.below = below.seg.myFill.above
          }

          // since now we know if we're filled below us, we can calculate
          // whether we're filled above us by applying toggle to whatever is
          // below us
//          ev.seg.myFill.above = toggle
//            ? !ev.seg.myFill.below
//            : ev.seg.myFill.below;
          ev.seg.myFill.above =
            if (toggle) ev.seg.myFill.below != true else ev.seg.myFill.below
        } else {
          // now we fill in any missing transition information, since we are
          // all-knowing at this point

          if (ev.seg.otherFill == null) {
            // if we don't have other information, then we need to figure out if
            // we're inside the other polygon
//            let inside: boolean | null;
            val inside: Boolean?
            if (below == null) {
              // if nothing is below us, then we're not filled
              inside = false
            } else {
              // otherwise, something is below us
              // so copy the below segment's other polygon's above
              if (ev.primary == below.primary) {
                val belowSegOtherFill = below.seg.otherFill
                  ?: error("Unexpected state of otherFill (null)")
//                if (belowSegOtherFill == null) {
////                  throw new Error(
////                    "PolyBool: Unexpected state of otherFill (null)",
////                  );
//                }
                inside = belowSegOtherFill.above
              } else {
                inside = below.seg.myFill.above
              }
            }
//            ev.seg.otherFill = {
//              above: inside,
//              below: inside,
//            };
            ev.seg.otherFill = SegmentBoolFill(
              above = inside,
              below = inside,
            )
          }
        }

        this.log?.status(
          seg = ev.seg,
          above = above?.seg,
          below = below?.seg,
        )

        // insert the status and remember it for later removal
        ev.other.status = surrounding.insert(ev)
      } else {
        // end
//        println("ev: $ev")
//        println("ev.status: ${ev.status}")
        val st = ev.status

        requireNotNull(st) {
          "Zero-length segment detected; your epsilon is probably too small or too large"
        }

        // removing the status will create two new adjacent edges, so we'll need
        // to check for those
//        const i = this.status.getIndex(st);
//        if (i > 0 && i < this.status.nodes.length - 1) {
//          const before = this.status.nodes[i - 1];
//          const after = this.status.nodes[i + 1];
//          this.checkIntersection(before, after);
//        }

        this.log?.statusRemove(st.seg)

        // remove the status
        this.status.remove(st)

        // if we've reached this point, we've calculated everything there is to
        // know, so save the segment for reporting
        if (!ev.primary) {
          // make sure `seg.myFill` actually points to the primary polygon
          // though
          val evSegOtherFill = requireNotNull(ev.seg.otherFill) {
            "Unexpected state of otherFill (null)"
          }
          val s = ev.seg.myFill
          ev.seg.myFill = evSegOtherFill
          ev.seg.otherFill = s
        }
        segments.addLast(ev.seg)
      }

      // remove the event and continue
      this.events.removeHead()
    }

    this.log?.done()

    return segments
  }
}
