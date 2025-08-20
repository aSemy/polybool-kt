package dev.adamko.polybool


//// polybool - Boolean operations on polygons (union, intersection, etc)
//// by Sean Connelly (@velipso), https://sean.fun
//// Project Home: https://github.com/velipso/polybool
//// SPDX-License-Identifier: 0BSD
////
//
//import { type Geometry, type Vec2, type Vec6 } from "./Geometry";
//import type BuildLog from "./BuildLog";
//import { type SegmentBool, Intersecter, copySegmentBool } from "./Intersecter";
//import { SegmentSelector } from "./SegmentSelector";
//import {
//  SegmentChainer,
//  segmentsToReceiver,
//  type IPolyBoolReceiver,
//} from "./SegmentChainer";
//import { type Segment } from "./Segment";

//interface IPathStateCommon<K extends string> {
//  kind: K;
//}
internal interface IPathStateCommon<K> {
  val kind: K
}

//interface IPathStateBeginPath extends IPathStateCommon<"beginPath"> {}
//
//interface IPathStateMoveTo extends IPathStateCommon<"moveTo"> {
//  start: Vec2;
//  current: Vec2;
//}
//
//type IPathState = IPathStateBeginPath | IPathStateMoveTo;
sealed interface IPathState {
  data object BeginPath : IPathState
  data class MoveTo(
    val start: Vec2,
    var current: Vec2,
  ) : IPathState
}

