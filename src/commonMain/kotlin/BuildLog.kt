package dev.adamko.polybool

import kotlinx.serialization.Serializable

class BuildLog {

  @Serializable
  data class ISegFill(
    val seg: Segment,
    val fill: Boolean,
  )

  //  list: Array<{ type: String; data: unknown }> = [];
  private val list: ArrayDeque<String> = ArrayDeque()
  fun list(): List<String> = list.toList()
  private var nextSegmentId = 0
  private var curVert: Double? = Double.NaN

  private fun push(type: String, data: String?) {
//    this.list.push({
//      type,
//      data: JSON.parse(JSON.stringify(data)),
//    });
    if (data != null) {
      list.addLast("$type: $data")
    } else {
      list.addLast(type)
    }
  }

  fun info(msg: String, data: String? = null) {
//    this.push("info", { msg, data });
    this.push("info", "msg: $msg, data: $data")
  }

  fun segmentId(): Int = this.nextSegmentId++

  fun checkIntersection(seg1: SegmentBool, seg2: SegmentBool) {
//    this.push("check", { seg1, seg2 });
    push("check", "seg1: $seg1, seg2: $seg2")
  }

  fun segmentDivide(seg: SegmentBool, p: Vec2) {
//    this.push("div_seg", { seg, p });
    push("div_seg", "seg: $seg, p: $p")
  }

  fun segmentChop(seg: SegmentBool) {
//    this.push("chop", { seg });
    push("chop", "seg: $seg")
  }

  fun statusRemove(seg: SegmentBool) {
//    this.push("pop_seg", { seg });
    push("pop_seg", "seg: $seg")
  }

  fun segmentUpdate(seg: SegmentBool) {
//    this.push("seg_update", { seg });
    push("seg_update", "seg: $seg")
  }

  internal fun segmentNew(seg: SegmentBool, primary: Boolean) {
//    this.push("new_seg", { seg, primary });
    push("new_seg", "seg: $seg, primary: $primary")
  }

  internal fun tempStatus(
    seg: SegmentBool,
    above: SegmentBool?,
//    above: SegmentBool | false,
    below: SegmentBool?,
//    below: SegmentBool | false,
  ) {
//    this.push("temp_status", { seg, above, below });
    push("temp_status", "seg: $seg, above: $above, below: $below")
  }

  internal fun rewind(seg: SegmentBool) {
//    this.push("rewind", { seg });
    push("rewind", "seg: $seg")
  }

  internal fun status(
    seg: SegmentBool,
    above: SegmentBool?,
//    above: SegmentBool | false,
    below: SegmentBool?,
//    below: SegmentBool | false,
  ) {
//    this.push("status", { seg, above, below });
    push("status", "seg: $seg, above: $above, below: $below")
  }

  internal fun vert(x: Double?) {
//    if (x != this.curVert) {
//      this.push("vert", { x });
//      this.curVert = x;
//    }
    if (x != curVert) {
      push("vert", "x: $x")
      curVert = x
    }
  }

  internal fun selected(segs: List<SegmentBool>) {
//    this.push("selected", { segs });
    push("selected", "segs: $segs")
  }

  internal fun chainStart(sf: ISegFill, closed: Boolean) {
//    this.push("chain_start", { sf, closed });
    push("chain_start", "sf: $sf, closed: $closed")
  }

  internal fun chainNew(sf: ISegFill, closed: Boolean) {
//    this.push("chain_new", { sf, closed });
    push("chain_new", "sf: $sf, closed: $closed")
  }

  fun chainMatch(index: Int, closed: Boolean) {
//    this.push("chain_match", { index, closed });
    push("chain_match", "index: $index, closed: $closed")
  }

  fun chainClose(index: Int, closed: Boolean) {
//    this.push("chain_close", { index, closed });
    push("chain_close", "index: $index, closed: $closed")
  }

  internal fun chainAddHead(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_add_head", { index, sf, closed });
    push("chain_add_head", "index: $index, sf: $sf, closed: $closed")
  }

  internal fun chainAddTail(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_add_tail", { index, sf, closed });
    push("chain_add_tail", "index: $index, sf: $sf, closed: $closed")
  }

  internal fun chainSimplifyHead(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_simp_head", { index, sf, closed });
    push("chain_simp_head", "index: $index, sf: $sf, closed: $closed")
  }

  internal fun chainSimplifyTail(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_simp_tail", { index, sf, closed });
    push("chain_simp_tail", "index: $index, sf: $sf, closed: $closed")
  }

  internal fun chainSimplifyClose(index: Int, sf: ISegFill, closed: Boolean) {
//    this.push("chain_simp_close", { index, sf, closed });
    push("chain_simp_close", "index: $index, sf: $sf, closed: $closed")
  }

  internal fun chainSimplifyJoin(
    index1: Int,
    index2: Int,
    sf: ISegFill,
    closed: Boolean,
  ) {
//    this.push("chain_simp_join", { index1, index2, sf, closed });
    push("chain_simp_join", "index1: $index1, index2: $index2, sf: $sf, closed: $closed")
  }

  internal fun chainConnect(index1: Int, index2: Int, closed: Boolean) {
//    this.push("chain_con", { index1, index2, closed });
    push("chain_con", "index1: $index1, index2: $index2, closed: $closed")
  }

  internal fun chainReverse(index: Int, closed: Boolean) {
//    this.push("chain_rev", { index, closed });
    push("chain_rev", "index: $index, closed: $closed")
  }

  internal fun chainJoin(index1: Int, index2: Int, closed: Boolean) {
//    this.push("chain_join", { index1, index2, closed });
    push("chain_join", "index1: $index1, index2: $index2, closed: $closed")
  }

  internal fun done() {
//    this.push("done", null);
    push("done", null)
  }
}
