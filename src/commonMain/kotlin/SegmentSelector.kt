package dev.adamko.polybool


//// polybool - Boolean operations on polygons (union, intersection, etc)
//// by Sean Connelly (@velipso), https://sean.fun
//// Project Home: https://github.com/velipso/polybool
//// SPDX-License-Identifier: 0BSD
////
//
//import {
//  type SegmentBool,
//  SegmentBoolLine,
//  SegmentBoolCurve,
//} from "./Intersecter";
//import type BuildLog from "./BuildLog";

/** Filter a list of segments based on boolean operations. */
internal fun select(
  segments: List<SegmentBool>,
  selection: List<Int>,
  log: BuildLog?,
): List<SegmentBool> {
//  const result: SegmentBool[] = [];
  val result = ArrayDeque<SegmentBool>()
//  for (const seg of segments) {
  for (seg in segments) {
//    const index =
//      (seg.myFill.above ? 8 : 0) +
//      (seg.myFill.below ? 4 : 0) +
//      (seg.otherFill && seg.otherFill.above ? 2 : 0) +
//      (seg.otherFill && seg.otherFill.below ? 1 : 0);

    val index: Int = run {
      var i = 0
      if (seg.myFill.above == true) i += 8
      if (seg.myFill.below == true) i += 4
      if (seg.otherFill?.above == true) i += 2
      if (seg.otherFill?.below == true) i += 1
      i
    }
//      (if (seg.myFill.above == true) 8 else 0) +
//        (if (seg.myFill.below == true) 4 else 0) +
//        (if (seg.otherFill?.above == true) 2 else 0) +
//        (if (seg.otherFill?.below == true) 1 else 0)
    val flags = selection[index]
//    const above = (flags & 1) !== 0; // bit 1 if filled above
    val above = (flags and 1) != 0 // bit 1 if filled above
//    const below = (flags & 2) !== 0; // bit 2 if filled below
    val below = (flags and 2) != 0 // bit 2 if filled below
    if ((!seg.closed && flags != 0) || (seg.closed && above != below)) {
      // copy the segment to the results, while also calculating the fill status
//      const fill = { above, below };
//      if (seg instanceof SegmentBoolLine) {
//        result.push(new SegmentBoolLine(seg.data, fill, seg.closed, log));
//      } else if (seg instanceof SegmentBoolCurve) {
//        result.push(new SegmentBoolCurve(seg.data, fill, seg.closed, log));
//      } else {
//        throw new Error(
//          "PolyBool: Unknown SegmentBool type in SegmentSelector",
//        );
//      }
      val fill = SegmentBoolFill(
        above = above,
        below = below,
      )
      when (seg) {
        is SegmentBoolLine  -> {
//        result.push(new SegmentBoolLine(seg.data, fill, seg.closed, log));
          result.addLast(SegmentBoolLine(seg.data, fill, seg.closed, log))
        }

        is SegmentBoolCurve -> {
//        result.push(new SegmentBoolCurve(seg.data, fill, seg.closed, log));
          result.addLast(SegmentBoolCurve(seg.data, fill, seg.closed, log))
        }
      }
    }
  }
  log?.selected(result)
  return result
}

//export class SegmentSelector {
object SegmentSelector {

  //@ f o rmatter:off
  fun union(segments: List<SegmentBool>, log: BuildLog?): List<SegmentBool> {
    // primary | secondary
    // above1 below1 above2 below2    Keep?               Value
    //    0      0      0      0   =>   yes if open         4
    //    0      0      0      1   =>   yes filled below    2
    //    0      0      1      0   =>   yes filled above    1
    //    0      0      1      1   =>   no                  0
    //    0      1      0      0   =>   yes filled below    2
    //    0      1      0      1   =>   yes filled below    2
    //    0      1      1      0   =>   no                  0
    //    0      1      1      1   =>   no                  0
    //    1      0      0      0   =>   yes filled above    1
    //    1      0      0      1   =>   no                  0
    //    1      0      1      0   =>   yes filled above    1
    //    1      0      1      1   =>   no                  0
    //    1      1      0      0   =>   no                  0
    //    1      1      0      1   =>   no                  0
    //    1      1      1      0   =>   no                  0
    //    1      1      1      1   =>   no                  0
    return select(
      segments,
      listOf(
        4, 2, 1, 0,
        2, 2, 0, 0,
        1, 0, 1, 0,
        0, 0, 0, 0,
      ),
      log,
    )
  }

