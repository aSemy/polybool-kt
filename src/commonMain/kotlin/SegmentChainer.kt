////
//// polybool - Boolean operations on polygons (union, intersection, etc)
//// by Sean Connelly (@velipso), https://sean.fun
//// Project Home: https://github.com/velipso/polybool
//// SPDX-License-Identifier: 0BSD
////
//
//import { type Geometry, type Vec6 } from "./Geometry";
//import { type SegmentBool } from "./Intersecter";
//import type BuildLog from "./BuildLog";
//import { type Segment, SegmentLine, SegmentCurve } from "./Segment";
//
////
//// converts a list of segments into a list of regions, while also removing
//// unnecessary verticies
////

//export interface IPolyBoolReceiver {
//  beginPath: () => void;
//  moveTo: (x: number, y: number) => void;
//  lineTo: (x: number, y: number) => void;
//  bezierCurveTo: (
//    cp1x: number,
//    cp1y: number,
//    cp2x: number,
//    cp2y: number,
//    x: number,
//    y: number,
//  ) => void;
//  closePath: () => void;
//}
interface IPolyBoolReceiver {
  fun beginPath()
  fun moveTo(x: Double, y: Double)
  fun lineTo(x: Double, y: Double)
  fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double)
  fun closePath()
}

fun IPolyBoolReceiver(
  beginPath: () -> Unit,
  moveTo: (x: Double, y: Double) -> Unit,
  lineTo: (x: Double, y: Double) -> Unit,
  bezierCurveTo: (cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) -> Unit,
  closePath: () -> Unit,
): IPolyBoolReceiver {
  return object : IPolyBoolReceiver {
    override fun beginPath() = beginPath()

    override fun moveTo(x: Double, y: Double): Unit =
      moveTo(x, y)

    override fun lineTo(x: Double, y: Double): Unit =
      lineTo(x, y)

    override fun bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double) =
      bezierCurveTo(
        cp1x,
        cp1y,
        cp2x,
        cp2y,
        x,
        y,
      )

    override fun closePath() = closePath()
  }
}

//export function joinLines(
//  seg1: SegmentLine,
//  seg2: SegmentLine,
//  geo: Geometry,
//): SegmentLine | false {
//  if (geo.isCollinear(seg1.p0, seg1.p1, seg2.p1)) {
//    return new SegmentLine(seg1.p0, seg2.p1, geo);
//  }
//  return false;
//}
fun joinLines(
  seg1: SegmentLine,
  seg2: SegmentLine,
  geo: Geometry
): SegmentLine? {
  if (!geo.isCollinear(seg1.p0, seg1.p1, seg2.p1)) {
    return null
  }
  return SegmentLine(
    p0 = seg1.p0,
    p1 = seg2.p1,
    geo = geo,
  )
}
/*
export function joinCurves(
  seg1: SegmentCurve,
  seg2: SegmentCurve,
  geo: Geometry,
): SegmentCurve | false {
  if (geo.isCollinear(seg1.p2, seg1.p3, seg2.p1)) {
    const dx = seg2.p1[0] - seg1.p2[0];
    const dy = seg2.p1[1] - seg1.p2[1];
    const t =
      Math.abs(dx) > Math.abs(dy)
        ? (seg1.p3[0] - seg1.p2[0]) / dx
        : (seg1.p3[1] - seg1.p2[1]) / dy;
    const ts = geo.snap01(t);
    if (ts !== 0 && ts !== 1) {
      const ns = new SegmentCurve(
        seg1.p0,
        [
          seg1.p0[0] + (seg1.p1[0] - seg1.p0[0]) / t,
          seg1.p0[1] + (seg1.p1[1] - seg1.p0[1]) / t,
        ],
        [
          seg2.p2[0] - (t * (seg2.p3[0] - seg2.p2[0])) / (1 - t),
          seg2.p2[1] - (t * (seg2.p3[1] - seg2.p2[1])) / (1 - t),
        ],
        seg2.p3,
        geo,
      );
      // double check that if we split at T, we get seg1/seg2 back
      const [left, right] = ns.split([t]);
      if (left.isEqual(seg1) && right.isEqual(seg2)) {
        return ns;
      }
    }
  }
  return false;
}
*/
private fun joinCurves(
  seg1: SegmentCurve,
  seg2: SegmentCurve,
  geo: Geometry,
): SegmentCurve? {
  TODO()
}

