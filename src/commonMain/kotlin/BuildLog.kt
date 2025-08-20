package dev.adamko.polybool

////
//// polybool - Boolean operations on polygons (union, intersection, etc)
//// by Sean Connelly (@velipso), https://sean.fun
//// Project Home: https://github.com/velipso/polybool
//// SPDX-License-Identifier: 0BSD
////
//
//import { type SegmentBool } from "./Intersecter";
//import { type Vec2 } from "./Geometry";
//import { type Segment } from "./Segment";

//interface ISegFill {
//  seg: Segment;
//  fill: boolean;
//}

private typealias unknown = Any

//export default class BuildLog {
class BuildLog {
    class ISegFill(
    val seg:  Segment ,
    val fill: Boolean,
  )


  //  list: Array<{ type: String; data: unknown }> = [];
  private var nextSegmentId = 0;
//  curVert = NaN;

  fun push(type: String, data: unknown) {
//    this.list.push({
//      type,
//      data: JSON.parse(JSON.stringify(data)),
//    });
    TODO()
  }

  fun info(msg: String, data: Any? = null) {
//    this.push("info", { msg, data });
    TODO()
  }

  fun segmentId(): Int {
    return this.nextSegmentId++;
  }

  fun checkIntersection(seg1: SegmentBool, seg2: SegmentBool) {
//    this.push("check", { seg1, seg2 });
    TODO()
  }

 fun  segmentDivide(seg: SegmentBool, p: Vec2) {
//    this.push("div_seg", { seg, p });
  TODO()
   }

 fun  segmentChop(seg: SegmentBool) {
//    this.push("chop", { seg });
  TODO()
   }

  fun statusRemove(seg: SegmentBool) {
//    this.push("pop_seg", { seg });
  TODO()
   }

  fun segmentUpdate(seg: SegmentBool) {
//    this.push("seg_update", { seg });
  TODO()
   }

  internal fun segmentNew(seg: SegmentBool, primary: Boolean) {
//    this.push("new_seg", { seg, primary });
    TODO()
  }

  internal fun tempStatus(
    seg: SegmentBool,
    above: SegmentBool?,
//    above: SegmentBool | false,
    below: SegmentBool?,
//    below: SegmentBool | false,
  ) {
//    this.push("temp_status", { seg, above, below });
    TODO()
  }

  internal fun rewind(seg: SegmentBool) {
//    this.push("rewind", { seg });
    TODO()
  }

  internal fun status(
    seg: SegmentBool,
    above: SegmentBool?,
//    above: SegmentBool | false,
    below: SegmentBool?,
//    below: SegmentBool | false,
  ) {
//    this.push("status", { seg, above, below });
    TODO()
  }

  internal fun vert(x: Double?) {
//    if (x !== this.curVert) {
//      this.push("vert", { x });
//      this.curVert = x;
//    }
    TODO()
  }

  internal fun selected(segs: List<SegmentBool>) {
//    this.push("selected", { segs });
    TODO()
  }

  internal fun chainStart(sf: ISegFill, closed: Boolean) {
//    this.push("chain_start", { sf, closed });
    TODO()
  }

  internal fun chainNew(sf: ISegFill, closed: Boolean) {
//    this.push("chain_new", { sf, closed });
    TODO()
  }

  fun chainMatch(index: Int, closed: Boolean) {
//    this.push("chain_match", { index, closed });
    TODO()
  }

  fun chainClose(index: Int, closed: Boolean) {
//    this.push("chain_close", { index, closed });
    TODO()
  }

  internal fun chainAddHead(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_add_head", { index, sf, closed });
    TODO()
  }

  internal fun chainAddTail(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_add_tail", { index, sf, closed });
    TODO()
  }

  internal fun chainSimplifyHead(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_simp_head", { index, sf, closed });
    TODO()
  }

  internal fun chainSimplifyTail(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_simp_tail", { index, sf, closed });
    TODO()
  }

  internal fun chainSimplifyClose(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_simp_close", { index, sf, closed });
    TODO()
  }

  internal fun chainSimplifyJoin(
    index1: Int,
    index2: Int,
    sf: ISegFill,
    closed: Boolean,
  ) {
//    this.push("chain_simp_join", { index1, index2, sf, closed });
    TODO()
  }

  internal fun chainConnect(index1: Int, index2: Int, closed: Boolean) {
//    this.push("chain_con", { index1, index2, closed });
    TODO()
  }

  internal fun chainReverse(index: Int, closed: Boolean) {
//    this.push("chain_rev", { index, closed });
    TODO()
  }

  internal fun chainJoin(index1: Int, index2: Int, closed: Boolean) {
//    this.push("chain_join", { index1, index2, closed });
    TODO()
  }

  internal fun done() {
//    this.push("done", null);
    TODO()
  }
}