  // prettier-ignore
  fun intersect(
    segments: List<SegmentBool>,
    log: BuildLog?
  ): List<SegmentBool> {
    // primary & secondary
    // above1 below1 above2 below2    Keep?               Value
    //    0      0      0      0   =>   no                  0
    //    0      0      0      1   =>   no                  0
    //    0      0      1      0   =>   no                  0
    //    0      0      1      1   =>   yes if open         4
    //    0      1      0      0   =>   no                  0
    //    0      1      0      1   =>   yes filled below    2
    //    0      1      1      0   =>   no                  0
    //    0      1      1      1   =>   yes filled below    2
    //    1      0      0      0   =>   no                  0
    //    1      0      0      1   =>   no                  0
    //    1      0      1      0   =>   yes filled above    1
    //    1      0      1      1   =>   yes filled above    1
    //    1      1      0      0   =>   yes if open         4
    //    1      1      0      1   =>   yes filled below    2
    //    1      1      1      0   =>   yes filled above    1
    //    1      1      1      1   =>   no                  0
    return select(
      segments,
      listOf(
        0, 0, 0, 4,
        0, 2, 0, 2,
        0, 0, 1, 1,
        4, 2, 1, 0,
      ),
      log,
    )
  }

  // prettier-ignore
  fun difference(segments: List<SegmentBool>, log: BuildLog?): List<SegmentBool> {
    // primary - secondary
    // above1 below1 above2 below2    Keep?               Value
    //    0      0      0      0   =>   yes if open         4
    //    0      0      0      1   =>   no                  0
    //    0      0      1      0   =>   no                  0
    //    0      0      1      1   =>   no                  0
    //    0      1      0      0   =>   yes filled below    2
    //    0      1      0      1   =>   no                  0
    //    0      1      1      0   =>   yes filled below    2
    //    0      1      1      1   =>   no                  0
    //    1      0      0      0   =>   yes filled above    1
    //    1      0      0      1   =>   yes filled above    1
    //    1      0      1      0   =>   no                  0
    //    1      0      1      1   =>   no                  0
    //    1      1      0      0   =>   no                  0
    //    1      1      0      1   =>   yes filled above    1
    //    1      1      1      0   =>   yes filled below    2
    //    1      1      1      1   =>   no                  0
//    return select(
//      segments,
//      [
//        4, 0, 0, 0,
//        2, 0, 2, 0,
//        1, 1, 0, 0,
//        0, 1, 2, 0
//      ],
//      log,
//    );
    TODO()
  }

  // prettier-ignore
  fun differenceRev(segments: List<SegmentBool>, log: BuildLog?): List<SegmentBool> {
//    // secondary - primary
//    // above1 below1 above2 below2    Keep?               Value
//    //    0      0      0      0   =>   yes if open         4
//    //    0      0      0      1   =>   yes filled below    2
//    //    0      0      1      0   =>   yes filled above    1
//    //    0      0      1      1   =>   no                  0
//    //    0      1      0      0   =>   no                  0
//    //    0      1      0      1   =>   no                  0
//    //    0      1      1      0   =>   yes filled above    1
//    //    0      1      1      1   =>   yes filled above    1
//    //    1      0      0      0   =>   no                  0
//    //    1      0      0      1   =>   yes filled below    2
//    //    1      0      1      0   =>   no                  0
//    //    1      0      1      1   =>   yes filled below    2
//    //    1      1      0      0   =>   no                  0
//    //    1      1      0      1   =>   no                  0
//    //    1      1      1      0   =>   no                  0
//    //    1      1      1      1   =>   no                  0
//    return select(
//      segments,
//      [
//        4, 2, 1, 0,
//        0, 0, 1, 1,
//        0, 2, 0, 2,
//        0, 0, 0, 0
//      ],
//      log,
//    );
    TODO()
  }

  // prettier-ignore
  fun xor(segments: List<SegmentBool>, log: BuildLog?): List<SegmentBool> {
//    // primary ^ secondary
//    // above1 below1 above2 below2    Keep?               Value
//    //    0      0      0      0   =>   yes if open         4
//    //    0      0      0      1   =>   yes filled below    2
//    //    0      0      1      0   =>   yes filled above    1
//    //    0      0      1      1   =>   no                  0
//    //    0      1      0      0   =>   yes filled below    2
//    //    0      1      0      1   =>   no                  0
//    //    0      1      1      0   =>   no                  0
//    //    0      1      1      1   =>   yes filled above    1
//    //    1      0      0      0   =>   yes filled above    1
//    //    1      0      0      1   =>   no                  0
//    //    1      0      1      0   =>   no                  0
//    //    1      0      1      1   =>   yes filled below    2
//    //    1      1      0      0   =>   no                  0
//    //    1      1      0      1   =>   yes filled above    1
//    //    1      1      1      0   =>   yes filled below    2
//    //    1      1      1      1   =>   no                  0
//    return select(
//      segments,
//      [
//        4, 2, 1, 0,
//        2, 0, 0, 1,
//        1, 0, 0, 2,
//        0, 1, 2, 0
//      ],
//      log,
//    );
    TODO()
  }
}