/*
export function joinSegments(
  seg1: Segment | undefined,
  seg2: Segment | undefined,
  geo: Geometry,
): Segment | false {
  if (seg1 === seg2) {
    return false;
  }
  if (seg1 is SegmentLine && seg2 is SegmentLine) {
    return joinLines(seg1, seg2, geo);
  }
  if (seg1 is SegmentCurve && seg2 is SegmentCurve) {
    return joinCurves(seg1, seg2, geo);
  }
  return false;
}
*/
private fun joinSegments(
  seg1: Segment?,
  seg2: Segment?,
  geo: Geometry,
): Segment? {
  if (seg1 == seg2) {
    return null
  }
  if (seg1 is SegmentLine && seg2 is SegmentLine) {
    return joinLines(seg1, seg2, geo)
  }
  if (seg1 is SegmentCurve && seg2 is SegmentCurve) {
    return joinCurves(seg1, seg2, geo)
  }
  return null
}

//interface ISegsFill {
//  segs: Segment[];
//  fill: boolean;
//}
private data class ISegsFill(
  val segs: ArrayDeque<Segment>,
  val fill: Boolean,
) : List<Segment> by segs {
  constructor(segs: List<Segment>, fill: Boolean) : this(ArrayDeque(segs), fill)
}

internal fun SegmentChainer(
  segments: List<SegmentBool>,
  geo: Geometry,
  log: BuildLog?,
): List<List<Segment>> {
//  const closedChains: ISegsFill[] = [];
//  const openChains: ISegsFill[] = [];
//  const regions: Segment[][] = [];

  val closedChains = ArrayDeque<ISegsFill>()
  val openChains = ArrayDeque<ISegsFill>()
  val regions = ArrayDeque<List<Segment>>()

  for (segb in segments) {
    var seg = segb.data
    val closed = segb.closed
//    const chains = closed ? closedChains : openChains;
    val chains = if (closed) closedChains else openChains
//    const pt1 = seg.start();
    val pt1 = seg.start()
//    const pt2 = seg.end();
    val pt2 = seg.end()

    //    const reverseChain = (index: number) => {
//      log?.chainReverse(index, closed);
//      const newChain: Segment[] = [];
//      for (const seg of chains[index].segs) {
//        newChain.unshift(seg.reverse());
//      }
//      chains[index] = {
//        segs: newChain,
//        fill: !chains[index].fill,
//      };
//      return newChain;
//    };
    fun reverseChain(index: Int): List<Segment> {
      log?.chainReverse(index, closed)
//      const newChain: Segment[] = [];
      val newChain = ArrayDeque<Segment>()
//      for (const seg of chains[index].segs) {
//        newChain.unshift(seg.reverse());
//      }
      for (seg in chains[index]) {
        newChain.addFirst(seg.reverse())
      }
//      chains[index] = {
//        segs: newChain,
//        fill: !chains[index].fill,
//      };
      chains[index] = ISegsFill(segs = newChain, fill = !chains[index].fill)
      return newChain
    }

    if (seg is SegmentLine && geo.isEqualVec2(pt1, pt2)) {
      println("Warning: Zero-length segment detected; your epsilon is probably too small or too large")
      continue
    }

//    log?.chainStart({ seg, fill: !!segb.myFill.above }, closed);

    // search for two chains that this segment matches
//    const firstMatch = {
//      index: 0,
//      matchesHead: false,
//      matchesPt1: false,
//    };
//    const secondMatch = {
//      index: 0,
//      matchesHead: false,
//      matchesPt1: false,
//    };
//    let nextMatch: typeof firstMatch | null = firstMatch;
    data class Match(
      var index: Int = 0,
      var matchesHead: Boolean = false,
      var matchesPt1: Boolean = false,
    )

    val firstMatch = Match()
    val secondMatch = Match()
    var nextMatch: Match? = firstMatch
    //    function setMatch(
//      index: number,
//      matchesHead: boolean,
//      matchesPt1: boolean,
//    ) {
//      // return true if we've matched twice
//      if (nextMatch) {
//        nextMatch.index = index;
//        nextMatch.matchesHead = matchesHead;
//        nextMatch.matchesPt1 = matchesPt1;
//      }
//      if (nextMatch === firstMatch) {
//        nextMatch = secondMatch;
//        return false;
//      }
//      nextMatch = null;
//      return true; // we've matched twice, we're done here
//    }
    fun setMatch(
      index: Int,
      matchesHead: Boolean,
      matchesPt1: Boolean,
    ): Boolean {
      // return true if we've matched twice
      nextMatch?.let {
        it.index = index
        it.matchesHead = matchesHead
        it.matchesPt1 = matchesPt1
      }
      if (nextMatch == firstMatch) {
        nextMatch = secondMatch
        return false
      }
      nextMatch = null
      return true // we've matched twice, we're done here
    }


//    for (let i = 0; i < chains.length; i++) {
//      const chain = chains[i].segs;
//      const head = chain[0].start();
//      const tail = chain[chain.length - 1].end();
//      if (geo.isEqualVec2(head, pt1)) {
//        if (setMatch(i, true, true)) {
//          break;
//        }
//      } else if (geo.isEqualVec2(head, pt2)) {
//        if (setMatch(i, true, false)) {
//          break;
//        }
//      } else if (geo.isEqualVec2(tail, pt1)) {
//        if (setMatch(i, false, true)) {
//          break;
//        }
//      } else if (geo.isEqualVec2(tail, pt2)) {
//        if (setMatch(i, false, false)) {
//          break;
//        }
//      }
//    }
    for (i in chains.indices) {
      val chain = chains[i].segs
      val head = chain.first().start()
      val tail = chain.last().end()
      if (geo.isEqualVec2(head, pt1)) {
        if (setMatch(i, matchesHead = true, matchesPt1 = true)) {
          break
        }
      } else if (geo.isEqualVec2(head, pt2)) {
        if (setMatch(i, matchesHead = true, matchesPt1 = false)) {
          break
        }
      } else if (geo.isEqualVec2(tail, pt1)) {
        if (setMatch(i, matchesHead = false, matchesPt1 = true)) {
          break
        }
      } else if (geo.isEqualVec2(tail, pt2)) {
        if (setMatch(i, matchesHead = false, matchesPt1 = false)) {
          break
        }
      }
    }

    if (nextMatch == firstMatch) {
      // we didn't match anything, so create a new chain
//      const fill = !!segb.myFill.above;
      val fill = segb.myFill.above!!
//      chains.push({ segs: [seg], fill });
      chains.add(ISegsFill(listOf(seg), fill))
//      log?.chainNew({ seg, fill }, closed);
      log?.chainNew(BuildLog.ISegFill(seg, fill), closed)
    } else if (nextMatch == secondMatch) {
      // we matched a single chain
      val index = firstMatch.index
      log?.chainMatch(index, closed)

      // add the other point to the appropriate end
//      const { segs: chain, fill } = chains[index];
      val chain = chains[index]
      if (firstMatch.matchesHead) {
        if (firstMatch.matchesPt1) {
          seg = seg.reverse()
//          log?.chainAddHead(index, { seg, fill }, closed);
          chain.segs.addFirst(seg)
        } else {
//          log?.chainAddHead(index, { seg, fill }, closed);
          chain.segs.addFirst(seg)
        }
      } else {
        if (firstMatch.matchesPt1) {
//          log?.chainAddTail(index, { seg, fill }, closed);
          chain.segs.addLast(seg)
        } else {
          seg = seg.reverse()
          //log?.chainAddTail(index, { seg, fill }, closed);
          chain.segs.addLast(seg)
        }
      }

      // simplify chain
      if (firstMatch.matchesHead) {
        val next = chain[1]
        val newSeg = joinSegments(seg, next, geo)
        if (newSeg != null) {
          chain.segs.removeFirst()
//          chain[0] = newSeg;
          chain.segs[0] = newSeg
//          log?.chainSimplifyHead(index, { seg: newSeg, fill }, closed);
        }
      } else {
//        const next = chain[chain.length - 2];
        val next = chain[chain.size - 2]
        val newSeg = joinSegments(next, seg, geo)
        if (newSeg != null) {
          chain.segs.removeLast()
          chain.segs[chain.size - 1] = newSeg
//          log?.chainSimplifyTail(index, { seg: newSeg, fill }, closed);
        }
      }

      // check for closed chain
      if (closed) {
        var finalChain = chain.segs
        var segS = finalChain[0]
        var segE = finalChain[finalChain.size - 1]
        if (
          finalChain.size > 0 &&
          geo.isEqualVec2(segS.start(), segE.end())
        ) {
          // see if chain is clockwise
          var winding = 0.0
          var last = finalChain[0].start()
          for (seg in finalChain) {
            val here = seg.end()
            winding += here.y * last.x - here.x * last.y
            last = here
          }
          // this assumes Cartesian coordinates (Y is positive going up)
          val isClockwise = winding < 0
          if (isClockwise == chain.fill) {
            finalChain = ArrayDeque(reverseChain(index))
            segS = finalChain.first()
            segE = finalChain.last()
          }

          val newStart = joinSegments(segE, segS, geo)
          if (newStart != null) {
            finalChain.removeLast()
            finalChain[0] = newStart
//            log?.chainSimplifyClose(index, { seg: newStart, fill }, closed);
          }

          // we have a closed chain!
          log?.chainClose(index, closed)
//          chains.splice(index, 1);
          chains.removeAt(index)
          regions.addLast(finalChain)
//          TODO()
        }
      }
    } else {
//      // otherwise, we matched two chains, so we need to combine those chains together
//      const appendChain = (index1: number, index2: number) => {
//        // index1 gets index2 appended to it, and index2 is removed
//        const { segs: chain1, fill } = chains[index1];
//        const { segs: chain2 } = chains[index2];
//
//        // add seg to chain1's tail
//        log?.chainAddTail(index1, { seg, fill }, closed);
//        chain1.push(seg);
//
//        // simplify chain1's tail
//        const next = chain1[chain1.length - 2];
//        const newEnd = joinSegments(next, seg, geo);
//        if (newEnd) {
//          chain1.pop();
//          chain1[chain1.length - 1] = newEnd;
//          log?.chainSimplifyTail(index1, { seg: newEnd, fill }, closed);
//        }
//
//        // simplify chain2's head
//        const tail = chain1[chain1.length - 1];
//        const head = chain2[0];
//        const newJoin = joinSegments(tail, head, geo);
//        if (newJoin) {
//          chain2.shift();
//          chain1[chain1.length - 1] = newJoin;
//          log?.chainSimplifyJoin(
//            index1,
//            index2,
//            { seg: newJoin, fill },
//            closed,
//          );
//        }
//
//        log?.chainJoin(index1, index2, closed);
//        chains[index1].segs = chain1.concat(chain2);
//        chains.splice(index2, 1);
//      };
//
//      const F = firstMatch.index;
//      const S = secondMatch.index;
//
//      log?.chainConnect(F, S, closed);
//
//      // reverse the shorter chain, if needed
//      const reverseF = chains[F].segs.length < chains[S].segs.length;
//      if (firstMatch.matchesHead) {
//        if (secondMatch.matchesHead) {
//          if (reverseF) {
//            if (!firstMatch.matchesPt1) {
//              // <<<< F <<<< <-- >>>> S >>>>
//              seg = seg.reverse();
//            }
//            // <<<< F <<<< --> >>>> S >>>>
//            reverseChain(F);
//            // >>>> F >>>> --> >>>> S >>>>
//            appendChain(F, S);
//          } else {
//            if (firstMatch.matchesPt1) {
//              // <<<< F <<<< --> >>>> S >>>>
//              seg = seg.reverse();
//            }
//            // <<<< F <<<< <-- >>>> S >>>>
//            reverseChain(S);
//            // <<<< F <<<< <-- <<<< S <<<<   logically same as:
//            // >>>> S >>>> --> >>>> F >>>>
//            appendChain(S, F);
//          }
//        } else {
//          if (firstMatch.matchesPt1) {
//            // <<<< F <<<< --> >>>> S >>>>
//            seg = seg.reverse();
//          }
//          // <<<< F <<<< <-- <<<< S <<<<   logically same as:
//          // >>>> S >>>> --> >>>> F >>>>
//          appendChain(S, F);
//        }
//      } else {
//        if (secondMatch.matchesHead) {
//          if (!firstMatch.matchesPt1) {
//            // >>>> F >>>> <-- >>>> S >>>>
//            seg = seg.reverse();
//          }
//          // >>>> F >>>> --> >>>> S >>>>
//          appendChain(F, S);
//        } else {
//          if (reverseF) {
//            if (firstMatch.matchesPt1) {
//              // >>>> F >>>> --> <<<< S <<<<
//              seg = seg.reverse();
//            }
//            // >>>> F >>>> <-- <<<< S <<<<
//            reverseChain(F);
//            // <<<< F <<<< <-- <<<< S <<<<   logically same as:
//            // >>>> S >>>> --> >>>> F >>>>
//            appendChain(S, F);
//          } else {
//            if (!firstMatch.matchesPt1) {
//              // >>>> F >>>> <-- <<<< S <<<<
//              seg = seg.reverse();
//            }
//            // >>>> F >>>> --> <<<< S <<<<
//            reverseChain(S);
//            // >>>> F >>>> --> >>>> S >>>>
//            appendChain(F, S);
//          }
//        }
//      }
      TODO()
    }
  }
  for (oc in openChains) {
    regions.addLast(oc.segs)
  }

  return regions
}

