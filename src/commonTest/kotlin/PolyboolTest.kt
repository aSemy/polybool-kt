package dev.adamko.polybool

import de.infix.testBalloon.framework.testSuite
import io.kotest.matchers.shouldBe

@Suppress("unused")
val PolyBoolTest by testSuite {

  testSuite("basic intersection") {
    val result = polybool.intersect(triangle1, triangle2)

    test("regions") {
      result.regions shouldBe listOf(
        listOf(
          Vec2(10.0, 0.0),
          Vec2(5.0, 0.0),
          Vec2(7.5, 5.0),
        )
      )
    }
    test("inverted") {
      result.inverted shouldBe false
    }
  }

  testSuite("basic union") {
    val result = polybool.union(triangle1, triangle2)

    test("regions") {
      result.regions shouldBe listOf(
        listOf(
          Vec2(10.0, 10.0),
          Vec2(7.5, 5.0),
          Vec2(5.0, 10.0),
          Vec2(0.0, 0.0),
          Vec2(15.0, 0.0)
        )
      )
    }
    test("inverted") {
      result.inverted shouldBe false
    }
  }

  testSuite("union with curve") {
    val result = polybool.union(box1, curve1)

    test("regions") {
      result.regions shouldBe listOf(
        listOf(
          Vec2(10.0, 0.0),
          Vec6(10.0, -2.5, 7.5, -3.75, 5.0, -3.75),
          Vec2(5.0, -5.0),
          Vec2(0.0, -5.0),
          Vec2(0.0, 0.0),
        )
      )
    }

    test("inverted") {
      result.inverted shouldBe false
    }
  }

  test("example") {
    val shape1 = polybool.shape()
      .beginPath()
      .moveTo(50.0, 50.0)
      .lineTo(150.0, 150.0)
      .lineTo(190.0, 50.0)
      .closePath()
      .moveTo(130.0, 50.0)
      .lineTo(290.0, 150.0)
      .lineTo(290.0, 50.0)
      .closePath()
    val shape2 = polybool.shape()
      .beginPath()
      .moveTo(110.0, 20.0)
      .lineTo(110.0, 110.0)
      .lineTo(20.0, 20.0)
      .closePath()
      .moveTo(130.0, 170.0)
      .lineTo(130.0, 20.0)
      .lineTo(260.0, 20.0)
      .lineTo(260.0, 170.0)
      .closePath()

    val log =
      shape1.combine(shape2)
        .intersect()
        .output(Receiver())
        .done()
    log.joinToString("\n") shouldBe listOf(
      "beginPath",
      "moveTo x:110.0 y:110.0",
      "lineTo x:50.0 y:50.0",
      "lineTo x:110.0 y:50.0",
      "lineTo x:110.0 y:110.0",
      "closePath",
      "moveTo x:150.0 y:150.0",
      "lineTo x:178.0 y:80.0",
      "lineTo x:130.0 y:50.0",
      "lineTo x:130.0 y:130.0",
      "lineTo x:150.0 y:150.0",
      "closePath",
      "moveTo x:260.0 y:131.25",
      "lineTo x:178.0 y:80.0",
      "lineTo x:190.0 y:50.0",
      "lineTo x:260.0 y:50.0",
      "lineTo x:260.0 y:131.25",
      "closePath",
    ).joinToString("\n")
    /*
beginPath
moveTo x:110.0 y:110.0
lineTo x:50.0 y:50.0
lineTo x:110.0 y:50.0
lineTo x:110.0 y:110.0
closePath
moveTo x:150.0 y:150.0
lineTo x:178.0 y:80.0
lineTo x:130.0 y:50.0
lineTo x:130.0 y:130.0
lineTo x:150.0 y:150.0
closePath
moveTo x:190.0 y:50.0   <<<
lineTo x:190.0 y:50.0   <<<
closePath               <<<
moveTo x:260.0 y:131.25
lineTo x:178.0 y:80.0
lineTo x:190.0 y:50.0
lineTo x:260.0 y:50.0
lineTo x:260.0 y:131.25
closePath
     */
  }

  test("transforms") {
    val log = polybool.shape()
      .setTransform(3.0, 0.0, 0.0, 2.0, 100.0, 200.0)
      .beginPath()
      .moveTo(50.0, 50.0)
      .lineTo(-10.0, 50.0)
      .lineTo(10.0, 10.0)
      .closePath()
      .output(Receiver())
      .done()

    log.joinToString("\n") shouldBe listOf(
      "beginPath",
      "moveTo x:250.0 y:300.0",
      "lineTo x:70.0 y:300.0",
      "lineTo x:130.0 y:220.0",
      "lineTo x:250.0 y:300.0",
      "closePath",
    ).joinToString("\n")
  }
}


private val triangle1 = Polygon(
  regions = listOf(
    listOf(
      Vec2(0.0, 0.0),
      Vec2(5.0, 10.0),
      Vec2(10.0, 0.0),
    )
  ),
  inverted = false,
)

private val triangle2 = Polygon(
  regions = listOf(
    listOf(
      Vec2(5.0, 0.0),
      Vec2(10.0, 10.0),
      Vec2(15.0, 0.0),
    )
  ),
  inverted = false,
)

private val box1 = Polygon(
  regions = listOf(
    listOf(
      Vec2(0.0, 0.0),
      Vec2(5.0, 0.0),
      Vec2(5.0, -5.0),
      Vec2(0.0, -5.0),
    ),
  ),
  inverted = false,
)

private val curve1 = Polygon(
  regions = listOf(
    listOf(
      Vec2(0.0, 0.0),
      Vec6(0.0, -5.0, 10.0, -5.0, 10.0, 0.0),
    )
  ),
  inverted = false,
)

private class Receiver : IPolyBoolReceiver {
  private val log: ArrayDeque<String> = ArrayDeque()

  override fun beginPath() {
    this.log.addLast("beginPath")
  }

  override fun moveTo(x: Double, y: Double) {
    println("moveTo x:$x y:$y")
    this.log.addLast("moveTo x:$x y:$y")
  }

  override fun lineTo(x: Double, y: Double) {
    this.log.addLast("lineTo x:$x y:$y")
  }

  override fun bezierCurveTo(
    cp1x: Double,
    cp1y: Double,
    cp2x: Double,
    cp2y: Double,
    x: Double,
    y: Double,
  ) {
    this.log.addLast("bezierCurveTo cp1x:$cp1x cp1y:$cp1y cp2x:$cp2x cp2y:$cp2y x:$x y:$y")
  }

  override fun closePath() {
    this.log.addLast("closePath")
  }

  fun done(): List<Any> {
    return this.log.toList()
  }
}
