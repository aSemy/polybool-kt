package dev.adamko.polybool

import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.test.Test
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value
import org.graalvm.polyglot.proxy.ProxyObject

class SegmentChainerTest {

  @Test
  fun test1() {
    val geo = GeometryEpsilon()
    val segments = listOf(
      SegmentBoolLine(
        SegmentLine(
          p0 = Vec2(1.0, 2.0),
          p1 = Vec2(3.0, 4.0),
          geo = geo,
        )
      )
    )

    val buildLog = BuildLog()

    val ktResult = runCatching {
      SegmentChainer(
        segments = segments,
        geo = geo,
        log = buildLog,
      )
    }.getOrNull()

    println("BuildLog.list():")
    buildLog.list().forEach {
      println(it)
    }

    val ktResultStr = ktResult?.toJsonString()

    val jsResult = segmentChainerJs(segments, geo)
    val jsResultObj = json.encodeToString(JsonArray.serializer(), json.parseToJsonElement(jsResult).jsonArray)

    ktResultStr shouldBe jsResultObj
  }

  @Test
  fun test2() {
    val geo = GeometryEpsilon()
    val segments = listOf(
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(50.0, 50.0), p1 = Vec2(110.0, 50.0), geo = geo),
        fill = SegmentBoolFill(above = true, below = false),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = true, below = false),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(110.0, 50.0), p1 = Vec2(110.0, 110.0), geo = geo),
        fill = SegmentBoolFill(above = true, below = false),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = true, below = false),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(50.0, 50.0), p1 = Vec2(110.0, 110.0), geo = geo),
        fill = SegmentBoolFill(above = false, below = true),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = false, below = true),
        otherFill = null,
      ),
      SegmentBoolLine(
        SegmentLine(p0 = Vec2(130.0, 50.0), p1 = Vec2(130.0, 130.0), geo = geo),
        SegmentBoolFill(above = false, below = true),
        closed = true,
        null,
        myFill = SegmentBoolFill(above = false, below = true),
        null
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(130.0, 130.0), p1 = Vec2(150.0, 150.0), geo = geo),
        fill = SegmentBoolFill(above = false, below = true),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = false, below = true),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(130.0, 50.0), p1 = Vec2(178.0, 80.0), geo = geo),
        fill = SegmentBoolFill(above = true, below = false),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = true, below = false),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(150.0, 150.0), p1 = Vec2(178.0, 80.0), geo = geo),
        fill = SegmentBoolFill(above = false, below = true),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = false, below = true),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(130.0, 50.0), p1 = Vec2(190.0, 50.0), geo = geo),
        fill = SegmentBoolFill(above = true, below = false),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = true, below = false),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(130.0, 50.0), p1 = Vec2(190.0, 50.0), geo = geo),
        fill = SegmentBoolFill(above = false, below = true),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = false, below = true),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(178.0, 80.0), p1 = Vec2(190.0, 50.0), geo = geo),
        fill = SegmentBoolFill(above = true, below = false),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = true, below = false),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(190.0, 50.0), p1 = Vec2(260.0, 50.0), geo = geo),
        fill = SegmentBoolFill(above = true, below = false),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = true, below = false),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(260.0, 50.0), p1 = Vec2(260.0, 131.25), geo = geo),
        fill = SegmentBoolFill(above = true, below = false),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = true, below = false),
        otherFill = null,
      ),
      SegmentBoolLine(
        data = SegmentLine(p0 = Vec2(178.0, 80.0), p1 = Vec2(260.0, 131.25), geo = geo),
        fill = SegmentBoolFill(above = false, below = true),
        closed = true,
        log = null,
        myFill = SegmentBoolFill(above = false, below = true),
        otherFill = null,
      ),
    )

    val buildLog = BuildLog()

    val ktResult = kotlin.runCatching {
      SegmentChainer(
        segments = segments,
        geo = geo,
        log = buildLog,
      )
    }.getOrNull()


    println("BuildLog.list():")
    buildLog.list().forEach {
      println(it)
    }


    val ktResultStr = ktResult?.toJsonString()
    val jsResult = segmentChainerJs(segments, geo)
    val jsResultObj = json.encodeToString(JsonArray.serializer(), json.parseToJsonElement(jsResult).jsonArray)

    ktResultStr shouldBe jsResultObj
  }

  companion object {
    private val json = Json {
      prettyPrint = true
      allowTrailingComma = true
    }

    private fun segmentChainerJs(
      segments: List<SegmentBool>,
      geo: Geometry,
    ): String {

      val jsFilePath = "/Users/dev/projects/adam/polybool-kt/dist/polybool.js"
      val jsCode = Path(jsFilePath).readText()

      // Create a GraalVM polyglot context
      Context.newBuilder("js")
        .allowAllAccess(true)
        .option("js.esm-eval-returns-exports", "true")
        .build().use { context ->

          val source = Source.newBuilder("js", jsCode, "polybool.js")
            .mimeType("application/javascript+module")
            .build()

          val exports: Value = context.eval(source)
          val geoJs = geo.toJsValue(exports)
          val segmentsJs = segments.map { segment ->
            segment.toJs(exports)
          }.toTypedArray()

          val buildLog = exports.getMember("BuildLog").newInstance()

          val segmentChainer = exports.getMember("SegmentChainer")
          val output = segmentChainer.execute(segmentsJs, geoJs, buildLog)

          val result = context.convertValueToJson(output)
          println("JS result: $result")

          val buildLogList = buildLog.getMember("list")
//          println("buildLogList: $buildLogList")
          println("buildLogList:")
          for (i in 0 until buildLogList.arraySize) {
            println(buildLogList.getArrayElement(i))
          }

          return result
          // Process the result
//        val resultInKotlin = mapResultToKotlin(output)
//        println(resultInKotlin)
        }
    }

    private fun List<List<Segment>>.toJsonString(): String {
      val str = buildString {
        append("[")
        this@toJsonString.forEach { segment ->
          append("[")
          segment.forEach { seg ->
            append("{")
            when (seg) {
              is SegmentCurve -> TODO()
              is SegmentLine  -> {
                append("\"p0\":[${seg.p0.x},${seg.p0.y}],")
                append("\"p1\":[${seg.p1.x},${seg.p1.y}],")
                val geo = seg.geo
                require(geo is GeometryEpsilon) { "expected GeometryEpsilon, got $geo" }
                append("\"geo\":{\"epsilon\":${geo.epsilon}}")
              }
            }
            append("}")
//            if (segment.last() != seg)
            append(",")
          }
          append("]")
          append(",")
        }
        append("]")
      }

      return json.encodeToString(JsonArray.serializer(), json.parseToJsonElement(str).jsonArray)
        .replace(".0", "")
//        .replace("1.0", "1")
    }
  }
}