//export function segmentsToReceiver<T extends IPolyBoolReceiver>(
//  segments: Segment[][],
//  geo: Geometry,
//  receiver: T,
//  matrix: Vec6,
//): T {
fun <T : IPolyBoolReceiver> segmentsToReceiver(
  segments: List<List<Segment>>,
  geo: Geometry,
  receiver: T,
  matrix: Vec6,
): T {
  val (a, b, c, d, e, f) = matrix
  receiver.beginPath()
  for (region in segments) {
    if (region.isEmpty()) {
      continue
    }
    region.forEachIndexed { i, seg ->
      if (i == 0) {
        val (p0x, p0y) = seg.start()
        receiver.moveTo(a * p0x + c * p0y + e, b * p0x + d * p0y + f)
      }
      when (seg) {
        is SegmentLine  -> {
          val (p1x, p1y) = seg.p1
          receiver.lineTo(a * p1x + c * p1y + e, b * p1x + d * p1y + f)
        }

        is SegmentCurve -> {
          val (p1x, p1y) = seg.p1
          val (p2x, p2y) = seg.p2
          val (p3x, p3y) = seg.p3
          receiver.bezierCurveTo(
            cp1x = a * p1x + c * p1y + e,
            cp1y = b * p1x + d * p1y + f,
            cp2x = a * p2x + c * p2y + e,
            cp2y = b * p2x + d * p2y + f,
            x = a * p3x + c * p3y + e,
            y = b * p3x + d * p3y + f,
          )
        }
      }
    }
    val first = region.first()
    val last = region.last()
    if (geo.isEqualVec2(first.start(), last.end())) {
      receiver.closePath()
    }
  }
  return receiver
}
