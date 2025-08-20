////
//// polybool - Boolean operations on polygons (union, intersection, etc)
//// by Sean Connelly (@velipso), https://sean.fun
//// Project Home: https://github.com/velipso/polybool
//// SPDX-License-Identifier: 0BSD
////
//
//import {
//  type Vec2,
//  type Vec6,
//  type Geometry,
//  GeometryEpsilon,
//} from "./Geometry";
//import { Shape, type ShapeCombined } from "./Shape";
//import BuildLog from "./BuildLog";
//export * from "./Segment";
//export * from "./Geometry";
//export * from "./Intersecter";
//export * from "./SegmentSelector";
//export * from "./SegmentChainer";
//export * from "./Shape";
//export * from "./BuildLog";

//export interface Polygon {
//  regions: Array<Array<Vec2 | Vec6>>;
//  inverted: boolean;
//}
class Polygon(
  val regions: List<List<Vector>>,
  val inverted: Boolean,
)

//export interface Segments {
//  shape: Shape;
//  inverted: boolean;
//}
data class Segments(
  val shape: Shape,
  val inverted: Boolean,
)

//export interface CombinedSegments {
//  shape: ShapeCombined;
//  inverted1: boolean;
//  inverted2: boolean;
//}
data class CombinedSegments(
  val shape: ShapeCombined,
  val inverted1: Boolean,
  val inverted2: Boolean,
)