private fun SegmentBool.toJs(exports: Value): Value {
  when (this) {
    is SegmentBoolCurve -> TODO()
    is SegmentBoolLine  -> {
      val segmentBoolLineClass = exports.getMember("SegmentBoolLine")
      return segmentBoolLineClass.newInstance(
        data.toJsValue(exports),
        fill?.toJs( ),
        closed,
        log,
        myFill.toJs( ),
        otherFill?.toJs( ),
      )
    }
  }
}

private fun SegmentBoolFill.toJs(): ProxyObject {
  return ProxyObject.fromMap(
    mapOf(
      "above" to above,
      "below" to below,
    )
  )
}


private fun SegmentLine.toJsValue(exports: Value): Value {
  val segmentLineClass = exports.getMember("SegmentLine")
  return segmentLineClass.newInstance(
    doubleArrayOf(p0.x, p0.y),
    doubleArrayOf(p1.x, p1.y),
    geo.toJsValue(exports),
  )
}

private fun Geometry.toJsValue(exports: Value): Value {
  require(this is GeometryEpsilon) { "expected GeometryEpsilon, got $this" }
  val geometryEpsilonClass = exports.getMember("GeometryEpsilon")
  return geometryEpsilonClass.newInstance(
    epsilon
  )
}


private fun createSegmentLine(
  exports: Value,
  x1: Double,
  y1: Double,
  x2: Double,
  y2: Double,
  geo: Value,
): Value {
  val segmentLineClass = exports.getMember("SegmentLine")
  return segmentLineClass.newInstance(
    doubleArrayOf(x1, y1),
    doubleArrayOf(x2, y2),
    geo,
  )
}


private fun Context.convertValueToJson(value: Value): String {
  // Use JSON.stringify to convert the GraalVM Value to a JSON string
  val jsonStringifier = eval("js", "JSON.stringify")
  val jsonString = jsonStringifier.execute(value).asString()
  return jsonString
}