//export class Shape {
class Shape internal constructor(
  private val geo: Geometry,
  private val segments: List<SegmentBool>? = null,
  private val log: BuildLog? = null,
) {
  //  private readonly geo: Geometry;
//  private readonly log: BuildLog | null;
//  private pathState: IPathState = { kind: "beginPath" };
  private var pathState: IPathState = IPathState.BeginPath
  //  private resultState:
//    | { state: "new"; selfIntersect: Intersecter }
//    | { state: "seg"; segments: SegmentBool[] }
//    | { state: "reg"; segments: SegmentBool[]; regions: Segment[][] };
  private var resultState: ResultState = if (segments != null) {
    ResultState.Segments(segments)
  } else {
    ResultState.New(Intersecter(true, geo, log))
  }

  private sealed interface ResultState {
    data class New(val selfIntersect: Intersecter) : ResultState
    data class Segments(val segments: List<SegmentBool>) : ResultState
    data class Regions(
      val segments: List<SegmentBool>,
      val regions: List<List<Segment>>,
    ) : ResultState
  }

  companion object {
    private fun ResultState.requireNew(msg: () -> String): ResultState.New {
      require(this is ResultState.New, msg)
      return this
    }
  }

  //  private readonly saveStack: Array<{ matrix: Vec6 }> = [];
  private val saveStack: ArrayDeque<Vec6> = ArrayDeque()
  private var matrix: Vec6 = Vec6(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)

//  constructor(
//    geo: Geometry,
//    segments: SegmentBool[] | null = null,
//    log: BuildLog | null = null,
//  ) {
//    this.geo = geo;
//    this.log = log;
//    if (segments) {
//      this.resultState = { state: "seg", segments };
//    } else {
//      this.resultState = {
//        state: "new",
//        selfIntersect: new Intersecter(true, this.geo, this.log),
//      };
//    }
//  }

  fun setTransform(
    a: Double,
    b: Double,
    c: Double,
    d: Double,
    e: Double,
    f: Double,
  ): Shape {
    require(this.resultState is ResultState.New) {
      "Cannot change shape after using it in an operation"
    }
    this.matrix = Vec6(a, b, c, d, e, f)
    return this
  }

  fun resetTransform(): Shape {
    this.matrix = Vec6(1, 0, 0, 1, 0, 0)
    return this
  }

  fun getTransform(): Vec6 {
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
//    const [a, b, c, d, e, f] = this.matrix;
//    return { a, b, c, d, e, f };
    TODO()
  }

  fun transform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double) {
//    const [a0, b0, c0, d0, e0, f0] = this.matrix;
//    this.matrix = [
//      a0 * a + c0 * b,
//      b0 * a + d0 * b,
//      a0 * c + c0 * d,
//      b0 * c + d0 * d,
//      a0 * e + c0 * f + e0,
//      b0 * e + d0 * f + f0,
//    ];
//    return this;
    TODO()
  }

  fun rotate(angle: Double) {
//    const cos = Math.cos(angle);
//    const sin = Math.sin(angle);
//    const [a0, b0, c0, d0, e0, f0] = this.matrix;
//    this.matrix = [
//      a0 * cos + c0 * sin,
//      b0 * cos + d0 * sin,
//      c0 * cos - a0 * sin,
//      d0 * cos - b0 * sin,
//      e0,
//      f0,
//    ];
//    return this;
    TODO()
  }

  fun rotateDeg(angle: Double) {
//    const ang = ((angle % 360) + 360) % 360;
//    if (ang === 0) {
//      return this;
//    }
//    let cos = 0;
//    let sin = 0;
//    if (ang === 90) {
//      sin = 1;
//    } else if (ang === 180) {
//      cos = -1;
//    } else if (ang === 270) {
//      sin = -1;
//    } else if (ang === 45) {
//      cos = sin = Math.SQRT1_2;
//    } else if (ang === 135) {
//      sin = Math.SQRT1_2;
//      cos = -Math.SQRT1_2;
//    } else if (ang === 225) {
//      cos = sin = -Math.SQRT1_2;
//    } else if (ang === 315) {
//      cos = Math.SQRT1_2;
//      sin = -Math.SQRT1_2;
//    } else if (ang === 30) {
//      cos = Math.sqrt(3) / 2;
//      sin = 0.5;
//    } else if (ang === 60) {
//      cos = 0.5;
//      sin = Math.sqrt(3) / 2;
//    } else if (ang === 120) {
//      cos = -0.5;
//      sin = Math.sqrt(3) / 2;
//    } else if (ang === 150) {
//      cos = -Math.sqrt(3) / 2;
//      sin = 0.5;
//    } else if (ang === 210) {
//      cos = -Math.sqrt(3) / 2;
//      sin = -0.5;
//    } else if (ang === 240) {
//      cos = -0.5;
//      sin = -Math.sqrt(3) / 2;
//    } else if (ang === 300) {
//      cos = 0.5;
//      sin = -Math.sqrt(3) / 2;
//    } else if (ang === 330) {
//      cos = Math.sqrt(3) / 2;
//      sin = -0.5;
//    } else {
//      const rad = (Math.PI * ang) / 180;
//      cos = Math.cos(rad);
//      sin = Math.sin(rad);
//    }
//    const [a0, b0, c0, d0, e0, f0] = this.matrix;
//    this.matrix = [
//      a0 * cos + c0 * sin,
//      b0 * cos + d0 * sin,
//      c0 * cos - a0 * sin,
//      d0 * cos - b0 * sin,
//      e0,
//      f0,
//    ];
//    return this;
    TODO()
  }

  fun scale(sx: Double, sy: Double) {
//    const [a0, b0, c0, d0, e0, f0] = this.matrix;
//    this.matrix = [a0 * sx, b0 * sx, c0 * sy, d0 * sy, e0, f0];
//    return this;
    TODO()
  }

  fun translate(tx: Double, ty: Double) {
//    const [a0, b0, c0, d0, e0, f0] = this.matrix;
//    this.matrix = [
//      a0,
//      b0,
//      c0,
//      d0,
//      a0 * tx + c0 * ty + e0,
//      b0 * tx + d0 * ty + f0,
//    ];
//    return this;
    TODO()
  }

  fun save() {
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
//    this.saveStack.push({ matrix: this.matrix });
//    return this;
    TODO()
  }

  fun restore() {
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
//    const s = this.saveStack.pop();
//    if (s) {
//      this.matrix = s.matrix;
//    }
//    return this;
    TODO()
  }

  fun transformPoint(x: Double, y: Double): Vec2 {
    val (a, b, c, d, e, f) = this.matrix
//    return [a * x + c * y + e, b * x + d * y + f];
    return Vec2(x = a * x + c * y + e, y = b * x + d * y + f)
  }

  fun beginPath(): Shape {
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
    val state = requireNotNull(this.resultState as? ResultState.New) {
      "resultState is not New"
    }
    state.selfIntersect.beginPath()
    return this.endPath()
  }

  fun moveTo(x: Double, y: Double): Shape {
    resultState.requireNew { "Cannot change shape after using it in an operation" }
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
//    if (this.pathState.kind !== "beginPath") {
//      this.beginPath();
//    }
    if (pathState !is IPathState.BeginPath) {
      beginPath()
    }
    val current = this.transformPoint(x, y)
//    this.pathState = {
//      kind: "moveTo",
//      start: current,
//      current,
//    };
    pathState = IPathState.MoveTo(
      start = current,
      current = current,
    )
    return this
  }

  fun lineTo(x: Double, y: Double): Shape {
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
    val resultState = resultState.requireNew { "Cannot change shape after using it in an operation" }
//    if (this.pathState.kind !== "moveTo") {
//      throw new Error("PolyBool: Must call moveTo prior to calling lineTo");
//    }
    val pathState = this.pathState
    require(pathState is IPathState.MoveTo) {
      "Must call moveTo prior to calling lineTo"
    }
    val current = this.transformPoint(x, y)
    resultState.selfIntersect.addLine(pathState.current, current)
    pathState.current = current
    return this
  }

  fun rect(x: Double, y: Double, width: Double, height: Double) {
//    return this.moveTo(x, y)
//      .lineTo(x + width, y)
//      .lineTo(x + width, y + height)
//      .lineTo(x, y + height)
//      .closePath()
//      .moveTo(x, y);
    TODO()
  }

  fun bezierCurveTo(
    cp1x: Double,
    cp1y: Double,
    cp2x: Double,
    cp2y: Double,
    x: Double,
    y: Double,
  ): Shape {
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
    val resultState = this.resultState
    require(resultState is ResultState.New) {
      "Cannot change shape after using it in an operation"
    }
//    if (this.pathState.kind !== "moveTo") {
//      throw new Error(
//        "PolyBool: Must call moveTo prior to calling bezierCurveTo",
//      );
//    }
    val pathState = this.pathState
    require(pathState is IPathState.MoveTo) {
      "Must call moveTo prior to calling bezierCurveTo"
    }
    val current = this.transformPoint(x, y)
    resultState.selfIntersect.addCurve(
      from = pathState.current,
      c1 = this.transformPoint(cp1x, cp1y),
      c2 = this.transformPoint(cp2x, cp2y),
      to = current,
    )
    pathState.current = current
    return this
  }

  fun closePath(): Shape {
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
    val state = resultState.requireNew {
      "Cannot change shape after using it in an operation"
    }
    // close with a line if needed
    val pathState = this.pathState
    if (
      pathState is IPathState.MoveTo &&
      !this.geo.isEqualVec2(pathState.start, pathState.current)
//      this.pathState.kind === "moveTo" &&
//      !this.geo.isEqualVec2(this.pathState.start, this.pathState.current)
    ) {
      state.selfIntersect.addLine(
        from = pathState.current,
        to = pathState.start,
      )
      pathState.current = pathState.start
    }
    state.selfIntersect.closePath()
    return this.endPath()
  }

  fun endPath(): Shape {
//    if (this.resultState.state !== "new") {
//      throw new Error(
//        "PolyBool: Cannot change shape after using it in an operation",
//      );
//    }
//    this.pathState = { kind: "beginPath" };
    require(resultState is ResultState.New) {
      "Cannot change shape after using it in an operation"
    }
    pathState = IPathState.BeginPath
    return this
  }

  private fun selfIntersect(): List<SegmentBool> {
//    if (this.resultState.state === "new") {
//      this.resultState = {
//        state: "seg",
//        segments: this.resultState.selfIntersect.calculate(),
//      };
//    }
    when (val resultState = this.resultState) {
      is ResultState.New                                                   -> {
        val newResultState = ResultState.Segments(resultState.selfIntersect.calculate())
        this.resultState = newResultState
        return newResultState.segments
      }

      is ResultState.Regions                                               -> return resultState.segments
      is ResultState.Segments -> return resultState.segments
    }
  }

  fun segments(): List<List<Segment>> {
//    if (this.resultState.state !== "reg") {
//      const seg = this.selfIntersect();
//      this.resultState = {
//        state: "reg",
//        segments: seg,
//        regions: SegmentChainer(seg, this.geo, this.log),
//      };
//    }
//    val initialResultState = this.resultState
//    if (initialResultState !is ResultState.Reg) {
//      val seg = selfIntersect()
//      this.resultState = ResultState.Reg(
//        segments = seg,
//        regions = SegmentChainer(seg, this.geo, this.log),
//      )
//    }
    val initialResultState = this.resultState
    val resultState: ResultState.Regions =
      if (initialResultState is ResultState.Regions) {
        initialResultState
      } else {
        val seg = selfIntersect()
        ResultState.Regions(
          segments = seg,
          regions = SegmentChainer(
            segments = seg,
            geo = this.geo,
            log = this.log,
          ),
        )
      }
    this.resultState = resultState
    return resultState.regions
  }

  fun <T : IPolyBoolReceiver> output(
    receiver: T,
    matrix: Vec6 = Vec6(1, 0, 0, 1, 0, 0),
  ): T {
    return segmentsToReceiver(this.segments(), this.geo, receiver, matrix)
  }

  fun combine(shape: Shape): ShapeCombined {
    val int = Intersecter(false, this.geo, this.log)
//    for (const seg of this.selfIntersect()) {
//      int.addSegment(copySegmentBool(seg, this.log), true);
//    }
    selfIntersect().forEach { segment ->
      val copy = copySegmentBool(segment, log)
      int.addSegment(copy, true)
    }
//    for (const seg of shape.selfIntersect()) {
//      int.addSegment(copySegmentBool(seg, this.log), false);
//    }
    shape.selfIntersect().forEach { segment ->
      val copy = copySegmentBool(segment, log)
      int.addSegment(copy, false)
    }
    return ShapeCombined(int.calculate(), this.geo, this.log)
  }
}

class ShapeCombined(
  private val segments: List<SegmentBool>,
  private val geo: Geometry,
  private val log: BuildLog? = null,
) {

  fun union(): Shape {
    return Shape(
      geo = this.geo,
      segments = SegmentSelector.union(this.segments, this.log),
      log = this.log,
    )
  }

  fun intersect(): Shape {
    return Shape(
      geo = this.geo,
      segments = SegmentSelector.intersect(this.segments, this.log),
      log = this.log,
    )
  }

  fun difference(): Shape {
    return Shape(
      geo = this.geo,
      segments = SegmentSelector.difference(this.segments, this.log),
      log = this.log,
    )
  }

  fun differenceRev(): Shape {
    return Shape(
      geo = this.geo,
      segments = SegmentSelector.differenceRev(this.segments, this.log),
      log = this.log,
    )
  }

  fun xor(): Shape {
    return Shape(
      geo = this.geo,
      segments = SegmentSelector.xor(this.segments, this.log),
      log = this.log,
    )
  }
}