class PolyBool(
  private val geo: Geometry = GeometryEpsilon(),
  private val log: BuildLog? = null
) {
//  private readonly geo: Geometry;
//  private log: BuildLog | null;
//
//  constructor(
//    geo: Geometry = new GeometryEpsilon(),
//    log: BuildLog | null = null,
//  ) {
//    this.geo = geo;
//    this.log = log;
//  }

  fun shape(): Shape {
    return Shape(this.geo, null, this.log)
  }

  fun buildLog(enable: Boolean) {
//    this.log = enable ? new BuildLog() : null;
//    return this.log?.list;
    TODO()
  }

  fun segments(poly: Polygon): Segments {
    val shape = this.shape()
    shape.beginPath()
//    for (const region of poly.regions) {
    poly.regions.forEach { region ->
      val lastPoint = region.last()
//      shape.moveTo(
//        lastPoint[lastPoint.length - 2],
//        lastPoint[lastPoint.length - 1],
//      )
      when (lastPoint) {
        is Vec2 -> shape.moveTo(lastPoint.x, lastPoint.y)
        is Vec6 -> shape.moveTo(lastPoint.e, lastPoint.f)
      }
      for (p in region) {
//        if (p.length === 2) {
//          shape.lineTo(p[0], p[1]);
//        } else if (p.length === 6) {
//          shape.bezierCurveTo(p[0], p[1], p[2], p[3], p[4], p[5]);
//        } else {
//          throw new Error("PolyBool: Invalid point in region");
//        }
        when (p) {
          is Vec2 -> shape.lineTo(p.x, p.y)
          is Vec6 -> shape.bezierCurveTo(p[0], p[1], p[2], p[3], p[4], p[5])
        }
      }
      shape.closePath()
    }
//    return { shape, inverted: poly.inverted };
    return Segments(
      shape = shape,
      inverted = poly.inverted,
    )
  }

  fun combine(segments1: Segments, segments2: Segments): CombinedSegments {
    return CombinedSegments(
      shape = segments1.shape.combine(segments2.shape),
      inverted1 = segments1.inverted,
      inverted2 = segments2.inverted,
    )
  }

  fun selectUnion(combined: CombinedSegments): Segments {
//    return {
//      shape: combined.inverted1
//        ? combined.inverted2
//          ? combined.shape.intersect()
//          : combined.shape.difference()
//        : combined.inverted2
//          ? combined.shape.differenceRev()
//          : combined.shape.union(),
//      inverted: combined.inverted1 || combined.inverted2,
//    };
    return Segments(
      shape = if (combined.inverted1) {
        if (combined.inverted2) {
          combined.shape.intersect()
        } else {
          combined.shape.difference()
        }
      } else {
        if (combined.inverted2) {
          combined.shape.differenceRev()
        } else {
          combined.shape.union()
        }
      },
      inverted = combined.inverted1 || combined.inverted2,
    )
  }

  fun selectIntersect(combined: CombinedSegments): Segments {
//    return {
//      shape: combined.inverted1
//        ? combined.inverted2
//          ? combined.shape.union()
//          : combined.shape.differenceRev()
//        : combined.inverted2
//          ? combined.shape.difference()
//          : combined.shape.intersect(),
//      inverted: combined.inverted1 && combined.inverted2,
//    };

    return Segments(
      shape = when {
        combined.inverted1 && combined.inverted2  -> combined.shape.union()
        combined.inverted1 && !combined.inverted2 -> combined.shape.differenceRev()
        !combined.inverted1 && combined.inverted2 -> combined.shape.difference()
        else                                      -> combined.shape.intersect()
      },
      inverted = combined.inverted1 && combined.inverted2,
    )
  }

  fun selectDifference(combined: CombinedSegments): Segments {
//    return {
//      shape: combined.inverted1
//        ? combined.inverted2
//          ? combined.shape.differenceRev()
//          : combined.shape.union()
//        : combined.inverted2
//          ? combined.shape.intersect()
//          : combined.shape.difference(),
//      inverted: combined.inverted1 && !combined.inverted2,
//    };

    TODO()
  }

  fun selectDifferenceRev(combined: CombinedSegments): Segments {
//    return {
//      shape: combined.inverted1
//        ? combined.inverted2
//          ? combined.shape.difference()
//          : combined.shape.intersect()
//        : combined.inverted2
//          ? combined.shape.union()
//          : combined.shape.differenceRev(),
//      inverted: !combined.inverted1 && combined.inverted2,
//    };
    TODO()
  }

  fun selectXor(combined: CombinedSegments): Segments {
//    return {
//      shape: combined.shape.xor(),
//      inverted: combined.inverted1 !== combined.inverted2,
//    };
    TODO()
  }

  fun polygon(segments: Segments): Polygon {
//    const regions: Array<Array<Vec2 | Vec6>> = [];
    val regions: ArrayDeque<ArrayDeque<Vector>> = ArrayDeque()
//    const receiver = {
//      beginPath: () => {},
//      moveTo: () => {
//        regions.push([]);
//      },
//      lineTo: (x: number, y: number) => {
//        regions[regions.length - 1].push([x, y]);
//      },
//      bezierCurveTo: (
//        c1x: number,
//        c1y: number,
//        c2x: number,
//        c2y: number,
//        x: number,
//        y: number,
//      ) => {
//        regions[regions.length - 1].push([c1x, c1y, c2x, c2y, x, y]);
//      },
//      closePath: () => {},
//    };
    val receiver = IPolyBoolReceiver(
      beginPath = {},
      moveTo = { _, _ ->
        regions.addLast(ArrayDeque())
      },
      lineTo = { x: Double, y: Double ->
        regions.last().addLast(Vec2(x, y))
      },
      bezierCurveTo = { c1x: Double, c1y: Double, c2x: Double, c2y: Double, x: Double, y: Double ->
        regions.last().addLast(Vec6(c1x, c1y, c2x, c2y, x, y))
      },
      closePath = {},
    )
    segments.shape.output(receiver)
//    return { regions, inverted: segments.inverted };
    return Polygon(
      regions = regions,
      inverted = segments.inverted,
    )
  }

  // helper functions for common operations
  fun union(poly1: Polygon, poly2: Polygon): Polygon {
    val seg1 = this.segments(poly1);
    val seg2 = this.segments(poly2);
    val comb = this.combine(seg1, seg2);
    val seg3 = this.selectUnion(comb);
    return this.polygon(seg3);
  }

  //  intersect(poly1: Polygon, poly2: Polygon): Polygon {
//    const seg1 = this.segments(poly1);
//    const seg2 = this.segments(poly2);
//    const comb = this.combine(seg1, seg2);
//    const seg3 = this.selectIntersect(comb);
//    return this.polygon(seg3);
//  }
  fun intersect(
    poly1: Polygon,
    poly2: Polygon,
  ): Polygon {
    val seg1 = segments(poly1)
    val seg2 = segments(poly2)
    val comb = combine(seg1, seg2)
    val seg3 = selectIntersect(comb)
    return this.polygon(seg3)
  }

  fun difference(poly1: Polygon, poly2: Polygon): Polygon {
//    const seg1 = this.segments(poly1);
//    const seg2 = this.segments(poly2);
//    const comb = this.combine(seg1, seg2);
//    const seg3 = this.selectDifference(comb);
//    return this.polygon(seg3);
    TODO()
  }

  fun differenceRev(poly1: Polygon, poly2: Polygon): Polygon {
//    const seg1 = this.segments(poly1);
//    const seg2 = this.segments(poly2);
//    const comb = this.combine(seg1, seg2);
//    const seg3 = this.selectDifferenceRev(comb);
//    return this.polygon(seg3);
    TODO()
  }

  fun xor(poly1: Polygon, poly2: Polygon): Polygon {
//    const seg1 = this.segments(poly1);
//    const seg2 = this.segments(poly2);
//    const comb = this.combine(seg1, seg2);
//    const seg3 = this.selectXor(comb);
//    return this.polygon(seg3);
    TODO()
  }
}

val polybool = PolyBool()

//const polybool = new PolyBool();
//
//export default polybool;
